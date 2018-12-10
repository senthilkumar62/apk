@file:Suppress("DEPRECATION")

package org.zubbl.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager

class CommonMethods {


    fun getProgressDialog(context: Context): ProgressDialog {
        return ProgressDialog(context)
    }

    //Show Loader
    fun showLoader(pd: ProgressDialog) {
        if (!pd.isShowing) {
            pd.setMessage("Please wait. Loading...")
            pd.setCancelable(false)
            pd.show()
        }
    }

    //Dismiss Loader
    fun dismissLoader(pd: ProgressDialog) {
        pd.dismiss()
    }

    fun isGreaterThanLolipop(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    //Hide key board
    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view.windowToken != null)
                imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}