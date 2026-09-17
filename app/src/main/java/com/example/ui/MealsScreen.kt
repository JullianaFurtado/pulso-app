package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.FoodDatabase
import com.example.FoodItem
import com.example.MealLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Categoria padronizada de refeição para agrupamento e filtros.
 */
enum class MealCategoryType(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val suggestedFoodIds: List<String>
) {
    CAFE(
        title = "Café da Manhã",
        icon = Icons.Default.LocalCafe,
        description = "Primeira energia do dia",
        suggestedFoodIds = listOf("ovo_mexido", "pao_frances", "cafe_leite", "tapioca", "banana_prata", "aveia_flocos")
    ),
    ALMOCO(
        title = "Almoço",
        icon = Icons.Default.WbSunny,
        description = "Principal refeição diurna",
        suggestedFoodIds = listOf("arroz_branco", "feijao_carioca", "peito_frango_grelhado", "patinho_bovino", "salada_alface_tomate", "file_tilapia_grelhado")
    ),
    LANCHE(
        title = "Lanche",
        icon = Icons.Default.Spa,
        description = "Pausas e lanches intermediários",
        suggestedFoodIds = listOf("banana_prata", "iogurte_natural", "whey_protein", "maca_fuji", "pao_forma_integral", "castanha_do_para")
    ),
    JANTAR(
        title = "Jantar",
        icon = Icons.Default.Restaurant,
        description = "Refeição noturna nutritiva e leve",
        suggestedFoodIds = listOf("peito_frango_grelhado", "omelete_simples", "sopa_legumes", "salada_alface_tomate", "arroz_integral", "file_tilapia_grelhado")
    ),
    OUTRO(
        title = "Ceia / Outro",
        icon = Icons.Default.Nightlife,
        description = "Pós-treino ou ceia noturna",
        suggestedFoodIds = listOf("cha_verde", "iogurte_natural", "castanha_do_para", "clara_ovo", "abacate")
    )
}

/**
 * Tela de Refeições Diárias organizada por tipos de refeição
 * (Café da Manhã, Almoço, Lanche, Jantar e Outros),
 * com painel de macronutrientes do dia, atalhos rápidos de adição em 1 toque
 * e montador interativo de pratos.
 */
