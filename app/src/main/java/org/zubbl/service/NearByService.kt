package org.zubbl.service

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import com.google.gson.Gson
import org.zubbl.application.AppController
import org.zubbl.configs.Constant
import org.zubbl.configs.SessionManager
import org.zubbl.customviews.CustomDialog
import org.zubbl.interfaces.ServiceListener
import org.zubbl.model.Login.MessageModel
import org.zubbl.model.common.JsonResp
import org.zubbl.utils.CommonMethods
import org.zubbl.utils.NetworkUtils
import org.zubbl.utils.RequestCallback
import org.zubbl.utils.RequestCode
import javax.inject.Inject


class NearByService:IntentService(NearByService::class.java.name),ServiceListener {
    @Inject
    lateinit var customDialog: CustomDialog
    @Inject
    lateinit var commonMethods: CommonMethods
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var networkUtils: NetworkUtils
    @Inject
    lateinit var gson: Gson
    private  var lat:Double=0.0
    private var lng:Double=0.0
    private var bundle=Bundle()
    lateinit var receiver : ResultReceiver



    override fun onHandleIntent(intent: Intent?) {
        Log.e("service","onHandler")
        if (!networkUtils.haveNetworkConnection()) {
            return
        }
        if(intent!=null){
            receiver = intent.getParcelableExtra(Constant.HOME)
        }
        if (intent != null && intent.extras != null) {
            lat = intent.getDoubleExtra("lat", 0.0)
            Log.e("lat",""+lat)
            lng=intent.getDoubleExtra("lng", 0.0)
            Log.e("lng",""+lng)
            callNearByApi()
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppController.appComponent.inject(this)
    }

    private fun callNearByApi() {
        Log.e("service","apicalling"+sessionManager.getPersonID().toDouble())
        //add request values
        val query = HashMap<String, Double>()
        query["lat"] =lat
        query["lng"]=lng
        query["userId"]=sessionManager.getPersonID().toDouble()
        apiService.nearBy(query).enqueue(RequestCallback(RequestCode.REQ_MERCHANT_DETAILS, this))
    }


    override fun onSuccess(jsonResp: JsonResp, data: String) {
        when (jsonResp.requestCode) {
            RequestCode.REQ_MERCHANT_DETAILS-> {
                if (jsonResp.responseCode == 200) {
                    Log.e("nearbyapi", "" + jsonResp.headers)
                    jsonResp.headers?.let {
                        when {
                            jsonResp.headers!!["status"]!![0].contains( "200") -> {
                                Log.e("mercahntdetails",jsonResp.strResponse)
                                sendResult(Constant.SUCCESS, jsonResp.strResponse)
                            }
                            jsonResp.headers!!["status"]!![0].contains( "403") -> try{
                                val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                                messageModel.message.let{sendResult(Constant.AUTHORIZATION_FAILURE,messageModel.message!!)}
                            }catch (e:Exception){}
                            else -> try{
                                val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                                messageModel.message.let{sendResult(Constant.FAILURE,messageModel.message!!)}
                            }catch (e:Exception){}
                        }

                    }
                }
            }
        }
    }


    override fun onFailure(jsonResp: JsonResp, data: String) {
        sendResult(Constant.FAILURE, data)

    }
    private fun sendResult(requestCode:Int,result:String){
        bundle.clear()
        bundle.putString("Details",result)
        receiver.send(requestCode,bundle)
    }

}