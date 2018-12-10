package org.zubbl.views.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.zubbl.R
import org.zubbl.adapter.SettingAdapter
import org.zubbl.configs.Constant
import org.zubbl.interfaces.SideMenuInterface

class SettingFragment:Fragment(),SideMenuInterface {

    private var ctx: Context? = null
    var bundle=Bundle()
    private lateinit var rootView: View
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (view != null) {
            val parent = rootView.parent as ViewGroup
            parent.removeView(view)
        } else {
            rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx=this.context

        //initialize the variables
        initViews()
    }
    private fun initViews(){
        recyclerView=rootView.findViewById(R.id.rv_setting)
        //Setting Adapter for leaderboard details list
        recyclerView.layoutManager = LinearLayoutManager(this.ctx)
        recyclerView.adapter = SettingAdapter(ctx!!,Constant.SETTINGS_MENU,Constant.SETTINGS_ICONS,this)
    }

    override fun sideMenuClick(position: Int) {
        when(position){
            0->{

            }
            1->{

            }
            2->{

            }
        }

    }
}