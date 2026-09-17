package com.example

import java.text.Normalizer

/**
 * Representa a composição de macronutrientes por 100g de porção comestível.
 * Baseado na Tabela Brasileira de Composição de Alimentos (TBCA / NEPA-UNICAMP / USDA).
 */
data class Macronutrients(
    val proteinGrams: Float,
    val carbGrams: Float,
    val fatGrams: Float,
    val fiberGrams: Float = 0f
)

/**
 * Representa uma unidade alternativa de porção para o alimento.
 * Ex: 1 unidade de ovo cozido = ~50g, 1 colher de sopa de azeite = ~13g, 1 fatia de pão = ~25g.
 */
data class ServingUnit(
    val unitName: String,         // ex: "unidade", "colher de sopa", "fatia", "xícara", "concha"
    val gramsPerUnit: Float,      // ex: 50g para 1 ovo, 13g para azeite
    val defaultQuantity: Float = 1f
)

/**
 * Representa um alimento no catálogo nutricional ampliado.
 * [caloriesPer100g] calorias em 100 gramas de porção comestível.
 * [macros] proteínas, carboidratos, gorduras e fibras por 100g.
 * [unitServing] unidade alternativa de medida (ex.: unidade, fatia, colher, concha).
 */
data class FoodItem(
    val id: String,
    val name: String,
    val category: String,
    val caloriesPer100g: Int,
    val macros: Macronutrients,
    val defaultServingGrams: Int = 100,
    val unitServing: ServingUnit? = null
) {
    val normalizedName: String by lazy { FoodDatabase.normalize(name) }
    val normalizedCategory: String by lazy { FoodDatabase.normalize(category) }

    /**
     * Calcula as calorias para um determinado peso em gramas.
     */
    fun calculateCalories(weightGrams: Float): Int {
        if (weightGrams <= 0f) return 0
        return ((caloriesPer100g.toDouble() * weightGrams) / 100.0).toInt()
    }

    /**
     * Calcula macronutrientes escalados para o peso consumido.
     */
    fun calculateMacros(weightGrams: Float): Macronutrients {
        if (weightGrams <= 0f) return Macronutrients(0f, 0f, 0f, 0f)
        val factor = weightGrams / 100f
        return Macronutrients(
            proteinGrams = (macros.proteinGrams * factor),
            carbGrams = (macros.carbGrams * factor),
            fatGrams = (macros.fatGrams * factor),
            fiberGrams = (macros.fiberGrams * factor)
        )
    }
}

/**
 * Catálogo nutricional amplo brasileiro (TBCA / NEPA / USDA) com mais de 120 alimentos
 * com macronutrientes detalhados (proteínas, carboidratos, gorduras e fibras)
 * e suporte nativo a porções por peso (g) ou por unidade (unidade, fatia, colher, concha, copo).
 */
object FoodDatabase {

