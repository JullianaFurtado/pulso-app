package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Tipos de métricas para foco interativo no gráfico de anéis concêntricos.
 */
enum class RingMetricType(val title: String) {
    ALL("Visão Geral"),
    CONSUMED("Alimentação"),
    BURNED("Queima"),
    WATER("Hidratação")
}

/**
 * Gráfico visualmente circular concêntrico (Anéis de Metas Diárias) integrando:
 * 1. Anel Externo: Calorias Consumidas (Alimentação)
 * 2. Anel Intermediário: Calorias Queimadas (Treinos/Exercícios)
 * 3. Anel Interno: Água Consumida (Hidratação)
 *
 * Suporta foco interativo por toque e gradientes vibrantes modernos.
 */
@Composable
fun TriMetricCircularChart(
    caloriesConsumed: Int,
    calorieGoal: Int,
    caloriesBurned: Int,
    calorieBurnGoal: Int = 400,
    waterConsumedMl: Int,
    waterGoalMl: Int = 2000,
    selectedMetric: RingMetricType = RingMetricType.ALL,
    onSelectMetric: ((RingMetricType) -> Unit)? = null,
    modifier: Modifier = Modifier,
    chartSize: Dp = 160.dp,
    strokeWidth: Dp = 10.dp,
    spacing: Dp = 5.dp
) {
    val consumedFraction = if (calorieGoal > 0) (caloriesConsumed.toFloat() / calorieGoal.toFloat()).coerceAtLeast(0f) else 0f
    val burnedFraction = if (calorieBurnGoal > 0) (caloriesBurned.toFloat() / calorieBurnGoal.toFloat()).coerceAtLeast(0f) else 0f
    val waterFraction = if (waterGoalMl > 0) (waterConsumedMl.toFloat() / waterGoalMl.toFloat()).coerceAtLeast(0f) else 0f

    val animConsumed by animateFloatAsState(
        targetValue = consumedFraction,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "animConsumed"
    )
    val animBurned by animateFloatAsState(
        targetValue = burnedFraction,
        animationSpec = tween(durationMillis = 900, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "animBurned"
    )
    val animWater by animateFloatAsState(
        targetValue = waterFraction,
        animationSpec = tween(durationMillis = 900, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "animWater"
    )

    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }
    val spacingPx = with(LocalDensity.current) { spacing.toPx() }

    val normalStroke = remember(strokePx) {
        Stroke(width = strokePx, cap = StrokeCap.Round)
    }
    val emphasizedStroke = remember(strokePx) {
        Stroke(width = strokePx * 1.25f, cap = StrokeCap.Round)
    }

    // Cores vibrantes com gradientes modernos
    val consumedStart = Color(0xFFFF8A00)
    val consumedEnd = Color(0xFFE11D48)
    val consumedTrack = Color(0xFFFF8A00).copy(alpha = if (selectedMetric == RingMetricType.CONSUMED || selectedMetric == RingMetricType.ALL) 0.18f else 0.08f)

    val burnedStart = Color(0xFFF43F5E)
    val burnedEnd = Color(0xFF9333EA)
    val burnedTrack = Color(0xFFF43F5E).copy(alpha = if (selectedMetric == RingMetricType.BURNED || selectedMetric == RingMetricType.ALL) 0.18f else 0.08f)

    val waterStart = Color(0xFF00C6FF)
    val waterEnd = Color(0xFF0072FF)
    val waterTrack = Color(0xFF0072FF).copy(alpha = if (selectedMetric == RingMetricType.WATER || selectedMetric == RingMetricType.ALL) 0.18f else 0.08f)

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(chartSize)
            .testTag("tri_metric_circular_chart")
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                // Ao tocar no gráfico, cicla interativamente entre as métricas
                val nextMetric = when (selectedMetric) {
                    RingMetricType.ALL -> RingMetricType.CONSUMED
                    RingMetricType.CONSUMED -> RingMetricType.BURNED
                    RingMetricType.BURNED -> RingMetricType.WATER
                    RingMetricType.WATER -> RingMetricType.ALL
                }
                onSelectMetric?.invoke(nextMetric)
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(chartSize)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (size.minDimension / 2f) - (strokePx / 2f)

            // 1. Anel Externo (Calorias Consumidas)
            val isConsumedFocused = selectedMetric == RingMetricType.CONSUMED
            val isConsumedActive = selectedMetric == RingMetricType.ALL || isConsumedFocused
            val r1 = maxRadius
            val size1 = Size(r1 * 2, r1 * 2)
            val topLeft1 = Offset(center.x - r1, center.y - r1)

            drawArc(
                color = consumedTrack,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft1,
                size = size1,
                style = if (isConsumedFocused) emphasizedStroke else normalStroke
            )
            if (animConsumed > 0f) {
                val brush1 = Brush.sweepGradient(
                    0.0f to consumedStart,
                    0.5f to consumedEnd,
                    1.0f to consumedStart,
                    center = center
                )
                drawArc(
                    brush = brush1,
                    startAngle = -90f,
                    sweepAngle = (animConsumed * 360f).coerceAtMost(360f),
                    useCenter = false,
                    topLeft = topLeft1,
                    size = size1,
                    style = if (isConsumedFocused) emphasizedStroke else normalStroke,
                    alpha = if (isConsumedActive) 1f else 0.4f
                )
            }

            // 2. Anel Médio (Calorias Queimadas)
            val isBurnedFocused = selectedMetric == RingMetricType.BURNED
            val isBurnedActive = selectedMetric == RingMetricType.ALL || isBurnedFocused
            val r2 = r1 - strokePx - spacingPx
            if (r2 > 0) {
                val size2 = Size(r2 * 2, r2 * 2)
                val topLeft2 = Offset(center.x - r2, center.y - r2)

                drawArc(
                    color = burnedTrack,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft2,
                    size = size2,
                    style = if (isBurnedFocused) emphasizedStroke else normalStroke
                )
                if (animBurned > 0f) {
                    val brush2 = Brush.sweepGradient(
                        0.0f to burnedStart,
                        0.5f to burnedEnd,
                        1.0f to burnedStart,
                        center = center
                    )
                    drawArc(
                        brush = brush2,
                        startAngle = -90f,
                        sweepAngle = (animBurned * 360f).coerceAtMost(360f),
                        useCenter = false,
                        topLeft = topLeft2,
                        size = size2,
                        style = if (isBurnedFocused) emphasizedStroke else normalStroke,
                        alpha = if (isBurnedActive) 1f else 0.4f
                    )
                }
            }

            // 3. Anel Interno (Água Consumida)
            val isWaterFocused = selectedMetric == RingMetricType.WATER
            val isWaterActive = selectedMetric == RingMetricType.ALL || isWaterFocused
            val r3 = r1 - (strokePx + spacingPx) * 2
            if (r3 > 0) {
                val size3 = Size(r3 * 2, r3 * 2)
                val topLeft3 = Offset(center.x - r3, center.y - r3)

                drawArc(
                    color = waterTrack,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft3,
                    size = size3,
                    style = if (isWaterFocused) emphasizedStroke else normalStroke
                )
                if (animWater > 0f) {
                    val brush3 = Brush.sweepGradient(
                        0.0f to waterStart,
                        0.5f to waterEnd,
                        1.0f to waterStart,
                        center = center
                    )
                    drawArc(
                        brush = brush3,
                        startAngle = -90f,
                        sweepAngle = (animWater * 360f).coerceAtMost(360f),
                        useCenter = false,
                        topLeft = topLeft3,
                        size = size3,
                        style = if (isWaterFocused) emphasizedStroke else normalStroke,
                        alpha = if (isWaterActive) 1f else 0.4f
                    )
                }
            }
        }

        // Centro Interativo com Animação Fluida de Conteúdo
        AnimatedContent(
            targetState = selectedMetric,
            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(200)) },
            label = "centerMetricsTransition"
        ) { metric ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                when (metric) {
                    RingMetricType.ALL -> {
                        val netCalories = (caloriesConsumed - caloriesBurned).coerceAtLeast(0)
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = consumedStart,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "$netCalories",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "kcal líq.",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    RingMetricType.CONSUMED -> {
                        val percent = if (calorieGoal > 0) (caloriesConsumed * 100 / calorieGoal) else 0
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = consumedStart,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$caloriesConsumed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = consumedStart
                        )
                        Text(
                            text = "$percent% da meta",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    RingMetricType.BURNED -> {
                        val percent = if (calorieBurnGoal > 0) (caloriesBurned * 100 / calorieBurnGoal) else 0
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = burnedStart,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$caloriesBurned",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = burnedStart
                        )
                        Text(
                            text = "$percent% queima",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    RingMetricType.WATER -> {
                        val percent = if (waterGoalMl > 0) (waterConsumedMl * 100 / waterGoalMl) else 0
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = waterStart,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$waterConsumedMl",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = waterStart
                        )
                        Text(
                            text = "$percent% água",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card interativo completo com o gráfico circular, abas de seleção e ações rápidas integradas.
 */
@Composable
fun TriMetricCircularCard(
    caloriesConsumed: Int,
    calorieGoal: Int,
    caloriesBurned: Int,
    calorieBurnGoal: Int = 400,
    waterConsumedMl: Int,
    waterGoalMl: Int = 2000,
    onQuickAddWater: ((Int) -> Unit)? = null,
    onOpenAddMeal: (() -> Unit)? = null,
    onOpenAddActivity: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedMetric by remember { mutableStateOf(RingMetricType.ALL) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tri_metric_circular_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Cabeçalho com indicador de toque interativo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Anéis de Metas do Dia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Toque nos anéis ou nas métricas para focar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Botão de Reset/Visão Geral
                if (selectedMetric != RingMetricType.ALL) {
                    Surface(
                        onClick = { selectedMetric = RingMetricType.ALL },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "Geral",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gráfico Circular Concêntrico + Legendas Interativas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Gráfico Interativo
                TriMetricCircularChart(
                    caloriesConsumed = caloriesConsumed,
                    calorieGoal = calorieGoal,
                    caloriesBurned = caloriesBurned,
                    calorieBurnGoal = calorieBurnGoal,
                    waterConsumedMl = waterConsumedMl,
                    waterGoalMl = waterGoalMl,
                    selectedMetric = selectedMetric,
                    onSelectMetric = { newMetric -> selectedMetric = newMetric },
                    chartSize = 152.dp,
                    strokeWidth = 10.dp,
                    spacing = 5.dp
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Legenda com Chips Clicáveis
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Métrica 1: Calorias Consumidas
                    InteractiveMetricLegendItem(
                        icon = Icons.Default.Restaurant,
                        label = "Consumo",
                        current = "$caloriesConsumed",
                        target = "$calorieGoal kcal",
                        progressFraction = if (calorieGoal > 0) (caloriesConsumed.toFloat() / calorieGoal).coerceIn(0f, 1f) else 0f,
                        color = Color(0xFFFF8A00),
                        isSelected = selectedMetric == RingMetricType.CONSUMED,
                        onClick = {
                            selectedMetric = if (selectedMetric == RingMetricType.CONSUMED) RingMetricType.ALL else RingMetricType.CONSUMED
                        }
                    )

                    // Métrica 2: Calorias Queimadas
                    InteractiveMetricLegendItem(
                        icon = Icons.Default.Bolt,
                        label = "Queima",
                        current = "$caloriesBurned",
                        target = "$calorieBurnGoal kcal",
                        progressFraction = if (calorieBurnGoal > 0) (caloriesBurned.toFloat() / calorieBurnGoal).coerceIn(0f, 1f) else 0f,
                        color = Color(0xFFF43F5E),
                        isSelected = selectedMetric == RingMetricType.BURNED,
                        onClick = {
                            selectedMetric = if (selectedMetric == RingMetricType.BURNED) RingMetricType.ALL else RingMetricType.BURNED
                        }
                    )

                    // Métrica 3: Água Consumida
                    InteractiveMetricLegendItem(
                        icon = Icons.Default.WaterDrop,
                        label = "Água",
                        current = "$waterConsumedMl",
                        target = "$waterGoalMl ml",
                        progressFraction = if (waterGoalMl > 0) (waterConsumedMl.toFloat() / waterGoalMl).coerceIn(0f, 1f) else 0f,
                        color = Color(0xFF0072FF),
                        isSelected = selectedMetric == RingMetricType.WATER,
                        onClick = {
                            selectedMetric = if (selectedMetric == RingMetricType.WATER) RingMetricType.ALL else RingMetricType.WATER
                        }
                    )
                }
            }

            // Ações Rápidas Contextuais com Animação Fluida
            AnimatedVisibility(
                visible = selectedMetric != RingMetricType.ALL,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(150))
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    when (selectedMetric) {
                        RingMetricType.WATER -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Água rápida:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FilledTonalButton(
                                    onClick = { onQuickAddWater?.invoke(200) },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("+200ml", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                FilledTonalButton(
                                    onClick = { onQuickAddWater?.invoke(350) },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("+350ml", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                FilledTonalButton(
                                    onClick = { onQuickAddWater?.invoke(500) },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("+500ml", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        RingMetricType.CONSUMED -> {
                            FilledTonalButton(
                                onClick = { onOpenAddMeal?.invoke() },
                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFFFF8A00).copy(alpha = 0.15f),
                                    contentColor = Color(0xFFFF8A00)
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Registrar Alimento / Refeição", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                        RingMetricType.BURNED -> {
                            FilledTonalButton(
                                onClick = { onOpenAddActivity?.invoke() },
                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFFF43F5E).copy(alpha = 0.15f),
                                    contentColor = Color(0xFFF43F5E)
                                )
                            ) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Registrar Treino / Exercício", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveMetricLegendItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    current: String,
    target: String,
    progressFraction: Float,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val percent = (progressFraction * 100).toInt()
    val bgModifier = if (isSelected) {
        Modifier
            .border(1.5.dp, color, RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
    } else {
        Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(bgModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = if (isSelected) 0.3f else 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            }
            Text(
                text = "$current / $target",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
