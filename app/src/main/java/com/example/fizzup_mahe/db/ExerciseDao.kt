package com.example.fizzup_mahe.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fizzup_mahe.model.Exercise

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercise_table")
    suspend fun getAll(): List<Exercise>


    @Insert
    fun insert(exercises: List<Exercise>)

    @Query("DELETE FROM exercise_table")
    suspend fun clearAll()
}