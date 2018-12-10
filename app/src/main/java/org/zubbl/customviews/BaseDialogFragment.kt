package org.zubbl.customviews

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.zubbl.R


open class BaseDialogFragment : DialogFragment() {
    private var layoutId: Int = 0

    fun setLayoutId(layoutId: Int) {
        this.layoutId = layoutId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Dialog_NoActionBar);
        } else {
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_NoActionBar);
        }*/
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.share_dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(layoutId, container, false)
        initViews(v)
        return v
    }

    /* override fun onAttach(activity: Activity?) {
         super.onAttach(activity)
         mActivity = activity!!
     }*/

    open fun initViews(v: View) {
        dialog.setCanceledOnTouchOutside(false)
    }
}
