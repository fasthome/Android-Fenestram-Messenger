package io.fasthome.network.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

class NetworkController(
    private val context: Context
) {

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkNetwork(connectivityManager)
        } else {
            checkNetworkLegacy(connectivityManager)
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkNetwork(connectivityManager: ConnectivityManager?): Boolean {
        val capabilities =
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun checkNetworkLegacy(connectivityManager: ConnectivityManager?): Boolean {
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}