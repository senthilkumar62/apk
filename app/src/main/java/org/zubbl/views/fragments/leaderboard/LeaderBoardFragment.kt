@file:Suppress("DEPRECATION")

package org.zubbl.views.fragments.leaderboard

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_leaderboard.*
import kotlinx.android.synthetic.main.leaderboard_detail_row.*
import org.zubbl.R
import org.zubbl.adapter.LBRecycleAdapter
import org.zubbl.application.AppController
import org.zubbl.configs.Constant
import org.zubbl.model.leaderboardModel.LBSummary
import org.zubbl.model.leaderboardModel.LeaderBoardList
import javax.inject.Inject

class LeaderBoardFragment:Fragment() {
    @Inject
    lateinit var gson: Gson
    private lateinit var rootView: View
    private var ctx: Context? = null
    val bundle = Bundle()
    var leaderBoardList: List<LeaderBoardList>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AppController.appComponent.inject(this)

        if (view != null) {
            val parent = rootView.parent as ViewGroup
            parent.removeView(view)
        } else {
            rootView = inflater.inflate(R.layout.content_leaderboard, container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ctx = context

        //get the bundle values
        getInstance()

        //init view
        initViews()
    }

    private fun getInstance() {
        if (arguments != null) {
            val jsonRes = arguments!!.getString("jsonRes")
            //cast json response to model object
            val lbSummary:LBSummary = gson.fromJson(jsonRes,LBSummary::class.java)
            //setup list view
            lbSummary.leaderBoardData?.let{
                if( lbSummary.leaderBoardData?.leaderBoardList!!.isNotEmpty()){
                    leaderBoardList=lbSummary.leaderBoardData?.leaderBoardList
                    setupLeaderBoardDetails()
                }
            }
        }
    }

    private fun initViews() {
        leaderBoardList?.let{
            bottom_row.visibility=View.VISIBLE
            val size=leaderBoardList!!.size
            val leaderBoardData=leaderBoardList!![size-1]
            tv_rank.text=leaderBoardData.rank.toString()
            leaderBoardData.profileImage?.let{
                val url = Constant.IMAGE_URL + it
                displayProfile(iv_profile_leaderboard,url)
            }
            leaderBoardData.firstName?.let{tv_name_LB.text=it  }
            leaderBoardData.points?.let{tv_points_LB.text=it}
        }
    }

    private fun setupLeaderBoardDetails() {
        //Setting Adapter for leaderboard details list
        rv_leaderboard.layoutManager = LinearLayoutManager(this.ctx)
        rv_leaderboard.adapter =LBRecycleAdapter(ctx!!,leaderBoardList!!)
    }
    private fun displayProfile(imageView:de.hdodenhof.circleimageview.CircleImageView,url:String){
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.useravatar)
        requestOptions.error(R.drawable.useravatar)
        Glide.with(ctx!!)
                .load(url)
                .apply(requestOptions)
                .into(imageView)
    }
}