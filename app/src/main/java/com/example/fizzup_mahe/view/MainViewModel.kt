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
    private val _dataFromServer = MutableLiveData<Boolean>()
    val dataFromServer: LiveData<Boolean> = _dataFromServer

    private val _exercise = Transformations.switchMap(networkRepo.isConnected) { connected ->
        // Removing the callback once data has been acquired from server
        if (connected) networkRepo.removeCallback()

        viewModelScope.launch {
            try {
                exerciseRepo.fetchExercises()
                _dataFromServer.value = true
            } catch (error: ServerRequestError) {
                _dataFromServer.value = false
            }
        }

        exerciseRepo.exercises
    }
    val exercises: LiveData<List<Exercise>> = _exercise
}