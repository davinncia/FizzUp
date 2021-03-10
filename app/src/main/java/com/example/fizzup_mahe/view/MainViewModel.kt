package com.example.fizzup_mahe.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fizzup_mahe.model.Exercise
import com.example.fizzup_mahe.repository.ExerciseRepository
import com.example.fizzup_mahe.repository.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    networkRepo: NetworkRepository,
    private val exerciseRepo: ExerciseRepository
) : ViewModel() {

    val isNetworkAvailable = networkRepo.isConnected

    private val _exercise = Transformations.switchMap(isNetworkAvailable) { connected ->
        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepo.fetchExercises()
        }
        // Removing the callback once data has been acquired from server
        if (connected) networkRepo.removeCallback()

        exerciseRepo.exercises
    }

    val exercises: LiveData<List<Exercise>> = _exercise//exerciseRepo.exercises

}