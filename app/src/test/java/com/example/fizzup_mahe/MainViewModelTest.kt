package com.example.fizzup_mahe

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.fizzup_mahe.model.Exercise
import com.example.fizzup_mahe.repository.ExerciseRepository
import com.example.fizzup_mahe.repository.NetworkRepository
import com.example.fizzup_mahe.utils.MainCoroutineScopeRule
import com.example.fizzup_mahe.utils.getValueForTest
import com.example.fizzup_mahe.view.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @Mock
    private val contextMock = Mockito.mock(Context::class.java)

    @Mock
    private val networkRepoMock = Mockito.mock(NetworkRepository::class.java)

    @Mock
    private val exerciseRepoMock = Mockito.mock(ExerciseRepository::class.java)

    @Test
    fun whenDataBaseEmits_ProductsAreUpdated() {
        //GIVEN
        Mockito.`when`(exerciseRepoMock.exercises).thenReturn(MutableLiveData(getDummyExercises()))
        Mockito.`when`(networkRepoMock.isConnected).thenReturn(MutableLiveData(false))
        val viewModel = MainViewModel(contextMock, networkRepoMock, exerciseRepoMock)
        //WHEN
        val products = viewModel.exercises.getValueForTest()
        //THEN
        Assert.assertEquals(getDummyExercises(), products)
    }

    @Test
    fun whenNetworkAvailable_FetchDataFromServer() {
        //GIVEN
        Mockito.`when`(exerciseRepoMock.exercises).thenReturn(MutableLiveData(listOf()))
        Mockito.`when`(networkRepoMock.isConnected).thenReturn(MutableLiveData(true))
        val viewModel = MainViewModel(contextMock, networkRepoMock, exerciseRepoMock)
        //WHEN
        val products = viewModel.exercises.getValueForTest() //Rmq: Needs to be observed
        //THEN
        runBlocking {
            Mockito.verify(exerciseRepoMock).fetchExercises()
        }
    }

    @Test
    fun whenNetworkUnavailable_ServerRequestIsNotTriggered() {
        //GIVEN
        Mockito.`when`(exerciseRepoMock.exercises).thenReturn(MutableLiveData(getDummyExercises()))
        Mockito.`when`(networkRepoMock.isConnected).thenReturn(MutableLiveData(false))
        val viewModel = MainViewModel(contextMock, networkRepoMock, exerciseRepoMock)
        //WHEN
        val products = viewModel.exercises.getValueForTest() //Rmq: Needs to be observed
        //THEN
        runBlocking {
            Mockito.verify(exerciseRepoMock, Mockito.never()).fetchExercises()
        }
    }

    @Test
    fun whenServerRequestExecuted_NetworkCallbackIsRemoved() {
        //GIVEN
        Mockito.`when`(exerciseRepoMock.exercises).thenReturn(MutableLiveData(getDummyExercises()))
        Mockito.`when`(networkRepoMock.isConnected).thenReturn(MutableLiveData(true))
        val viewModel = MainViewModel(contextMock, networkRepoMock, exerciseRepoMock)
        //WHEN
        val products = viewModel.exercises.getValueForTest() //Rmq: Needs to be observed
        //THEN
        runBlocking {
            Mockito.verify(exerciseRepoMock).fetchExercises()
        }
        Mockito.verify(networkRepoMock).removeCallback()
    }

    private fun getDummyExercises() = listOf(
        Exercise(1, "Push ups", "push.url"),
        Exercise(2, "Pull ups", "pull.url")
    )

}