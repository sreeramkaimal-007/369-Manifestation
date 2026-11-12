package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.DailyLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    fun getLogForDate(date: String): Flow<DailyLog?>

    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    suspend fun getLogForDateSync(date: String): DailyLog?

    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<DailyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: DailyLog)

    @Query("SELECT COUNT(*) FROM daily_logs WHERE morningCount >= 3 AND afternoonCount >= 6 AND eveningCount >= 9")
    fun getCompletedDaysCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_logs")
    fun getTotalLoggedDaysCount(): Flow<Int>
}
