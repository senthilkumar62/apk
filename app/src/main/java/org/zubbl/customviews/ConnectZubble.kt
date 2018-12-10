@file:Suppress("DEPRECATION")

package org.zubbl.customviews

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.connect_zubbl.*
import org.zubbl.R
import org.zubbl.application.AppController
import org.zubbl.configs.Constant
import org.zubbl.configs.SessionManager
import org.zubbl.interfaces.ServiceListener
import org.zubbl.model.Login.MessageModel
import org.zubbl.model.MerchantDetails
import org.zubbl.model.MerchantSummery
import org.zubbl.model.common.JsonResp
import org.zubbl.service.ApiService
import org.zubbl.utils.CommonMethods
import org.zubbl.utils.RequestCallback
import org.zubbl.utils.RequestCode
import org.zubbl.views.activities.LoginActivity
import javax.inject.Inject


class ConnectZubble : BottomSheetDialogFragment(), View.OnClickListener,ServiceListener {
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
    var bundle= Bundle()

    private  var merchantDetails: MerchantDetails?=null
    private var offerId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppController.appComponent.inject(this)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.share_dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.connect_zubbl, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getInstances()

        initViews()

    }

    private fun getInstances() {
        val bundle = this.arguments
        try {
            if (bundle != null) {
                val details = bundle.getString("Details")
                val merchantSummery: MerchantSummery = gson.fromJson(details, MerchantSummery::class.java)
                merchantSummery.merchantDetails?.let { merchantDetails = it }
                offerId = bundle.getString("offerId")
            }
        }catch (e:Exception){}


    }

    private fun initViews() {
        progressDialog=commonMethods.getProgressDialog(context!!)
        btn_connect_zubble.setOnClickListener(this)
        setInformation()

    }
    private fun setInformation(){
        merchantDetails?.let {
            merchantDetails?.name?.let { name_connectZubbl.text = it }
            merchantDetails?.venueAddress1?.let {address_connectZubbl.text = it }
            merchantDetails?.emailId?.let{tv_mail_connetZubbl.text=it}
            merchantDetails?.telephone?.let{phonenumber_connectZubbl.text=it}
            merchantDetails!!.totalPoints?.let { tv_totalpoints_connetZubbl.text = it.toString() }
            merchantDetails?.tapInPoints?.let {tv_tap_ins_connetZubbl.text = it.toString() }
            merchantDetails?.zubblPoints?.let{tv_points_connetZubbl.text=it.toString()}
            merchantDetails?.lastCheckin?.let{
                last_seen.visibility=View.VISIBLE
                last_seen.text="Last check-in "+it}

            merchantDetails?.profileImage.let {
                val url = Constant.IMAGE_URL + merchantDetails?.profileImage
                Log.e("image", url)
                var requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.img_4)
                requestOptions.error(R.drawable.img_4)
                requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
                Glide.with(context!!)
                        .load(url)
                        .apply(requestOptions)
                        .into(iv_logo_connectZubbl)
            }
        }
    }
    override fun onClick(v: View?) {
        when(v?.id){
            btn_connect_zubble.id->{

                callAddApi()

            }
        }
    }
    private fun callAddApi() {
        //add request values
        try {
            val query = HashMap<String, String>()
            Log.e("addapi", "offerId"+offerId+" userid"+sessionManager.getPersonID()+"mercahnt id "+merchantDetails?.merchantId.toString())
            query["offerId"] =offerId
            query["userId"] = sessionManager.getPersonID()
            query["merchantId"] = merchantDetails?.merchantId.toString()
            //show loader
            commonMethods.showLoader(progressDialog)
            apiService.addZubbl(query).enqueue(RequestCallback(RequestCode.REQ_CONNECT_ZUBBL, this))
        }catch (e:Exception){}
    }


    override fun onSuccess(jsonResp: JsonResp, data: String) {
        commonMethods.dismissLoader(progressDialog)
        when (jsonResp.requestCode) {
            RequestCode.REQ_CONNECT_ZUBBL-> {
                if (jsonResp.responseCode == 200) {
                    Log.e("header", "" + jsonResp.headers)
                    jsonResp.headers?.let {
                        if (jsonResp.headers!!["status"]!![0].contains( "200")) {
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let {
                                dismiss()
                                bundle.clear()
                                val dialog=AddZubblDailog()
                                bundle.putString("message",it)
                                bundle.putString("merchantId", merchantDetails?.merchantId.toString())
                                dialog.arguments=bundle
                                dialog.show(fragmentManager,"test")
                            }
                        } else if(jsonResp.headers!!["status"]!![0].contains( "403")) {
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let {
                                displayAuthorizationPopup(messageModel.message!!)
                            }
                        }
                        else {
                            //cast json response to model object
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let {
                                dismiss()
                                customDialog.showDialog(fragmentManager!!, it, true)
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
            customDialog.showDialog(fragmentManager!!, data, true)
        } else {
            customDialog.showDialog(fragmentManager!!, getString(R.string.server_error), true)
        }
    }
    // handle the authorization failures  and it will navigate to login page
    private fun displayAuthorizationPopup(message:String){
        customDialog.showDialog(fragmentManager!!,  message, 0, getString(R.string.okay), object : CustomDialog.BtnOkClick {
            override fun clicked() {
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })

    }

}