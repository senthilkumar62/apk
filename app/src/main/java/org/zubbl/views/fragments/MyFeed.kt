package org.zubbl.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_myfeed.*
import org.sufficientlysecure.htmltextview.HtmlAssetsImageGetter
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.zubbl.R
import org.zubbl.adapter.MyFeedRecycleAdapter
import org.zubbl.application.AppController
import org.zubbl.configs.Constant
import org.zubbl.configs.SessionManager
import org.zubbl.customviews.CustomDialog
import org.zubbl.customviews.CustomTabLayout
import org.zubbl.model.myfeed.MyFeedSummery
import org.zubbl.model.myfeed.OffersList
import org.zubbl.receivers.LocationUpdateReceiver
import org.zubbl.service.ApiService
import org.zubbl.service.MyFeedService
import org.zubbl.utils.CommonMethods
import org.zubbl.views.activities.LoginActivity
import org.zubbl.views.activities.NearByActivity
import javax.inject.Inject
import org.sufficientlysecure.htmltextview.HtmlResImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView




class MyFeed:Fragment(),TabLayout.OnTabSelectedListener ,View.OnClickListener,LocationUpdateReceiver.Receiver{

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var customDialog: CustomDialog
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var commonMethods: CommonMethods
    private var ctx: Context? = null
    var bundle=Bundle()
    private lateinit var rootView: View
    private lateinit var tabLayout: CustomTabLayout
    private lateinit var recyclerView: RecyclerView
    private var isFirstTime=true
    private lateinit var serviceIntent:Intent
    private var locationUpdateReceiver:LocationUpdateReceiver?=null
    private var offersList:List<OffersList>?=null
    private var mHandler:Handler= Handler()
    lateinit var runner:Runnable


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //init app controller
        AppController.appComponent.inject(this)

