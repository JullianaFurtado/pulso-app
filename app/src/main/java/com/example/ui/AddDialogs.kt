package com.example.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.FoodDatabase
import com.example.FoodItem

/**
 * Diálogo para registrar consumo de água personalizado ou rápido.
 */
@Composable
fun AddWaterDialog(
    onDismiss: () -> Unit,
    onConfirm: (amountMl: Int) -> Unit
) {
    var customAmountText by remember { mutableStateOf("") }
    val quickOptions = listOf(150, 200, 250, 350, 500)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = "Água",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(text = "Registrar Água", fontWeight = FontWeight.Bold)
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Escolha um volume rápido ou digite em ml:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Linha 1 de opções rápidas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickOptions.take(3).forEach { amount ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onConfirm(amount)
                                    onDismiss()
                                }
                                .testTag("quick_water_${amount}"),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${amount}ml",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Linha 2 de opções rápidas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickOptions.drop(3).forEach { amount ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onConfirm(amount)
                                    onDismiss()
                                }
                                .testTag("quick_water_${amount}"),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${amount}ml",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = customAmountText,
                    onValueChange = { customAmountText = it.filter { char -> char.isDigit() } },
                    label = { Text("Outro valor (ml)") },
                    placeholder = { Text("Ex: 400") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_water_input"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = customAmountText.toIntOrNull() ?: 250
                    if (amount > 0) {
                        onConfirm(amount)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("confirm_water_button")
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Diálogo para registrar atividade física.
 */
@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onConfirm: (type: String, durationMin: Int, calories: Int) -> Unit
) {
    val predefinedTypes = listOf("Caminhada", "Corrida", "Musculação", "Ciclismo", "Alongamento / Yoga")
    var selectedType by remember { mutableStateOf(predefinedTypes[0]) }
    var durationText by remember { mutableStateOf("30") }
    var caloriesText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "Exercício",
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        title = {
            Text(text = "Registrar Atividade Física", fontWeight = FontWeight.Bold)
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Tipo de Exercício:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                predefinedTypes.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (type == selectedType),
                                onClick = { selectedType = type }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (type == selectedType),
                            onClick = { selectedType = type }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = type, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter { char -> char.isDigit() } },
                    label = { Text("Duração (minutos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activity_duration_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it.filter { char -> char.isDigit() } },
                    label = { Text("Calorias estimadas (opcional)") },
                    placeholder = { Text("Ex: 180") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activity_calories_input"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val duration = durationText.toIntOrNull() ?: 15
                    val calories = caloriesText.toIntOrNull() ?: (duration * 6) // estimativa padrão
                    if (duration > 0) {
                        onConfirm(selectedType, duration, calories)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("confirm_activity_button")
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Diálogo interativo e dinâmico para registrar refeições.
 * Permite alternar entre porção por peso (g) e porção por unidade (ex.: 2 ovos fritos, fatias, colheres),
 * busca ampla por alimentos e exibição ao vivo dos macronutrientes calculados (Proteínas, Carboidratos, Gorduras, Fibras).
 */
@Composable
fun AddMealDialog(
    onDismiss: () -> Unit,
    onConfirm: (
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
    ) -> Unit
) {
    val mealTypes = listOf("Café da Manhã", "Almoço", "Lanche", "Jantar", "Ceia / Outro")
    var selectedMealType by remember { mutableStateOf(mealTypes[1]) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
    
    // Modo de entrada: 0 = Por Peso (g), 1 = Por Unidade / Porção
    var inputModeIndex by remember { mutableIntStateOf(0) }
    
    var weightText by remember { mutableStateOf("100") }
    var unitCount by remember { mutableStateOf(1f) }
    var customCaloriesText by remember { mutableStateOf("") }
    var isHealthy by remember { mutableStateOf(true) }

    // Busca de alimentos com catálogo ampliado (TBCA / USDA)
    val searchResults = remember(searchQuery) {
        if (searchQuery.isNotBlank()) {
            FoodDatabase.search(searchQuery).take(6)
        } else {
            emptyList()
        }
    }

    // Calcula os gramas efetivos dependendo do modo (peso direto vs por unidades)
    val effectiveWeightGrams by remember(selectedFood, inputModeIndex, weightText, unitCount) {
        derivedStateOf {
            if (inputModeIndex == 1 && selectedFood?.unitServing != null) {
                (unitCount * selectedFood!!.unitServing!!.gramsPerUnit).coerceAtLeast(1f)
            } else {
                (weightText.toFloatOrNull() ?: 100f).coerceAtLeast(1f)
            }
        }
    }

    // Calorias calculadas em tempo real
    val calculatedCalories by remember(selectedFood, effectiveWeightGrams, customCaloriesText) {
        derivedStateOf {
            if (selectedFood != null) {
                selectedFood!!.calculateCalories(effectiveWeightGrams)
            } else {
                customCaloriesText.toIntOrNull() ?: 0
            }
        }
    }

    // Macronutrientes calculados em tempo real
    val calculatedMacros by remember(selectedFood, effectiveWeightGrams) {
        derivedStateOf {
            selectedFood?.calculateMacros(effectiveWeightGrams)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = "Alimentação",
                tint = MaterialTheme.colorScheme.tertiary
            )
        },
        title = {
            Text(text = "Registrar Refeição", fontWeight = FontWeight.Bold)
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Tipo de Refeição
                Text(
                    text = "Tipo de Refeição:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    mealTypes.take(3).forEach { type ->
                        val isSelected = selectedMealType == type
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMealType = type },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    mealTypes.drop(3).forEach { type ->
                        val isSelected = selectedMealType == type
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMealType = type },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 2. Busca e Nome do Alimento
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            if (selectedFood != null && it != selectedFood!!.name) {
                                selectedFood = null
                            }
                        },
                        label = { Text("Buscar alimento (ex: ovo cozido, arroz, frango...)") },
                        placeholder = { Text("Digite para buscar na base nutricional") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            if (selectedFood != null) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selecionado",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("food_search_input"),
                        singleLine = true
                    )

                    // Card de confirmação de alimento selecionado
                    if (selectedFood != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = selectedFood!!.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Base TBCA/USDA • ${selectedFood!!.category} • ${selectedFood!!.caloriesPer100g} kcal / 100g",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Sugestões rápidas de busca
                    if (searchResults.isNotEmpty() && selectedFood == null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text(
                                    text = "Base Nutricional (TBCA / USDA):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                searchResults.forEach { food ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                selectedFood = food
                                                searchQuery = food.name
                                                if (food.unitServing != null) {
                                                    inputModeIndex = 1
                                                    unitCount = food.unitServing.defaultQuantity
                                                } else {
                                                    inputModeIndex = 0
                                                    weightText = food.defaultServingGrams.toString()
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = food.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            val servingInfo = if (food.unitServing != null)
                                                "1 ${food.unitServing.unitName} ≈ ${food.unitServing.gramsPerUnit.toInt()}g"
                                            else "${food.defaultServingGrams}g porção padrão"
                                            Text(
                                                text = "${food.category} • $servingInfo",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${food.caloriesPer100g} kcal",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "P:${food.macros.proteinGrams.toInt()}g C:${food.macros.carbGrams.toInt()}g G:${food.macros.fatGrams.toInt()}g",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Escolha Dinâmica de Medida: PESO (g) ou UNIDADE
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Como deseja quantificar?",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )

                            // Alternador de Abas: Peso vs Unidade
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = inputModeIndex == 0,
                                    onClick = { inputModeIndex = 0 },
                                    label = { Text("Por Peso (g)") }
                                )
                                FilterChip(
                                    selected = inputModeIndex == 1,
                                    onClick = { inputModeIndex = 1 },
                                    label = {
                                        val unitLabel = selectedFood?.unitServing?.unitName ?: "Unidade"
                                        Text("Por $unitLabel")
                                    }
                                )
                            }
                        }

                        // MODO 1: Quantidade por UNIDADES (ex.: ovos cozidos, fatias, conchas)
                        if (inputModeIndex == 1) {
                            val unitLabel = selectedFood?.unitServing?.unitName ?: "unidade(s)"
                            val gramsPerUnit = selectedFood?.unitServing?.gramsPerUnit ?: 50f

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Quantidade:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${if (unitCount % 1f == 0f) unitCount.toInt().toString() else "%.1f".format(unitCount)} $unitLabel (≈ ${effectiveWeightGrams.toInt()}g)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (unitCount > 0.5f) unitCount = (unitCount - 0.5f).coerceAtLeast(0.5f)
                                        }
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Diminuir quantidade")
                                    }

                                    Slider(
                                        value = unitCount,
                                        onValueChange = { unitCount = it },
                                        valueRange = 0.5f..10f,
                                        steps = 18,
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = {
                                            if (unitCount < 10f) unitCount = (unitCount + 0.5f).coerceAtMost(10f)
                                        }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Aumentar quantidade")
                                    }
                                }

                                // Botões rápidos de contagem de unidades
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(1f, 2f, 3f, 4f).forEach { count ->
                                        OutlinedButton(
                                            onClick = { unitCount = count },
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("${count.toInt()}x", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        } else {
                            // MODO 0: Quantidade direta por PESO (g)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = weightText,
                                    onValueChange = {
                                        if (it.length <= 5 && it.all { char -> char.isDigit() }) {
                                            weightText = it
                                        }
                                    },
                                    label = { Text("Peso do alimento consumido") },
                                    placeholder = { Text("Ex: 100") },
                                    leadingIcon = {
                                        Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    },
                                    trailingIcon = {
                                        Text("g", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp))
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("food_weight_input"),
                                    singleLine = true
                                )

                                // Atalhos rápidos de gramatura
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(50, 100, 150, 200, 250).forEach { grams ->
                                        OutlinedButton(
                                            onClick = { weightText = grams.toString() },
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("${grams}g", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Painel Dinâmico de Macronutrientes e Calorias
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Energia Estimada",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Porção calculada: ${effectiveWeightGrams.toInt()}g",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "$calculatedCalories kcal",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        // Distribuição dos 4 Macronutrientes (Proteínas, Carboidratos, Gorduras, Fibras)
                        if (calculatedMacros != null) {
                            Text(
                                text = "Composição de Macronutrientes:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Proteínas
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Proteína", style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            text = "%.1fg".format(calculatedMacros!!.proteinGrams),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                // Carboidratos
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Carboidrato", style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            text = "%.1fg".format(calculatedMacros!!.carbGrams),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }

                                // Gorduras
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Gordura", style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            text = "%.1fg".format(calculatedMacros!!.fatGrams),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                // Fibras
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Fibra", style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            text = "%.1fg".format(calculatedMacros!!.fiberGrams),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Se o alimento não for reconhecido, permite definir calorias personalizadas
                if (selectedFood == null) {
                    OutlinedTextField(
                        value = customCaloriesText,
                        onValueChange = {
                            if (it.length <= 5 && it.all { char -> char.isDigit() }) {
                                customCaloriesText = it
                            }
                        },
                        label = { Text("Calorias manuais estimadas (opcional)") },
                        placeholder = { Text("Ex: 180") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 5. Opção Refeição Saudável
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isHealthy = !isHealthy }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isHealthy,
                        onCheckedChange = { isHealthy = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Refeição nutritiva e balanceada",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val food = searchQuery.trim().ifEmpty { "Alimento" }
                    val finalWeight = effectiveWeightGrams.toInt()
                    val cal = calculatedCalories
                    val macros = calculatedMacros
                    val unitName = if (inputModeIndex == 1) (selectedFood?.unitServing?.unitName ?: "un") else "g"
                    val unitQty = if (inputModeIndex == 1) unitCount else 0f

                    onConfirm(
                        selectedMealType,
                        food,
                        finalWeight,
                        cal,
                        isHealthy,
                        macros?.proteinGrams ?: 0f,
                        macros?.carbGrams ?: 0f,
                        macros?.fatGrams ?: 0f,
                        macros?.fiberGrams ?: 0f,
                        unitName,
                        unitQty
                    )
                    onDismiss()
                },
                modifier = Modifier.testTag("confirm_meal_button")
            ) {
                Text("Salvar Refeição")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
