package org.zubbl.views.fragments.leaderboard
/*
*created by lalitha
* 05/12/2018
 */
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_progress.*
import org.zubbl.R
import org.zubbl.application.AppController
import org.zubbl.model.leaderboardModel.LBSummary
import org.zubbl.model.leaderboardModel.ProgressData
import javax.inject.Inject

class ProgressFragment:Fragment() {
    @Inject
    lateinit var gson: Gson
    private lateinit var rootView: View
    private var ctx: Context? = null
    val bundle = Bundle()
    var progressData:ProgressData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AppController.appComponent.inject(this)

        if (view != null) {
            val parent = rootView.parent as ViewGroup
            parent.removeView(view)
        } else {
            rootView = inflater.inflate(R.layout.fragment_progress, container, false)
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
            val lbSummary: LBSummary = gson.fromJson(jsonRes, LBSummary::class.java)
            //setup list view
            lbSummary.leaderBoardData?.let{
                lbSummary.leaderBoardData?.progressData?.let{
                    progressData=lbSummary.leaderBoardData?.progressData
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        try {
            progressData?.let {

                progressData?.loggedUserTotalTapIns?.let { tv_tap_ins_progress.text = it }

                next_rank.text = progressData?.nextRankPoints.toString()

                progressData?.nextRankPoints?.let {//set progress bar max range
                    progressBar.max = progressData?.nextRankPoints!!.toInt() }

                progressData?.loggedUserTotalPoints?.let { progressBar.progress = progressData?.rank!! }
                //total points
                progressData?.loggedUserTotalPoints?.let {
                    tv_points_progress.text = it
                    progressBar.progress = it.toInt()
                    your_rank.text = it
                }
                //rank
                when(progressData?.rank!!.toInt()){
                    0->{tv_rank_progress.text = progressData?.rank.toString()}
                    1->tv_rank_progress.text = progressData?.rank.toString()+"st"
                    2->tv_rank_progress.text = progressData?.rank.toString()+"nd"
                    3->tv_rank_progress.text = progressData?.rank.toString()+"rd"
                    else->tv_rank_progress.text = progressData?.rank.toString()+"th"
                }
                progressData?.rankingLevel?.let{tv_rank_level.text=it}
                progressData?.nextRankLevel?.let{next_rank_level.text=it}

            }
        }catch(e:Exception){ Log.e("ProgressFragment",""+e)}
    }

}