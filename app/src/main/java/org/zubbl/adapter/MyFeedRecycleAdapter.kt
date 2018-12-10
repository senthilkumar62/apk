package org.zubbl.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.zubbl.R
import org.zubbl.configs.Constant
import org.zubbl.model.myfeed.OffersList

class MyFeedRecycleAdapter (ctx: Context, private var offersList: List<OffersList>): RecyclerView.Adapter<MyFeedRecycleAdapter.RecycleViewHolder>() {
    private var ctx: Context = ctx
    private var inflater: LayoutInflater = LayoutInflater.from(ctx)
    private var isFirst:Boolean=true


    override fun getItemCount(): Int {
        return offersList.size
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecycleViewHolder {
        val view=inflater.inflate(R.layout.myfeed_item_row, parent, false)
        val  mViewHolder=RecycleViewHolder(view)
        return mViewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        val offersList=offersList[position]
        offersList.offerTitle?.let {  holder.mName.text=it}
        offersList.fromDate?.let { holder.time.text=it }
        holder.joinedMembers.text= offersList.joinedUsers.toString()
        holder.totalMembers.text= "of "+offersList.totalUsers+" Participants"
        offersList.userProfile?.let {
            val size=offersList.userProfile!!.size
            for(i in 0 until size){
                val userProfile = offersList.userProfile!![i]
                val url = Constant.IMAGE_URL + userProfile.userProfile
                when(i){
                    0->{displayProfile(holder.profile1,url)}
                    1->{
                        holder.profile2.visibility=View.VISIBLE
                        displayProfile(holder.profile2,url)
                    }
                    2->{holder.profile3.visibility=View.VISIBLE
                        displayProfile(holder.profile3,url)
                    }
                }
            }
        }
//        offersList.merchantImage.let {
//            val url = Constant.IMAGE_URL +offersList.merchantImage
//            Log.e("image", url)
//            var requestOptions = RequestOptions()
//            requestOptions.placeholder(R.drawable.img_4)
//            requestOptions.error(R.drawable.img_4)
//            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
//            Glide.with(ctx)
//                    .load(url)
//                    .apply(requestOptions)
//                    .into(holder.mImage)
//        }
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
        internal var mName: AppCompatTextView = itemView.findViewById(R.id.tv_mName_feed)
        internal var profile1: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.iv_profile1)
        internal var profile2: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.iv_profile2)
        internal var profile3: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.iv_profile3)
        internal var time: AppCompatTextView =itemView.findViewById(R.id.tv_time_feed)
        internal var joinedMembers : AppCompatTextView =itemView.findViewById(R.id.tv_joinedCount_feed)
        internal var totalMembers: AppCompatTextView =itemView.findViewById(R.id.tv_totalCount_feed)
        internal var mImage:ImageView=itemView.findViewById(R.id.iv_merchantImage_feed)
    }

}