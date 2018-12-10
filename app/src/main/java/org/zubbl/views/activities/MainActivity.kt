@file:Suppress("DEPRECATION")

package org.zubbl.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_sidemenu.*
import org.zubbl.R
import org.zubbl.adapter.LeftMenuAdapter
import org.zubbl.application.AppController
import org.zubbl.configs.Constant
import org.zubbl.configs.SessionManager
import org.zubbl.customviews.CustomDialog
import org.zubbl.interfaces.SideMenuInterface
import org.zubbl.utils.CommonMethods
import org.zubbl.views.fragments.MyFeed
import org.zubbl.views.fragments.SettingFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity() ,View.OnClickListener, SideMenuInterface{


    lateinit var context: Context
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var customDialog: CustomDialog
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var commonMethods: CommonMethods

    private lateinit var rvMenu:RecyclerView
    private lateinit var ivLeftAction: ImageView
    private lateinit var ivrightAction: ImageView
    private lateinit var tvActionBarTitle: AppCompatTextView
    private  lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Injecting Dependency
        AppController.appComponent.inject(this)

        //Setting Context
        context = this@MainActivity

        //Initialize View Components
        initViews()
    }

    override fun onResume() {
        super.onResume()
        showHomePage()
    }


    private fun initViews() {

        //drawerLayout
        drawerLayout= findViewById(R.id.drawer_layout)
        drawerLayout.setScrimColor(resources.getColor(android.R.color.transparent))
        drawerLayout.closeDrawer(Gravity.START)

        ivLeftAction = findViewById(R.id.iv_left_action)
        ivrightAction = findViewById(R.id.iv_right_action)
        tvActionBarTitle = findViewById(R.id.tv_action_bar_title)

        //set Actionbar title
        tvActionBarTitle.text=getString(R.string.zubbl)
        ivLeftAction.setImageResource(R.drawable.hamburger)
        //set onclick listener
        ivLeftAction.setOnClickListener(this)
        ivrightAction.setOnClickListener(this)
        iv_menu_close.setOnClickListener(this)

        //Setting Adapter for SideMenu
        rvMenu = findViewById(R.id.rv_menu)
        val linearLayoutManager = LinearLayoutManager(this)
        rvMenu.layoutManager = linearLayoutManager
        rvMenu.adapter = LeftMenuAdapter(context, Constant.MENU_TITLE, this)

        //click recycleview first item by default
        showHomePage()
        //
        checkForUpdates()

    }

    //perform click actions
    override fun onClick(v: View?) {
        when (v?.id) {
            ivLeftAction.id->{
                showSideNavigationDrawer()
            }
            iv_menu_close.id->{
                onBackPressed()
            }
        }
    }

    //perform logout option
    private fun doLogout() {
        customDialog.showDialog(supportFragmentManager, getString(R.string.logout_message),0, getString(R.string.confirm_caps),
                getString(R.string.cancel_caps),
                object : CustomDialog.BtnAllowClick {
                    override fun clicked() {
                        val remeberme=sessionManager.getRememberMe()
                        var username:String?=""
                        var password:String?=""

                        //get the user details before clear the session
                        if(remeberme) {
                            username = sessionManager.getUserName()
                            password=sessionManager.getPassword()
                        }

                        //clear the all session varibles
                        sessionManager.clearAll()

                        //set the session varbiles for remember username and password
                        if(remeberme) {
                            sessionManager.setSessionUserName(username)
                            sessionManager.setPassword(password)
                            sessionManager.setRememberMe(remeberme)
                        }

                        finishAffinity()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    }
                },
                object : CustomDialog.BtnDenyClick {
                    override fun clicked() {}
                })
    }

    //Side Menu Item Click Listener
    override fun sideMenuClick(position: Int) {
        var fragment: Fragment? = null

        when (position) {
            0 -> {
                tv_action_bar_title.text=getString(R.string.zubbl)
                fragment=MyFeed()
            }
            1 -> {
                onBackPressed()
            }
            2->{tv_action_bar_title.text=getString(R.string.setting_title)
                fragment=SettingFragment()
            }
            3 -> {
                onBackPressed()
                doLogout()
            }
        }

        if(fragment != null){
            drawer_layout.closeDrawers()
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                    .replace(R.id.flt_content_view, fragment).commit()
        }
    }

    //show the default homepage fragemnt
    private fun showHomePage() {
        rvMenu.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                rvMenu.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
                rvMenu.getViewTreeObserver().removeOnPreDrawListener(this)
                return true
            }
        })
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.START)){
            hideBlurryImage()
            drawerLayout.closeDrawers()
        }
        else{
            finish()
        }
    }
    private fun checkForUpdates(){
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(view: View, v: Float) {

            }

            override fun onDrawerOpened(view: View) {
                Log.e("tag"," onDrawerOpened")
                showBlurryImage()
            }

            override fun onDrawerClosed(view: View) {
                // your refresh code can be called from here
                Log.e("tag","onDrawerClosed")
                hideBlurryImage()
            }

            override fun onDrawerStateChanged(i: Int) {

            }
        });
    }
    private fun showBlurryImage(){
        //Blur Layout - on dialog show
        Log.e("bluerrin","display")
        // frameLayout_gift.visibility=View.VISIBLE
        // Blurry.with(applicationContext).radius(35).sampling(3).onto(main_container as ViewGroup?)
    }
    private fun hideBlurryImage() {
        Log.e("bluerrin","close")
        //UnBlur on dialog dismiss
        // Blurry.delete(main_container as ViewGroup?)
        // frameLayout_gift.visibility=View.GONE
    }
    //check navigation bar open or not
    private  fun showSideNavigationDrawer() {
        if (drawer_layout != null) {
            showBlurryImage()
            drawer_layout.openDrawer(Gravity.START)
        }
    }
}