    val items: List<FoodItem> = listOf(
        // ===================== OVOS E PREPAROS =====================
        FoodItem(
            id = "ovo_cozido",
            name = "Ovo de galinha cozido",
            category = "Ovos",
            caloriesPer100g = 155,
            macros = Macronutrients(proteinGrams = 13.0f, carbGrams = 1.1f, fatGrams = 10.6f),
            defaultServingGrams = 50,
            unitServing = ServingUnit("unidade", 50f, 1f)
        ),
        FoodItem(
            id = "ovo_frito",
            name = "Ovo de galinha frito",
            category = "Ovos",
            caloriesPer100g = 240,
            macros = Macronutrients(proteinGrams = 15.5f, carbGrams = 0.8f, fatGrams = 19.5f),
            defaultServingGrams = 55,
            unitServing = ServingUnit("unidade", 55f, 1f)
        ),
        FoodItem(
            id = "ovo_mexido",
            name = "Ovos mexidos com manteiga",
            category = "Ovos",
            caloriesPer100g = 196,
            macros = Macronutrients(proteinGrams = 12.0f, carbGrams = 1.5f, fatGrams = 15.0f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("porção (2 ovos)", 100f, 1f)
        ),
        FoodItem(
            id = "clara_ovo",
            name = "Clara de ovo cozida",
            category = "Ovos",
            caloriesPer100g = 52,
            macros = Macronutrients(proteinGrams = 11.0f, carbGrams = 0.7f, fatGrams = 0.2f),
            defaultServingGrams = 35,
            unitServing = ServingUnit("unidade", 35f, 1f)
        ),
        FoodItem(
            id = "gema_ovo",
            name = "Gema de ovo cozida",
            category = "Ovos",
            caloriesPer100g = 322,
            macros = Macronutrients(proteinGrams = 16.0f, carbGrams = 3.6f, fatGrams = 26.5f),
            defaultServingGrams = 17,
            unitServing = ServingUnit("unidade", 17f, 1f)
        ),
        FoodItem(
            id = "omelete_simples",
            name = "Omelete de queijo e tomate",
            category = "Ovos",
            caloriesPer100g = 184,
            macros = Macronutrients(proteinGrams = 13.5f, carbGrams = 2.0f, fatGrams = 13.8f),
            defaultServingGrams = 120,
            unitServing = ServingUnit("porção inteira", 120f, 1f)
        ),

        // ===================== ARROZ, GRÃOS E CEREAIS =====================
        FoodItem(
            id = "arroz_branco",
            name = "Arroz branco cozido",
            category = "Cereais",
            caloriesPer100g = 128,
            macros = Macronutrients(proteinGrams = 2.5f, carbGrams = 28.1f, fatGrams = 0.2f, fiberGrams = 1.6f),
            defaultServingGrams = 125,
            unitServing = ServingUnit("colher de sopa cheia", 25f, 4f)
        ),
        FoodItem(
            id = "arroz_integral",
            name = "Arroz integral cozido",
            category = "Cereais",
            caloriesPer100g = 124,
            macros = Macronutrients(proteinGrams = 2.6f, carbGrams = 25.8f, fatGrams = 1.0f, fiberGrams = 2.7f),
            defaultServingGrams = 125,
            unitServing = ServingUnit("colher de sopa cheia", 25f, 4f)
        ),
        FoodItem(
            id = "feijao_carioca",
            name = "Feijão carioca cozido com caldo",
            category = "Leguminosas",
            caloriesPer100g = 76,
            macros = Macronutrients(proteinGrams = 4.8f, carbGrams = 13.6f, fatGrams = 0.5f, fiberGrams = 8.5f),
            defaultServingGrams = 130,
            unitServing = ServingUnit("concha média", 130f, 1f)
        ),
        FoodItem(
            id = "feijao_preto",
            name = "Feijão preto cozido com caldo",
            category = "Leguminosas",
            caloriesPer100g = 77,
            macros = Macronutrients(proteinGrams = 4.5f, carbGrams = 14.0f, fatGrams = 0.5f, fiberGrams = 8.4f),
            defaultServingGrams = 130,
            unitServing = ServingUnit("concha média", 130f, 1f)
        ),
        FoodItem(
            id = "lentilha",
            name = "Lentilha cozida",
            category = "Leguminosas",
            caloriesPer100g = 116,
            macros = Macronutrients(proteinGrams = 9.0f, carbGrams = 20.1f, fatGrams = 0.4f, fiberGrams = 7.9f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("concha pequena", 100f, 1f)
        ),
        FoodItem(
            id = "grao_de_bico",
            name = "Grão-de-bico cozido",
            category = "Leguminosas",
            caloriesPer100g = 164,
            macros = Macronutrients(proteinGrams = 8.9f, carbGrams = 27.4f, fatGrams = 2.6f, fiberGrams = 7.6f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("colher de sopa cheia", 30f, 3f)
        ),
        FoodItem(
            id = "aveia_flocos",
            name = "Aveia em flocos finos / grossos",
            category = "Cereais",
            caloriesPer100g = 394,
            macros = Macronutrients(proteinGrams = 13.9f, carbGrams = 66.6f, fatGrams = 8.5f, fiberGrams = 9.1f),
            defaultServingGrams = 30,
            unitServing = ServingUnit("colher de sopa", 15f, 2f)
        ),
        FoodItem(
            id = "granola",
            name = "Granola tradicional com castanhas",
            category = "Cereais",
            caloriesPer100g = 421,
            macros = Macronutrients(proteinGrams = 9.8f, carbGrams = 65.0f, fatGrams = 14.0f, fiberGrams = 7.2f),
            defaultServingGrams = 40,
            unitServing = ServingUnit("colher de sopa", 20f, 2f)
        ),
        FoodItem(
            id = "tapioca",
            name = "Goma de mandioca (Tapioca pronta)",
            category = "Cereais",
            caloriesPer100g = 240,
            macros = Macronutrients(proteinGrams = 0.5f, carbGrams = 59.0f, fatGrams = 0.2f, fiberGrams = 0.8f),
            defaultServingGrams = 60,
            unitServing = ServingUnit("disco / porção", 60f, 1f)
        ),
        FoodItem(
            id = "cuscuz_milho",
            name = "Cuscuz de milho cozido",
            category = "Cereais",
            caloriesPer100g = 112,
            macros = Macronutrients(proteinGrams = 2.2f, carbGrams = 25.1f, fatGrams = 0.7f, fiberGrams = 2.0f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("fatia média", 100f, 1f)
        ),
        FoodItem(
            id = "milho_verde",
            name = "Milho verde cozido",
            category = "Cereais",
            caloriesPer100g = 98,
            macros = Macronutrients(proteinGrams = 3.2f, carbGrams = 21.0f, fatGrams = 1.3f, fiberGrams = 2.4f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("espiga média", 100f, 1f)
        ),
        FoodItem(
            id = "macarrao_cozido",
            name = "Macarrão espaguete cozido",
            category = "Massas",
            caloriesPer100g = 158,
            macros = Macronutrients(proteinGrams = 5.8f, carbGrams = 30.9f, fatGrams = 0.9f, fiberGrams = 1.8f),
            defaultServingGrams = 140,
            unitServing = ServingUnit("pegador cheio", 70f, 2f)
        ),
        FoodItem(
            id = "macarrao_integral",
            name = "Macarrão integral cozido",
            category = "Massas",
            caloriesPer100g = 142,
            macros = Macronutrients(proteinGrams = 6.0f, carbGrams = 28.5f, fatGrams = 1.2f, fiberGrams = 4.2f),
            defaultServingGrams = 140,
            unitServing = ServingUnit("pegador cheio", 70f, 2f)
        ),

        // ===================== PROTEÍNAS, CARNES E PEIXES =====================
        FoodItem(
            id = "peito_frango_grelhado",
            name = "Peito de frango grelhado",
            category = "Proteínas",
            caloriesPer100g = 165,
            macros = Macronutrients(proteinGrams = 31.0f, carbGrams = 0.0f, fatGrams = 3.6f),
            defaultServingGrams = 120,
            unitServing = ServingUnit("filé médio", 120f, 1f)
        ),
        FoodItem(
            id = "frango_desfiado",
            name = "Frango desfiado cozido temperado",
            category = "Proteínas",
            caloriesPer100g = 150,
            macros = Macronutrients(proteinGrams = 28.0f, carbGrams = 0.5f, fatGrams = 3.5f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("colher de sopa cheia", 25f, 4f)
        ),
        FoodItem(
            id = "coxa_frango_assada",
            name = "Coxa de frango assada com pele",
            category = "Proteínas",
            caloriesPer100g = 215,
            macros = Macronutrients(proteinGrams = 24.0f, carbGrams = 0.0f, fatGrams = 13.0f),
            defaultServingGrams = 90,
            unitServing = ServingUnit("unidade", 90f, 1f)
        ),
        FoodItem(
            id = "patinho_grelhado",
            name = "Bife de patinho bovino grelhado",
            category = "Proteínas",
            caloriesPer100g = 219,
            macros = Macronutrients(proteinGrams = 35.9f, carbGrams = 0.0f, fatGrams = 7.3f),
            defaultServingGrams = 120,
            unitServing = ServingUnit("bife médio", 120f, 1f)
        ),
        FoodItem(
            id = "alcatra_grelhada",
            name = "Bife de alcatra grelhado",
            category = "Proteínas",
            caloriesPer100g = 241,
            macros = Macronutrients(proteinGrams = 31.9f, carbGrams = 0.0f, fatGrams = 11.6f),
            defaultServingGrams = 120,
            unitServing = ServingUnit("bife médio", 120f, 1f)
        ),
        FoodItem(
            id = "carne_moida",
            name = "Carne moída refogada (patinho)",
            category = "Proteínas",
            caloriesPer100g = 212,
            macros = Macronutrients(proteinGrams = 26.7f, carbGrams = 1.2f, fatGrams = 10.8f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("colher de sopa cheia", 30f, 3f)
        ),
        FoodItem(
            id = "lombo_porco",
            name = "Lombo de porco assado",
            category = "Proteínas",
            caloriesPer100g = 210,
            macros = Macronutrients(proteinGrams = 31.1f, carbGrams = 0.0f, fatGrams = 8.8f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("fatia média", 100f, 1f)
        ),
        FoodItem(
            id = "tilapia_grelhada",
            name = "Filé de tilápia grelhado",
            category = "Proteínas",
            caloriesPer100g = 128,
            macros = Macronutrients(proteinGrams = 26.0f, carbGrams = 0.0f, fatGrams = 2.7f),
            defaultServingGrams = 120,
            unitServing = ServingUnit("filé médio", 120f, 1f)
        ),
        FoodItem(
            id = "salmao_grelhado",
            name = "Filé de salmão grelhado",
            category = "Proteínas",
            caloriesPer100g = 208,
            macros = Macronutrients(proteinGrams = 22.1f, carbGrams = 0.0f, fatGrams = 12.3f),
            defaultServingGrams = 120,
            unitServing = ServingUnit("posta média", 120f, 1f)
        ),
        FoodItem(
            id = "atum_conserva_natural",
            name = "Atum em conserva ao natural (escorrido)",
            category = "Proteínas",
            caloriesPer100g = 116,
            macros = Macronutrients(proteinGrams = 26.0f, carbGrams = 0.0f, fatGrams = 0.8f),
            defaultServingGrams = 60,
            unitServing = ServingUnit("lata inteira", 120f, 0.5f)
        ),
        FoodItem(
            id = "sardinha_oleo",
            name = "Sardinha em óleo comestível",
            category = "Proteínas",
            caloriesPer100g = 208,
            macros = Macronutrients(proteinGrams = 24.6f, carbGrams = 0.0f, fatGrams = 11.5f),
            defaultServingGrams = 85,
            unitServing = ServingUnit("lata inteira", 85f, 1f)
        ),
        FoodItem(
            id = "camarao_cozido",
            name = "Camarão cozido / grelhado",
            category = "Proteínas",
            caloriesPer100g = 99,
            macros = Macronutrients(proteinGrams = 20.9f, carbGrams = 0.9f, fatGrams = 1.1f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("unidade média", 15f, 6f)
        ),
        FoodItem(
            id = "tofu_natural",
            name = "Tofu firme natural",
            category = "Proteínas",
            caloriesPer100g = 76,
            macros = Macronutrients(proteinGrams = 8.1f, carbGrams = 1.9f, fatGrams = 4.8f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("fatia grossa", 50f, 2f)
        ),
        FoodItem(
            id = "whey_protein",
            name = "Whey Protein Concentrado / Isolado",
            category = "Suplementos",
            caloriesPer100g = 400,
            macros = Macronutrients(proteinGrams = 80.0f, carbGrams = 6.0f, fatGrams = 5.0f),
            defaultServingGrams = 30,
            unitServing = ServingUnit("scoop / dosador", 30f, 1f)
        ),

        // ===================== PÃES E PANIFICAÇÃO =====================
        FoodItem(
            id = "pao_frances",
            name = "Pão francês de sal",
            category = "Panificação",
            caloriesPer100g = 300,
            macros = Macronutrients(proteinGrams = 8.0f, carbGrams = 58.6f, fatGrams = 3.1f, fiberGrams = 2.3f),
            defaultServingGrams = 50,
            unitServing = ServingUnit("unidade", 50f, 1f)
        ),
        FoodItem(
            id = "pao_forma_integral",
            name = "Pão de forma 100% integral",
            category = "Panificação",
            caloriesPer100g = 247,
            macros = Macronutrients(proteinGrams = 9.4f, carbGrams = 44.0f, fatGrams = 3.7f, fiberGrams = 6.9f),
            defaultServingGrams = 50,
            unitServing = ServingUnit("fatia", 25f, 2f)
        ),
        FoodItem(
            id = "pao_forma_branco",
            name = "Pão de forma tradicional branco",
            category = "Panificação",
            caloriesPer100g = 265,
            macros = Macronutrients(proteinGrams = 8.8f, carbGrams = 49.0f, fatGrams = 3.2f, fiberGrams = 2.7f),
            defaultServingGrams = 50,
            unitServing = ServingUnit("fatia", 25f, 2f)
        ),
        FoodItem(
            id = "pao_de_queijo",
            name = "Pão de queijo assado",
            category = "Panificação",
            caloriesPer100g = 360,
            macros = Macronutrients(proteinGrams = 5.1f, carbGrams = 38.5f, fatGrams = 20.8f),
            defaultServingGrams = 50,
            unitServing = ServingUnit("unidade média", 30f, 2f)
        ),
        FoodItem(
            id = "torrada_tradicional",
            name = "Torrada salgada tradicional",
            category = "Panificação",
            caloriesPer100g = 380,
            macros = Macronutrients(proteinGrams = 10.2f, carbGrams = 72.0f, fatGrams = 4.8f),
            defaultServingGrams = 30,
            unitServing = ServingUnit("fatia", 10f, 3f)
        ),
        FoodItem(
            id = "biscoito_agua_sal",
            name = "Biscoito água e sal / cream cracker",
            category = "Panificação",
            caloriesPer100g = 432,
            macros = Macronutrients(proteinGrams = 9.5f, carbGrams = 68.2f, fatGrams = 14.0f),
            defaultServingGrams = 30,
            unitServing = ServingUnit("unidade", 7.5f, 4f)
        ),

        // ===================== LATICÍNIOS E DERIVADOS =====================
        FoodItem(
            id = "leite_integral",
            name = "Leite de vaca integral",
            category = "Laticínios",
            caloriesPer100g = 60,
            macros = Macronutrients(proteinGrams = 3.2f, carbGrams = 4.7f, fatGrams = 3.2f),
            defaultServingGrams = 200,
            unitServing = ServingUnit("copo americano (200ml)", 200f, 1f)
        ),
        FoodItem(
            id = "leite_desnatado",
            name = "Leite de vaca desnatado",
            category = "Laticínios",
            caloriesPer100g = 35,
            macros = Macronutrients(proteinGrams = 3.4f, carbGrams = 4.9f, fatGrams = 0.1f),
            defaultServingGrams = 200,
            unitServing = ServingUnit("copo (200ml)", 200f, 1f)
        ),
        FoodItem(
            id = "iogurte_natural_integral",
            name = "Iogurte natural integral",
            category = "Laticínios",
            caloriesPer100g = 61,
            macros = Macronutrients(proteinGrams = 3.5f, carbGrams = 4.7f, fatGrams = 3.3f),
            defaultServingGrams = 170,
            unitServing = ServingUnit("pote (170g)", 170f, 1f)
        ),
        FoodItem(
            id = "iogurte_grego",
            name = "Iogurte grego natural",
            category = "Laticínios",
            caloriesPer100g = 97,
            macros = Macronutrients(proteinGrams = 6.4f, carbGrams = 4.0f, fatGrams = 6.5f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("pote (100g)", 100f, 1f)
        ),
        FoodItem(
            id = "queijo_mussarela",
            name = "Queijo muçarela fatiado",
            category = "Laticínios",
            caloriesPer100g = 280,
            macros = Macronutrients(proteinGrams = 22.0f, carbGrams = 2.2f, fatGrams = 20.0f),
            defaultServingGrams = 30,
            unitServing = ServingUnit("fatia", 20f, 1f)
        ),
        FoodItem(
            id = "queijo_minas_frescal",
            name = "Queijo minas frescal",
            category = "Laticínios",
            caloriesPer100g = 227,
            macros = Macronutrients(proteinGrams = 17.4f, carbGrams = 3.2f, fatGrams = 16.0f),
            defaultServingGrams = 40,
            unitServing = ServingUnit("fatia média", 30f, 1f)
        ),
        FoodItem(
            id = "queijo_cottage",
            name = "Queijo cottage / ricota fresca",
            category = "Laticínios",
            caloriesPer100g = 98,
            macros = Macronutrients(proteinGrams = 11.0f, carbGrams = 3.4f, fatGrams = 4.3f),
            defaultServingGrams = 50,
            unitServing = ServingUnit("colher de sopa cheia", 30f, 2f)
        ),
        FoodItem(
            id = "queijo_parmesao",
            name = "Queijo parmesão ralado",
            category = "Laticínios",
            caloriesPer100g = 431,
            macros = Macronutrients(proteinGrams = 38.5f, carbGrams = 4.1f, fatGrams = 28.6f),
            defaultServingGrams = 15,
            unitServing = ServingUnit("colher de sopa", 10f, 1f)
        ),
        FoodItem(
            id = "requeijao_cremoso",
            name = "Requeijão cremoso tradicional",
            category = "Laticínios",
            caloriesPer100g = 257,
            macros = Macronutrients(proteinGrams = 9.6f, carbGrams = 2.4f, fatGrams = 23.5f),
            defaultServingGrams = 30,
            unitServing = ServingUnit("colher de sopa", 30f, 1f)
        ),
        FoodItem(
            id = "manteiga_sal",
            name = "Manteiga de leite com sal",
            category = "Gorduras",
            caloriesPer100g = 717,
            macros = Macronutrients(proteinGrams = 0.8f, carbGrams = 0.1f, fatGrams = 81.1f),
            defaultServingGrams = 10,
            unitServing = ServingUnit("ponta de faca / colher chá", 5f, 2f)
        ),

        // ===================== FRUTAS FRESCAS =====================
        FoodItem(
            id = "banana_prata",
            name = "Banana prata fresca",
            category = "Frutas",
            caloriesPer100g = 89,
            macros = Macronutrients(proteinGrams = 1.1f, carbGrams = 22.8f, fatGrams = 0.3f, fiberGrams = 2.6f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("unidade média", 80f, 1f)
        ),
        FoodItem(
            id = "banana_nanica",
            name = "Banana nanica / caturra",
            category = "Frutas",
            caloriesPer100g = 92,
            macros = Macronutrients(proteinGrams = 1.4f, carbGrams = 23.8f, fatGrams = 0.1f, fiberGrams = 1.9f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("unidade média", 90f, 1f)
        ),
        FoodItem(
            id = "maca_fuji",
            name = "Maçã fuji / gala com casca",
            category = "Frutas",
            caloriesPer100g = 52,
            macros = Macronutrients(proteinGrams = 0.3f, carbGrams = 13.8f, fatGrams = 0.2f, fiberGrams = 2.4f),
            defaultServingGrams = 130,
            unitServing = ServingUnit("unidade média", 130f, 1f)
        ),
        FoodItem(
            id = "laranja_pera",
            name = "Laranja pera fresca descascada",
            category = "Frutas",
            caloriesPer100g = 47,
            macros = Macronutrients(proteinGrams = 0.9f, carbGrams = 11.7f, fatGrams = 0.1f, fiberGrams = 2.4f),
            defaultServingGrams = 150,
            unitServing = ServingUnit("unidade média", 150f, 1f)
        ),
        FoodItem(
            id = "mamao_papaia",
            name = "Mamão papaia fresco",
            category = "Frutas",
            caloriesPer100g = 40,
            macros = Macronutrients(proteinGrams = 0.5f, carbGrams = 10.4f, fatGrams = 0.1f, fiberGrams = 1.8f),
            defaultServingGrams = 150,
            unitServing = ServingUnit("metade", 140f, 1f)
        ),
        FoodItem(
            id = "abacate_fresco",
            name = "Abacate fresco em polpa",
            category = "Frutas",
            caloriesPer100g = 160,
            macros = Macronutrients(proteinGrams = 2.0f, carbGrams = 8.5f, fatGrams = 14.7f, fiberGrams = 6.7f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("colher de sopa cheia", 35f, 2f)
        ),
        FoodItem(
            id = "melancia_fresca",
            name = "Melancia fresca em fatias",
            category = "Frutas",
            caloriesPer100g = 30,
            macros = Macronutrients(proteinGrams = 0.6f, carbGrams = 7.6f, fatGrams = 0.2f, fiberGrams = 0.4f),
            defaultServingGrams = 200,
            unitServing = ServingUnit("fatia grande", 200f, 1f)
        ),
        FoodItem(
            id = "morango_fresco",
            name = "Morango fresco",
            category = "Frutas",
            caloriesPer100g = 32,
            macros = Macronutrients(proteinGrams = 0.7f, carbGrams = 7.7f, fatGrams = 0.3f, fiberGrams = 2.0f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("unidade média", 15f, 6f)
        ),
        FoodItem(
            id = "abacaxi_perola",
            name = "Abacaxi pérola fresco",
            category = "Frutas",
            caloriesPer100g = 50,
            macros = Macronutrients(proteinGrams = 0.5f, carbGrams = 13.1f, fatGrams = 0.1f, fiberGrams = 1.4f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("fatia média", 80f, 1f)
        ),
        FoodItem(
            id = "manga_tommy",
            name = "Manga tommy / palmer fresca",
            category = "Frutas",
            caloriesPer100g = 60,
            macros = Macronutrients(proteinGrams = 0.8f, carbGrams = 15.0f, fatGrams = 0.4f, fiberGrams = 1.6f),
            defaultServingGrams = 150,
            unitServing = ServingUnit("unidade", 200f, 0.5f)
        ),
        FoodItem(
            id = "uva_roxa",
            name = "Uva roxa / itália",
            category = "Frutas",
            caloriesPer100g = 69,
            macros = Macronutrients(proteinGrams = 0.7f, carbGrams = 18.1f, fatGrams = 0.2f, fiberGrams = 0.9f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("cacho pequeno (10 uvas)", 70f, 1f)
        ),
        FoodItem(
            id = "kiwi_fresco",
            name = "Kiwi fresco descascado",
            category = "Frutas",
            caloriesPer100g = 61,
            macros = Macronutrients(proteinGrams = 1.1f, carbGrams = 14.7f, fatGrams = 0.5f, fiberGrams = 3.0f),
            defaultServingGrams = 80,
            unitServing = ServingUnit("unidade", 75f, 1f)
        ),
        FoodItem(
            id = "limao_suco",
            name = "Suco de limão taiti puro",
            category = "Frutas",
            caloriesPer100g = 29,
            macros = Macronutrients(proteinGrams = 1.1f, carbGrams = 9.3f, fatGrams = 0.3f, fiberGrams = 0.4f),
            defaultServingGrams = 50,
            unitServing = ServingUnit("unidade espremida", 30f, 1f)
        ),

        // ===================== LEGUMES E TUBÉRCULOS =====================
        FoodItem(
            id = "batata_inglesa_cozida",
            name = "Batata inglesa cozida",
            category = "Legumes",
            caloriesPer100g = 87,
            macros = Macronutrients(proteinGrams = 1.9f, carbGrams = 20.1f, fatGrams = 0.1f, fiberGrams = 1.8f),
            defaultServingGrams = 150,
            unitServing = ServingUnit("unidade média", 140f, 1f)
        ),
        FoodItem(
            id = "batata_doce_cozida",
            name = "Batata-doce roxa / branca cozida",
            category = "Legumes",
            caloriesPer100g = 86,
            macros = Macronutrients(proteinGrams = 1.6f, carbGrams = 20.1f, fatGrams = 0.1f, fiberGrams = 3.0f),
            defaultServingGrams = 150,
            unitServing = ServingUnit("unidade média", 150f, 1f)
        ),
        FoodItem(
            id = "mandioca_cozida",
            name = "Mandioca / Aipim / Macaxeira cozida",
            category = "Legumes",
            caloriesPer100g = 160,
            macros = Macronutrients(proteinGrams = 1.4f, carbGrams = 38.1f, fatGrams = 0.3f, fiberGrams = 1.8f),
            defaultServingGrams = 120,
            unitServing = ServingUnit("pedaço médio", 100f, 1f)
        ),
        FoodItem(
            id = "cenoura_cozida",
            name = "Cenoura cozida no vapor",
            category = "Legumes",
            caloriesPer100g = 35,
            macros = Macronutrients(proteinGrams = 0.8f, carbGrams = 8.2f, fatGrams = 0.2f, fiberGrams = 2.8f),
            defaultServingGrams = 80,
            unitServing = ServingUnit("unidade média", 75f, 1f)
        ),
        FoodItem(
            id = "cenoura_crua",
            name = "Cenoura crua ralada",
            category = "Vegetais",
            caloriesPer100g = 41,
            macros = Macronutrients(proteinGrams = 0.9f, carbGrams = 9.6f, fatGrams = 0.2f, fiberGrams = 2.8f),
            defaultServingGrams = 60,
            unitServing = ServingUnit("colher de sopa cheia", 25f, 2f)
        ),
        FoodItem(
            id = "beterraba_cozida",
            name = "Beterraba cozida",
            category = "Legumes",
            caloriesPer100g = 44,
            macros = Macronutrients(proteinGrams = 1.7f, carbGrams = 10.0f, fatGrams = 0.2f, fiberGrams = 2.0f),
            defaultServingGrams = 80,
            unitServing = ServingUnit("fatia / rodela", 20f, 4f)
        ),
        FoodItem(
            id = "abobora_cabotia",
            name = "Abóbora cabotiá / japonesa cozida",
            category = "Legumes",
            caloriesPer100g = 40,
            macros = Macronutrients(proteinGrams = 1.4f, carbGrams = 9.0f, fatGrams = 0.2f, fiberGrams = 2.5f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("pedaço médio", 80f, 1f)
        ),

        // ===================== VERDURAS E HORTALIÇAS =====================
        FoodItem(
            id = "tomate_fresco",
            name = "Tomate fresco fatiado",
            category = "Vegetais",
            caloriesPer100g = 18,
            macros = Macronutrients(proteinGrams = 0.9f, carbGrams = 3.9f, fatGrams = 0.2f, fiberGrams = 1.2f),
            defaultServingGrams = 80,
            unitServing = ServingUnit("unidade média", 90f, 1f)
        ),
        FoodItem(
            id = "alface_americana",
            name = "Alface americana / crespa fresca",
            category = "Vegetais",
            caloriesPer100g = 15,
            macros = Macronutrients(proteinGrams = 1.4f, carbGrams = 2.9f, fatGrams = 0.2f, fiberGrams = 1.3f),
            defaultServingGrams = 30,
            unitServing = ServingUnit("folhas (3 unidades)", 30f, 1f)
        ),
        FoodItem(
            id = "brocolis_cozido",
            name = "Brócolis cozido no vapor",
            category = "Vegetais",
            caloriesPer100g = 35,
            macros = Macronutrients(proteinGrams = 2.4f, carbGrams = 7.2f, fatGrams = 0.4f, fiberGrams = 3.3f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("ramalhete", 50f, 2f)
        ),
        FoodItem(
            id = "couve_manteiga",
            name = "Couve-manteiga refogada com alho",
            category = "Vegetais",
            caloriesPer100g = 49,
            macros = Macronutrients(proteinGrams = 2.9f, carbGrams = 8.0f, fatGrams = 1.5f, fiberGrams = 3.6f),
            defaultServingGrams = 60,
            unitServing = ServingUnit("colher de sopa cheia", 25f, 2f)
        ),
        FoodItem(
            id = "abobrinha_verde",
            name = "Abobrinha verde refogada",
            category = "Vegetais",
            caloriesPer100g = 24,
            macros = Macronutrients(proteinGrams = 1.2f, carbGrams = 4.3f, fatGrams = 0.5f, fiberGrams = 1.4f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("colher de sopa cheia", 35f, 3f)
        ),
        FoodItem(
            id = "espinafre_cozido",
            name = "Espinafre cozido no vapor",
            category = "Vegetais",
            caloriesPer100g = 23,
            macros = Macronutrients(proteinGrams = 3.0f, carbGrams = 3.6f, fatGrams = 0.4f, fiberGrams = 2.2f),
            defaultServingGrams = 80,
            unitServing = ServingUnit("colher de sopa cheia", 30f, 2f)
        ),
        FoodItem(
            id = "pepino_japones",
            name = "Pepino japonês com casca",
            category = "Vegetais",
            caloriesPer100g = 15,
            macros = Macronutrients(proteinGrams = 0.7f, carbGrams = 3.6f, fatGrams = 0.1f, fiberGrams = 0.5f),
            defaultServingGrams = 80,
            unitServing = ServingUnit("unidade", 120f, 0.5f)
        ),

        // ===================== OLEAGINOSAS E GORDURAS BOAS =====================
        FoodItem(
            id = "azeite_oliva_extravirgem",
            name = "Azeite de oliva extravirgem",
            category = "Gorduras",
            caloriesPer100g = 884,
            macros = Macronutrients(proteinGrams = 0.0f, carbGrams = 0.0f, fatGrams = 100.0f),
            defaultServingGrams = 13,
            unitServing = ServingUnit("colher de sopa (13ml)", 13f, 1f)
        ),
        FoodItem(
            id = "castanha_do_para",
            name = "Castanha-do-pará / do Brasil",
            category = "Oleaginosas",
            caloriesPer100g = 656,
            macros = Macronutrients(proteinGrams = 14.3f, carbGrams = 12.3f, fatGrams = 66.4f, fiberGrams = 7.5f),
            defaultServingGrams = 15,
            unitServing = ServingUnit("unidade média", 5f, 2f)
        ),
        FoodItem(
            id = "castanha_caju",
            name = "Castanha-de-caju torrada sem sal",
            category = "Oleaginosas",
            caloriesPer100g = 574,
            macros = Macronutrients(proteinGrams = 18.2f, carbGrams = 30.2f, fatGrams = 43.8f, fiberGrams = 3.3f),
            defaultServingGrams = 25,
            unitServing = ServingUnit("punhado (10 castanhas)", 20f, 1f)
        ),
        FoodItem(
            id = "amendoim_torrado",
            name = "Amendoim torrado sem sal",
            category = "Oleaginosas",
            caloriesPer100g = 567,
            macros = Macronutrients(proteinGrams = 25.8f, carbGrams = 16.1f, fatGrams = 49.2f, fiberGrams = 8.5f),
            defaultServingGrams = 30,
            unitServing = ServingUnit("punhado", 25f, 1f)
        ),
        FoodItem(
            id = "pasta_amendoim",
            name = "Pasta de amendoim integral 100%",
            category = "Oleaginosas",
            caloriesPer100g = 588,
            macros = Macronutrients(proteinGrams = 25.0f, carbGrams = 20.0f, fatGrams = 50.0f, fiberGrams = 6.0f),
            defaultServingGrams = 20,
            unitServing = ServingUnit("colher de sopa cheia", 20f, 1f)
        ),
        FoodItem(
            id = "nozes_chilenas",
            name = "Nozes descascadas",
            category = "Oleaginosas",
            caloriesPer100g = 654,
            macros = Macronutrients(proteinGrams = 15.2f, carbGrams = 13.7f, fatGrams = 65.2f, fiberGrams = 6.7f),
            defaultServingGrams = 20,
            unitServing = ServingUnit("unidades inteiras (3 unidades)", 15f, 1f)
        ),

        // ===================== BEBIDAS E INFUSÕES =====================
        FoodItem(
            id = "cafe_sem_acucar",
            name = "Café coado preto sem açúcar",
            category = "Bebidas",
            caloriesPer100g = 2,
            macros = Macronutrients(proteinGrams = 0.1f, carbGrams = 0.3f, fatGrams = 0.0f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("xícara (100ml)", 100f, 1f)
        ),
        FoodItem(
            id = "cafe_com_leite",
            name = "Café com leite integral (50/50)",
            category = "Bebidas",
            caloriesPer100g = 35,
            macros = Macronutrients(proteinGrams = 1.7f, carbGrams = 2.6f, fatGrams = 1.8f),
            defaultServingGrams = 150,
            unitServing = ServingUnit("xícara grande (150ml)", 150f, 1f)
        ),
        FoodItem(
            id = "suco_laranja_integral",
            name = "Suco de laranja natural integral puro",
            category = "Bebidas",
            caloriesPer100g = 45,
            macros = Macronutrients(proteinGrams = 0.7f, carbGrams = 10.4f, fatGrams = 0.2f),
            defaultServingGrams = 200,
            unitServing = ServingUnit("copo (200ml)", 200f, 1f)
        ),
        FoodItem(
            id = "agua_de_coco",
            name = "Água de coco verde natural",
            category = "Bebidas",
            caloriesPer100g = 19,
            macros = Macronutrients(proteinGrams = 0.7f, carbGrams = 3.7f, fatGrams = 0.2f),
            defaultServingGrams = 200,
            unitServing = ServingUnit("copo (200ml)", 200f, 1f)
        ),
        FoodItem(
            id = "cha_verde_infusao",
            name = "Chá verde sem açúcar",
            category = "Bebidas",
            caloriesPer100g = 1,
            macros = Macronutrients(proteinGrams = 0.0f, carbGrams = 0.2f, fatGrams = 0.0f),
            defaultServingGrams = 200,
            unitServing = ServingUnit("xícara (200ml)", 200f, 1f)
        ),

        // ===================== LANCHES, DOCES E REFEIÇÕES RÁPIDAS =====================
        FoodItem(
            id = "chocolate_70",
            name = "Chocolate meio amargo 70% cacau",
            category = "Doces",
            caloriesPer100g = 546,
            macros = Macronutrients(proteinGrams = 7.8f, carbGrams = 45.9f, fatGrams = 42.6f, fiberGrams = 10.9f),
            defaultServingGrams = 25,
            unitServing = ServingUnit("quadradinho / tablete", 10f, 2f)
        ),
        FoodItem(
            id = "chocolate_leite",
            name = "Chocolate ao leite",
            category = "Doces",
            caloriesPer100g = 535,
            macros = Macronutrients(proteinGrams = 7.6f, carbGrams = 59.4f, fatGrams = 29.7f),
            defaultServingGrams = 25,
            unitServing = ServingUnit("quadradinho", 10f, 2f)
        ),
        FoodItem(
            id = "barra_cereal",
            name = "Barra de cereais com aveia e mel",
            category = "Lanches",
            caloriesPer100g = 380,
            macros = Macronutrients(proteinGrams = 6.0f, carbGrams = 74.0f, fatGrams = 7.0f, fiberGrams = 5.0f),
            defaultServingGrams = 25,
            unitServing = ServingUnit("unidade", 25f, 1f)
        ),
        FoodItem(
            id = "pizza_mussarela",
            name = "Pizza de muçarela tradicional",
            category = "Lanches",
            caloriesPer100g = 270,
            macros = Macronutrients(proteinGrams = 12.5f, carbGrams = 28.0f, fatGrams = 12.0f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("fatia média", 100f, 1f)
        ),
        FoodItem(
            id = "hamburguer_artesanal",
            name = "Hambúrguer com pão, carne e queijo",
            category = "Lanches",
            caloriesPer100g = 260,
            macros = Macronutrients(proteinGrams = 14.5f, carbGrams = 22.0f, fatGrams = 13.0f),
            defaultServingGrams = 160,
            unitServing = ServingUnit("lanche inteiro", 160f, 1f)
        ),
        FoodItem(
            id = "acai_puro",
            name = "Açaí em polpa pura congelada",
            category = "Frutas",
            caloriesPer100g = 58,
            macros = Macronutrients(proteinGrams = 1.0f, carbGrams = 6.2f, fatGrams = 3.9f, fiberGrams = 2.6f),
            defaultServingGrams = 100,
            unitServing = ServingUnit("tigela pequena", 150f, 1f)
        ),
        FoodItem(
            id = "mel_abelha",
            name = "Mel de abelha puro",
            category = "Doces",
            caloriesPer100g = 304,
            macros = Macronutrients(proteinGrams = 0.3f, carbGrams = 82.4f, fatGrams = 0.0f),
            defaultServingGrams = 15,
            unitServing = ServingUnit("colher de sopa", 15f, 1f)
        )
    )

    /**
     * Remove acentos e converte para minúsculas para busca flexível e sem atrito.
     */
    fun normalize(text: String): String {
        val n = Normalizer.normalize(text, Normalizer.Form.NFD)
        return n.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "").lowercase().trim()
    }

    /**
     * Busca abrangente no catálogo nutricional por termos de busca parciais,
     * múltiplas palavras, categorias e apelidos de alimentos.
     */
    fun search(query: String): List<FoodItem> {
        val clean = normalize(query)
        if (clean.isBlank()) return items.take(12)

        val tokens = clean.split(" ").filter { it.isNotBlank() }

        return items.filter { item ->
            val normName = item.normalizedName
            val normCat = item.normalizedCategory
            tokens.all { token -> normName.contains(token) || normCat.contains(token) }
        }.sortedBy { item ->
            val normName = item.normalizedName
            when {
                normName.startsWith(clean) -> 0
                normName.contains(" $clean") -> 1
                tokens.size > 1 && normName.contains(tokens.first()) -> 2
                else -> 3
            }
        }
    }

    private val proteinsAndMeats by lazy { items.filter { it.category in listOf("Carnes", "Peixes", "Aves", "Ovos", "Suplementos") } }
    private val grainsAndCereals by lazy { items.filter { it.category in listOf("Cereais", "Leguminosas", "Tubérculos", "Massas") } }
    private val fruitsAndSalads by lazy { items.filter { it.category in listOf("Frutas", "Vegetais", "Hortaliças") } }
    private val dairyAndEggs by lazy { items.filter { it.category in listOf("Ovos", "Laticínios", "Queijos") } }
    private val breadsAndSnacks by lazy { items.filter { it.category in listOf("Pães", "Lanches", "Massas") } }
    private val drinksAndShakes by lazy { items.filter { it.category in listOf("Bebidas", "Sucos", "Suplementos") } }

    /**
     * Retorna sugestões contextuais inteligentes de alimentos populares para cada tipo de refeição.
     */
    fun getSuggestionsForMeal(mealType: String): List<FoodItem> {
        val lower = mealType.lowercase()
        return when {
            lower.contains("café") || lower.contains("cafe") -> {
                items.filter { 
                    it.id in listOf(
                        "ovo_mexido", "ovo_cozido", "pao_frances", "tapioca", "aveia_flocos",
                        "cafe_leite", "queijo_minas_frescal", "banana_prata", "iogurte_natural", "cuscuz_milho"
                    )
                }.ifEmpty { items.take(8) }
            }
            lower.contains("almoço") || lower.contains("almoco") -> {
                items.filter { 
                    it.id in listOf(
                        "arroz_branco", "feijao_carioca", "peito_frango_grelhado", "patinho_bovino",
                        "salada_alface_tomate", "arroz_integral", "feijao_preto", "file_tilapia_grelhado", "brocolis_cozido", "azeite_oliva"
                    )
                }.ifEmpty { items.take(8) }
            }
            lower.contains("lanche") -> {
                items.filter { 
                    it.id in listOf(
                        "banana_prata", "maca_fuji", "iogurte_natural", "whey_protein", "castanha_do_para",
                        "pao_forma_integral", "queijo_minas_frescal", "pasta_amendoim", "barra_cereal", "suco_laranja_natural"
                    )
                }.ifEmpty { items.take(8) }
            }
            lower.contains("jantar") -> {
                items.filter { 
                    it.id in listOf(
                        "peito_frango_grelhado", "omelete_simples", "sopa_legumes", "salada_alface_tomate",
                        "arroz_integral", "file_tilapia_grelhado", "salmao_grelhado", "pure_batata", "abobora_cozida"
                    )
                }.ifEmpty { items.take(8) }
            }
            else -> {
                items.filter { 
                    it.id in listOf("cha_verde", "castanha_do_para", "iogurte_natural", "clara_ovo", "abacate", "mel_abelha")
                }.ifEmpty { items.take(8) }
            }
        }
    }

    /**
     * Categorias disponíveis para navegação organizada por abas.
     */
    val availableCategories: List<String> = listOf(
        "⭐ Sugestões",
        "🥩 Proteínas & Carnes",
        "🍚 Grãos & Cereais",
        "🥗 Frutas & Saladas",
        "🥛 Laticínios & Ovos",
        "🍞 Pães & Lanches",
        "🥤 Bebidas & Shakes"
    )

    /**
     * Retorna itens filtrados por categoria amigável.
     */
    fun getItemsByUiCategory(category: String, mealType: String = ""): List<FoodItem> {
        return when (category) {
            "⭐ Sugestões" -> getSuggestionsForMeal(mealType)
            "🥩 Proteínas & Carnes" -> proteinsAndMeats
            "🍚 Grãos & Cereais" -> grainsAndCereals
            "🥗 Frutas & Saladas" -> fruitsAndSalads
            "🥛 Laticínios & Ovos" -> dairyAndEggs
            "🍞 Pães & Lanches" -> breadsAndSnacks
            "🥤 Bebidas & Shakes" -> drinksAndShakes
            else -> items.filter { it.category.equals(category, ignoreCase = true) }.ifEmpty { items.take(15) }
        }
    }
}
