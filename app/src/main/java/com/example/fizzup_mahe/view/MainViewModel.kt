package com.example.fizzup_mahe.view

import androidx.lifecycle.*
import com.example.fizzup_mahe.model.Exercise
import com.example.fizzup_mahe.repository.ExerciseRepository
import com.example.fizzup_mahe.repository.ServerRequestError
import com.example.fizzup_mahe.repository.NetworkRepository
import kotlinx.coroutines.launch

class MainViewModel(
    networkRepo: NetworkRepository,
    exerciseRepo: ExerciseRepository
) : ViewModel() {

    //TODO diff utils adapter
    val exercises = exerciseRepo.exercises

    val dataFromServer: LiveData<Boolean> = Transformations.map(networkRepo.isConnected) { isConnected ->
        // Removing the callback once data has been acquired from server
        if (isConnected) networkRepo.removeCallback()

        viewModelScope.launch {
            try {
                exerciseRepo.fetchExercises()
            } catch (error: ServerRequestError) {
                // TODO UI
            }
        }

        isConnected
    }

}