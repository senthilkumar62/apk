@file:Suppress("DEPRECATION")

package org.zubbl.views.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.zubbl.R
import org.zubbl.application.AppController
import org.zubbl.configs.SessionManager
import org.zubbl.customviews.CustomDialog
import org.zubbl.interfaces.ServiceListener
import org.zubbl.model.Login.MessageModel
import org.zubbl.model.Login.UserSummary
import org.zubbl.model.common.JsonResp
import org.zubbl.service.ApiService
import org.zubbl.utils.CommonMethods
import org.zubbl.utils.RequestCallback
import org.zubbl.utils.RequestCode
import org.zubbl.utils.Validator
import javax.inject.Inject


class LoginActivity:AppCompatActivity() , View.OnClickListener,ServiceListener{
    @Inject
    lateinit var customDialog: CustomDialog
    @Inject
    lateinit var commonMethods: CommonMethods
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var gson: Gson
    private lateinit var progressDialog: ProgressDialog
    @Inject
    lateinit var validator: Validator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Injecting Dependency
        AppController.appComponent.inject(this)

        //Initialize View Components
        initViews()

        //do rememberme the username and password
        doRememberMe()
    }

    //Initialize the variables
    private fun initViews() {
        progressDialog = commonMethods.getProgressDialog(this)
        btn_register_login.setOnClickListener(this)
        tv_forgotPassword_login.setOnClickListener(this)
        cb_rememberMe_login.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        val face = Typeface.createFromAsset(assets, "font/muli_regular.ttf")
        cb_rememberMe_login.typeface = face
    }

    private fun doRememberMe(){
        if(sessionManager.getRememberMe()){
            cb_rememberMe_login.post({ cb_rememberMe_login.isChecked = true })
            Log.e("remeber",sessionManager.getSessionUserName()+"  "+sessionManager.getPassword())
            sessionManager.getSessionUserName()?.let{
                if(it.isNotEmpty()) edt_email_login.setText(sessionManager.getSessionUserName())
                else
                    edt_email_login.setText(sessionManager.getUserName())
            }
            edt_password_login.setText(sessionManager.getPassword())
        }
    }

    override fun onClick(v: View?) {
        //commonMethods.hideKeyboard(activity = Activity())
        when(v?.id){
            btn_login.id->{onLoginclicked()
            }
            btn_register_login.id->{
                commonMethods.hideKeyboard(this)
                startActivity(Intent( this@LoginActivity,RegisterActivity::class.java))
            }
            tv_forgotPassword_login.id->{
                commonMethods.hideKeyboard(this)
                startActivity(Intent( this@LoginActivity,ForgotPasswordActivity::class.java))
            }
           
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("login","resume")
        commonMethods.hideKeyboard(this)
    }

    //on Login button click
    private fun onLoginclicked() {
        commonMethods.hideKeyboard(this)
        if (isValidCredentials()) {
            sessionManager.setRememberMe(cb_rememberMe_login.isChecked)
            Log.e("remember",""+cb_rememberMe_login.isChecked)
            //add request values
            val query = HashMap<String, String>()
            query["username"] =edt_email_login.text.toString().trim()
            query["password"]=edt_password_login.text.toString()

            //show loader
            commonMethods.showLoader(progressDialog)

            apiService.login(query).enqueue(RequestCallback(RequestCode.REQ_LOGIN, this))
        }
    }

    private  fun isValidCredentials(): Boolean {
        var isValid = true
        if(edt_email_login.text.isNullOrEmpty()) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.empty_eamil), true)
        }
        else if( !validator.isValidateEmail(edt_email_login.text.toString()) ) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.email_invalid), true)
        }
        else if (edt_password_login.text.isNullOrEmpty()) {
            isValid = false
            //show alert for password
            customDialog.showDialog(supportFragmentManager, resources.getString(R.string.empty_password), true)
        }
        return isValid
    }

    override fun onSuccess(jsonResp: JsonResp, data: String) {
        commonMethods.dismissLoader(progressDialog)
        when (jsonResp.requestCode) {
            RequestCode.REQ_LOGIN -> {
                if (jsonResp.responseCode == 200) {
                    Log.e("header", "" + jsonResp.headers)
                    jsonResp.headers?.let {
                        if (jsonResp.headers!!["status"]!![0].contains( "200")) {
                            val messageModel: UserSummary = gson.fromJson(jsonResp.strResponse, UserSummary::class.java)
                            Log.e("userinformation",""+messageModel.userInformation)

                            messageModel.userInformation?.let {

                                if ( messageModel.userInformation!!.isNotEmpty()) {

                                    //set the session variables
                                    sessionManager.setToken(messageModel.userInformation?.get(0)?.loginToken)
                                    sessionManager.setpersonId(messageModel.userInformation?.get(0)?.userId)
                                    sessionManager.setUserName(messageModel.userInformation?.get(0)?.email)
                                    sessionManager.setPassword(edt_password_login.text.toString())

                                    //call the mainactivity
                                    val intent = Intent(this@LoginActivity,MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        } else {
                            //cast json response to model object
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let { customDialog.showDialog(supportFragmentManager, it, true)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(jsonResp: JsonResp, data: String) {
        commonMethods.dismissLoader(progressDialog)
        if (!data.isEmpty()) {
            customDialog.showDialog(supportFragmentManager, data, true)
        } else {
            customDialog.showDialog(supportFragmentManager, getString(R.string.server_error), true)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e("login","Onpause")
    }

}