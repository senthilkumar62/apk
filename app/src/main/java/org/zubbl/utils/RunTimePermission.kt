package org.zubbl.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import org.zubbl.application.AppController

class RunTimePermission(context: Context) {

    private var permissionList: ArrayList<String> = ArrayList()
    private val preferences: SharedPreferences

    var isFirstTimePermission: Boolean
        get() = preferences.getBoolean("isFirstTimePermission", false)
        set(isFirstTime) = preferences.edit().putBoolean("isFirstTimePermission", isFirstTime).apply()

    var fcmToken: String
        get() = preferences.getString("fcmToken", "")
        set(fcmToken) = preferences.edit().putString("fcmToken", fcmToken).apply()

    private val isMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    init {
        AppController.appComponent.inject(this)
        preferences = context.getSharedPreferences("smile_permission", Context.MODE_PRIVATE)
    }

    fun checkHasPermission(context: Activity?, permissions: String): Boolean{
        var isValid=true
        if (isMarshmallow && context != null && permissions != null) {
            if (ContextCompat.checkSelfPermission(context, permissions) != PackageManager.PERMISSION_GRANTED) {
                    isValid=false
            }
        }
        return isValid
    }

    fun isPermissionBlocked(context: Activity?, permissions: String?): Boolean {
        if (isMarshmallow && context != null && permissions != null && isFirstTimePermission) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permissions)) {
                    return true
                }
        }
        return false
    }

    fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray?): Array<String> {
        permissionList = ArrayList()
        if (grantResults != null && grantResults.isNotEmpty()) {
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permissions[i])
                }
            }
        }
        return permissionList.toTypedArray()
    }

    fun isPermissionEnabled(context: Activity, permission: String, message: String): Boolean {
        if (isMarshmallow && ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                showMessageOKCancel(context, message,
                        DialogInterface.OnClickListener { _, _ -> ActivityCompat.requestPermissions(context, arrayOf(permission), 150) })
            } else {
                ActivityCompat.requestPermissions(context, arrayOf(permission), 150)
            }
            return false
        } else {
            return true
        }
    }

    private fun showMessageOKCancel(context: Context, message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }
}
