package org.zubbl.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build

@Suppress("DEPRECATION")
class NetworkUtils(private var application: Application){

   /* fun NetworkUtils(application: Application){
        this.application = application
    }*/

    //Checking for Active Network Connection
    fun haveNetworkConnection(): Boolean {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val networks = cm.allNetworks
            var networkInfo: NetworkInfo
            for (mNetwork in networks) {
                networkInfo = cm.getNetworkInfo(mNetwork)
                if (networkInfo.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        } else {
            val info = cm.allNetworkInfo
            if (info != null) {
                for (anInfo in info) {
                    if (anInfo.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }

        return false
    }
}