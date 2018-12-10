package org.zubbl.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import org.zubbl.R
import org.zubbl.application.AppController
import org.zubbl.configs.Constant
import org.zubbl.configs.SessionManager
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class SplashActivity:AppCompatActivity() {

    @Inject
  lateinit var sessionManager: SessionManager
    var stream: InputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppController.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        getIntentValues()
    }

    /**
     * This method is used to get intent bundle values from previous called activity.
     * After 3 sec delay login activity called here.
     */
    private fun getIntentValues() {
        val handler = Handler()
        handler.postDelayed({ callActivityIntent() }, 3000)
    }

    //This method contains next activity navigation from this activity.
    private fun callActivityIntent() {

        //check supports transition
        val intentFlag = if (sessionManager.getUserName().isNullOrEmpty())
            Intent(this, LoginActivity::class.java)
        else
            Intent(this@SplashActivity,MainActivity::class.java)

        intentFlag.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentFlag)
    }

    //This predefined life cycle method triggered when screen getting invisible state in long time.
    override fun onStop() {
        super.onStop()
        finish()
    }
}