package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.graphicsLayer
import com.example.ui.theme.instagramBounce
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.components.ScanCoffeeBagSheet
import com.example.ui.components.SettingsDialog
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
            val viewModel: CoffeeViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            MyApplicationTheme(themeColor = uiState.themeColor) {
                CoffeeApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeApp(viewModel: CoffeeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val themeUnlockEvent by viewModel.themeUnlockEvent.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showLogSheet by remember { mutableStateOf(false) }
    var selectedRecipeForLog by remember { mutableStateOf<CoffeeRecipe?>(null) }
    var showTimerSheet by remember { mutableStateOf(false) }
    var showAddRecipeDialog by remember { mutableStateOf(false) }
    var showAddBeanBagDialog by remember { mutableStateOf(false) }
    var showScanCoffeeBagSheet by remember { mutableStateOf(false) }
    var selectedBeanBagForEdit by remember { mutableStateOf<BeanBag?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val logSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val timerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scanSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val activeBeanBag = uiState.beanBags.firstOrNull { it.isActive }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        Triple(CoffeeTab.LOG, "Brews", Icons.Default.Coffee),
                        Triple(CoffeeTab.RECIPES, "Recipes", Icons.Default.MenuBook),
                        Triple(CoffeeTab.STATS, "Beans & Stats", Icons.Default.BarChart)
                    ).forEach { (tab, label, icon) ->
                        val isSelected = uiState.currentTab == tab

                        val tabBgColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
                            label = "tab_bg"
                        )
                        val tabBorderColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
                            label = "tab_border"
                        )
                        val iconScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.15f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "tab_icon_scale"
                        )

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = tabBgColor,
                            border = BorderStroke(1.dp, tabBorderColor),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .instagramBounce(scaleDown = 0.94f) { viewModel.setTab(tab) }
                                .testTag("tab_${tab.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        },
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = uiState.currentTab,
                transitionSpec = {
                    val isForward = targetState.ordinal >= initialState.ordinal
                    val slideOffset = { fullWidth: Int -> if (isForward) fullWidth / 4 else -fullWidth / 4 }
                    val exitOffset = { fullWidth: Int -> if (isForward) -fullWidth / 4 else fullWidth / 4 }

                    (slideInHorizontally(
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = 420f),
                        initialOffsetX = slideOffset
                    ) + fadeIn(
                        animationSpec = spring(dampingRatio = 0.9f, stiffness = 450f)
                    )).togetherWith(
                        slideOutHorizontally(
                            animationSpec = spring(dampingRatio = 0.85f, stiffness = 420f),
                            targetOffsetX = exitOffset
                        ) + fadeOut(
                            animationSpec = spring(dampingRatio = 0.9f, stiffness = 450f)
                        )
                    ).using(SizeTransform(clip = false))
                },
                label = "instagram_tab_transition"
            ) { tab ->
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
                            onOpenScanBag = { showScanCoffeeBagSheet = true },
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
                            onDeleteBrew = { id -> viewModel.deleteBrew(id) },
                            onOpenSettings = { showSettingsDialog = true }
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
                            onDeleteRecipe = { id -> viewModel.deleteRecipe(id) },
                            onOpenSettings = { showSettingsDialog = true }
                        )
                    }

                    CoffeeTab.STATS -> {
                        StatsScreen(
                            stats = uiState.stats,
                            beanBags = uiState.beanBags,
                            onOpenAddBeanBag = { showAddBeanBagDialog = true },
                            onOpenScanBag = { showScanCoffeeBagSheet = true },
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
                            onDeleteBeanBag = { id -> viewModel.deleteBeanBag(id) },
                            onOpenSettings = { showSettingsDialog = true }
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
            onSeekSeconds = { viewModel.seekTimer(it) },
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
            onOpenScanner = {
                showAddBeanBagDialog = false
                showScanCoffeeBagSheet = true
            },
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

    // Scan Coffee Bag Sheet (AI Camera Scanner)
    if (showScanCoffeeBagSheet) {
        ScanCoffeeBagSheet(
            sheetState = scanSheetState,
            onDismiss = { showScanCoffeeBagSheet = false },
            onSaveBag = { name, roaster, roastLevel, roastDate, initialWeight, remainingWeight, isActive ->
                viewModel.saveBeanBag(
                    name = name,
                    roaster = roaster,
                    roastLevel = roastLevel,
                    roastDate = roastDate,
                    initialWeightGrams = initialWeight,
                    remainingWeightGrams = remainingWeight,
                    isActive = isActive
                )
                showScanCoffeeBagSheet = false
                scope.launch {
                    snackbarHostState.showSnackbar("Added $name to Bean Inventory from scan!")
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

    // App Settings & Theme Color Choice Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            selectedThemeColor = uiState.themeColor,
            trackedCoffees = uiState.stats.totalBrews,
            onSelectThemeColor = { color ->
                viewModel.setThemeColor(color)
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    // Theme Unlock Milestone Celebration Dialog
    if (themeUnlockEvent != null) {
        val unlockedTheme = themeUnlockEvent!!
        AlertDialog(
            onDismissRequest = { viewModel.clearThemeUnlockEvent() },
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(unlockedTheme.previewColor),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(unlockedTheme.secondaryPreview)
                    )
                }
            },
            title = {
                Text(
                    text = "🎉 New Theme Unlocked!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column {
                    Text(
                        text = "You reached ${unlockedTheme.requiredCoffees} ${if (unlockedTheme.requiredCoffees == 1) "coffee" else "coffees"} tracked!",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The \"${unlockedTheme.displayName}\" palette (${unlockedTheme.description}) is now unlocked and available in App Settings.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setThemeColor(unlockedTheme)
                        viewModel.clearThemeUnlockEvent()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply Now", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.clearThemeUnlockEvent() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Later")
                }
            }
        )
    }
}
