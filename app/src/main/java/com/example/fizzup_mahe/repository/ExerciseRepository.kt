package com.example.fizzup_mahe.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.fizzup_mahe.db.AppDatabase
import com.example.fizzup_mahe.db.ExerciseDao
import com.example.fizzup_mahe.model.Exercise
import com.example.fizzup_mahe.service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repository in charge of acquiring the list of exercices from the server or cache if there is no connection available.
 */
class ExerciseRepository private constructor(context: Context) {

    private val url = "https://s3-us-west-1.amazonaws.com/"
    private val retrofit =
        Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build()
    private val service = retrofit.create(ApiService::class.java)

    private val db = AppDatabase.getDatabase(context.applicationContext)
    private val dao = db.exerciseDao()

    val exercises: LiveData<List<Exercise>> = dao.getAll()


    suspend fun fetchExercises() {

        try {
            val response = service.getAll().data
            // Caching response and updating exercises by observation
            dao.insert(response)
        } catch (cause: Throwable) {
            // If anything throws an exception, inform the caller
            throw ServerRequestError("Unable to fetch exercises from server", cause)
        }
    }

    companion object {
        // Singleton pattern
        private var INSTANCE: ExerciseRepository? = null

        fun getInstance(context: Context): ExerciseRepository {
            if (INSTANCE == null) {
                synchronized(ExerciseRepository) {
                    if (INSTANCE == null) {
                        INSTANCE = ExerciseRepository(context)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}

/**
 * Thrown when there was a error fetching exercises from server.
 *
 * @property message user ready error message
 * @property cause the original cause of this exception
 */
class ServerRequestError(message: String, cause: Throwable?) : Throwable(message, cause)