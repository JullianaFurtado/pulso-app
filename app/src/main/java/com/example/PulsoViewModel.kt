package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Modelo de dados para análise de padrões e dicas personalizadas.
 */
data class PatternInsight(
    val title: String,
    val description: String,
    val category: String, // "Água", "Exercício", "Alimentação", "Educação Digital"
    val urgencyLevel: String = "info" // "info", "success", "alert"
)

/**
 * ViewModel que orquestra os estados reativos da UI, persistência via Room e
 * agendamentos em segundo plano com WorkManager.
 */
class PulsoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.pulsoDao()

    // Timestamp do início do dia atual (00:00:00) para filtros diários
    val startOfDayTimestamp: Long
        get() {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }

    // Preferências e Perfil do Usuário
    private val prefs = application.getSharedPreferences("pulso_settings", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadInitialProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _themeMode = MutableStateFlow(
        run {
            val savedName = prefs.getString("selected_theme_mode", AppThemeMode.SYSTEM.name)
            try {
                AppThemeMode.valueOf(savedName ?: AppThemeMode.SYSTEM.name)
            } catch (e: Exception) {
                AppThemeMode.SYSTEM
            }
        }
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Metas diárias (baseadas nas estimativas do perfil quando cadastrado)
    private val _waterGoalMl = MutableStateFlow(_userProfile.value.recommendedWaterMl)
    val waterGoalMl: StateFlow<Int> = _waterGoalMl.asStateFlow()

    private val _activityGoalMinutes = MutableStateFlow(_userProfile.value.recommendedActivityMinutes)
    val activityGoalMinutes: StateFlow<Int> = _activityGoalMinutes.asStateFlow()

    private val _calorieGoalKcal = MutableStateFlow(_userProfile.value.recommendedCaloriesKcal)
    val calorieGoalKcal: StateFlow<Int> = _calorieGoalKcal.asStateFlow()

    private val _proteinGoalGrams = MutableStateFlow(_userProfile.value.recommendedProteinGrams)
    val proteinGoalGrams: StateFlow<Int> = _proteinGoalGrams.asStateFlow()

    private val _carbGoalGrams = MutableStateFlow(_userProfile.value.recommendedCarbGrams)
    val carbGoalGrams: StateFlow<Int> = _carbGoalGrams.asStateFlow()

    private val _fatGoalGrams = MutableStateFlow(_userProfile.value.recommendedFatGrams)
    val fatGoalGrams: StateFlow<Int> = _fatGoalGrams.asStateFlow()

    private val _fiberGoalGrams = MutableStateFlow(_userProfile.value.recommendedFiberGrams)
    val fiberGoalGrams: StateFlow<Int> = _fiberGoalGrams.asStateFlow()

    private val _mealGoalCount = MutableStateFlow(3)
    val mealGoalCount: StateFlow<Int> = _mealGoalCount.asStateFlow()

    // Estado do agendamento de lembretes
    private val _reminderEnabled = MutableStateFlow(true)
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled.asStateFlow()

    private val _reminderIntervalHours = MutableStateFlow(2L)
    val reminderIntervalHours: StateFlow<Long> = _reminderIntervalHours.asStateFlow()

    // Dados agregados do dia atual
    val todayWaterMl: StateFlow<Int> = dao.getTotalWaterSince(startOfDayTimestamp)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayActivityMinutes: StateFlow<Int> = dao.getTotalActivityMinutesSince(startOfDayTimestamp)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayCalories: StateFlow<Int> = dao.getTotalCaloriesSince(startOfDayTimestamp)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayMealCount: StateFlow<Int> = dao.getTotalMealsSince(startOfDayTimestamp)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayMealCalories: StateFlow<Int> = dao.getTotalMealCaloriesSince(startOfDayTimestamp)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Listas detalhadas para histórico
    val todayWaterLogs: StateFlow<List<WaterLog>> = dao.getWaterLogsSince(startOfDayTimestamp)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayActivityLogs: StateFlow<List<ActivityLog>> = dao.getActivityLogsSince(startOfDayTimestamp)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMealLogs: StateFlow<List<MealLog>> = dao.getMealLogsSince(startOfDayTimestamp)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Totais de Macronutrientes do dia
    val todayProteinGrams: StateFlow<Float> = todayMealLogs.map { list ->
        list.sumOf { it.proteinGrams.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val todayCarbGrams: StateFlow<Float> = todayMealLogs.map { list ->
        list.sumOf { it.carbGrams.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val todayFatGrams: StateFlow<Float> = todayMealLogs.map { list ->
        list.sumOf { it.fatGrams.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val todayFiberGrams: StateFlow<Float> = todayMealLogs.map { list ->
        list.sumOf { it.fiberGrams.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // Histórico completo
    val allWaterLogs: StateFlow<List<WaterLog>> = dao.getAllWaterLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allActivityLogs: StateFlow<List<ActivityLog>> = dao.getAllActivityLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMealLogs: StateFlow<List<MealLog>> = dao.getAllMealLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Análise de padrões e dicas personalizadas em tempo real
    val dynamicInsights: StateFlow<List<PatternInsight>> = combine(
        todayWaterMl,
        todayActivityMinutes,
        todayMealCount,
        waterGoalMl,
        activityGoalMinutes
    ) { water, activity, meals, targetWater, targetActivity ->
        generateInsights(water, activity, meals, targetWater, targetActivity)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Inicializa o agendamento de lembrete com intervalo padrão de 2h
        WaterReminderWorker.schedulePeriodicReminder(getApplication(), 2L)
    }

    private fun generateInsights(
        water: Int,
        activity: Int,
        meals: Int,
        targetWater: Int,
        targetActivity: Int
    ): List<PatternInsight> {
        val list = mutableListOf<PatternInsight>()

        // 1. Análise de Água
        val waterPercentage = if (targetWater > 0) (water * 100) / targetWater else 0
        when {
            waterPercentage >= 100 -> {
                list.add(
                    PatternInsight(
                        title = "Meta de Hidratação Concluída! 💧",
                        description = "Excelente! Você atingiu ${water}ml hoje. Seu organismo agradece com mais energia e foco.",
                        category = "Água",
                        urgencyLevel = "success"
                    )
                )
            }
            waterPercentage < 40 -> {
                list.add(
                    PatternInsight(
                        title = "Atenção ao consumo de água 🥤",
                        description = "Você bebeu ${water}ml de ${targetWater}ml até agora. Beber água em intervalos regulares reduz a fadiga mental.",
                        category = "Água",
                        urgencyLevel = "alert"
                    )
                )
            }
            else -> {
                list.add(
                    PatternInsight(
                        title = "Bom progresso de hidratação 🌊",
                        description = "Você está em $waterPercentage% da sua meta. Mantenha uma garrafa ao alcance no seu posto de trabalho.",
                        category = "Água",
                        urgencyLevel = "info"
                    )
                )
            }
        }

        // 2. Análise de Atividade Física
        if (activity >= targetActivity) {
            list.add(
                PatternInsight(
                    title = "Corpo em Movimento! 🏃",
                    description = "Você completou $activity min de exercícios hoje. O movimento libera endorfinas essenciais para o bem-estar.",
                    category = "Exercício",
                    urgencyLevel = "success"
                )
            )
        } else {
            val remaining = targetActivity - activity
            list.add(
                PatternInsight(
                    title = "Pausa Ativa Recomendada 🧘",
                    description = "Faltam $remaining minutos para a meta diária. Uma caminhada de 10 minutos ou alongamento revigora a circulação.",
                    category = "Exercício",
                    urgencyLevel = "info"
                )
            )
        }

        // 3. Educação Digital e Autocuidado Conectado
        list.add(
            PatternInsight(
                title = "Educação Digital: Regra 20-20-20 👀",
                description = "A cada 20 minutos olhando para telas, fixe o olhar em um objeto a 6 metros (20 pés) de distância por 20 segundos para relaxar os olhos.",
                category = "Educação Digital",
                urgencyLevel = "info"
            )
        )

        list.add(
            PatternInsight(
                title = "Higiene Digital no Sono 🌙",
                description = "Evite telas 45 minutos antes de dormir para não suprimir a produção natural de melatonina pela luz azul.",
                category = "Educação Digital",
                urgencyLevel = "info"
            )
        )

        return list
    }

    // --- Ações de Inserção ---
    fun logWater(amountMl: Int) {
        viewModelScope.launch {
            dao.insertWaterLog(WaterLog(amountMl = amountMl))
        }
    }

    fun logActivity(activityType: String, durationMinutes: Int, calories: Int = 0) {
        viewModelScope.launch {
            dao.insertActivityLog(
                ActivityLog(
                    activityType = activityType,
                    durationMinutes = durationMinutes,
                    caloriesBurned = calories
                )
            )
        }
    }

    private fun loadInitialProfile(): UserProfile {
        val registered = prefs.getBoolean("user_registered", false)
        val name = prefs.getString("user_name", "") ?: ""
        val age = prefs.getInt("user_age", 0)
        val gender = prefs.getString("user_gender", "Masculino") ?: "Masculino"
        val weight = prefs.getFloat("user_weight", 0f)
        val height = prefs.getFloat("user_height", 0f)
        val activityLevel = prefs.getString("user_activity_level", "Moderado") ?: "Moderado"
        val wellnessGoal = prefs.getString("user_wellness_goal", "Equilíbrio & Longevidade") ?: "Equilíbrio & Longevidade"

        return UserProfile(
            name = name,
            age = age,
            gender = gender,
            weightKg = weight,
            heightCm = height,
            isRegistered = registered,
            activityLevel = activityLevel,
            wellnessGoal = wellnessGoal
        )
    }

    fun saveUserProfile(profile: UserProfile) {
        val updated = profile.copy(isRegistered = true)
        _userProfile.value = updated

        // Atualiza as metas recomendadas automaticamente com base no perfil biométrico
        _waterGoalMl.value = updated.recommendedWaterMl
        _activityGoalMinutes.value = updated.recommendedActivityMinutes
        _calorieGoalKcal.value = updated.recommendedCaloriesKcal
        _proteinGoalGrams.value = updated.recommendedProteinGrams
        _carbGoalGrams.value = updated.recommendedCarbGrams
        _fatGoalGrams.value = updated.recommendedFatGrams
        _fiberGoalGrams.value = updated.recommendedFiberGrams

        // Persiste nas preferências
        prefs.edit()
            .putBoolean("user_registered", true)
            .putString("user_name", updated.name)
            .putInt("user_age", updated.age)
            .putString("user_gender", updated.gender)
            .putFloat("user_weight", updated.weightKg)
            .putFloat("user_height", updated.heightCm)
            .putString("user_activity_level", updated.activityLevel)
            .putString("user_wellness_goal", updated.wellnessGoal)
            .apply()
    }

    fun logMeal(
        mealType: String,
        description: String = "",
        isHealthyChoice: Boolean = true,
        foodName: String = "",
        weightGrams: Int = 0,
        calories: Int = 0,
        proteinGrams: Float = 0f,
        carbGrams: Float = 0f,
        fatGrams: Float = 0f,
        fiberGrams: Float = 0f,
        servingUnit: String = "g",
        quantityUnits: Float = 0f
    ) {
        val finalDescription = when {
            foodName.isNotBlank() && servingUnit != "g" && quantityUnits > 0f -> {
                val qtyStr = if (quantityUnits % 1.0f == 0f) quantityUnits.toInt().toString() else "%.1f".format(quantityUnits)
                "$foodName ($qtyStr $servingUnit • ${weightGrams}g)"
            }
            foodName.isNotBlank() && weightGrams > 0 -> "$foodName (${weightGrams}g)"
            foodName.isNotBlank() -> foodName
            description.isNotBlank() -> description
            else -> "Refeição"
        }

        viewModelScope.launch {
            dao.insertMealLog(
                MealLog(
                    mealType = mealType,
                    description = finalDescription,
                    isHealthyChoice = isHealthyChoice,
                    foodName = foodName,
                    weightGrams = weightGrams,
                    calories = calories,
                    proteinGrams = proteinGrams,
                    carbGrams = carbGrams,
                    fatGrams = fatGrams,
                    fiberGrams = fiberGrams,
                    servingUnit = servingUnit,
                    quantityUnits = quantityUnits
                )
            )
        }
    }

    /**
     * Registra múltiplos itens de refeição em lote (ex: montador de prato completo).
     */
    fun logMultipleMeals(
        mealType: String,
        items: List<com.example.ui.PlateItem>
    ) {
        viewModelScope.launch {
            items.forEach { item ->
                val finalDescription = when {
                    item.foodName.isNotBlank() && item.servingUnit != "g" && item.quantityUnits > 0f -> {
                        val qtyStr = if (item.quantityUnits % 1.0f == 0f) item.quantityUnits.toInt().toString() else "%.1f".format(item.quantityUnits)
                        "${item.foodName} ($qtyStr ${item.servingUnit} • ${item.weightGrams}g)"
                    }
                    item.foodName.isNotBlank() && item.weightGrams > 0 -> "${item.foodName} (${item.weightGrams}g)"
                    else -> item.foodName
                }

                dao.insertMealLog(
                    MealLog(
                        mealType = mealType,
                        description = finalDescription,
                        isHealthyChoice = item.isHealthy,
                        foodName = item.foodName,
                        weightGrams = item.weightGrams,
                        calories = item.calories,
                        proteinGrams = item.proteinGrams,
                        carbGrams = item.carbGrams,
                        fatGrams = item.fatGrams,
                        fiberGrams = item.fiberGrams,
                        servingUnit = item.servingUnit,
                        quantityUnits = item.quantityUnits
                    )
                )
            }
        }
    }

    fun updateCalorieGoal(newGoal: Int) {
        _calorieGoalKcal.value = newGoal
    }

    // --- Ações de Deleção ---
    fun removeWaterLog(log: WaterLog) {
        viewModelScope.launch {
            dao.deleteWaterLog(log)
        }
    }

    fun removeActivityLog(log: ActivityLog) {
        viewModelScope.launch {
            dao.deleteActivityLog(log)
        }
    }

    fun removeMealLog(log: MealLog) {
        viewModelScope.launch {
            dao.deleteMealLog(log)
        }
    }

    // --- Configurações do WorkManager ---
    fun toggleReminder(enabled: Boolean) {
        _reminderEnabled.value = enabled
        if (enabled) {
            WaterReminderWorker.schedulePeriodicReminder(getApplication(), _reminderIntervalHours.value)
        } else {
            WaterReminderWorker.cancelReminders(getApplication())
        }
    }

    fun updateReminderInterval(hours: Long) {
        _reminderIntervalHours.value = hours
        if (_reminderEnabled.value) {
            WaterReminderWorker.schedulePeriodicReminder(getApplication(), hours)
        }
    }

    fun triggerTestReminder() {
        WaterReminderWorker.triggerImmediateTest(getApplication())
    }

    fun updateWaterGoal(newGoal: Int) {
        _waterGoalMl.value = newGoal
    }

    fun updateActivityGoal(newGoal: Int) {
        _activityGoalMinutes.value = newGoal
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("selected_theme_mode", mode.name).apply()
    }
}

/**
 * Factory para criação do ViewModel com Application context.
 */
class PulsoViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PulsoViewModel::class.java)) {
            return PulsoViewModel(application) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida: ${modelClass.name}")
    }
}
