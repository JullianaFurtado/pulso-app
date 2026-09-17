package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.FoodDatabase
import com.example.FoodItem
import com.example.Macronutrients
import com.example.ServingUnit

/**
 * Item que compõe um prato ou refeição em montagem.
 */
data class PlateItem(
    val foodName: String,
    val category: String = "Personalizado",
    val weightGrams: Int,
    val calories: Int,
    val proteinGrams: Float,
    val carbGrams: Float,
    val fatGrams: Float,
    val fiberGrams: Float,
    val servingUnit: String = "g",
    val quantityUnits: Float = 0f,
    val isHealthy: Boolean = true
)

/**
 * Modal BottomSheet / Diálogo Interativo e Organizado para Registro de Refeições.
 * Apresenta:
 * 1. Seleção intuitiva de tipo de refeição com ícones e horários sugeridos.
 * 2. Catálogo navegável por categorias e busca inteligente na base nutricional TBCA.
 * 3. Seletor de porção interativo (Unidades/Medidas caseiras vs Peso em gramas) com sliders e atalhos rápidos.
 * 4. Visualização em tempo real de macronutrientes (Proteínas, Carboidratos, Gorduras, Fibras) e calorias.
 * 5. Montador de Prato completo (adicionar múltiplos itens e registrar com um único clique).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMealDialog(
    initialMealType: String = "Almoço",
    onDismiss: () -> Unit,
    onConfirmMealItem: (
        mealType: String,
        foodName: String,
        weightGrams: Int,
        calories: Int,
        isHealthy: Boolean,
        proteinGrams: Float,
        carbGrams: Float,
        fatGrams: Float,
        fiberGrams: Float,
        servingUnit: String,
        quantityUnits: Float
    ) -> Unit,
    onConfirmMultipleItems: ((mealType: String, items: List<PlateItem>) -> Unit)? = null
) {
    val mealCategories = listOf(
        Triple("Café da Manhã", Icons.Default.LocalCafe, "Manhã"),
        Triple("Almoço", Icons.Default.WbSunny, "Meio-dia"),
        Triple("Lanche", Icons.Default.Spa, "Tarde"),
        Triple("Jantar", Icons.Default.Restaurant, "Noite"),
        Triple("Ceia / Outro", Icons.Default.Nightlife, "Extra")
    )

    var selectedMealType by remember {
        mutableStateOf(
            if (mealCategories.any { it.first.equals(initialMealType, ignoreCase = true) }) {
                mealCategories.first { it.first.equals(initialMealType, ignoreCase = true) }.first
            } else {
                "Almoço"
            }
        )
    }

    var selectedUiCategory by remember { mutableStateOf("⭐ Sugestões") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }

    // Modo de medida: 0 = Peso (gramas), 1 = Unidades / Medidas caseiras
    var portionMode by remember { mutableIntStateOf(1) }
    var unitCount by remember { mutableFloatStateOf(1f) }
    var weightGramsText by remember { mutableStateOf("100") }
    var isHealthyChoice by remember { mutableStateOf(true) }

    // Entrada Manual Personalizada
    var customFoodName by remember { mutableStateOf("") }
    var customCaloriesText by remember { mutableStateOf("") }
    var customProteinText by remember { mutableStateOf("") }
    var customCarbsText by remember { mutableStateOf("") }
    var customFatText by remember { mutableStateOf("") }

    // Prato em Montagem (permite adicionar múltiplos alimentos em uma só refeição)
    val plateItems = remember { mutableStateListOf<PlateItem>() }
    var showPlateSummary by remember { mutableStateOf(false) }

    // Itens da categoria selecionada ou busca
    val displayedFoods = remember(searchQuery, selectedUiCategory, selectedMealType) {
        if (searchQuery.isNotBlank()) {
            FoodDatabase.search(searchQuery).take(20)
        } else {
            FoodDatabase.getItemsByUiCategory(selectedUiCategory, selectedMealType)
        }
    }

    // Gramas efetivos calculados em tempo real
    val effectiveWeightGrams by remember(selectedFood, portionMode, unitCount, weightGramsText) {
        derivedStateOf {
            if (selectedFood != null) {
                if (portionMode == 1 && selectedFood!!.unitServing != null) {
                    (unitCount * selectedFood!!.unitServing!!.gramsPerUnit).coerceAtLeast(1f)
                } else {
                    (weightGramsText.toFloatOrNull() ?: selectedFood!!.defaultServingGrams.toFloat()).coerceAtLeast(1f)
                }
            } else {
                (weightGramsText.toFloatOrNull() ?: 100f).coerceAtLeast(1f)
            }
        }
    }

    // Calorias calculadas em tempo real
    val liveCalories by remember(selectedFood, effectiveWeightGrams, customCaloriesText) {
        derivedStateOf {
            if (selectedFood != null) {
                selectedFood!!.calculateCalories(effectiveWeightGrams)
            } else {
                customCaloriesText.toIntOrNull() ?: 0
            }
        }
    }

    // Macronutrientes calculados em tempo real
    val liveMacros by remember(selectedFood, effectiveWeightGrams, customProteinText, customCarbsText, customFatText) {
        derivedStateOf {
            if (selectedFood != null) {
                selectedFood!!.calculateMacros(effectiveWeightGrams)
            } else {
                Macronutrients(
                    proteinGrams = customProteinText.toFloatOrNull() ?: 0f,
                    carbGrams = customCarbsText.toFloatOrNull() ?: 0f,
                    fatGrams = customFatText.toFloatOrNull() ?: 0f,
                    fiberGrams = 0f
                )
            }
        }
    }

    // Totais do Prato Montado (Cálculos eficientes em derivedStateOf sem alocações extras)
    val totalPlateCalories by remember {
        derivedStateOf { plateItems.sumOf { it.calories } }
    }
    val totalPlateProtein by remember {
        derivedStateOf { plateItems.sumOf { it.proteinGrams.toDouble() }.toFloat() }
    }
    val totalPlateCarb by remember {
        derivedStateOf { plateItems.sumOf { it.carbGrams.toDouble() }.toFloat() }
    }
    val totalPlateFat by remember {
        derivedStateOf { plateItems.sumOf { it.fatGrams.toDouble() }.toFloat() }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.testTag("interactive_meal_sheet"),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 40.dp, height = 4.dp),
                shape = RoundedCornerShape(2.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp)
        ) {
            // ================= 1. CABEÇALHO COM TÍTULO E BOTÃO FECHAR =================
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
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Registrar Refeição",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Base nutricional oficial TBCA / NEPA",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Badge do Prato se houver itens adicionados
                if (plateItems.isNotEmpty()) {
                    FilledTonalButton(
                        onClick = { showPlateSummary = !showPlateSummary },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("toggle_plate_summary_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${plateItems.size} no prato (${totalPlateCalories} kcal)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ================= 2. SELETOR HORIZONTAL DE REFEIÇÃO (Café, Almoço, Lanche, Jantar...) =================
            Text(
                text = "Qual refeição você está fazendo?",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mealCategories) { (title, icon, timeHint) ->
                    val isSelected = selectedMealType == title
                    val containerColor by animateColorAsState(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        label = "mealChipColor"
                    )
                    val contentColor by animateColorAsState(
                        if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "mealChipTextColor"
                    )

                    Surface(
                        onClick = { selectedMealType = title },
                        shape = RoundedCornerShape(14.dp),
                        color = containerColor,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.testTag("meal_type_chip_$title")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = contentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = contentColor
                                )
                                Text(
                                    text = timeHint,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = contentColor.copy(alpha = 0.75f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ================= 3. BARRA DE BUSCA RÁPIDA =================
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotBlank()) {
                        selectedFood = null
                    }
                },
                placeholder = { Text("Buscar alimento (ex: ovo mexido, arroz, frango, aveia...)") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpar busca")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("interactive_food_search_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ================= 4. ABAS DE CATEGORIAS NUTRICIONAIS =================
            if (searchQuery.isBlank()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(FoodDatabase.availableCategories + listOf("✏️ Personalizado")) { cat ->
                        val isCatSelected = (selectedUiCategory == cat)
                        FilterChip(
                            selected = isCatSelected,
                            onClick = {
                                selectedUiCategory = cat
                                selectedFood = null
                            },
                            label = {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("food_category_chip_$cat")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ================= CORPO ROLÁVEL PRINCIPAL =================
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Se o usuário selecionou a aba "Personalizado"
                if (selectedUiCategory == "✏️ Personalizado" && searchQuery.isBlank()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Adicionar Alimento Personalizado",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedTextField(
                                    value = customFoodName,
                                    onValueChange = { customFoodName = it },
                                    label = { Text("Nome do alimento ou prato") },
                                    placeholder = { Text("Ex: Omelete especial da casa") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = customCaloriesText,
                                        onValueChange = { customCaloriesText = it.filter { char -> char.isDigit() } },
                                        label = { Text("Calorias (kcal)") },
                                        placeholder = { Text("Ex: 250") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = weightGramsText,
                                        onValueChange = { weightGramsText = it.filter { char -> char.isDigit() } },
                                        label = { Text("Peso (g)") },
                                        placeholder = { Text("Ex: 150") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = customProteinText,
                                        onValueChange = { customProteinText = it.filter { char -> char.isDigit() || char == '.' } },
                                        label = { Text("Proteína (g)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = customCarbsText,
                                        onValueChange = { customCarbsText = it.filter { char -> char.isDigit() || char == '.' } },
                                        label = { Text("Carboidrato (g)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = customFatText,
                                        onValueChange = { customFatText = it.filter { char -> char.isDigit() || char == '.' } },
                                        label = { Text("Gordura (g)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (customFoodName.isNotBlank() && (customCaloriesText.toIntOrNull() ?: 0) > 0) {
                                            val item = PlateItem(
                                                foodName = customFoodName.trim(),
                                                weightGrams = weightGramsText.toIntOrNull() ?: 100,
                                                calories = customCaloriesText.toIntOrNull() ?: 0,
                                                proteinGrams = customProteinText.toFloatOrNull() ?: 0f,
                                                carbGrams = customCarbsText.toFloatOrNull() ?: 0f,
                                                fatGrams = customFatText.toFloatOrNull() ?: 0f,
                                                fiberGrams = 0f,
                                                servingUnit = "g",
                                                quantityUnits = 0f,
                                                isHealthy = true
                                            )
                                            plateItems.add(item)
                                            customFoodName = ""
                                            customCaloriesText = ""
                                            customProteinText = ""
                                            customCarbsText = ""
                                            customFatText = ""
                                            showPlateSummary = true
                                        }
                                    },
                                    enabled = customFoodName.isNotBlank() && (customCaloriesText.toIntOrNull() ?: 0) > 0,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("add_custom_food_to_plate_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Adicionar ao Prato", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }

                // ================= 5. PAINEL INTERATIVO DE PORÇÃO E MACROS (SE ALIMENTO SELECIONADO) =================
                if (selectedFood != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("interactive_portion_panel"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Título do Alimento Ativo
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = selectedFood!!.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${selectedFood!!.category} • ${selectedFood!!.caloriesPer100g} kcal / 100g",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = { selectedFood = null },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Trocar alimento", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))

                                // Alternador: Porção por Unidade vs Porção por Peso em Gramas
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Forma de Medida:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        val hasUnit = selectedFood!!.unitServing != null
                                        if (hasUnit) {
                                            FilterChip(
                                                selected = portionMode == 1,
                                                onClick = { portionMode = 1 },
                                                label = {
                                                    Text(
                                                        text = "Por ${selectedFood!!.unitServing!!.unitName}",
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        }

                                        FilterChip(
                                            selected = portionMode == 0 || !hasUnit,
                                            onClick = { portionMode = 0 },
                                            label = { Text("Por Peso (g)", style = MaterialTheme.typography.labelSmall) },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }

                                // Controles Interativos de Quantidade
                                if (portionMode == 1 && selectedFood!!.unitServing != null) {
                                    val unitInfo = selectedFood!!.unitServing!!
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Quantidade de porções:",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.primary
                                            ) {
                                                val qStr = if (unitCount % 1f == 0f) unitCount.toInt().toString() else "%.1f".format(unitCount)
                                                Text(
                                                    text = "$qStr ${unitInfo.unitName} (≈ ${effectiveWeightGrams.toInt()}g)",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        // Stepper com botões e slider
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            IconButton(
                                                onClick = { if (unitCount > 0.5f) unitCount = (unitCount - 0.5f).coerceAtLeast(0.5f) },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.surface)
                                            ) {
                                                Icon(Icons.Default.Remove, contentDescription = "Menos")
                                            }

                                            Slider(
                                                value = unitCount,
                                                onValueChange = { unitCount = it },
                                                valueRange = 0.5f..8f,
                                                steps = 14,
                                                modifier = Modifier.weight(1f)
                                            )

                                            IconButton(
                                                onClick = { if (unitCount < 8f) unitCount = (unitCount + 0.5f).coerceAtMost(8f) },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.surface)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = "Mais")
                                            }
                                        }

                                        // Atalhos de Unidade
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(1f, 2f, 3f, 4f).forEach { q ->
                                                OutlinedButton(
                                                    onClick = { unitCount = q },
                                                    modifier = Modifier.weight(1f),
                                                    contentPadding = PaddingValues(2.dp),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("${q.toInt()}x", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Modo Peso em Gramas
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = weightGramsText,
                                            onValueChange = {
                                                if (it.length <= 5 && it.all { c -> c.isDigit() }) {
                                                    weightGramsText = it
                                                }
                                            },
                                            label = { Text("Peso total consumido") },
                                            trailingIcon = { Text("g", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) },
                                            leadingIcon = { Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        // Atalhos rápidos de gramatura
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            listOf(50, 100, 150, 200, 250).forEach { g ->
                                                OutlinedButton(
                                                    onClick = { weightGramsText = g.toString() },
                                                    modifier = Modifier.weight(1f),
                                                    contentPadding = PaddingValues(2.dp),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text("${g}g", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }
                                        }
                                    }
                                }

                                // Visualizador de Energia e Macronutrientes em Tempo Real
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.LocalFireDepartment,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.tertiary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Energia Calculada",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Text(
                                                text = "$liveCalories kcal",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }

                                        // 4 Cartões de Macronutrientes
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            MacroBadge(
                                                label = "Proteína",
                                                valueGrams = liveMacros.proteinGrams,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            MacroBadge(
                                                label = "Carboidrato",
                                                valueGrams = liveMacros.carbGrams,
                                                color = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            MacroBadge(
                                                label = "Gordura",
                                                valueGrams = liveMacros.fatGrams,
                                                color = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.weight(1f)
                                            )
                                            MacroBadge(
                                                label = "Fibra",
                                                valueGrams = liveMacros.fiberGrams,
                                                color = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }

                                // Botões de Ação do Alimento
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { selectedFood = null },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Cancelar", style = MaterialTheme.typography.labelMedium)
                                    }

                                    Button(
                                        onClick = {
                                            val food = selectedFood!!
                                            val item = PlateItem(
                                                foodName = food.name,
                                                category = food.category,
                                                weightGrams = effectiveWeightGrams.toInt(),
                                                calories = liveCalories,
                                                proteinGrams = liveMacros.proteinGrams,
                                                carbGrams = liveMacros.carbGrams,
                                                fatGrams = liveMacros.fatGrams,
                                                fiberGrams = liveMacros.fiberGrams,
                                                servingUnit = if (portionMode == 1 && food.unitServing != null) food.unitServing.unitName else "g",
                                                quantityUnits = if (portionMode == 1 && food.unitServing != null) unitCount else 0f,
                                                isHealthy = isHealthyChoice
                                            )
                                            plateItems.add(item)
                                            selectedFood = null
                                            showPlateSummary = true
                                        },
                                        modifier = Modifier
                                            .weight(1.4f)
                                            .testTag("add_to_plate_button"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Adicionar ao Prato", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                // ================= 6. GRADE / LISTA DE ALIMENTOS DISPONÍVEIS =================
                item {
                    Text(
                        text = if (searchQuery.isNotBlank()) "Resultados encontrados (${displayedFoods.size}):"
                        else "Selecione um alimento para montar a refeição:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(displayedFoods, key = { it.id }) { food ->
                    val isSelected = (selectedFood?.id == food.id)
                    FoodItemCard(
                        food = food,
                        isSelected = isSelected,
                        onSelect = {
                            selectedFood = food
                            if (food.unitServing != null) {
                                portionMode = 1
                                unitCount = food.unitServing.defaultQuantity
                            } else {
                                portionMode = 0
                                weightGramsText = food.defaultServingGrams.toString()
                            }
                        },
                        onQuickAdd = {
                            val defaultGrams = if (food.unitServing != null) {
                                (food.unitServing.defaultQuantity * food.unitServing.gramsPerUnit).toInt()
                            } else {
                                food.defaultServingGrams
                            }
                            val defaultCalories = food.calculateCalories(defaultGrams.toFloat())
                            val defaultMacros = food.calculateMacros(defaultGrams.toFloat())

                            val item = PlateItem(
                                foodName = food.name,
                                category = food.category,
                                weightGrams = defaultGrams,
                                calories = defaultCalories,
                                proteinGrams = defaultMacros.proteinGrams,
                                carbGrams = defaultMacros.carbGrams,
                                fatGrams = defaultMacros.fatGrams,
                                fiberGrams = defaultMacros.fiberGrams,
                                servingUnit = food.unitServing?.unitName ?: "g",
                                quantityUnits = food.unitServing?.defaultQuantity ?: 0f,
                                isHealthy = true
                            )
                            plateItems.add(item)
                            showPlateSummary = true
                        }
                    )
                }

                if (displayedFoods.isEmpty() && searchQuery.isNotBlank()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Nenhum item com '$searchQuery' encontrado.", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        selectedUiCategory = "✏️ Personalizado"
                                        customFoodName = searchQuery
                                        searchQuery = ""
                                    }
                                ) {
                                    Text("Cadastrar como Alimento Personalizado")
                                }
                            }
                        }
                    }
                }
            }

            // ================= 7. RODAPÉ FIXO DE PRATO MONTADO (SE HOUVER ITENS) =================
            if (plateItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("plate_summary_bottom_bar"),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Prato: $selectedMealType (${plateItems.size} ${if (plateItems.size == 1) "item" else "itens"})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "P: ${totalPlateProtein.toInt()}g • C: ${totalPlateCarb.toInt()}g • G: ${totalPlateFat.toInt()}g",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            Text(
                                text = "$totalPlateCalories kcal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        // Lista expansível dos itens no prato
                        if (showPlateSummary) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(6.dp))

                            plateItems.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "• ${item.foodName} (${item.weightGrams}g)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${item.calories} kcal",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = { plateItems.removeAt(index) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Remover do prato",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showPlateSummary = !showPlateSummary },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(if (showPlateSummary) "Ocultar" else "Ver Detalhes", style = MaterialTheme.typography.labelSmall)
                            }

                            Button(
                                onClick = {
                                    if (onConfirmMultipleItems != null) {
                                        onConfirmMultipleItems(selectedMealType, plateItems.toList())
                                    } else {
                                        plateItems.forEach { item ->
                                            onConfirmMealItem(
                                                selectedMealType,
                                                item.foodName,
                                                item.weightGrams,
                                                item.calories,
                                                item.isHealthy,
                                                item.proteinGrams,
                                                item.carbGrams,
                                                item.fatGrams,
                                                item.fiberGrams,
                                                item.servingUnit,
                                                item.quantityUnits
                                            )
                                        }
                                    }
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1.6f)
                                    .testTag("save_plate_full_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Finalizar Registro", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Cartão de Alimento com suporte a seleção detalhada ou adição rápida em 1 toque.
 */
@Composable
fun FoodItemCard(
    food: FoodItem,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onQuickAdd: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .testTag("food_card_${food.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) borderColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val servingHint = if (food.unitServing != null) {
                    "1 ${food.unitServing.unitName} ≈ ${food.unitServing.gramsPerUnit.toInt()}g"
                } else {
                    "${food.defaultServingGrams}g porção padrão"
                }

                Text(
                    text = "${food.category} • $servingHint",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Tags resumidas de Macros
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "P: ${food.macros.proteinGrams.toInt()}g",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("•", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "C: ${food.macros.carbGrams.toInt()}g",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text("•", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "G: ${food.macros.fatGrams.toInt()}g",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = "${food.caloriesPer100g} kcal",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Botão de Adição Rápida
                FilledTonalButton(
                    onClick = onQuickAdd,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("quick_add_food_${food.id}")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("+ 1x", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                }
            }
        }
    }
}

/**
 * Mini indicador de macronutriente para o painel de cálculo.
 */
@Composable
fun MacroBadge(
    label: String,
    valueGrams: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = color,
                maxLines = 1
            )
            Text(
                text = "%.1fg".format(valueGrams),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
