package org.zubbl.adapter

import android.content.Context
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.zubbl.R
import org.zubbl.configs.Constant
import org.zubbl.model.leaderboardModel.LeaderBoardList

class LBRecycleAdapter (ctx: Context, private var leaderBoardList: List<LeaderBoardList>): RecyclerView.Adapter<LBRecycleAdapter.RecycleViewHolder>() {
    private var ctx: Context = ctx
    private var inflater: LayoutInflater = LayoutInflater.from(ctx)
    private var isFirst:Boolean=true


    override fun getItemCount(): Int {
        return leaderBoardList.size-1
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecycleViewHolder {
        val view=inflater.inflate(R.layout.leaderboard_detail_row, parent, false)
        val  mViewHolder=RecycleViewHolder(view)
        return mViewHolder
    }

    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        val leaderBoardData=leaderBoardList[position]
        if(position%2!=0)
            holder.rootLayot.setBackgroundColor(ContextCompat.getColor(ctx, R.color.odd_bg))
        else{
            holder.rootLayot.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
        }

        if(leaderBoardData.rank==1){
            Log.e("rank","postion"+position+"rank"+leaderBoardData.rank)
            holder.rank.setBackgroundResource(R.drawable.blue_circle)
            holder.rank.setTextColor(ContextCompat.getColor(ctx, R.color.white))
        }
        else{
            holder.rank.setBackgroundResource(0)
            holder.rank.setTextColor(ContextCompat.getColor(ctx, R.color.rank_color))
        }

        holder.rank.text=leaderBoardData.rank.toString()
        leaderBoardData.profileImage?.let{
            val url = Constant.IMAGE_URL + it
            displayProfile(holder.profile,url)
        }
        leaderBoardData.firstName?.let{holder.name.text=it  }
        leaderBoardData.points?.let{holder.points.text=it}

    }
    private fun displayProfile(imageView:de.hdodenhof.circleimageview.CircleImageView,url:String){
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.useravatar)
        requestOptions.error(R.drawable.useravatar)
        Glide.with(ctx)
                .load(url)
                .apply(requestOptions)
                .into(imageView)
    }

    inner class RecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var rank: AppCompatTextView = itemView.findViewById(R.id.tv_rank)
        internal var profile: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.iv_profile_leaderboard)
        internal var name:AppCompatTextView=itemView.findViewById(R.id.tv_name_LB)
        internal var points :AppCompatTextView=itemView.findViewById(R.id.tv_points_LB)
        internal var rootLayot:RelativeLayout=itemView.findViewById(R.id.rootlayout_LB)

    }

}


