package com.example.fizzup_mahe.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Repository exposing network status.
 */
@Suppress("DEPRECATION")
class NetworkRepository private constructor(private val context: Context) {

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    // Using broadcast receiver for api < 21
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val connectivityManager =
                    context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val activeNetwork = connectivityManager.activeNetworkInfo

                _isConnected.value = activeNetwork?.isConnectedOrConnecting == true
            }
        }
    }

    init {
        setNetworkCallback()
    }

    fun removeCallback() {
        context.unregisterReceiver(broadcastReceiver)
    }

    private fun setNetworkCallback() {
        context.registerReceiver(
            broadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    companion object {
        // Singleton pattern
        private var INSTANCE: NetworkRepository? = null

        fun getInstance(context: Context): NetworkRepository {
            if (INSTANCE == null) {
                synchronized(NetworkRepository) {
                    if (INSTANCE == null) {
                        INSTANCE = NetworkRepository(context)
                    }
                }
            }
            return INSTANCE!!
        }
    }
}