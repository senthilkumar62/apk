@file:Suppress("DEPRECATION")

package org.zubbl.customviews

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
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
import kotlinx.android.synthetic.main.fragment_joinzubble.*
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


class JoinZubblDialog: BottomSheetDialogFragment(),View.OnClickListener,ServiceListener {
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
    var bundle=Bundle()

    private var ctx: Context? = null
    private var merchantDetails:MerchantDetails?=null
    private var offerId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppController.appComponent.inject(this)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.share_dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_joinzubble, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = context

        //get instance values
        getInstances()
        //initialize the views
        initViews()
    }

    private fun getInstances() {
        val bundleRes = this.arguments
        try {
            if (bundleRes != null) {
                val details = bundleRes.getString("Details")
                val merchantSummery: MerchantSummery = gson.fromJson(details, MerchantSummery::class.java)
                merchantSummery.merchantDetails?.let { merchantDetails = it }
                offerId = bundleRes.getString("offerId")
             // add the response to bundle for next popup
                bundle.clear()
                merchantDetails?.merchantId?.let{bundle.putString("merchantId", merchantDetails?.merchantId.toString())}
                bundle.putString("offerId", offerId)
                bundle.putString("Details", details)
            }
        }catch (e:Exception){}
    }

    private fun initViews() {
        progressDialog=commonMethods.getProgressDialog(context!!)
        btn_join.setOnClickListener(this)
        //display the merchant details
        setInformation()

    }
    @SuppressLint("SetTextI18n")
    private fun setInformation(){
        merchantDetails?.let {
            merchantDetails?.name?.let { name_joinZubbl.text = it }
            merchantDetails?.joinedMembers?.let { tv_joinedmaembers.text = it.toString() }
            merchantDetails?.totalMembers?.let { tv_totalmembers.text = "of "+  it.toString() }
            var address: String = ""
            merchantDetails?.venueAddress1?.let { address = it }
            merchantDetails?.venueAddress2?.let { address += " $it" }
            address_joinZubbl.text = address
            merchantDetails?.profileImage?.let {
                val url = Constant.IMAGE_URL + merchantDetails?.profileImage
                Log.e("image", url)
                var requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.img_4)
                requestOptions.error(R.drawable.img_4)
                requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
                Glide.with(context!!)
                        .load(url)
                        .apply(requestOptions)
                        .into(iv_logo_addZubbl)
            }
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            btn_join.id->{
                callJoinApi()
            }
        }
    }
    private fun callJoinApi() {

        //add request values
        Log.e("join api", "Mid "+(merchantDetails?.merchantId.toString())+" userID"  +sessionManager.getPersonID()+"offerid"+offerId)
        val query = HashMap<String, String>()
        query["merchantId"] = merchantDetails?.merchantId.toString()
        query["userId"]=sessionManager.getPersonID()

        //show loader
        commonMethods.showLoader(progressDialog)
        apiService.joinZubble(query).enqueue(RequestCallback(RequestCode.REQ_JOIN_ZUBBL, this))
    }


    override fun onSuccess(jsonResp: JsonResp, data: String) {
        commonMethods.dismissLoader(progressDialog)
        when (jsonResp.requestCode) {
            RequestCode.REQ_JOIN_ZUBBL-> {
                if (jsonResp.responseCode == 200) {
                    Log.e("header", "" + jsonResp.headers)
                    jsonResp.headers?.let {
                        if (jsonResp.headers!!["status"]!![0].contains( "200")) {
                            //sucess response call the connect popup
                            dismiss()
                            val connectZubblDialog = ConnectZubble()
                            Log.e("joinbundle",""+bundle)
                            connectZubblDialog.arguments = bundle
                            connectZubblDialog.show(fragmentManager, "Custom Bottom Sheet")
                        } else if(jsonResp.headers!!["status"]!![0].contains( "403")) {
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let {
                                displayAuthorizationPopup(messageModel.message!!)
                            }
                        }else {
                            //cast json response to model object
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let { customDialog.showDialog(fragmentManager!!, it, true)
                                dismiss()
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