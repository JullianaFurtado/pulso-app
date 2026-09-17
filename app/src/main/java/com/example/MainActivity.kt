package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AddActivityDialog
import com.example.ui.AddMealDialog
import com.example.ui.AddWaterDialog
import com.example.ui.DashboardScreen
import com.example.ui.MealsScreen
import com.example.ui.ReminderSettingsScreen
import com.example.ui.StatisticsScreen
import com.example.ui.UserProfileScreen
import com.example.ui.WaterScreen
import com.example.ui.WorkoutsScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.PulsoTheme

enum class PulsoTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Início", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    WATER("Água", Icons.Filled.WaterDrop, Icons.Outlined.WaterDrop),
    MEALS("Refeições", Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    WORKOUTS("Treinos", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    STATISTICS("Estatísticas", Icons.Filled.BarChart, Icons.Outlined.BarChart)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: PulsoViewModel = viewModel(
                factory = PulsoViewModelFactory(application)
            )
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            PulsoTheme(themeMode = themeMode) {
                PulsoApp(viewModel = viewModel)
            }
        }
    }
}

/**
 * Interface principal do aplicativo Pulso com navegação por abas,
 * FAB de adição rápida e diálogos integrados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulsoApp(viewModel: PulsoViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val calorieGoal by viewModel.calorieGoalKcal.collectAsStateWithLifecycle()
    val todayMealCalories by viewModel.todayMealCalories.collectAsStateWithLifecycle()
    val todayProtein by viewModel.todayProteinGrams.collectAsStateWithLifecycle()
    val todayCarb by viewModel.todayCarbGrams.collectAsStateWithLifecycle()
    val todayFat by viewModel.todayFatGrams.collectAsStateWithLifecycle()
    val todayFiber by viewModel.todayFiberGrams.collectAsStateWithLifecycle()

    var showProfileScreen by remember { mutableStateOf(false) }

    // Se o usuário ainda não tiver concluído o registro de dados corporais,
    // ou se acionou a tela de perfil para editar seus dados e ver as estimativas recomendadas:
    if (!userProfile.isRegistered || showProfileScreen) {
        UserProfileScreen(
            currentProfile = userProfile,
            onSaveProfile = { profile ->
                viewModel.saveUserProfile(profile)
                showProfileScreen = false
            },
            onDismissOrBack = if (userProfile.isRegistered) {
                { showProfileScreen = false }
            } else null
        )
        return
    }

    var currentTab by remember { mutableStateOf(PulsoTab.DASHBOARD) }

    // Estados observados da ViewModel via collectAsStateWithLifecycle
    val waterMl by viewModel.todayWaterMl.collectAsStateWithLifecycle()
    val waterGoal by viewModel.waterGoalMl.collectAsStateWithLifecycle()
    val activityMin by viewModel.todayActivityMinutes.collectAsStateWithLifecycle()
    val activityGoal by viewModel.activityGoalMinutes.collectAsStateWithLifecycle()
    val caloriesBurned by viewModel.todayCalories.collectAsStateWithLifecycle()
    val mealCount by viewModel.todayMealCount.collectAsStateWithLifecycle()

    val insights by viewModel.dynamicInsights.collectAsStateWithLifecycle()

    val todayWaterLogs by viewModel.todayWaterLogs.collectAsStateWithLifecycle()
    val todayActivityLogs by viewModel.todayActivityLogs.collectAsStateWithLifecycle()
    val todayMealLogs by viewModel.todayMealLogs.collectAsStateWithLifecycle()

    val allWaterLogs by viewModel.allWaterLogs.collectAsStateWithLifecycle()
    val allActivityLogs by viewModel.allActivityLogs.collectAsStateWithLifecycle()
    val allMealLogs by viewModel.allMealLogs.collectAsStateWithLifecycle()

    val reminderEnabled by viewModel.reminderEnabled.collectAsStateWithLifecycle()
    val reminderInterval by viewModel.reminderIntervalHours.collectAsStateWithLifecycle()
    val currentThemeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    // Controle de Telas Secundárias e Diálogos
    var showWaterDialog by remember { mutableStateOf(false) }
    var showActivityDialog by remember { mutableStateOf(false) }
    var showMealDialog by remember { mutableStateOf(false) }
    var selectedMealTypeForDialog by remember { mutableStateOf("Almoço") }
    var showQuickActionSheet by remember { mutableStateOf(false) }
    var showThemeMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pulso",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showProfileScreen = true },
                        modifier = Modifier.testTag("profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil e Biometria",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showThemeMenu = true },
                            modifier = Modifier.testTag("theme_quick_button")
                        ) {
                            Icon(
                                imageVector = when (currentThemeMode) {
                                    AppThemeMode.AMOLED -> Icons.Default.Brightness4
                                    AppThemeMode.DARK -> Icons.Default.Brightness4
                                    else -> Icons.Default.Palette
                                },
                                contentDescription = "Selecionar tema",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showThemeMenu,
                            onDismissRequest = { showThemeMenu = false }
                        ) {
                            AppThemeMode.values().forEach { mode ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = mode.title + if (mode == currentThemeMode) "  ✓" else "",
                                            fontWeight = if (mode == currentThemeMode) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        viewModel.setThemeMode(mode)
                                        showThemeMenu = false
                                    },
                                    modifier = Modifier.testTag("menu_theme_${mode.name.lowercase()}")
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                PulsoTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = (currentTab == tab),
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == tab) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            val contentModifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)

            when (currentTab) {
                PulsoTab.DASHBOARD -> {
                    DashboardScreen(
                        waterMl = waterMl,
                        waterGoal = waterGoal,
                        activityMinutes = activityMin,
                        activityGoal = activityGoal,
                        caloriesBurned = caloriesBurned,
                        mealCount = mealCount,
                        insights = insights,
                        userName = userProfile.name,
                        calorieGoal = calorieGoal,
                        proteinGoal = userProfile.recommendedProteinGrams,
                        carbGoal = userProfile.recommendedCarbGrams,
                        fatGoal = userProfile.recommendedFatGrams,
                        fiberGoal = userProfile.recommendedFiberGrams,
                        todayMealCalories = todayMealCalories,
                        todayProtein = todayProtein,
                        todayCarb = todayCarb,
                        todayFat = todayFat,
                        todayFiber = todayFiber,
                        todayMealLogs = todayMealLogs,
                        onOpenProfile = { showProfileScreen = true },
                        onQuickAddWater = { amount -> viewModel.logWater(amount) },
                        onOpenAddWater = { showWaterDialog = true },
                        onOpenAddActivity = { showActivityDialog = true },
                        onOpenAddMeal = { showMealDialog = true },
                        onOpenAddMealWithType = { type ->
                            selectedMealTypeForDialog = type
                            showMealDialog = true
                        },
                        onDeleteMeal = { log -> viewModel.removeMealLog(log) },
                        onNavigateToWater = { currentTab = PulsoTab.WATER },
                        onNavigateToMeals = { currentTab = PulsoTab.MEALS },
                        modifier = contentModifier
                    )
                }
                PulsoTab.WATER -> {
                    WaterScreen(
                        todayWaterMl = waterMl,
                        waterGoalMl = waterGoal,
                        todayWaterLogs = todayWaterLogs,
                        onQuickAddWater = { amount -> viewModel.logWater(amount) },
                        onOpenAddWater = { showWaterDialog = true },
                        onDeleteWaterLog = { log -> viewModel.removeWaterLog(log) },
                        modifier = contentModifier
                    )
                }
                PulsoTab.MEALS -> {
                    MealsScreen(
                        todayMealLogs = todayMealLogs,
                        calorieGoal = calorieGoal,
                        todayMealCalories = todayMealCalories,
                        todayProtein = todayProtein,
                        todayCarb = todayCarb,
                        todayFat = todayFat,
                        todayFiber = todayFiber,
                        onOpenAddMeal = { mealType ->
                            selectedMealTypeForDialog = mealType
                            showMealDialog = true
                        },
                        onDeleteMeal = { log -> viewModel.removeMealLog(log) },
                        onQuickAddPredefinedFood = { mealType, food ->
                            val defaultGrams = if (food.unitServing != null) {
                                (food.unitServing.defaultQuantity * food.unitServing.gramsPerUnit).toInt()
                            } else {
                                food.defaultServingGrams
                            }
                            val defaultCalories = food.calculateCalories(defaultGrams.toFloat())
                            val defaultMacros = food.calculateMacros(defaultGrams.toFloat())

                            viewModel.logMeal(
                                mealType = mealType,
                                foodName = food.name,
                                weightGrams = defaultGrams,
                                calories = defaultCalories,
                                isHealthyChoice = true,
                                proteinGrams = defaultMacros.proteinGrams,
                                carbGrams = defaultMacros.carbGrams,
                                fatGrams = defaultMacros.fatGrams,
                                fiberGrams = defaultMacros.fiberGrams,
                                servingUnit = food.unitServing?.unitName ?: "g",
                                quantityUnits = food.unitServing?.defaultQuantity ?: 0f
                            )
                        },
                        modifier = contentModifier
                    )
                }
                PulsoTab.WORKOUTS -> {
                    WorkoutsScreen(
                        todayActivityMinutes = activityMin,
                        activityGoalMinutes = activityGoal,
                        todayCaloriesBurned = caloriesBurned,
                        todayActivityLogs = todayActivityLogs,
                        onQuickAddWorkout = { type, duration, calories ->
                            viewModel.logActivity(type, duration, calories)
                        },
                        onOpenAddWorkoutDialog = { showActivityDialog = true },
                        onDeleteWorkoutLog = { log -> viewModel.removeActivityLog(log) },
                        modifier = contentModifier
                    )
                }
                PulsoTab.STATISTICS -> {
                    StatisticsScreen(
                        todayWaterMl = waterMl,
                        waterGoalMl = waterGoal,
                        todayActivityMinutes = activityMin,
                        activityGoalMinutes = activityGoal,
                        todayCaloriesBurned = caloriesBurned,
                        todayMealCalories = todayMealCalories,
                        calorieGoalKcal = calorieGoal,
                        todayProtein = todayProtein,
                        todayCarb = todayCarb,
                        todayFat = todayFat,
                        todayFiber = todayFiber,
                        todayMealLogs = todayMealLogs,
                        todayActivityLogs = todayActivityLogs,
                        modifier = contentModifier
                    )
                }
            }
        }

        // Bottom Sheet para Ações Rápidas do FAB
        if (showQuickActionSheet) {
            ModalBottomSheet(
                onDismissRequest = { showQuickActionSheet = false },
                modifier = Modifier.testTag("quick_action_sheet")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "O que você deseja registrar?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        onClick = {
                            showQuickActionSheet = false
                            showMealDialog = true
                        },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.fillMaxWidth().testTag("action_sheet_add_meal")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Montar Prato Interativo",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Adicione múltiplos alimentos da TBCA antes de salvar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        onClick = {
                            showQuickActionSheet = false
                            showWaterDialog = true
                        },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth().testTag("action_sheet_add_water")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Consumo de Água",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Registrar ml de água consumidos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        onClick = {
                            showQuickActionSheet = false
                            showActivityDialog = true
                        },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth().testTag("action_sheet_add_activity")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Atividade Física",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Caminhada, corrida, musculação ou alongamento",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Diálogos Modais
        if (showWaterDialog) {
            AddWaterDialog(
                onDismiss = { showWaterDialog = false },
                onConfirm = { amount -> viewModel.logWater(amount) }
            )
        }

        if (showActivityDialog) {
            AddActivityDialog(
                onDismiss = { showActivityDialog = false },
                onConfirm = { type, duration, calories ->
                    viewModel.logActivity(type, duration, calories)
                }
            )
        }

        if (showMealDialog) {
            com.example.ui.InteractiveMealDialog(
                initialMealType = selectedMealTypeForDialog,
                onDismiss = { showMealDialog = false },
                onConfirmMealItem = { mealType, foodName, weightGrams, calories, isHealthy, protein, carb, fat, fiber, unit, qty ->
                    viewModel.logMeal(
                        mealType = mealType,
                        foodName = foodName,
                        weightGrams = weightGrams,
                        calories = calories,
                        isHealthyChoice = isHealthy,
                        proteinGrams = protein,
                        carbGrams = carb,
                        fatGrams = fat,
                        fiberGrams = fiber,
                        servingUnit = unit,
                        quantityUnits = qty
                    )
                },
                onConfirmMultipleItems = { mealType, items ->
                    viewModel.logMultipleMeals(mealType, items)
                }
            )
        }
    }
}

/**
 * Composable Greeting preservado para testes automatizados.
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PulsoTheme { Greeting("Android") }
}