@Composable
fun MealsScreen(
    todayMealLogs: List<MealLog>,
    calorieGoal: Int,
    todayMealCalories: Int,
    todayProtein: Float,
    todayCarb: Float,
    todayFat: Float,
    todayFiber: Float,
    onOpenAddMeal: (mealType: String) -> Unit,
    onDeleteMeal: (MealLog) -> Unit,
    onQuickAddPredefinedFood: ((mealType: String, food: FoodItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Todas") }
    val filterOptions = listOf("Todas", "Café da Manhã", "Almoço", "Lanche", "Jantar", "Ceia / Outro")

    val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "BR"))

    // Filtra lista de refeições se houver chip ativo
    val filteredLogs = remember(todayMealLogs, selectedFilter) {
        if (selectedFilter == "Todas") {
            todayMealLogs.sortedByDescending { it.timestamp }
        } else {
            todayMealLogs
                .filter { it.mealType.equals(selectedFilter, ignoreCase = true) }
                .sortedByDescending { it.timestamp }
        }
    }

    // Agrupamento por categoria para exibir cards organizados por refeição
    val mealsByCategory = remember(todayMealLogs) {
        MealCategoryType.values().associateWith { category ->
            todayMealLogs.filter { log ->
                when (category) {
                    MealCategoryType.CAFE -> log.mealType.contains("Café", ignoreCase = true) || log.mealType.contains("Cafe", ignoreCase = true)
                    MealCategoryType.ALMOCO -> log.mealType.contains("Almoço", ignoreCase = true) || log.mealType.contains("Almoco", ignoreCase = true)
                    MealCategoryType.LANCHE -> log.mealType.contains("Lanche", ignoreCase = true)
                    MealCategoryType.JANTAR -> log.mealType.contains("Jantar", ignoreCase = true)
                    MealCategoryType.OUTRO -> !log.mealType.contains("Café", ignoreCase = true) &&
                            !log.mealType.contains("Cafe", ignoreCase = true) &&
                            !log.mealType.contains("Almoço", ignoreCase = true) &&
                            !log.mealType.contains("Almoco", ignoreCase = true) &&
                            !log.mealType.contains("Lanche", ignoreCase = true) &&
                            !log.mealType.contains("Jantar", ignoreCase = true)
                }
            }
        }
    }

    val progressCalories = if (calorieGoal > 0) (todayMealCalories.toFloat() / calorieGoal).coerceIn(0f, 1f) else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("meals_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ================= 1. CABEÇALHO =================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Refeições do Dia",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Registro interativo por prato (TBCA / NEPA)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { onOpenAddMeal("Almoço") },
                    modifier = Modifier.testTag("meals_screen_add_fab_button"),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Montar Prato")
                }
            }
        }

        // ================= 2. CARD DE RESUMO DE CALORIAS E MACROS =================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("meals_nutrition_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Consumo Calórico",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "Meta recomendada: $calorieGoal kcal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Text(
                            text = "$todayMealCalories kcal",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { progressCalories },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Linha de Macronutrientes (Proteína, Carboidrato, Gordura, Fibra)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Proteínas", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${todayProtein.toInt()}g",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Carboidratos", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${todayCarb.toInt()}g",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Gorduras", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${todayFat.toInt()}g",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Fibras", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${todayFiber.toInt()}g",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
        }

        // ================= 3. FILTRO DE ABAS =================
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    FilterChip(
                        selected = (selectedFilter == filter),
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("meal_filter_$filter")
                    )
                }
            }
        }

        // ================= 4. SEÇÕES INTERATIVAS POR REFEIÇÃO =================
        if (selectedFilter == "Todas") {
            MealCategoryType.values().forEach { category ->
                val logsForCategory = mealsByCategory[category].orEmpty()
                val totalCaloriesCategory = logsForCategory.sumOf { it.calories }
                val totalProteinCategory = logsForCategory.sumOf { it.proteinGrams.toDouble() }.toFloat()
                val totalCarbCategory = logsForCategory.sumOf { it.carbGrams.toDouble() }.toFloat()
                val totalFatCategory = logsForCategory.sumOf { it.fatGrams.toDouble() }.toFloat()

                item {
                    val suggestedFoods = remember(category) {
                        FoodDatabase.items.filter { it.id in category.suggestedFoodIds }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("category_card_${category.name.lowercase()}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header da categoria de refeição
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
                                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = category.icon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = category.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = category.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (logsForCategory.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.tertiaryContainer
                                        ) {
                                            Text(
                                                text = "$totalCaloriesCategory kcal",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        }
                                    }

                                    FilledTonalButton(
                                        onClick = { onOpenAddMeal(category.title) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("add_meal_to_${category.name.lowercase()}")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Adicionar", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }

                            // Resumo de macros da refeição se houver itens
                            if (logsForCategory.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("P: ${totalProteinCategory.toInt()}g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text("C: ${totalCarbCategory.toInt()}g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                                    Text("G: ${totalFatCategory.toInt()}g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Lista de Itens Registrados nesta Refeição
                            if (logsForCategory.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    logsForCategory.forEach { log ->
                                        MealItemRow(
                                            log = log,
                                            timeFormat = timeFormat,
                                            onDelete = { onDeleteMeal(log) }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            // Atalhos Rápidos de Alimentos em 1 Toque para esta Refeição
                            Text(
                                text = "Atalhos rápidos para ${category.title}:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(suggestedFoods, key = { it.id }) { food ->
                                    val defGrams = if (food.unitServing != null) {
                                        (food.unitServing.defaultQuantity * food.unitServing.gramsPerUnit).toInt()
                                    } else food.defaultServingGrams
                                    val defCal = food.calculateCalories(defGrams.toFloat())

                                    Surface(
                                        onClick = {
                                            if (onQuickAddPredefinedFood != null) {
                                                onQuickAddPredefinedFood(category.title, food)
                                            } else {
                                                onOpenAddMeal(category.title)
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.testTag("quick_food_${category.name.lowercase()}_${food.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Column {
                                                Text(
                                                    text = food.name,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "$defCal kcal • ${defGrams}g",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Modo Filtrado Específico
            if (filteredLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nenhuma refeição encontrada para $selectedFilter.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onOpenAddMeal(selectedFilter) }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Registrar em $selectedFilter")
                            }
                        }
                    }
                }
            } else {
                items(filteredLogs, key = { it.id }) { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("meal_log_filtered_${log.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Box(modifier = Modifier.padding(14.dp)) {
                            MealItemRow(
                                log = log,
                                timeFormat = timeFormat,
                                onDelete = { onDeleteMeal(log) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Componente individual de linha de refeição com dados nutricionais e deleção.
 */
@Composable
fun MealItemRow(
    log: MealLog,
    timeFormat: SimpleDateFormat,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("meal_item_${log.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val portionText = if (log.quantityUnits > 0f) {
                val qStr = if (log.quantityUnits % 1f == 0f) log.quantityUnits.toInt().toString() else "%.1f".format(log.quantityUnits)
                " ($qStr ${log.servingUnit} • ${log.weightGrams}g)"
            } else if (log.weightGrams > 0) {
                " (${log.weightGrams}g)"
            } else ""

            Text(
                text = "${log.description.ifBlank { log.foodName }}$portionText",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Linha com calorias e macros
            val macros = buildList {
                if (log.calories > 0) add("${log.calories} kcal")
                if (log.proteinGrams > 0f) add("P: ${log.proteinGrams.toInt()}g")
                if (log.carbGrams > 0f) add("C: ${log.carbGrams.toInt()}g")
                if (log.fatGrams > 0f) add("G: ${log.fatGrams.toInt()}g")
                if (log.fiberGrams > 0f) add("F: ${log.fiberGrams.toInt()}g")
            }.joinToString(" • ")

            if (macros.isNotBlank()) {
                Text(
                    text = macros,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "Registrado às ${timeFormat.format(Date(log.timestamp))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.testTag("delete_meal_${log.id}")
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Remover refeição",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
        }
    }
}
