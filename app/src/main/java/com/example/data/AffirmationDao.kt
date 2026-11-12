package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.model.Affirmation
import kotlinx.coroutines.flow.Flow

@Dao
interface AffirmationDao {
    @Query("SELECT * FROM affirmations ORDER BY id DESC")
    fun getAllAffirmations(): Flow<List<Affirmation>>

    @Query("SELECT * FROM affirmations WHERE isActive = 1 LIMIT 1")
    fun getActiveAffirmation(): Flow<Affirmation?>

    @Query("SELECT * FROM affirmations WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveAffirmationSync(): Affirmation?

    @Query("SELECT COUNT(*) FROM affirmations")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAffirmation(affirmation: Affirmation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(affirmations: List<Affirmation>)

    @Update
    suspend fun updateAffirmation(affirmation: Affirmation)

    @Delete
    suspend fun deleteAffirmation(affirmation: Affirmation)

    @Query("UPDATE affirmations SET isActive = 0")
    suspend fun clearAllActive()

    @Transaction
    suspend fun setActiveAffirmation(id: Long) {
        clearAllActive()
        setActiveById(id)
    }

    @Query("UPDATE affirmations SET isActive = 1 WHERE id = :id")
    suspend fun setActiveById(id: Long)
}
