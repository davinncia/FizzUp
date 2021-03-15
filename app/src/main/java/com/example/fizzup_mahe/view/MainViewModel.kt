package com.example.fizzup_mahe.view

import android.content.Context
import androidx.lifecycle.*
import com.example.fizzup_mahe.R
import com.example.fizzup_mahe.model.Exercise
import com.example.fizzup_mahe.repository.ExerciseRepository
import com.example.fizzup_mahe.repository.NetworkRepository
import com.example.fizzup_mahe.repository.ServerRequestError
import kotlinx.coroutines.launch

class MainViewModel(
    private val appContext: Context,
    networkRepo: NetworkRepository,
    exerciseRepo: ExerciseRepository
) : ViewModel() {

    /**
     * List of exercises to be displayed. MediatorLiveData with two sources:
     * - changes in database
     * - available connection to network
     */
    private val _exercices = MediatorLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> = _exercices

    /**
     * Message to be displayed to the user, informing the source of the data presented.
     */
    private val _dataSourceInfo = MutableLiveData(appContext.getString(R.string.data_from_cache))
    val dataSourceInfo: LiveData<String> = _dataSourceInfo

    /**
     * Image resource of the data source.
     */
    private val _dataImageRes = MutableLiveData(R.drawable.ic_folder)
    val dataImageRes: LiveData<Int> = _dataImageRes

    init {
        _exercices.addSource(exerciseRepo.exercises) {
            _exercices.value = it
        }

        _exercices.addSource(networkRepo.isConnected) { connected ->
            if (connected) {
                viewModelScope.launch {
                    try {
                        exerciseRepo.fetchExercises()
                        dataFetchedFormServerUi()
                    } catch (error: ServerRequestError) {
                        errorFetchingDataUi()
                    }
                }
                // Removing callback once data has been acquired from server
                networkRepo.removeCallback()
                // Removing network as source
                _exercices.removeSource(networkRepo.isConnected)
            }
        }
    }

    private fun dataFetchedFormServerUi() {
        _dataSourceInfo.value = appContext.getString(R.string.data_from_cloud)
        _dataImageRes.value = R.drawable.ic_cloud
    }

    private fun errorFetchingDataUi() {
        _dataSourceInfo.value = appContext.getString(R.string.errror_server)
        _dataImageRes.value = R.drawable.ic_cloud_off
    }

}