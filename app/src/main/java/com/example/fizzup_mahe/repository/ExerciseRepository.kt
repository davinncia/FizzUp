package com.example.fizzup_mahe.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fizzup_mahe.db.AppDatabase
import com.example.fizzup_mahe.db.ExerciseDao
import com.example.fizzup_mahe.model.Exercise
import com.example.fizzup_mahe.service.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Repository in charge of acquiring the list of exercices from the server or cache if there is no connection available.
 */
class ExerciseRepository private constructor(context: Context) {

    private val url = "https://s3-us-west-1.amazonaws.com/"
    private val retrofit =
        Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build()
    private val service = retrofit.create(ApiService::class.java)

    private val dao: ExerciseDao

    private var _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> = _exercises

    init {
        val db = AppDatabase.getDatabase(context)
        dao = db.exerciseDao()
    }

    suspend fun fetchExercises() {
        var response: List<Exercise>? = null

        try {
            response = service.getAll().execute().body()?.data
        } catch (e: IOException) {
            Log.d("debuglog", e.toString())
        }

        if (!response.isNullOrEmpty()) {
            // Successfully acquired data from server
            withContext(Dispatchers.Main) { _exercises.value = response }
            // Caching response
            dao.clearAll()
            dao.insert(response)
        } else {
            fetchFromLocal()
        }

    }

    private suspend fun fetchFromLocal() {
        withContext(Dispatchers.Main) { _exercises.value = dao.getAll() }
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