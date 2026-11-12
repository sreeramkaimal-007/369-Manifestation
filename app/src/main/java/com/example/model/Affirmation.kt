package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "affirmations")
data class Affirmation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val category: String,
    val intentionNote: String = "",
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
