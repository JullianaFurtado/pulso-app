package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MealLog
import com.example.PatternInsight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Diário Inteligente:
 * Orçamento calórico central (Meta - Consumidas + Exercício = Restantes),
 * Metas de Macronutrientes com barras de progresso proporcionais,
 * Gráfico Circular de Metas (Queima, Água e Consumo),
 * Seções de Refeições com calorias e lista de alimentos, e Hidratação.
 */
@Composable
fun DashboardScreen(
    waterMl: Int,
    waterGoal: Int,
    activityMinutes: Int,
    activityGoal: Int,
    caloriesBurned: Int,
    mealCount: Int,
    insights: List<PatternInsight>,
    onQuickAddWater: (Int) -> Unit,
    onOpenAddWater: () -> Unit,
    onOpenAddActivity: () -> Unit,
    onOpenAddMeal: () -> Unit,
    onOpenAddMealWithType: (String) -> Unit = {},
    onNavigateToWater: () -> Unit = {},
    onNavigateToMeals: () -> Unit = {},
    onDeleteMeal: (MealLog) -> Unit = {},
    modifier: Modifier = Modifier,
    userName: String = "",
    calorieGoal: Int = 2000,
    proteinGoal: Int = 120,
    carbGoal: Int = 220,
    fatGoal: Int = 60,
    fiberGoal: Int = 28,
    todayMealCalories: Int = 0,
    todayProtein: Float = 0f,
    todayCarb: Float = 0f,
    todayFat: Float = 0f,
    todayFiber: Float = 0f,
    todayMealLogs: List<MealLog> = emptyList(),
    onOpenProfile: (() -> Unit)? = null
) {
    val todayFormatted = remember {
        val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR"))
        dateFormat.format(Date()).replaceFirstChar { it.uppercase() }
    }

    // Orçamento Calórico: Calorias Restantes = Meta - Alimentos + Exercício
    val remainingCalories = (calorieGoal - todayMealCalories + caloriesBurned).coerceAtLeast(0)
    val calorieProgress = if (calorieGoal > 0) (todayMealCalories.toFloat() / (calorieGoal + caloriesBurned).toFloat()).coerceIn(0f, 1f) else 0f
    val animatedCalorieProgress by animateFloatAsState(targetValue = calorieProgress, label = "calAnim")
    var selectedMacroDetail by remember { mutableStateOf<Pair<String, String>?>(null) }

    val mealTypes = remember {
        listOf(
            "Café da Manhã" to "☀️",
            "Almoço" to "🍽️",
            "Lanches & Petiscos" to "🥪",
            "Jantar" to "🌙"
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Cabeçalho de Boas-Vindas e Perfil
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = todayFormatted,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (userName.isNotBlank()) "Olá, $userName!" else "Diário Nutricional",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (onOpenProfile != null) {
                    OutlinedButton(
                        onClick = onOpenProfile,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Metas & Perfil", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // 1.5. Dock de Ações Rápidas em 1 Toque
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_quick_dock"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickActionDockItem(
                        icon = Icons.Default.LocalDrink,
                        label = "+250ml",
                        color = Color(0xFF0072FF),
                        onClick = { onQuickAddWater(250) }
                    )
                    QuickActionDockItem(
                        icon = Icons.Default.WaterDrop,
                        label = "+500ml",
                        color = Color(0xFF0288D1),
                        onClick = { onQuickAddWater(500) }
                    )
                    QuickActionDockItem(
                        icon = Icons.Default.FitnessCenter,
                        label = "+Treino",
                        color = Color(0xFFF43F5E),
                        onClick = onOpenAddActivity
                    )
                    QuickActionDockItem(
                        icon = Icons.Default.Restaurant,
                        label = "+Refeição",
                        color = Color(0xFFFF8A00),
                        onClick = onOpenAddMeal
                    )
                }
            }
        }

        // 2. Orçamento Calórico Diário (Hero Card)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calorie_budget_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Orçamento Diário",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Restantes = Meta - Alimentos + Exercício",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Indicador de Densidade Nutricional
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                todayMealCalories == 0 -> MaterialTheme.colorScheme.surfaceVariant
                                todayMealCalories <= calorieGoal -> Color(0xFF10B981).copy(alpha = 0.15f)
                                else -> Color(0xFFF97316).copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = when {
                                    todayMealCalories == 0 -> "Comece o dia"
                                    todayMealCalories <= calorieGoal -> "No Objetivo ✓"
                                    else -> "Excedendo meta"
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    todayMealCalories == 0 -> MaterialTheme.colorScheme.onSurfaceVariant
                                    todayMealCalories <= calorieGoal -> Color(0xFF10B981)
                                    else -> Color(0xFFF97316)
                                }
                            )
                        }
                    }

                    // Contador Central em Destaque
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Meta Base
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$calorieGoal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Meta",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text("-", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        // Ingeridas
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$todayMealCalories",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF97316)
                            )
                            Text(
                                text = "Alimentos",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text("+", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        // Exercício
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$caloriesBurned",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0288D1)
                            )
                            Text(
                                text = "Exercício",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text("=", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        // Restantes
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$remainingCalories",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Restantes",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Barra de progresso calórico
                    LinearProgressIndicator(
                        progress = { animatedCalorieProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        strokeCap = StrokeCap.Round
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // 3. Macronutrientes (Proteínas, Carboidratos, Gorduras, Fibras) com Toque Interativo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MacroTrackerItem(
                            label = "Proteínas",
                            currentGrams = todayProtein,
                            targetGrams = proteinGoal,
                            color = Color(0xFF0288D1),
                            onClick = {
                                val cal = (todayProtein * 4).toInt()
                                val remaining = (proteinGoal - todayProtein.toInt()).coerceAtLeast(0)
                                selectedMacroDetail = "Proteínas" to "Fornece aminoácidos essenciais para músculos e saciedade.\n\n• Consumido: ${todayProtein.toInt()}g (${cal} kcal)\n• Meta: ${proteinGoal}g\n• Restante: ${remaining}g"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        MacroTrackerItem(
                            label = "Carboidratos",
                            currentGrams = todayCarb,
                            targetGrams = carbGoal,
                            color = Color(0xFFF97316),
                            onClick = {
                                val cal = (todayCarb * 4).toInt()
                                val remaining = (carbGoal - todayCarb.toInt()).coerceAtLeast(0)
                                selectedMacroDetail = "Carboidratos" to "Principal fonte de energia rápida para o cérebro e treinos.\n\n• Consumido: ${todayCarb.toInt()}g (${cal} kcal)\n• Meta: ${carbGoal}g\n• Restante: ${remaining}g"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        MacroTrackerItem(
                            label = "Gorduras",
                            currentGrams = todayFat,
                            targetGrams = fatGoal,
                            color = Color(0xFFE11D48),
                            onClick = {
                                val cal = (todayFat * 9).toInt()
                                val remaining = (fatGoal - todayFat.toInt()).coerceAtLeast(0)
                                selectedMacroDetail = "Gorduras Boas" to "Fundamentais para absorção de vitaminas e síntese hormonal.\n\n• Consumido: ${todayFat.toInt()}g (${cal} kcal)\n• Meta: ${fatGoal}g\n• Restante: ${remaining}g"
                            },
                            modifier = Modifier.weight(1f)
                        )
                        MacroTrackerItem(
                            label = "Fibras",
                            currentGrams = todayFiber,
                            targetGrams = fiberGoal,
                            color = Color(0xFF10B981),
                            onClick = {
                                val remaining = (fiberGoal - todayFiber.toInt()).coerceAtLeast(0)
                                selectedMacroDetail = "Fibras Alimentares" to "Auxiliam a saúde intestinal, saciedade e controle glicêmico.\n\n• Consumido: ${todayFiber.toInt()}g\n• Meta: ${fiberGoal}g\n• Restante: ${remaining}g"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Gráfico Circular de Metas Interativo (Calorias Queimadas, Água e Calorias Consumidas)
        item {
            TriMetricCircularCard(
                caloriesConsumed = todayMealCalories,
                calorieGoal = calorieGoal,
                caloriesBurned = caloriesBurned,
                calorieBurnGoal = 400,
                waterConsumedMl = waterMl,
                waterGoalMl = waterGoal,
                onQuickAddWater = onQuickAddWater,
                onOpenAddMeal = onOpenAddMeal,
                onOpenAddActivity = onOpenAddActivity
            )
        }

        // 4. Seções de Refeições Diárias (Café da Manhã, Almoço, Lanches, Jantar)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Refeições do Dia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$mealCount registradas",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(items = mealTypes, key = { it.first }) { (type, emoji) ->
            val logsForType = todayMealLogs.filter { it.mealType.equals(type, ignoreCase = true) }
            val mealCalories = logsForType.sumOf { it.calories }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (mealCalories > 0) "$mealCalories kcal consumidas" else "Nenhum alimento registrado",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = { onOpenAddMealWithType(type) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Adicionar", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Lista de alimentos desta refeição se houver
                    if (logsForType.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        logsForType.forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = log.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (log.proteinGrams > 0 || log.carbGrams > 0 || log.fatGrams > 0) {
                                        Text(
                                            text = "P: ${log.proteinGrams.toInt()}g | C: ${log.carbGrams.toInt()}g | G: ${log.fatGrams.toInt()}g",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${log.calories} kcal",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(
                                        onClick = { onDeleteMeal(log) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Excluir",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Widget de Hidratação
        item {
            val progressWater = if (waterGoal > 0) (waterMl.toFloat() / waterGoal).coerceIn(0f, 1f) else 0f
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("water_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0288D1).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Água",
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Hidratação",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Meta diária: ${waterGoal}ml",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "${waterMl}ml",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0288D1)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progressWater },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF0288D1),
                        trackColor = Color(0xFF0288D1).copy(alpha = 0.15f),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onQuickAddWater(200) },
                            modifier = Modifier.weight(1f).testTag("quick_add_200"),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text("+200ml", style = MaterialTheme.typography.labelSmall)
                        }

                        FilledTonalButton(
                            onClick = { onQuickAddWater(350) },
                            modifier = Modifier.weight(1f).testTag("quick_add_350"),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text("+350ml", style = MaterialTheme.typography.labelSmall)
                        }

                        FilledTonalButton(
                            onClick = { onQuickAddWater(500) },
                            modifier = Modifier.weight(1f).testTag("quick_add_500"),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text("+500ml", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = onOpenAddWater,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Outro", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 8. Dicas e Insights Nutricionais
        if (insights.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dicas do Nutricionista Pulso",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    insights.take(2).forEach { insight ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when (insight.urgencyLevel) {
                                    "success" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                    "alert" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = when (insight.urgencyLevel) {
                                        "success" -> Icons.Default.CheckCircle
                                        "alert" -> Icons.Default.Warning
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = insight.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = insight.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Modal de Detalhes Interativos do Macronutriente
    if (selectedMacroDetail != null) {
        val (title, info) = selectedMacroDetail!!
        AlertDialog(
            onDismissRequest = { selectedMacroDetail = null },
            title = {
                Text(text = title, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = info, style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                TextButton(onClick = { selectedMacroDetail = null }) {
                    Text("Entendi")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun QuickActionDockItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        modifier = Modifier.testTag("dock_action_${label.lowercase().replace("+", "").replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun MacroTrackerItem(
    label: String,
    currentGrams: Float,
    targetGrams: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (targetGrams > 0) (currentGrams / targetGrams.toFloat()).coerceIn(0f, 1f) else 0f
    val animProgress by animateFloatAsState(targetValue = progress, label = "macroAnim")
    val percent = (progress * 100).toInt()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${currentGrams.toInt()}/${targetGrams}g",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
            strokeCap = StrokeCap.Round
        )
    }
}
