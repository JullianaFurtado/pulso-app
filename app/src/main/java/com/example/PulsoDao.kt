package com.example

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para o aplicativo Pulso.
 * Define métodos de inserção, remoção, somatórios agregados e consultas reativas via Flow.
 */
@Dao
interface PulsoDao {

    // --- Inserções ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(waterLog: WaterLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(activityLog: ActivityLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(mealLog: MealLog): Long

    // --- Remoções ---
    @Delete
    suspend fun deleteWaterLog(waterLog: WaterLog)

    @Delete
    suspend fun deleteActivityLog(activityLog: ActivityLog)

    @Delete
    suspend fun deleteMealLog(mealLog: MealLog)

    // --- Consultas de Água ---
    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllWaterLogs(): Flow<List<WaterLog>>

    @Query("SELECT * FROM water_logs WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getWaterLogsSince(sinceTimestamp: Long): Flow<List<WaterLog>>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM water_logs WHERE timestamp >= :sinceTimestamp")
    fun getTotalWaterSince(sinceTimestamp: Long): Flow<Int>

    // --- Consultas de Atividades Físicas ---
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllActivityLogs(): Flow<List<ActivityLog>>

    @Query("SELECT * FROM activity_logs WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getActivityLogsSince(sinceTimestamp: Long): Flow<List<ActivityLog>>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM activity_logs WHERE timestamp >= :sinceTimestamp")
    fun getTotalActivityMinutesSince(sinceTimestamp: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(caloriesBurned), 0) FROM activity_logs WHERE timestamp >= :sinceTimestamp")
    fun getTotalCaloriesSince(sinceTimestamp: Long): Flow<Int>

    // --- Consultas de Refeições ---
    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMealLogs(): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getMealLogsSince(sinceTimestamp: Long): Flow<List<MealLog>>

    @Query("SELECT COUNT(*) FROM meal_logs WHERE timestamp >= :sinceTimestamp")
    fun getTotalMealsSince(sinceTimestamp: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(calories), 0) FROM meal_logs WHERE timestamp >= :sinceTimestamp")
    fun getTotalMealCaloriesSince(sinceTimestamp: Long): Flow<Int>
}