        if (view != null) {
            val parent = rootView.parent as ViewGroup
            parent.removeView(view)
        } else {
            rootView = inflater.inflate(R.layout.fragment_myfeed, container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx=this.context
        //intent the backend serrvice
        serviceIntent= Intent(ctx,MyFeedService::class.java)
        //initialize the variables
        initViews()

        //initialize the tablayout
        initFragments()
        loadImage()


        runner = object: Runnable {
            override fun run() {
                Log.e("myfeed","startservice")
                callMyFeedService()
                // scheduled another events to be in 120 seconds later
                mHandler.postDelayed(runner,  120*1000) //milliseconds)
            }
        }



    }

    private fun initViews(){
        tabLayout = rootView.findViewById(R.id.tablayout_home)
        recyclerView=rootView.findViewById(R.id.rv_myfeed)
    }

    //initialize the tablayout
    private  fun initFragments() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.myFeed)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.nearby)))
        tabLayout.addOnTabSelectedListener(this)
    }
    private fun setupMyFeedData() {
        //Setting Adapter for leaderboard details list
        recyclerView.layoutManager = LinearLayoutManager(this.ctx)
        recyclerView.adapter =MyFeedRecycleAdapter(ctx!!,offersList!!)
    }

    override fun onClick(v: View?) {
        when(v?.id){
        }
    }
    // call the backend service
    private fun callMyFeedService(){
        locationUpdateReceiver= LocationUpdateReceiver(Handler())
        locationUpdateReceiver?.setReceiver(this)
        serviceIntent.putExtra(Constant.HOME, locationUpdateReceiver)
        ctx!!.startService(serviceIntent)
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {
    }

    //perform action when click on tabs
    override fun onTabSelected(tabLayout1: TabLayout.Tab?) {
        when(tabLayout1?.position) {
            0->{

            }
            1->{
                val tab = tabLayout.getTabAt(0)
                tab?.select()
                startActivity(Intent(context,NearByActivity::class.java))
            }
        }
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    // get the backend service result and update the ui
    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        when(resultCode)
        {
            Constant.SUCCESS->{
                resultData.let {
                    val details = resultData.getString("Details")
                    //update the ui and adda the markers
                    val myFeedSummery: MyFeedSummery = gson.fromJson(details,MyFeedSummery::class.java)
                    myFeedSummery.offersList?.let {
                        if(myFeedSummery.offersList!!.isNotEmpty()) {
                            //if list is not empty
                            offersList=myFeedSummery.offersList
                            // setupMyFeedData()
                        }else{// if  list is empty
                            // display no data found message
                            nodata_myfeed.visibility=View.VISIBLE
                        }
                    }
                }
            }
            Constant.FAILURE-> {//failure response
                if (isFirstTime) {
                    isFirstTime = false
                    resultData.let {
                        customDialog.showDialog(fragmentManager!!, resultData.getString("Details")!!, true)
                    }
                }
            }
            Constant.AUTHORIZATION_FAILURE->{//authorization error
                resultData.let {
                    displayAuthorizationPopup(resultData.getString("Details"))
                }
            }
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
        Log.e("start","callService and runner")
        mHandler.post(runner)

    }

    override fun onPause() {
        super.onPause()
        Log.e("start","callbacksremove")
        mHandler.removeCallbacks(runner)
        ctx!!.stopService(serviceIntent)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
        }
        web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN)
        web.getSettings().javaScriptEnabled = true
        web.getSettings().allowFileAccess = true;
      val yourData="\n <div class=\"darg-section-here\" tabindex=\"100\" style=\"overflow: hidden;\">\n <div class=\"darg-area canvas ui-droppable\" id=\"drag_area\" style=\"background-color: rgb(31, 213, 231);\">\n                                <!-- <p class=\"no_drag\">Drag a design widgets from the left to this area</p> -->\n                              <div class=\"tool_wrapper ui-widget ui-draggable ui-draggable-handle ui-draggable-disabled no_text_drag\" style=\"left: 15.5px; top: 7px; position: absolute; z-index: 1010;\"><div class=\"tool_handle ui-widget-header\">&nbsp;<span class=\"fa fa-trash\" title=\"Delete\"></span></div><div class=\"edit_container ui-resizable text_select_div\" onmouseup=\"textSelected()\" onmouseleave=\"clearDragg(this)\" style=\"width: 315px; height: 89px; line-height: inherit;\"><div class=\"edit\" contenteditable=\"true\" data-text=\"Double click to add your content\"><font face=\"Verdana\"><span style=\"font-size:3vw;\">mobile testing&nbsp;</span></font><div><font face=\"Verdana\"><span style=\"font-size:3vw;\"><br></span></font></div><div><font face=\"Verdana\"><span style=\"font-size:3vw;\">purpose zubbl&nbsp;</span></font></div></div><div class=\"ui-resizable-handle ui-resizable-e\" style=\"z-index: 90; display: block;\"></div><div class=\"ui-resizable-handle ui-resizable-s\" style=\"z-index: 90; display: block;\"></div><div class=\"ui-resizable-handle ui-resizable-se ui-icon ui-icon-gripsmall-diagonal-se\" style=\"z-index: 90; display: block;\"></div><div class=\"ui-rotatable-handle ui-draggable\" title=\"Rotate\"></div></div></div><div class=\"tool_wrapper ui-widget ui-draggable ui-draggable-handle\" style=\"left: 79.5px; top: 147px; position: absolute;\"><div class=\"tool_handle ui-widget-header\">&nbsp;<span class=\"fa fa-trash\" title=\"Delete\"></span></div><div class=\"img_container ui-resizable\" style=\"width: 200px; height: 200px;\"><img id=\"sample_video\" class=\"preview_image img-responsive upload-img-video\" src=\"http://50.57.127.125:9213/images/dbimages/1051-1544169771.jpg\"><div class=\"ui-resizable-handle ui-resizable-e\" style=\"z-index: 90; display: block;\"></div><div class=\"ui-resizable-handle ui-resizable-s\" style=\"z-index: 90; display: block;\"></div><div class=\"ui-resizable-handle ui-resizable-se ui-icon ui-icon-gripsmall-diagonal-se\" style=\"z-index: 90; display: block;\"></div><div class=\"ui-rotatable-handle ui-draggable\" title=\"Rotate\"></div></div></div></div>\n\n                            </div>\n                           "
//
//        web.loadData(yourData, "text/html", "UTF-8")
       // val htmlTextView = rootView.findViewById(R.id.html_text) as HtmlTextView

// loads html from string and displays http://www.example.com/cat_pic.png from the Internet
        // loads html from string and displays cat_pic.png from the app's assets folder
//        htmlTextView.setHtml(yourData,
//                 HtmlAssetsImageGetter(htmlTextView))
        val data= "file:///android_asset/sample.html"
        web.loadUrl(data);
    }



}