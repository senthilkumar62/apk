@file:Suppress("DEPRECATION")

package org.zubbl.views.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatTextView
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.gson.Gson
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.activity_forgotpasswword.*
import org.zubbl.R
import org.zubbl.application.AppController
import org.zubbl.configs.Constant
import org.zubbl.configs.SessionManager
import org.zubbl.customviews.CustomDialog
import org.zubbl.interfaces.ServiceListener
import org.zubbl.model.Login.ChangePassword
import org.zubbl.model.Login.ForgotPasswordSummary
import org.zubbl.model.Login.MessageModel
import org.zubbl.model.common.JsonResp
import org.zubbl.service.ApiService
import org.zubbl.utils.CommonMethods
import org.zubbl.utils.RequestCallback
import org.zubbl.utils.RequestCode
import org.zubbl.utils.Validator
import javax.inject.Inject


class ForgotPasswordActivity:AppCompatActivity(),View.OnClickListener,ServiceListener {
    private var currentState = 0

    private lateinit var tvHeaderText: AppCompatTextView

    private lateinit var edtuserName: AppCompatEditText
    private lateinit var edtOtp: AppCompatEditText
    private lateinit var edtNewPassword: AppCompatEditText
    private lateinit var edtConfirmPassword: AppCompatEditText

    @Inject
    lateinit var validator: Validator
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var commonMethods: CommonMethods
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var customDialog: CustomDialog

