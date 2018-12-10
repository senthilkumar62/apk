@file:Suppress("DEPRECATION")

package org.zubbl.views.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.util.Log
import android.view.View
import android.widget.RadioButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.activty_register.*
import org.zubbl.R
import org.zubbl.application.AppController
import org.zubbl.configs.SessionManager
import org.zubbl.customviews.CustomDialog
import org.zubbl.interfaces.ServiceListener
import org.zubbl.model.Login.MessageModel
import org.zubbl.model.Login.UserSummary
import org.zubbl.model.common.JsonResp
import org.zubbl.service.ApiService
import org.zubbl.utils.*
import javax.inject.Inject


class RegisterActivity:AppCompatActivity() , View.OnClickListener,ServiceListener{
    @Inject
    lateinit var customDialog: CustomDialog
    @Inject
    lateinit var commonMethods: CommonMethods
    @Inject
    lateinit var networkUtils: NetworkUtils
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var query:HashMap<String,Any>
    @Inject
    lateinit var validator: Validator
    private lateinit var progressDialog: ProgressDialog
    private var gender:Int=0



    override fun onCreate(savedInstanceState: Bundle?) {
        AppController.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_register)

        //Initialize the variables
        initViews()
    }

    //Initialize the variables
    private fun initViews() {
        progressDialog = commonMethods.getProgressDialog(this)
        tv_action_bar_title.text=getString(R.string.register)
        iv_left_action.visibility= View.VISIBLE
        iv_left_action.setImageResource(R.drawable.arrow)
        btn_register.setOnClickListener(this)
        iv_left_action.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            btn_register.id->{
                if(validateFields()){
                    doRegistration()
                }
            }
            iv_left_action.id->{
                commonMethods.hideKeyboard(this)
                onBackPressed()
            }
        }
    }
    //do registration
    private fun doRegistration()
    {  commonMethods.hideKeyboard(this)
        if (networkUtils.haveNetworkConnection()) {
            //call the api
            callRegisterApi()
        } else {
            //Show Error Dialog
            customDialog.showDialog(supportFragmentManager, resources.getString(R.string.network_failure), true)
        }
    }
    //check all the fields are validated or not
    private fun validateFields():Boolean{

        return isValidUserName(register_fname) && isValidLastName(register_lname) && isvalidAge(register_age) &&isvalidaGender() && isvalidEmail(register_emailaddress) && isvalidPassword(register_password) && isvalidConfirmPassword(register_confirmpassword,register_password)

    }
    //cal respective api
    private fun callRegisterApi() {
        //Display Loader
        commonMethods.showLoader(progressDialog)
        //Query Parameters
        query.clear()
        // val query = HashMap<String, Any>()
        query["first_name"] = register_fname.text.toString().trim()
        query["last_name"] = register_lname.text.toString().trim()
        query["username"] = register_emailaddress.text.toString().trim()
        query["password"] = register_password.text.toString()
        query["confirm_password"] = register_confirmpassword.text.toString()
        if(gender >0)
            query["gender"] = gender
        else
            query["gender"] = ""
        query["age"]=register_age.text.toString()

        apiService.register(query).enqueue(RequestCallback(RequestCode.REQ_REGISTER, this))
    }

    override fun onSuccess(jsonResp: JsonResp, data: String) {
        commonMethods.dismissLoader(progressDialog)
        when (jsonResp.requestCode) {
            RequestCode.REQ_REGISTER -> {
                if (jsonResp.responseCode == 200) {
                    Log.e("header", "" + jsonResp.headers)
                    jsonResp.headers?.let {
                        if (jsonResp.headers!!["status"]!![0].contains( "200")) {
                            callMainActivity(jsonResp)
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
    //first name validation
    private fun isValidUserName(value: AppCompatEditText):Boolean{
        var isValid=true
        if(value.text.isNullOrEmpty()) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.empty_firstname), true)
        }
        else if( !validator.isValidateUsername(value.text.toString()) ) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.firstName_invalid), true)
        }

        return isValid
    }

    //last name validation
    private fun isValidLastName(value: AppCompatEditText):Boolean{
        var isValid=true
        if(value.text.isNullOrEmpty())
          isValid=true
        else if( !validator.isValidateUsername(value.text.toString()) ) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.lastname_invalid), true)
        }

        return isValid
    }

    //gender validation
    private fun isvalidaGender():Boolean {
        val isValid = true
        if (radiogroup_gender.checkedRadioButtonId != -1) {
            // get selected radio button from radioGroup
            val selectedId = radiogroup_gender.checkedRadioButtonId

            // find the radiobutton by returned id
            val radioSexButton = findViewById<View>(selectedId) as RadioButton
            when( radioSexButton.text.toString())
            {
                "Male"->{gender=1}
                "Female"->{gender=2}
                "Other"->{gender=3}
            }
            Log.e("gender",gender.toString())
        } /*else {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.gender_invalid), true)
        }*/
        return isValid
    }
    //email validation
    private fun isvalidEmail(value:AppCompatEditText):Boolean{
        var isValid=true
        if(value.text.isNullOrEmpty()) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.empty_eamil), true)
        }
        else if( !validator.isValidateEmail(value.text.toString()) ) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.email_invalid), true)
        }
        return isValid
    }
    //new password validation
    private fun isvalidPassword(value:AppCompatEditText):Boolean{
        var isValid=true
        if(value.text.isNullOrEmpty()) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.empty_password), true)
        }
        else if( !validator.passwordValidate(value.text.toString()) ) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.invalid_password), true)
        }
        return isValid
    }
    //confirm password validation
    private fun isvalidConfirmPassword(conformPassword:AppCompatEditText,password:AppCompatEditText):Boolean{
        var isValid=true
        if(conformPassword.text.isNullOrEmpty()) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.empty_confirm), true)
        }
        else if( !validator.conformPasswordValidate(password.text.toString(),conformPassword.text.toString()) ) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.invalid_confirm), true)
        }
        return isValid
    }

    //age validation
    private fun isvalidAge(value:AppCompatEditText):Boolean{
        var isValid=true
        if(value.text.isNullOrEmpty()) {
            isValid=true
            // customDialog.showDialog(supportFragmentManager, getString(R.string.empty_age), true)
        }
        else if(value.text.toString().toInt() !in 18..100) {
            isValid=false
            customDialog.showDialog(supportFragmentManager, getString(R.string.dateofBirth), true)
        }
        return isValid
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    private fun  callMainActivity(jsonResp: JsonResp){
        val messageModel:UserSummary = gson.fromJson(jsonResp.strResponse, UserSummary::class.java)
        Log.e("userinformation",""+messageModel.userInformation)
        messageModel.userInformation?.let{
            val userInformation= messageModel.userInformation!![0]
            userInformation.message?.let {
                customDialog.showDialog(supportFragmentManager, userInformation.message!!, 0, "OK", object : CustomDialog.BtnOkClick {
                    override fun clicked() {
                        if ( messageModel.userInformation!!.isNotEmpty()) {
                            //clear the all session varibles
                            sessionManager.clearAll()

                            //set the session variables
                            sessionManager.setToken(messageModel.userInformation?.get(0)?.loginToken)
                            sessionManager.setpersonId(messageModel.userInformation?.get(0)?.userId)
                            sessionManager.setUserName(messageModel.userInformation?.get(0)?.userName)


                            //call the mainactivity
                            val i= Intent(this@RegisterActivity,MainActivity::class.java)
                            // set the new task and clear flags
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                            finish()
                        }
                    }
                })
            }

        }

    }

}