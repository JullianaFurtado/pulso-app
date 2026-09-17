package com.example

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidades do Banco de Dados Room para o aplicativo Pulso.
 * Representam os registros de ingestão de água, atividades físicas e alimentação.
 */

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val activityType: String,
    val durationMinutes: Int,
    val caloriesBurned: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val mealType: String,
    val description: String,
    val isHealthyChoice: Boolean = true,
    val foodName: String = "",
    val weightGrams: Int = 0,
    val calories: Int = 0,
    val proteinGrams: Float = 0f,
    val carbGrams: Float = 0f,
    val fatGrams: Float = 0f,
    val fiberGrams: Float = 0f,
    val servingUnit: String = "g",
    val quantityUnits: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)
