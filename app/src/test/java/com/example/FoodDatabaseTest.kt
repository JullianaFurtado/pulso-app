package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FoodDatabaseTest {

    @Test
    fun testEggSearchAndNutrients() {
        val resultsFrito = FoodDatabase.search("ovo frito")
        assertTrue("Deve encontrar ovo frito", resultsFrito.isNotEmpty())
        val ovoFrito = resultsFrito.first()
        assertTrue(ovoFrito.name.contains("frito", ignoreCase = true))
        assertNotNull(ovoFrito.unitServing)
        assertEquals("unidade", ovoFrito.unitServing?.unitName)
        assertEquals(55f, ovoFrito.unitServing?.gramsPerUnit ?: 0f, 0.1f)

        // 2 ovos fritos = 110g
        val caloriesFor2 = ovoFrito.calculateCalories(110f)
        assertTrue("Calorias de 2 ovos fritos deve ser por volta de 264", caloriesFor2 in 260..270)

        val macros = ovoFrito.calculateMacros(110f)
        assertTrue("Proteína deve ser aproximadamente 17g", macros.proteinGrams in 16f..18f)
    }

    @Test
    fun testOvoCozido() {
        val resultsCozido = FoodDatabase.search("ovo cozido")
        assertTrue("Deve encontrar ovo cozido", resultsCozido.isNotEmpty())
        val ovoCozido = resultsCozido.first()
        assertTrue(ovoCozido.name.contains("cozido", ignoreCase = true))
        assertEquals("unidade", ovoCozido.unitServing?.unitName)
    }

    @Test
    fun testSearchMultipleFoods() {
        val rice = FoodDatabase.search("arroz integral")
        assertTrue(rice.isNotEmpty())

        val chicken = FoodDatabase.search("frango grelhado")
        assertTrue(chicken.isNotEmpty())

        val banana = FoodDatabase.search("banana prata")
        assertTrue(banana.isNotEmpty())
    }
}
