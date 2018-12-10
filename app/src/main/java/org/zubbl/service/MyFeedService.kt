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

class MyFeedService : IntentService(MyFeedService::class.java.name), ServiceListener {
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
    private var bundle = Bundle()
    lateinit var receiver: ResultReceiver


    override fun onHandleIntent(intent: Intent?) {
        Log.e("service", "myfeedService")
        if (!networkUtils.haveNetworkConnection()) {
            return
        }
        if (intent != null) {
            receiver = intent.getParcelableExtra(Constant.HOME)
        }
        if (intent != null && intent.extras != null) {
            callMyFeeddApi()
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppController.appComponent.inject(this)
    }

    // call leaderboard api
    private fun callMyFeeddApi(){
        //add request values
        val query = HashMap<String, String>()
        query["userId"]=sessionManager.getPersonID()
        apiService.getMyFeedData(query).enqueue(RequestCallback(RequestCode.REQ_MYFEED, this))
    }

    override fun onSuccess(jsonResp: JsonResp, data: String) {
        when (jsonResp.requestCode) {
            RequestCode.REQ_MYFEED-> {
                if (jsonResp.responseCode == 200) {
                    jsonResp.headers?.let {
                        when {
                            jsonResp.headers!!["status"]!![0].contains("200") -> {
                              //  Log.e("myfeed", jsonResp.strResponse)
                                sendResult(Constant.SUCCESS, jsonResp.strResponse)
                            }
                            jsonResp.headers!!["status"]!![0].contains("403") -> try {
                                val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                                messageModel.message.let { sendResult(Constant.AUTHORIZATION_FAILURE, messageModel.message!!) }
                            } catch (e: Exception) {
                            }
                            else -> try {
                                val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                                messageModel.message.let { sendResult(Constant.FAILURE, messageModel.message!!) }
                            } catch (e: Exception) {
                            }
                        }

                    }
                }
            }
        }
    }


    override fun onFailure(jsonResp: JsonResp, data: String) {
        sendResult(Constant.FAILURE, data)

    }

    private fun sendResult(requestCode: Int, result: String) {
        bundle.clear()
        bundle.putString("Details", result)
        receiver.send(requestCode, bundle)
    }
}