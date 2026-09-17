package com.example

/**
 * Modelo de dados para o Perfil do Usuário no aplicativo Pulso.
 * Armazena nome, idade, sexo, peso e altura, além de fornecer métodos
 * para cálculo científico de estimativas recomendadas de hidratação,
 * atividade física e consumo calórico diário.
 */
data class UserProfile(
    val name: String = "",
    val age: Int = 0,
    val gender: String = "Masculino", // "Masculino", "Feminino", "Outro"
    val weightKg: Float = 0f,
    val heightCm: Float = 0f,
    val isRegistered: Boolean = false,
    val activityLevel: String = "Moderado", // "Sedentário", "Leve", "Moderado", "Intenso"
    val wellnessGoal: String = "Equilíbrio & Longevidade" // "Equilíbrio & Longevidade", "Mais Hidratação", "Perda de Gordura", "Ganho de Força"
) {
    /**
     * 1. Estimativa de Hidratação Diária Recomendada (em ml):
     * Baseada no padrão da Organização Mundial da Saúde (OMS) e medicina preventiva:
     * - Média: 35 ml de água por kg de peso corporal.
     * - Ajustado para faixa etária e objetivo de hidratação.
     */
    val recommendedWaterMl: Int
        get() {
            if (weightKg <= 0f) return 2000
            val base = when {
                age in 1..17 -> (weightKg * 40f).toInt().coerceAtMost(2200)
                age >= 60 -> (weightKg * 30f).toInt()
                else -> (weightKg * 35f).toInt()
            }
            val bonusGoal = if (wellnessGoal == "Mais Hidratação") 500 else 0
            val bonusActivity = when (activityLevel) {
                "Intenso" -> 400
                "Moderado" -> 200
                else -> 0
            }
            return (base + bonusGoal + bonusActivity).coerceIn(1200, 4800)
        }

    /**
     * 2. Estimativa de Atividade Física Diária Recomendada (em minutos):
     * Baseada nas Diretrizes de Atividade Física da OMS combinadas com a rotina do usuário:
     */
    val recommendedActivityMinutes: Int
        get() {
            val base = when {
                age in 1..17 -> 60
                age >= 65 -> 30
                else -> 40
            }
            val levelBonus = when (activityLevel) {
                "Intenso" -> 20
                "Moderado" -> 10
                "Leve" -> 0
                else -> -10
            }
            return (base + levelBonus).coerceIn(20, 90)
        }

    /**
     * 3. Estimativa de Consumo de Calorias Recomendado (em kcal/dia):
     * Calculada pela Equação de Mifflin-St Jeor (TMB) multiplicada pelo fator de rotina diária (TDEE):
     */
    val basalMetabolicRate: Int
        get() {
            if (weightKg <= 0f || heightCm <= 0f || age <= 0) return 1600
            val bmr = when (gender) {
                "Feminino" -> (10f * weightKg) + (6.25f * heightCm) - (5f * age) - 161f
                "Masculino" -> (10f * weightKg) + (6.25f * heightCm) - (5f * age) + 5f
                else -> (10f * weightKg) + (6.25f * heightCm) - (5f * age) - 78f
            }
            return bmr.toInt().coerceIn(1000, 3500)
        }

    val recommendedCaloriesKcal: Int
        get() {
            val bmr = basalMetabolicRate
            val activityMultiplier = when (activityLevel) {
                "Sedentário" -> 1.20f
                "Leve" -> 1.37f
                "Moderado" -> 1.55f
                "Intenso" -> 1.72f
                else -> 1.40f
            }
            val tdee = (bmr * activityMultiplier).toInt()
            val goalAdjustment = when (wellnessGoal) {
                "Perda de Gordura" -> -350
                "Ganho de Força" -> +300
                else -> 0
            }
            return (tdee + goalAdjustment).coerceIn(1200, 4500)
        }

    /**
     * Metas Nutricionais Diárias Recomendadas:
     * Distribuição equilibrada de macronutrientes conforme o objetivo de saúde.
     */
    val recommendedProteinGrams: Int
        get() {
            val calories = recommendedCaloriesKcal
            val ratio = when (wellnessGoal) {
                "Ganho de Força" -> 0.30f
                "Perda de Gordura" -> 0.30f
                else -> 0.25f
            }
            return ((calories * ratio) / 4f).toInt().coerceIn(45, 250)
        }

    val recommendedCarbGrams: Int
        get() {
            val calories = recommendedCaloriesKcal
            val ratio = when (wellnessGoal) {
                "Perda de Gordura" -> 0.40f
                "Ganho de Força" -> 0.45f
                else -> 0.50f
            }
            return ((calories * ratio) / 4f).toInt().coerceIn(100, 450)
        }

    val recommendedFatGrams: Int
        get() {
            val calories = recommendedCaloriesKcal
            val ratio = when (wellnessGoal) {
                "Perda de Gordura" -> 0.30f
                else -> 0.25f
            }
            return ((calories * ratio) / 9f).toInt().coerceIn(30, 120)
        }

    val recommendedFiberGrams: Int
        get() = when {
            gender == "Feminino" -> 25
            gender == "Masculino" -> 35
            else -> 28
        }

    /**
     * Índice de Massa Corporal (IMC) e classificação correspondente.
     */
    val bmi: Float
        get() {
            if (heightCm <= 0f || weightKg <= 0f) return 0f
            val heightM = heightCm / 100f
            return (weightKg / (heightM * heightM))
        }

    val bmiCategory: String
        get() {
            val v = bmi
            return when {
                v <= 0f -> "Não informado"
                v < 18.5f -> "Abaixo do peso"
                v < 25f -> "Peso saudável"
                v < 30f -> "Sobrepeso"
                v < 35f -> "Obesidade Grau I"
                else -> "Obesidade Grau II/III"
            }
        }
}
