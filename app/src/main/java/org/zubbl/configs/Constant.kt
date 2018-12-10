package org.zubbl.configs

import android.Manifest
import org.zubbl.R

class Constant {
    //Declaring Static fields
    companion object {

        /*TESTING SERVER*/
        const val BASE_URL = "http://50.57.127.125:9213/webservice/"
        const val IMAGE_URL="http://50.57.127.125:9213"

        /*UAT SERVER*/
        //  const val BASE_URL = ""

        /*LIVE SERVER*/
        //const val BASE_URL = "https://imod.powerholding-intl.com:7503"
        const val APP_NAME="ZUBBL"
        const val LOGGER_TAG="ERROR"

        val MENU_TITLE = arrayOf("My Offers", "Profile",
                "Settings", "Logout")
        val SETTINGS_MENU= arrayOf("Change Password","Terms & Conditions","Notification")
        val SETTINGS_ICONS= arrayOf(R.drawable.right_arrow,R.drawable.right_arrow,R.drawable.toggle_selector)


        //Fragments val Constant
        const val HOME="HOME"
        const val SUCCESS=1
        const val FAILURE=0
        const val AUTHORIZATION_FAILURE=403
      val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)



    }


}
