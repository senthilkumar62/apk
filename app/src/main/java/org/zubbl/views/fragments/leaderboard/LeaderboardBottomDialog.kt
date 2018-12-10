package org.zubbl.views.fragments.leaderboard

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.leaderboardhomepage.*
import org.zubbl.R
import org.zubbl.adapter.LeaderBoardAdapter
import org.zubbl.application.AppController
import org.zubbl.configs.SessionManager
import org.zubbl.customviews.CustomDialog
import org.zubbl.customviews.CustomTabLayout
import org.zubbl.interfaces.ServiceListener
import org.zubbl.model.Login.MessageModel
import org.zubbl.model.common.JsonResp
import org.zubbl.model.leaderboardModel.LBSummary
import org.zubbl.service.ApiService
import org.zubbl.utils.CommonMethods
import org.zubbl.utils.RequestCallback
import org.zubbl.utils.RequestCode
import org.zubbl.views.activities.LoginActivity
import javax.inject.Inject
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior




class LeaderboardBottomDialog :BottomSheetDialogFragment(), ServiceListener,View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var customDialog: CustomDialog
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var commonMethods:CommonMethods
    private lateinit var progressDialog: ProgressDialog
    private var ctx: Context? = null
    private var merchantId=""
    var bundle=Bundle()
    private lateinit var rootView: View
    private var tabLayout:CustomTabLayout?=null
    private var viewPager:ViewPager?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.share_dialog)

        //Injecting Dependency
        AppController.appComponent.inject(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        AppController.appComponent.inject(this)
        if (view != null) {
            val parent = rootView.parent as ViewGroup
            parent.removeView(view)
        } else {
            rootView = inflater.inflate(R.layout.leaderboardhomepage, container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Setting Context
        ctx = context!!
        // get intent values
        getInstances()
        //Initialize View Components
        initViews()
        //call api
        callLeaderBoardApi()

    }
    private fun getInstances() {

        val bundle = this.arguments
        try {
            if (bundle != null)
                merchantId=bundle.getString("merchantId")
            Log.e("leaderZubbl",merchantId)
        }catch (e:Exception){}
    }

    private fun initViews(){
        progressDialog=commonMethods.getProgressDialog(context!!)
        tabLayout=rootView.findViewById(R.id.tablayout_leaderboard)
        viewPager=rootView.findViewById(R.id.vp_leaderboard)
        tabLayout?.addTab(tabLayout?.newTab()!!.setText(getString(R.string.leaderboard)))
        tabLayout?.addTab(tabLayout?.newTab()!!.setText(getString(R.string.progress)))
        nodatafound_leaderB.setOnClickListener(this)
    }

    private fun setUpAdapter(jsonResp: String){
        nodatafound_leaderB.visibility=View.GONE
        viewPager!!.visibility=View.VISIBLE
        viewPager!!.adapter = LeaderBoardAdapter(childFragmentManager, jsonResp)
        tabLayout!!.setupWithViewPager(viewPager)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            nodatafound_leaderB.id->{
                callLeaderBoardApi()
            }

        }
    }

    // call leaderboard api
    private fun callLeaderBoardApi(){
        //add request values
        Log.e("leader api", "Mid "+(merchantId+" userID"  +sessionManager.getPersonID()))
        val query = HashMap<String, String>()
        query["merchantId"] = merchantId
        query["userId"]=sessionManager.getPersonID()
        //show loader
        commonMethods.showLoader(progressDialog)
        apiService.getLeaderBoardDetails(query).enqueue(RequestCallback(RequestCode.REQ_LEADERBOARD, this))
    }
    override fun onSuccess(jsonResp: JsonResp, data: String) {
        commonMethods.dismissLoader(progressDialog)
        when (jsonResp.requestCode) {
            RequestCode.REQ_LEADERBOARD-> {
                if (jsonResp.responseCode == 200) {
                    Log.e("header", "" + jsonResp.headers)
                    jsonResp.headers?.let {
                        if (jsonResp.headers!!["status"]!![0].contains( "200")) {
                            //sucess response call the connect popup
                            val lbSummary:LBSummary=gson.fromJson(jsonResp.strResponse,LBSummary::class.java)
                            lbSummary.leaderBoardData?.let{
                                //sucess response call the fragemnts
                                setUpAdapter(jsonResp.strResponse)}

                        } else if(jsonResp.headers!!["status"]!![0].contains( "403")) {
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let {
                                dismiss()
                                displayAuthorizationPopup(messageModel.message!!)
                            }
                        }else {
                            //failure response
                                nodatafound_leaderB.visibility=View.VISIBLE
                                viewPager?.visibility=View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(jsonResp: JsonResp, data: String) {
        commonMethods.dismissLoader(progressDialog)
        dismiss()
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

    override fun onResume() {
        super.onResume()
        val behavior = BottomSheetBehavior.from(rootView.getParent() as View)
        behavior.isHideable = false
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }


}