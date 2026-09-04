package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BeanBag
import com.example.data.CoffeeBrew
import com.example.data.CoffeeDatabase
import com.example.data.CoffeeRecipe
import com.example.data.CoffeeRepository
import com.example.ui.theme.AppThemeColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class CoffeeTab {
    LOG,
    RECIPES,
    STATS
}

enum class LogSortOption(val displayName: String) {
    NEWEST("Newest"),
    OLDEST("Oldest"),
    HIGHEST_RATING("Highest ★"),
    LOWEST_RATING("Lowest ★"),
    HIGHEST_DOSE("Dose (High)")
}

data class RecipeRatingSummary(
    val averageRating: Double = 0.0,
    val count: Int = 0
)

data class CoffeeStats(
    val totalBrews: Int = 0,
    val todayBrews: Int = 0,
    val weekBrews: Int = 0,
    val totalGrams: Double = 0.0,
    val avgGramsPerCup: Double = 0.0,
    val topRecipe: String = "—",
    val activeBagRemainingGrams: Double? = null,
    val activeBagName: String? = null
)

data class CoffeeUiState(
    val brews: List<CoffeeBrew> = emptyList(),
    val filteredBrews: List<CoffeeBrew> = emptyList(),
    val recipes: List<CoffeeRecipe> = emptyList(),
    val beanBags: List<BeanBag> = emptyList(),
    val recipeRatings: Map<String, RecipeRatingSummary> = emptyMap(),
    val currentTab: CoffeeTab = CoffeeTab.LOG,
    val filterRating: Int = 0, // 0 = all, 5 = 5 stars, 4 = 4+ stars, etc.
    val sortOption: LogSortOption = LogSortOption.NEWEST,
    val stats: CoffeeStats = CoffeeStats(),
    val themeColor: AppThemeColor = AppThemeColor.MILKY_COFFEE,
    val timerRunning: Boolean = false,
    val timerElapsedSeconds: Int = 0,
    val timerRecipe: CoffeeRecipe? = null,
    val isLoading: Boolean = false
)

private data class DataTriple(
    val brews: List<CoffeeBrew>,
    val recipes: List<CoffeeRecipe>,
    val beanBags: List<BeanBag>
)

private data class FilterSortPair(
    val filterRating: Int,
    val sortOption: LogSortOption
)

private data class TimerTriple(
    val isRunning: Boolean,
    val elapsed: Int,
    val recipe: CoffeeRecipe?
)

class CoffeeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("coffee_app_prefs", Context.MODE_PRIVATE)
    private val repository: CoffeeRepository
    private var timerJob: Job? = null

    private val _currentTab = MutableStateFlow(CoffeeTab.LOG)
    private val _filterRating = MutableStateFlow(0)
    private val _sortOption = MutableStateFlow(LogSortOption.NEWEST)
    private val _themeColor = MutableStateFlow(
        AppThemeColor.fromId(prefs.getString("pref_theme_color", AppThemeColor.MILKY_COFFEE.id))
    )
    private val _timerRunning = MutableStateFlow(false)
    private val _timerElapsedSeconds = MutableStateFlow(0)
    private val _timerRecipe = MutableStateFlow<CoffeeRecipe?>(null)

    val themeUnlockEvent = MutableStateFlow<AppThemeColor?>(null)

    val uiState: StateFlow<CoffeeUiState>

    init {
        val database = CoffeeDatabase.getDatabase(application, viewModelScope)
        repository = CoffeeRepository(database.coffeeDao())

        val dataFlow = combine(
            repository.allBrews,
            repository.allRecipes,
            repository.allBeanBags
        ) { brews, recipes, bags ->
            DataTriple(brews, recipes, bags)
        }

        val filterSortFlow = combine(
            _filterRating,
            _sortOption
        ) { filter, sort ->
            FilterSortPair(filter, sort)
        }

        val timerFlow = combine(
            _timerRunning,
            _timerElapsedSeconds,
            _timerRecipe
        ) { running, elapsed, timerRec ->
            TimerTriple(running, elapsed, timerRec)
        }

        uiState = combine(
            dataFlow,
            _currentTab,
            filterSortFlow,
            timerFlow,
            _themeColor
        ) { (brews, recipes, bags), tab, (filterRating, sortOption), (running, elapsed, timerRec), themeColor ->
            val stats = calculateStats(brews, bags)
            val recipeRatings = calculateRecipeRatings(brews)
            val filteredBrews = processFilteredBrews(brews, filterRating, sortOption)
            val safeTheme = if (themeColor.isUnlocked(stats.totalBrews)) themeColor else AppThemeColor.MILKY_COFFEE

            CoffeeUiState(
                brews = brews,
                filteredBrews = filteredBrews,
                recipes = recipes,
                beanBags = bags,
                recipeRatings = recipeRatings,
                currentTab = tab,
                filterRating = filterRating,
                sortOption = sortOption,
                stats = stats,
                themeColor = safeTheme,
                timerRunning = running,
                timerElapsedSeconds = elapsed,
                timerRecipe = timerRec,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoffeeUiState(isLoading = true)
        )
    }

    private fun calculateRecipeRatings(brews: List<CoffeeBrew>): Map<String, RecipeRatingSummary> {
        return brews.groupBy { it.recipeName }
            .mapValues { (_, brewList) ->
                val avg = if (brewList.isNotEmpty()) brewList.map { it.rating }.average() else 0.0
                RecipeRatingSummary(
                    averageRating = avg,
                    count = brewList.size
                )
            }
    }

    private fun processFilteredBrews(
        brews: List<CoffeeBrew>,
        filterRating: Int,
        sortOption: LogSortOption
    ): List<CoffeeBrew> {
        var result = if (filterRating > 0) {
            brews.filter { it.rating >= filterRating }
        } else {
            brews
        }

        result = when (sortOption) {
            LogSortOption.NEWEST -> result.sortedByDescending { it.timestamp }
            LogSortOption.OLDEST -> result.sortedBy { it.timestamp }
            LogSortOption.HIGHEST_RATING -> result.sortedByDescending { it.rating }
            LogSortOption.LOWEST_RATING -> result.sortedBy { it.rating }
            LogSortOption.HIGHEST_DOSE -> result.sortedByDescending { it.coffeeGrams }
        }

        return result
    }

    private fun calculateStats(brews: List<CoffeeBrew>, bags: List<BeanBag>): CoffeeStats {
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val startOfWeek = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val totalBrews = brews.size
        val todayBrews = brews.count { it.timestamp >= startOfToday }
        val weekBrews = brews.count { it.timestamp >= startOfWeek }
        val totalGrams = brews.sumOf { it.coffeeGrams }
        val avgGrams = if (totalBrews > 0) totalGrams / totalBrews else 0.0

        val topRecipe = brews.groupingBy { it.recipeName }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "—"

        val activeBag = bags.firstOrNull { it.isActive }

        return CoffeeStats(
            totalBrews = totalBrews,
            todayBrews = todayBrews,
            weekBrews = weekBrews,
            totalGrams = totalGrams,
            avgGramsPerCup = avgGrams,
            topRecipe = topRecipe,
            activeBagRemainingGrams = activeBag?.remainingWeightGrams,
            activeBagName = activeBag?.name
        )
    }

    fun setTab(tab: CoffeeTab) {
        _currentTab.value = tab
    }

    fun setFilterRating(rating: Int) {
        _filterRating.value = rating
    }

    fun setSortOption(sort: LogSortOption) {
        _sortOption.value = sort
    }

    fun logBrew(
        recipeName: String,
        coffeeGrams: Double,
        waterGrams: Double,
        ratio: String = "1:16.7",
        grindSize: String = "Medium",
        brewTimeSeconds: Int = 180,
        waterTempCelsius: Int = 93,
        beanRoast: String = "House Roast",
        rating: Int = 5,
        notes: String = "",
        bagId: Long? = null
    ) {
        viewModelScope.launch {
            val currentTotal = uiState.value.stats.totalBrews
            val brew = CoffeeBrew(
                recipeName = recipeName,
                coffeeGrams = coffeeGrams,
                waterGrams = waterGrams,
                ratio = ratio,
                grindSize = grindSize,
                brewTimeSeconds = brewTimeSeconds,
                waterTempCelsius = waterTempCelsius,
                beanRoast = beanRoast,
                rating = rating,
                notes = notes,
                timestamp = System.currentTimeMillis(),
                bagId = bagId
            )
            repository.logBrew(brew)
            checkThemeUnlockMilestone(currentTotal + 1)
        }
    }

    fun quickLogPreset(recipe: CoffeeRecipe) {
        viewModelScope.launch {
            val currentTotal = uiState.value.stats.totalBrews
            val activeBag = repository.getActiveBeanBag()
            val brew = CoffeeBrew(
                recipeName = recipe.name,
                coffeeGrams = recipe.defaultCoffeeGrams,
                waterGrams = recipe.defaultWaterGrams,
                ratio = recipe.ratio,
                grindSize = recipe.grindSize,
                brewTimeSeconds = recipe.targetTimeSeconds,
                waterTempCelsius = recipe.targetTempCelsius,
                beanRoast = activeBag?.name ?: recipe.coffeeOrigin.ifBlank { "House Blend" },
                rating = 5,
                notes = if (recipe.tastingNotes.isNotBlank()) "Notes: ${recipe.tastingNotes}" else "Quick log",
                timestamp = System.currentTimeMillis(),
                bagId = activeBag?.id
            )
            repository.logBrew(brew)
            checkThemeUnlockMilestone(currentTotal + 1)
        }
    }

    fun deleteBrew(id: Long) {
        viewModelScope.launch {
            repository.deleteBrew(id)
        }
    }

    fun saveRecipe(
        id: Long = 0,
        name: String,
        method: String,
        coffeeOrigin: String = "",
        roastLevel: String = "Medium",
        tastingNotes: String = "",
        coffeeGrams: Double,
        waterGrams: Double,
        ratio: String,
        grindSize: String,
        targetTimeSeconds: Int,
        targetTempCelsius: Int,
        steps: String
    ) {
        viewModelScope.launch {
            val recipe = CoffeeRecipe(
                id = id,
                name = name,
                method = method,
                coffeeOrigin = coffeeOrigin,
                roastLevel = roastLevel,
                tastingNotes = tastingNotes,
                defaultCoffeeGrams = coffeeGrams,
                defaultWaterGrams = waterGrams,
                ratio = ratio,
                grindSize = grindSize,
                targetTimeSeconds = targetTimeSeconds,
                targetTempCelsius = targetTempCelsius,
                steps = steps,
                isPreset = false
            )
            repository.saveRecipe(recipe)
        }
    }

    fun deleteRecipe(id: Long) {
        viewModelScope.launch {
            repository.deleteRecipe(id)
        }
    }

    fun saveBeanBag(
        id: Long = 0,
        name: String,
        roaster: String,
        roastLevel: String,
        roastDate: String,
        initialWeightGrams: Double,
        remainingWeightGrams: Double,
        isActive: Boolean
    ) {
        viewModelScope.launch {
            val bag = BeanBag(
                id = id,
                name = name,
                roaster = roaster,
                roastLevel = roastLevel,
                roastDate = roastDate,
                initialWeightGrams = initialWeightGrams,
                remainingWeightGrams = remainingWeightGrams,
                isActive = isActive
            )
            val savedId = repository.saveBeanBag(bag)
            if (isActive) {
                repository.setActiveBeanBag(if (id == 0L) savedId else id)
            }
        }
    }

    fun updateBeanBagWeight(bagId: Long, newWeight: Double) {
        viewModelScope.launch {
            val validWeight = if (newWeight < 0.0) 0.0 else newWeight
            repository.updateBeanBagWeight(bagId, validWeight)
        }
    }

    fun setActiveBeanBag(bagId: Long) {
        viewModelScope.launch {
            repository.setActiveBeanBag(bagId)
        }
    }

    fun deleteBeanBag(id: Long) {
        viewModelScope.launch {
            repository.deleteBeanBag(id)
        }
    }

    // --- Minimal Brew Timer ---
    fun startTimer(recipe: CoffeeRecipe?) {
        _timerRecipe.value = recipe
        _timerElapsedSeconds.value = 0
        _timerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerRunning.value) {
                delay(1000)
                _timerElapsedSeconds.value += 1
            }
        }
    }

    fun pauseOrResumeTimer() {
        if (_timerRunning.value) {
            _timerRunning.value = false
            timerJob?.cancel()
        } else {
            _timerRunning.value = true
            timerJob = viewModelScope.launch {
                while (_timerRunning.value) {
                    delay(1000)
                    _timerElapsedSeconds.value += 1
                }
            }
        }
    }

    fun resetTimer() {
        _timerRunning.value = false
        timerJob?.cancel()
        _timerElapsedSeconds.value = 0
    }

    fun finishTimerAndLog(rating: Int = 5, notes: String = "") {
        val recipe = _timerRecipe.value
        val elapsed = _timerElapsedSeconds.value
        resetTimer()
        if (recipe != null) {
            logBrew(
                recipeName = recipe.name,
                coffeeGrams = recipe.defaultCoffeeGrams,
                waterGrams = recipe.defaultWaterGrams,
                ratio = recipe.ratio,
                grindSize = recipe.grindSize,
                brewTimeSeconds = if (elapsed > 0) elapsed else recipe.targetTimeSeconds,
                waterTempCelsius = recipe.targetTempCelsius,
                rating = rating,
                notes = if (notes.isNotBlank()) notes else "Timed brew (${formatTime(elapsed)})"
            )
        }
    }

    fun seekTimer(seconds: Int) {
        _timerElapsedSeconds.value = seconds.coerceAtLeast(0)
    }

    fun clearThemeUnlockEvent() {
        themeUnlockEvent.value = null
    }

    private fun checkThemeUnlockMilestone(newTotal: Int) {
        val unlocked = when (newTotal) {
            1 -> AppThemeColor.MATCHA_GREEN
            10 -> AppThemeColor.ESPRESSO_AMBER
            30 -> AppThemeColor.BERRY_ROASTER
            50 -> AppThemeColor.NORDIC_SLATE
            else -> null
        }
        if (unlocked != null) {
            themeUnlockEvent.value = unlocked
        }
    }

    fun setThemeColor(color: AppThemeColor): Boolean {
        val total = uiState.value.stats.totalBrews
        if (!color.isUnlocked(total)) {
            return false
        }
        _themeColor.value = color
        prefs.edit().putString("pref_theme_color", color.id).apply()
        return true
    }

    private fun formatTime(totalSeconds: Int): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return "%02d:%02d".format(m, s)
    }
}

