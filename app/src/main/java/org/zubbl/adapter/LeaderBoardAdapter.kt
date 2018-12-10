package org.zubbl.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import org.zubbl.views.fragments.leaderboard.LeaderBoardFragment
import org.zubbl.views.fragments.leaderboard.ProgressFragment

class LeaderBoardAdapter (fm: FragmentManager?,jsonRes:String) : FragmentPagerAdapter(fm) {

    private var jsonRes=jsonRes

    override fun getItem(item: Int): Fragment? {
        var fragment: Fragment? = null
        when (item) {
            0 -> {
                fragment = LeaderBoardFragment()
            }
            1 -> {
                fragment = ProgressFragment()
            }
        }

        val bundle = Bundle()
        bundle.putString("jsonRes",jsonRes)
        fragment!!.arguments = bundle

        return fragment
    }

    override fun getCount(): Int {
        return 2
    }


    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> {
                title = "Leaderboard"
            }
            1 -> {
                title = "Progress"
            }
        }
        return title
    }



}