    lateinit var context: Context
    private lateinit var progressDialog: ProgressDialog
    private var activationPassword:String=" "
    private  var OTPValue : String?= ""
    private  lateinit var emailId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        AppController.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpasswword)
        context = this
        initviews()
    }

    private fun initviews(){
        progressDialog = commonMethods.getProgressDialog(context)
        tvHeaderText = findViewById(R.id.tv_action_bar_title)
        iv_left_action.visibility= View.VISIBLE
        tvHeaderText.text=getString(R.string.forgotpassword_title)
        iv_left_action.setImageResource(R.drawable.arrow)

        edtOtp = findViewById(R.id.forgot_otp)
        edtuserName = findViewById(R.id.forgot_emailaddress)
        edtNewPassword = findViewById(R.id.forgot_password)
        edtConfirmPassword = findViewById(R.id.forgot_confirmpassword)
        // passworUpdateForm = findViewById(R.id.llt_password_update)

        iv_left_action.setOnClickListener(this)
        btn_confirm_forgot.setOnClickListener(this)
        tv_forgot_resend.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_left_action -> {onBackBtnPressed()}
            R.id.btn_confirm_forgot ->{ onSubmitBtnPressed() }
            R.id.tv_forgot_resend->{
                edtOtp.setText("")
                val query = HashMap<String, String>()
                query["username"] = emailId
                //show loader
                commonMethods.showLoader(progressDialog)
                apiService.sendOTP(query).enqueue(RequestCallback(RequestCode.REQ_RESENDOTP, this))
            }
        }
    }

    //on back button pressed
    private fun onBackBtnPressed() {
        commonMethods.hideKeyboard(this)
        if (currentState == 0) {
            finish()
        } else {
            currentState -= 1
            showPrespectiveScreen()
        }
    }

    //on submit button pressed
    private fun onSubmitBtnPressed() {
        //check current view update
        //currentState += 1
        callRespectiveAPi()
    }

    //show username entry form
    private fun showPrespectiveScreen() {
        //clear the edittext fields
        edtOtp.setText("")
        forgot_confirmpassword.setText("")
        forgot_password.setText("")
        //update layout visiblity
        when(currentState)
        {
            0->{
               // input_forgot_email.isEnabled = true
                forgot_emailaddress.isEnabled=true
                forgot_emailaddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                email_error_forgot.text=""
                tv_action_bar_title.text=getString(R.string.forgotpassword_title)
                btn_confirm_forgot.text=getString(R.string.submit)
                changePassword_layout.visibility= GONE
                input_forgot_otp.visibility= GONE
                tv_forgot_resend.visibility= GONE
                otp_error_forgot.visibility= GONE
                input_forgot_email.visibility=VISIBLE
            }
            1->{
                tv_action_bar_title.text=getString(R.string.otp)
                forgot_emailaddress.isEnabled=false
                //input_forgot_email.isEnabled = false
                btn_confirm_forgot.text=getString(R.string.confirm)
                otp_error_forgot.visibility= GONE
                changePassword_layout.visibility= GONE
                forgotPassword_layout.visibility= VISIBLE
                input_forgot_otp.visibility=VISIBLE
                input_forgot_otp.isFocusable=true
                tv_forgot_resend.visibility=VISIBLE
            }
            2->{
                newPassword_error_forgot.visibility= GONE
                confirmPassword_error_forgot.visibility= GONE
                tv_action_bar_title.text=getString(R.string.resetPassword)
                forgotPassword_layout.visibility= GONE
                changePassword_layout.visibility= VISIBLE
            }
        }
    }

    private fun callRespectiveAPi() {
        when (currentState) {
            0 -> {
                if (isvalidEmail(forgot_emailaddress)) {
                    emailId = edtuserName.text.toString().trim()
                    //add request values
                    val query = HashMap<String, String>()
                    query["username"] = edtuserName.text.toString().trim()
                    //show loader
                    commonMethods.showLoader(progressDialog)
                    apiService.sendOTP(query).enqueue(RequestCallback(RequestCode.REQ_SEND_OTP, this))
                }
            }
            1 -> {//add request values
                Log.e("otp", OTPValue)
                val edtOtpValue = edtOtp.text.toString().trim()
                if(isValidateOTP()){
                    if (edtOtpValue == this.OTPValue?.trim()) {
                        currentState = 2
                        showPrespectiveScreen()
                    } else
                        setTextColor(otp_error_forgot, getString(R.string.invalid_otp),forgot_otp,true)
                }
            }
            2 -> {
                if (isvalidPassword(forgot_password)) {
                    //add request values
                    val query = HashMap<String, String>()
                    query["new_password"] = edtNewPassword.text.toString()
                    query["confirm_password"] = edtConfirmPassword.text.toString()
                    //show loader
                    commonMethods.showLoader(progressDialog)
                    apiService.changePassword(activationPassword,query).enqueue(RequestCallback(RequestCode.REQ_CHANGEPASSWORD, this))
                }
            }
        }
    }

    override fun onSuccess(jsonResp: JsonResp, data: String) {
        commonMethods.dismissLoader(progressDialog)
        when (jsonResp.requestCode) {
            RequestCode.REQ_SEND_OTP -> {
                //check response code
                if (jsonResp.responseCode == 200) {//on success response
                    jsonResp.headers?.let {
                        if (jsonResp.headers!!["status"]!![0].contains("200")) {
                            currentState = 1
                            showPrespectiveScreen()
                            val messageModel: ForgotPasswordSummary = gson.fromJson(jsonResp.strResponse, ForgotPasswordSummary::class.java)
                            messageModel.forgotpassword?.let {

                                if (messageModel.forgotpassword!!.isNotEmpty()) {
                                    Log.e("OTP",messageModel.forgotpassword!![0].oTP)
                                    messageModel.forgotpassword!![0].message?.let{ setErrorTextColor(email_error_forgot,it ,forgot_emailaddress,false)}
                                    messageModel.forgotpassword!![0].oTP?.let { OTPValue = it }
                                    messageModel.forgotpassword!![0].activationPassword?.let { activationPassword = it }
                                }
                            }
                        } else {
                            //cast json response to model object
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let {
                                setErrorTextColor(email_error_forgot,messageModel.message ,forgot_emailaddress,true)
                                // customDialog.showDialog(supportFragmentManager, it, true)
                            }
                        }
                    }
                }
            }
            RequestCode.REQ_RESENDOTP -> {
                //check response code
                if (jsonResp.responseCode == 200) {//on success response
                    jsonResp.headers?.let {
                        if (jsonResp.headers!!["status"]!![0].contains("200")) {
                            val messageModel: ForgotPasswordSummary = gson.fromJson(jsonResp.strResponse, ForgotPasswordSummary::class.java)
                            messageModel.forgotpassword?.let {
                                if (messageModel.forgotpassword!!.isNotEmpty()) {
                                    Log.e("RESendOTP",messageModel.forgotpassword!![0].oTP)
                                    messageModel.forgotpassword!![0].oTP?.let { OTPValue = it }
                                    messageModel.forgotpassword!![0].activationPassword?.let { activationPassword = it }
                                }
                            }
                        } else {
                            //cast json response to model object
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let {
                                setErrorTextColor(email_error_forgot,messageModel.message ,forgot_emailaddress,true)
                            }
                        }
                    }
                }
            }
            RequestCode.REQ_CHANGEPASSWORD -> {
                //check response code
                if (jsonResp.responseCode == 200) {//on success response
                    Log.e("otp", jsonResp.strResponse)
                    jsonResp.headers?.let {
                        if (jsonResp.headers!!["status"]!![0].contains( "200")) {
                            val message: ChangePassword = gson.fromJson(jsonResp.strResponse,ChangePassword::class.java)
                            message.forgotpassword?.let {
                                customDialog.showDialog(supportFragmentManager!!, it, 0, getString(R.string.okay), object : CustomDialog.BtnOkClick {
                                    override fun clicked() {
                                        val intent = Intent(context, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                })
                            }
                        } else {
                            //cast json response to model object
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let {
                                // input_forgot_email.error=it
                                // setErrorTextColor(newPassword_error_forgot, getString(R.string.invalid_password),forgot_password,true)
                                customDialog.showDialog(supportFragmentManager, it, true)
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onFailure(jsonResp: JsonResp, data: String) {
        //Dismiss Loader
        commonMethods.dismissLoader(progressDialog)
        if (!data.isEmpty()) {
            customDialog.showDialog(supportFragmentManager, data, true)
        } else {
            customDialog.showDialog(supportFragmentManager, getString(R.string.server_error), true)
        }

    }
    //check emial validation
    private fun isvalidEmail(value:AppCompatEditText):Boolean{
        var isValid=true
        if(value.text.isNullOrEmpty()) {
            isValid=false
            setErrorTextColor(email_error_forgot, getString(R.string.empty_eamil),forgot_emailaddress,true)
            // customDialog.showDialog(supportFragmentManager, getString(R.string.empty_eamil), true)

        }
        else if( !validator.isValidateEmail(value.text.toString()) ) {
            isValid=false
            setErrorTextColor(email_error_forgot, getString(R.string.email_invalid),forgot_emailaddress,true)
            // customDialog.showDialog(supportFragmentManager, getString(R.string.email_invalid), true)
        }
        return isValid
    }

    //check password validation
    private fun isvalidPassword(value:AppCompatEditText):Boolean{
        val isValid: Boolean
        if(value.text.isNullOrEmpty()) {
            isValid=false
            // setErrorTextColor(newPassword_error_forgot, getString(R.string.empty_password),forgot_password,true)
            customDialog.showDialog(supportFragmentManager, getString(R.string.empty_password), true)
        }
        else if( !validator.passwordValidate(value.text.toString()) ) {
            isValid=false
            // setErrorTextColor(newPassword_error_forgot, getString(R.string.invalid_password),forgot_password,true)
            customDialog.showDialog(supportFragmentManager, getString(R.string.invalid_password), true)
        }
        else{
            isValid =   isvalidConfirmPassword(forgot_confirmpassword,value)
        }
        return isValid
    }

    //check confirm password validation
    private fun isvalidConfirmPassword(conformPassword:AppCompatEditText,password:AppCompatEditText):Boolean{
        var isValid=true
        if(conformPassword.text.isNullOrEmpty()) {
            isValid=false
            //setErrorTextColor(confirmPassword_error_forgot, getString(R.string.empty_confirm),forgot_confirmpassword,true)
            customDialog.showDialog(supportFragmentManager, getString(R.string.empty_confirm), true)
        }
        else if( !validator.conformPasswordValidate(password.text.toString(),conformPassword.text.toString()) ) {
            isValid=false
            // setErrorTextColor(confirmPassword_error_forgot, getString(R.string.invalid_confirm),forgot_confirmpassword,true)
            customDialog.showDialog(supportFragmentManager, getString(R.string.invalid_confirm), true)
        }
        return isValid
    }

    //Validate OTP
    private fun isValidateOTP(): Boolean {
        return if (edtOtp.text.toString().trim().isNotEmpty()) {
            true
        } else {
            //Show Error Dialog
            setTextColor(otp_error_forgot, getString(R.string.otp_required),forgot_otp,true)
            // customDialog.showDialog(supportFragmentManager, resources.getString(R.string.otp_required), true)
            false
        }
    }


    override fun onBackPressed() {
        onBackBtnPressed()
    }

    //set the text  and color for error message
    private fun setErrorTextColor(textInputLayout: AppCompatTextView,message:String? ,editText:AppCompatEditText,isError:Boolean) {

        textInputLayout.visibility=View.VISIBLE
        textInputLayout.text=message
        if(isError){
            textInputLayout.setTextColor(ContextCompat.getColor(context, R.color.red))
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0)
        }
        else{
            textInputLayout.setTextColor(ContextCompat.getColor(context, R.color.green))
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0)
        }
    }

    //change the error text color
    private fun setTextColor(textInputLayout: AppCompatTextView,message:String? ,editText:AppCompatEditText,isError:Boolean) {

        textInputLayout.visibility=View.VISIBLE
        textInputLayout.text=message
        if(isError)
            textInputLayout.setTextColor(ContextCompat.getColor(context, R.color.red))
        else
            textInputLayout.setTextColor(ContextCompat.getColor(context, R.color.green))

    }

}
