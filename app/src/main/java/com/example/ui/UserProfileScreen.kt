package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.UserProfile
import kotlin.math.roundToInt

/**
 * Tela de Registro e Edição Interativa e Dinâmica do Perfil de Saúde do Usuário.
 * Oferece controles táteis interativos (Sliders sincronizados, incrementos rápidos,
 * seletores dinâmicos de rotina e objetivos) e atualiza em tempo real as estimativas
 * biológicas recomendadas (Água, Exercício, Calorias e Régua Gráfica de IMC).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    currentProfile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit,
    onDismissOrBack: (() -> Unit)? = null
) {
    // 1. Estados dos campos principais
    var name by remember { mutableStateOf(currentProfile.name) }
    var selectedGender by remember { mutableStateOf(currentProfile.gender.ifBlank { "Masculino" }) }

    // Idade com sincronia bidirecional entre texto e slider
    val initialAge = if (currentProfile.age > 0) currentProfile.age else 28
    var ageSliderValue by remember { mutableFloatStateOf(initialAge.toFloat().coerceIn(10f, 100f)) }
    var ageText by remember { mutableStateOf(if (currentProfile.age > 0) currentProfile.age.toString() else "28") }

    // Peso com sincronia bidirecional
    val initialWeight = if (currentProfile.weightKg > 0f) currentProfile.weightKg else 70f
    var weightSliderValue by remember { mutableFloatStateOf(initialWeight.coerceIn(40f, 160f)) }
    var weightText by remember { mutableStateOf(if (currentProfile.weightKg > 0f) String.format(java.util.Locale.US, "%.1f", currentProfile.weightKg) else "70.0") }

    // Altura com sincronia bidirecional
    val initialHeight = if (currentProfile.heightCm > 0f) currentProfile.heightCm else 172f
    var heightSliderValue by remember { mutableFloatStateOf(initialHeight.coerceIn(120f, 220f)) }
    var heightText by remember { mutableStateOf(if (currentProfile.heightCm > 0f) currentProfile.heightCm.toInt().toString() else "172") }

    // Estilo de vida e Metas
    var selectedActivityLevel by remember { mutableStateOf(currentProfile.activityLevel.ifBlank { "Moderado" }) }
    var selectedWellnessGoal by remember { mutableStateOf(currentProfile.wellnessGoal.ifBlank { "Equilíbrio & Longevidade" }) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Calcula objeto temporário para exibir estimativas em tempo real
    val previewProfile by remember(
        name, ageText, selectedGender, weightText, heightText, selectedActivityLevel, selectedWellnessGoal
    ) {
        derivedStateOf {
            val parsedAge = ageText.toIntOrNull() ?: ageSliderValue.roundToInt()
            val parsedWeight = weightText.replace(',', '.').toFloatOrNull() ?: weightSliderValue
            val parsedHeight = heightText.replace(',', '.').toFloatOrNull() ?: heightSliderValue
            UserProfile(
                name = name.trim(),
                age = parsedAge,
                gender = selectedGender,
                weightKg = parsedWeight,
                heightCm = parsedHeight,
                isRegistered = true,
                activityLevel = selectedActivityLevel,
                wellnessGoal = selectedWellnessGoal
            )
        }
    }

    // Progresso de preenchimento dinâmico (0f a 1f)
    val formProgress by remember(name, ageText, weightText, heightText) {
        derivedStateOf {
            var score = 0f
            if (name.trim().length >= 2) score += 0.35f
            if ((ageText.toIntOrNull() ?: 0) in 5..120) score += 0.20f
            if ((weightText.replace(',', '.').toFloatOrNull() ?: 0f) in 20f..300f) score += 0.25f
            if ((heightText.replace(',', '.').toFloatOrNull() ?: 0f) in 50f..250f) score += 0.20f
            score.coerceIn(0f, 1f)
        }
    }
    val animatedProgress by animateFloatAsState(
        targetValue = formProgress,
        animationSpec = spring(),
        label = "form_progress"
    )

    val isFormComplete = remember(formProgress) { formProgress >= 0.99f }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (currentProfile.isRegistered) "Perfil de Saúde" else "Registro Interativo",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = if (isFormComplete) "Ficha completa • Estimativas ativas" else "Preencha seus dados corporais",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (onDismissOrBack != null && currentProfile.isRegistered) {
                        IconButton(onClick = onDismissOrBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HERO CARD DINÂMICO: Mostra resumo ao vivo do perfil e progresso animado
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar com Iniciais ou Ícone de Gênero
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            if (name.isNotBlank()) {
                                val initials = name.trim().split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }
                                    .joinToString("")
                                Text(
                                    text = initials.ifBlank { "P" },
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = when (selectedGender) {
                                        "Feminino" -> Icons.Default.Face
                                        "Masculino" -> Icons.Default.Person
                                        else -> Icons.Default.Wc
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (name.isNotBlank()) "Olá, ${name.trim()}!" else "Bem-vindo ao Pulso!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = when {
                                    isFormComplete -> "Perfeito! Suas metas foram calculadas sob medida."
                                    name.isBlank() -> "Comece digitando seu nome abaixo."
                                    else -> "Ajuste os sliders táteis para calibrar suas metas."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Barra de progresso interativa
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preenchimento do Perfil",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${(animatedProgress * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            // SEÇÃO 1: Identidade e Gênero
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Identidade & Gênero",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo Nome Completo
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            errorMessage = null
                        },
                        label = { Text("Nome completo ou apelido") },
                        placeholder = { Text("Ex: Lucas Silva") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Sexo biológico (calibra equações metabólicas):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Seletor de Gênero Interativo com Animação
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("Masculino", Icons.Default.Person, "Masc"),
                            Triple("Feminino", Icons.Default.Face, "Fem"),
                            Triple("Outro", Icons.Default.Wc, "Outro")
                        ).forEach { (genderValue, icon, shortLabel) ->
                            val isSelected = selectedGender == genderValue
                            val bgColor by animateColorAsState(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                label = "gender_bg"
                            )
                            val contentColor by animateColorAsState(
                                if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                label = "gender_content"
                            )

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedGender = genderValue },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = bgColor),
                                border = if (isSelected) null else CardDefaults.outlinedCardBorder()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = contentColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = shortLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = contentColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SEÇÃO 2: Biometria Interativa (Sliders + Botões de Incremento + TextFields)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Dados Corporais & Sliders Táteis",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Arraste os controles ou utilize os botões rápidos para ajustar com precisão.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- CONTROLE INTERATIVO 1: IDADE ---
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Cake, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Idade", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            }
                            // Mostrador grande dinâmico
                            Text(
                                text = "$ageText anos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Slider tátil de Idade
                        Slider(
                            value = ageSliderValue,
                            onValueChange = { newValue ->
                                ageSliderValue = newValue
                                val rounded = newValue.roundToInt()
                                ageText = rounded.toString()
                                errorMessage = null
                            },
                            valueRange = 10f..100f,
                            steps = 89,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Botões de incremento e atalhos rápidos de idade
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(18, 25, 35, 50, 65).forEach { fastAge ->
                                    val isCur = ageText.toIntOrNull() == fastAge
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                ageSliderValue = fastAge.toFloat()
                                                ageText = fastAge.toString()
                                            },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isCur) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = "${fastAge}a",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCur) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            // Botões -1 e +1
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        val cur = ageText.toIntOrNull() ?: 28
                                        if (cur > 10) {
                                            val next = cur - 1
                                            ageSliderValue = next.toFloat()
                                            ageText = next.toString()
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Diminuir 1 ano", modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = {
                                        val cur = ageText.toIntOrNull() ?: 28
                                        if (cur < 100) {
                                            val next = cur + 1
                                            ageSliderValue = next.toFloat()
                                            ageText = next.toString()
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Aumentar 1 ano", modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        // Campo numérico direto sincronizado (mantém compatibilidade de teste)
                        OutlinedTextField(
                            value = ageText,
                            onValueChange = {
                                if (it.length <= 3 && it.all { c -> c.isDigit() }) {
                                    ageText = it
                                    val parsed = it.toIntOrNull()
                                    if (parsed != null && parsed in 10..100) {
                                        ageSliderValue = parsed.toFloat()
                                    }
                                    errorMessage = null
                                }
                            },
                            label = { Text("Digitar idade manual") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_age_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // --- CONTROLE INTERATIVO 2: PESO (KG) ---
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Peso Corporal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(
                                text = "$weightText kg",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        // Slider tátil de Peso (40kg a 160kg)
                        Slider(
                            value = weightSliderValue,
                            onValueChange = { newValue ->
                                weightSliderValue = newValue
                                weightText = String.format(java.util.Locale.US, "%.1f", newValue)
                                errorMessage = null
                            },
                            valueRange = 40f..160f,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.secondary,
                                activeTrackColor = MaterialTheme.colorScheme.secondary
                            )
                        )

                        // Botões de micro-ajuste tátil de peso
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(-2f, -0.5f, +0.5f, +2f).forEach { delta ->
                                    OutlinedButton(
                                        onClick = {
                                            val cur = weightText.replace(',', '.').toFloatOrNull() ?: 70f
                                            val next = (cur + delta).coerceIn(30f, 250f)
                                            weightSliderValue = next.coerceIn(40f, 160f)
                                            weightText = String.format(java.util.Locale.US, "%.1f", next)
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(
                                            text = if (delta > 0) "+$delta" else "$delta",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Precisão decimal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Campo numérico direto sincronizado (mantém compatibilidade de teste)
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = {
                                if (it.length <= 6) {
                                    weightText = it
                                    val parsed = it.replace(',', '.').toFloatOrNull()
                                    if (parsed != null && parsed in 40f..160f) {
                                        weightSliderValue = parsed
                                    }
                                    errorMessage = null
                                }
                            },
                            label = { Text("Digitar peso manual (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_weight_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // --- CONTROLE INTERATIVO 3: ALTURA (CM) ---
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Height, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Altura", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            }
                            val meters = (heightText.toFloatOrNull() ?: 170f) / 100f
                            Text(
                                text = "$heightText cm (${String.format(java.util.Locale.US, "%.2f", meters)} m)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }

                        // Slider tátil de Altura (120cm a 220cm)
                        Slider(
                            value = heightSliderValue,
                            onValueChange = { newValue ->
                                heightSliderValue = newValue
                                val rounded = newValue.roundToInt()
                                heightText = rounded.toString()
                                errorMessage = null
                            },
                            valueRange = 120f..220f,
                            steps = 99,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.tertiary,
                                activeTrackColor = MaterialTheme.colorScheme.tertiary
                            )
                        )

                        // Botões de incremento rápido de altura
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(160, 170, 175, 180, 185).forEach { fastH ->
                                    val isCur = heightText.toIntOrNull() == fastH
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                heightSliderValue = fastH.toFloat()
                                                heightText = fastH.toString()
                                            },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isCur) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = "${fastH}cm",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCur) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        val cur = heightText.toIntOrNull() ?: 170
                                        if (cur > 100) {
                                            val next = cur - 1
                                            heightSliderValue = next.toFloat()
                                            heightText = next.toString()
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Diminuir 1 cm", modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = {
                                        val cur = heightText.toIntOrNull() ?: 170
                                        if (cur < 230) {
                                            val next = cur + 1
                                            heightSliderValue = next.toFloat()
                                            heightText = next.toString()
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Aumentar 1 cm", modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Campo numérico direto sincronizado (mantém compatibilidade de teste)
                        OutlinedTextField(
                            value = heightText,
                            onValueChange = {
                                if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                    heightText = it
                                    val parsed = it.toIntOrNull()
                                    if (parsed != null && parsed in 120..220) {
                                        heightSliderValue = parsed.toFloat()
                                    }
                                    errorMessage = null
                                }
                            },
                            label = { Text("Digitar altura manual (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_height_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // SEÇÃO 3: Estilo de Vida & Objetivos (Super Interativo e Dinâmico)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Nível de Rotina & Foco Principal",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Personalize como o Pulso adaptará o gasto calórico e volume de exercícios para sua rotina real.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Nível de Atividade Diária:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4 Cards de Nível de Atividade
                    val activityOptions = listOf(
                        Triple("Sedentário", Icons.Default.AccessibilityNew, "Trabalho sentado / pouco movimento"),
                        Triple("Leve", Icons.Default.DirectionsWalk, "Caminhadas leves ou tarefas domésticas"),
                        Triple("Moderado", Icons.Default.DirectionsRun, "Exercícios físicos 3 a 5x na semana"),
                        Triple("Intenso", Icons.Default.FitnessCenter, "Treinos intensos diários / atleta")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activityOptions.forEach { (level, icon, desc) ->
                            val isSelected = selectedActivityLevel == level
                            val cardBg by animateColorAsState(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                label = "act_bg"
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedActivityLevel = level },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = if (isSelected) CardDefaults.outlinedCardBorder() else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = level,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Qual é o seu objetivo de bem-estar prioritário?",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4 Cards de Objetivos
                    val goalOptions = listOf(
                        Triple("Equilíbrio & Longevidade", Icons.Default.Spa, "Saúde estável e vitalidade diária"),
                        Triple("Mais Hidratação", Icons.Default.WaterDrop, "+500ml extras de água na meta diária"),
                        Triple("Perda de Gordura", Icons.Default.LocalFireDepartment, "Déficit calórico calculado de forma segura"),
                        Triple("Ganho de Força", Icons.Default.Bolt, "Superávit energético e foco muscular")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        goalOptions.take(2).forEach { (goal, icon, _) ->
                            val isSelected = selectedWellnessGoal == goal
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedWellnessGoal = goal },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = goal,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
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
                        goalOptions.drop(2).forEach { (goal, icon, _) ->
                            val isSelected = selectedWellnessGoal == goal
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedWellnessGoal = goal },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = goal,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SEÇÃO 4: PAINEL DE ESTIMATIVAS VIVAS EM TEMPO REAL
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Estimativas Vivas Recomendadas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Tempo Real",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // RÉGUA GRÁFICA INTERATIVA DE IMC
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Índice de Massa Corporal (IMC):",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            AnimatedContent(
                                targetState = previewProfile.bmi,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "bmi_text"
                            ) { bmiVal ->
                                Text(
                                    text = String.format(java.util.Locale.US, "%.1f", bmiVal),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        bmiVal < 18.5f -> Color(0xFF1976D2) // Azul
                                        bmiVal < 25f -> Color(0xFF2E7D32)   // Verde
                                        bmiVal < 30f -> Color(0xFFF57C00)   // Laranja
                                        else -> Color(0xFFD32F2F)           // Vermelho
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Barra colorida em 4 zonas do IMC
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.weight(18.5f).fillMaxSize().background(Color(0xFF64B5F6))) // Abaixo
                                Box(modifier = Modifier.weight(6.5f).fillMaxSize().background(Color(0xFF81C784)))  // Normal (18.5 a 24.9)
                                Box(modifier = Modifier.weight(5f).fillMaxSize().background(Color(0xFFFFB74D)))    // Sobrepeso (25 a 29.9)
                                Box(modifier = Modifier.weight(10f).fillMaxSize().background(Color(0xFFE57373)))   // Obesidade (30+)
                            }
                        }

                        // Marcador animado de posição na barra de IMC
                        val bmiRatio = remember(previewProfile.bmi) {
                            derivedStateOf {
                                val v = previewProfile.bmi
                                if (v <= 15f) 0.05f
                                else if (v >= 40f) 0.95f
                                else ((v - 15f) / 25f).coerceIn(0.05f, 0.95f)
                            }
                        }
                        val animatedBmiOffset by animateFloatAsState(
                            targetValue = bmiRatio.value,
                            animationSpec = spring(),
                            label = "bmi_pointer"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Abaixo", style = MaterialTheme.typography.labelSmall, color = Color(0xFF1976D2))
                            Text("Saudável", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            Text("Sobrepeso", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF57C00))
                            Text("Obesidade", style = MaterialTheme.typography.labelSmall, color = Color(0xFFD32F2F))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Diagnóstico: ${previewProfile.bmiCategory}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 CARDS DE ESTIMATIVAS NUMÉRICAS DINÂMICAS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Card 1: Hidratação
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Água Diária",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                AnimatedContent(
                                    targetState = previewProfile.recommendedWaterMl,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "water_anim"
                                ) { waterMl ->
                                    Text(
                                        text = "$waterMl ml",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = "35ml/kg",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Card 2: Atividade Física
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Exercício",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                AnimatedContent(
                                    targetState = previewProfile.recommendedActivityMinutes,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "act_anim"
                                ) { minutes ->
                                    Text(
                                        text = "$minutes min",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Text(
                                    text = "Meta OMS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Card 3: Calorias Recomendadas
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Gasto Calórico",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                AnimatedContent(
                                    targetState = previewProfile.recommendedCaloriesKcal,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "cal_anim"
                                ) { kcal ->
                                    Text(
                                        text = "$kcal kcal",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                Text(
                                    text = "Mifflin-St Jeor",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // BOTÃO SALVAR INTERATIVO COM ANIMAÇÃO
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Por favor, digite seu nome ou apelido para continuar."
                        return@Button
                    }
                    val age = ageText.toIntOrNull() ?: ageSliderValue.roundToInt()
                    if (age !in 5..120) {
                        errorMessage = "Por favor, selecione uma idade válida (5 a 120 anos)."
                        return@Button
                    }
                    val weight = weightText.replace(',', '.').toFloatOrNull() ?: weightSliderValue
                    if (weight !in 20f..300f) {
                        errorMessage = "Por favor, informe um peso corporal válido (20 a 300 kg)."
                        return@Button
                    }
                    val height = heightText.replace(',', '.').toFloatOrNull() ?: heightSliderValue
                    if (height !in 50f..250f) {
                        errorMessage = "Por favor, informe uma altura válida (50 a 250 cm)."
                        return@Button
                    }

                    val updatedProfile = UserProfile(
                        name = name.trim(),
                        age = age,
                        gender = selectedGender,
                        weightKg = weight,
                        heightCm = height,
                        isRegistered = true,
                        activityLevel = selectedActivityLevel,
                        wellnessGoal = selectedWellnessGoal
                    )
                    onSaveProfile(updatedProfile)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("save_profile_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (currentProfile.isRegistered) "Salvar e Atualizar Metas Vivas" else "Concluir Registro e Iniciar",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
