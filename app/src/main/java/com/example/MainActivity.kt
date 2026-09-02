package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.BeanBag
import com.example.data.CoffeeRecipe
import com.example.ui.CoffeeTab
import com.example.ui.CoffeeViewModel
import com.example.ui.components.AddBeanBagDialog
import com.example.ui.components.AddRecipeDialog
import com.example.ui.components.BrewTimerSheet
import com.example.ui.components.EditBeanWeightDialog
import com.example.ui.components.LogBrewSheet
import com.example.ui.screens.LogScreen
import com.example.ui.screens.RecipesScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CoffeeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeApp(viewModel: CoffeeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showLogSheet by remember { mutableStateOf(false) }
    var selectedRecipeForLog by remember { mutableStateOf<CoffeeRecipe?>(null) }
    var showTimerSheet by remember { mutableStateOf(false) }
    var showAddRecipeDialog by remember { mutableStateOf(false) }
    var showAddBeanBagDialog by remember { mutableStateOf(false) }
    var selectedBeanBagForEdit by remember { mutableStateOf<BeanBag?>(null) }

    val logSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val timerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val activeBeanBag = uiState.beanBags.firstOrNull { it.isActive }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == CoffeeTab.LOG,
                    onClick = { viewModel.setTab(CoffeeTab.LOG) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == CoffeeTab.LOG) Icons.Filled.Coffee else Icons.Outlined.Coffee,
                            contentDescription = "Log"
                        )
                    },
                    label = { Text("Log", fontWeight = if (uiState.currentTab == CoffeeTab.LOG) FontWeight.Black else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_log")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == CoffeeTab.RECIPES,
                    onClick = { viewModel.setTab(CoffeeTab.RECIPES) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == CoffeeTab.RECIPES) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                            contentDescription = "Recipes"
                        )
                    },
                    label = { Text("Recipes", fontWeight = if (uiState.currentTab == CoffeeTab.RECIPES) FontWeight.Black else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_recipes")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == CoffeeTab.STATS,
                    onClick = { viewModel.setTab(CoffeeTab.STATS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == CoffeeTab.STATS) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                            contentDescription = "Stats"
                        )
                    },
                    label = { Text("Stats & Beans", fontWeight = if (uiState.currentTab == CoffeeTab.STATS) FontWeight.Black else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("tab_stats")
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Crossfade(targetState = uiState.currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    CoffeeTab.LOG -> {
                        LogScreen(
                            brews = uiState.filteredBrews,
                            recipes = uiState.recipes,
                            activeBeanBag = activeBeanBag,
                            stats = uiState.stats,
                            filterRating = uiState.filterRating,
                            sortOption = uiState.sortOption,
                            onSetFilterRating = { viewModel.setFilterRating(it) },
                            onSetSortOption = { viewModel.setSortOption(it) },
                            onOpenEditBeanWeight = { bag -> selectedBeanBagForEdit = bag },
                            onOpenLogSheet = { recipe ->
                                selectedRecipeForLog = recipe
                                showLogSheet = true
                            },
                            onOpenTimer = { recipe ->
                                viewModel.startTimer(recipe)
                                showTimerSheet = true
                            },
                            onQuickLog = { recipe ->
                                viewModel.quickLogPreset(recipe)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Logged ${recipe.name} (${recipe.defaultCoffeeGrams.toInt()}g)")
                                }
                            },
                            onDeleteBrew = { id -> viewModel.deleteBrew(id) }
                        )
                    }

                    CoffeeTab.RECIPES -> {
                        RecipesScreen(
                            recipes = uiState.recipes,
                            recipeRatings = uiState.recipeRatings,
                            onOpenAddRecipeDialog = { showAddRecipeDialog = true },
                            onStartTimer = { recipe ->
                                viewModel.startTimer(recipe)
                                showTimerSheet = true
                            },
                            onOpenLogSheet = { recipe ->
                                selectedRecipeForLog = recipe
                                showLogSheet = true
                            },
                            onDeleteRecipe = { id -> viewModel.deleteRecipe(id) }
                        )
                    }

                    CoffeeTab.STATS -> {
                        StatsScreen(
                            stats = uiState.stats,
                            beanBags = uiState.beanBags,
                            onOpenAddBeanBag = { showAddBeanBagDialog = true },
                            onOpenEditBeanWeight = { bag -> selectedBeanBagForEdit = bag },
                            onToggleActiveBag = { bag ->
                                viewModel.saveBeanBag(
                                    id = bag.id,
                                    name = bag.name,
                                    roaster = bag.roaster,
                                    roastLevel = bag.roastLevel,
                                    roastDate = bag.roastDate,
                                    initialWeightGrams = bag.initialWeightGrams,
                                    remainingWeightGrams = bag.remainingWeightGrams,
                                    isActive = !bag.isActive
                                )
                            },
                            onDeleteBeanBag = { id -> viewModel.deleteBeanBag(id) }
                        )
                    }
                }
            }
        }
    }

    // Modal Log Brew Sheet
    if (showLogSheet) {
        LogBrewSheet(
            sheetState = logSheetState,
            recipes = uiState.recipes,
            beanBags = uiState.beanBags,
            initialRecipe = selectedRecipeForLog,
            onDismiss = { showLogSheet = false },
            onSave = { recipeName, coffeeGrams, waterGrams, ratio, grindSize, brewTimeSeconds, waterTemp, beanRoast, rating, notes, bagId ->
                viewModel.logBrew(
                    recipeName = recipeName,
                    coffeeGrams = coffeeGrams,
                    waterGrams = waterGrams,
                    ratio = ratio,
                    grindSize = grindSize,
                    brewTimeSeconds = brewTimeSeconds,
                    waterTempCelsius = waterTemp,
                    beanRoast = beanRoast,
                    rating = rating,
                    notes = notes,
                    bagId = bagId
                )
                showLogSheet = false
                scope.launch {
                    snackbarHostState.showSnackbar("Logged $recipeName ($coffeeGrams g)")
                }
            }
        )
    }

    // Modal Brew Timer Sheet
    if (showTimerSheet && uiState.timerRecipe != null) {
        val recipe = uiState.timerRecipe!!
        BrewTimerSheet(
            sheetState = timerSheetState,
            recipe = recipe,
            isRunning = uiState.timerRunning,
            elapsedSeconds = uiState.timerElapsedSeconds,
            onTogglePlayPause = { viewModel.pauseOrResumeTimer() },
            onReset = { viewModel.resetTimer() },
            onFinishAndLog = { rating, notes ->
                viewModel.finishTimerAndLog(rating, notes)
                showTimerSheet = false
                scope.launch {
                    snackbarHostState.showSnackbar("Logged timed brew for ${recipe.name}!")
                }
            },
            onDismiss = {
                showTimerSheet = false
            }
        )
    }

    // Add Recipe Dialog
    if (showAddRecipeDialog) {
        AddRecipeDialog(
            onDismiss = { showAddRecipeDialog = false },
            onSave = { name, method, origin, roast, notes, coffeeGrams, waterGrams, ratio, grindSize, targetTime, targetTemp, steps ->
                viewModel.saveRecipe(
                    name = name,
                    method = method,
                    coffeeOrigin = origin,
                    roastLevel = roast,
                    tastingNotes = notes,
                    coffeeGrams = coffeeGrams,
                    waterGrams = waterGrams,
                    ratio = ratio,
                    grindSize = grindSize,
                    targetTimeSeconds = targetTime,
                    targetTempCelsius = targetTemp,
                    steps = steps
                )
                showAddRecipeDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Added recipe $name")
                }
            }
        )
    }

    // Add Bean Bag Dialog
    if (showAddBeanBagDialog) {
        AddBeanBagDialog(
            onDismiss = { showAddBeanBagDialog = false },
            onSave = { name, roaster, roastLevel, roastDate, initialWeight, remainingWeight, isActive ->
                viewModel.saveBeanBag(
                    name = name,
                    roaster = roaster,
                    roastLevel = roastLevel,
                    roastDate = roastDate,
                    initialWeightGrams = initialWeight,
                    remainingWeightGrams = remainingWeight,
                    isActive = isActive
                )
                showAddBeanBagDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Added $name to Bean Inventory")
                }
            }
        )
    }

    // Edit Bean Bag Weight Dialog
    if (selectedBeanBagForEdit != null) {
        val currentBag = selectedBeanBagForEdit!!
        EditBeanWeightDialog(
            bag = currentBag,
            onDismiss = { selectedBeanBagForEdit = null },
            onSaveWeight = { newWeight ->
                viewModel.updateBeanBagWeight(currentBag.id, newWeight)
                selectedBeanBagForEdit = null
                scope.launch {
                    snackbarHostState.showSnackbar("Updated ${currentBag.name} remaining weight to ${newWeight.toInt()}g")
                }
            }
        )
    }
}
