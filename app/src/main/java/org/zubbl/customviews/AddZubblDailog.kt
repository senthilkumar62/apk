package org.zubbl.customviews

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.sucesscustom_dialog.*
import org.zubbl.R
import org.zubbl.interfaces.CallBackListener
import org.zubbl.views.fragments.leaderboard.LeaderboardBottomDialog


class AddZubblDailog:DialogFragment() ,View.OnClickListener{
    private var message:String?=""
    private var bundle=Bundle()
    private var merchantID:String?=""
    private var callBackListener: CallBackListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.share_dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.sucesscustom_dialog, container, false)
        dialog.setCanceledOnTouchOutside(false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customOkTxt1.setOnClickListener(this)
        //get Instance Values
        getInstances()
        //initialize the views
        initViews()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //getActivity() is fully created in onActivityCreated and instanceOf differentiate it between different Activities
        if (activity is CallBackListener)
            callBackListener = activity as CallBackListener?
    }

    private fun getInstances() {
        val bundleRes = this.arguments
        try {
            if (bundleRes != null) {
                message=bundleRes.getString("message")
                merchantID=bundleRes.getString("merchantId")
            }
        }catch (e:Exception){}
    }
    private fun initViews(){
        dialogTextMessage1.text=message
    }

    override fun onClick(v: View?) {
        when(v?.id){
            customOkTxt1.id->{
                if(callBackListener != null)
                    callBackListener!!.onDismiss()
                dismiss()
                Log.e("addZubbl",merchantID)
                bundle.putString("merchantId",merchantID)
                val dialogFragment = LeaderboardBottomDialog()
                dialogFragment.arguments=bundle
                dialogFragment.show(fragmentManager, "UploadDialogFragment")
            }
        }
    }
}