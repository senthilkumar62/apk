package org.zubbl.customviews

import android.annotation.SuppressLint
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import org.zubbl.R

@SuppressLint("ValidFragment")
class CustomDialog : BaseDialogFragment {
    private var message: String? = null
    private var btnPositiveBtnTxt: String = ""
    private var btnNegativeBtnTxt: String = ""
    private var btnCofirmTxt: String = ""
    private var customDialogImageSrc: Int = 0
    private var isPermissionDialog = false
    private var okClickListener: BtnOkClick? = null
    private var allowClickListener: BtnAllowClick? = null
    private var denyClickListener: BtnDenyClick? = null
    private var dialogMessage: TextView? = null
    private var allowTxt:TextView? = null
    private var denyTxt:TextView? = null
    private var okTxt:TextView? = null

    interface BtnOkClick {
        fun clicked()
    }

    interface BtnAllowClick {
        fun clicked()
    }

    interface BtnDenyClick {
        fun clicked()
    }

    //Needed
    constructor(){
    }

    @SuppressLint("ValidFragment")
    constructor(message: String, customDialogImageSrc: Int, btnCofirmTxt: String, okClickListener: BtnOkClick?) {
        this.message = message
        this.customDialogImageSrc = customDialogImageSrc
        this.btnCofirmTxt = btnCofirmTxt
        this.okClickListener = okClickListener
        isPermissionDialog = false
        setLayoutId(R.layout.activity_custom_dialog)
    }

    @SuppressLint("ValidFragment")
    constructor(message: String, customDialogImageSrc: Int, btnPositiveBtnTxt: String, btnNegativeBtnTxt: String, allowClickListener: BtnAllowClick,
                denyClickListener: BtnDenyClick) {
        this.message = message
        this.customDialogImageSrc = customDialogImageSrc
        this.btnPositiveBtnTxt = btnPositiveBtnTxt
        this.btnNegativeBtnTxt = btnNegativeBtnTxt
        this.allowClickListener = allowClickListener
        this.denyClickListener = denyClickListener
        isPermissionDialog = true
        setLayoutId(R.layout.activity_custom_dialog)
    }

    override fun initViews(v: View) {
        super.initViews(v)
        this.dialogMessage = v.findViewById<View>(R.id.dialogTextMessage) as TextView
        this.allowTxt = v.findViewById<View>(R.id.customAllowTxt) as TextView
        this.denyTxt = v.findViewById<View>(R.id.customDenyTxt) as TextView
        this.okTxt = v.findViewById<View>(R.id.customOkTxt) as TextView
        customImage = v.findViewById<View>(R.id.customDialogImage) as ImageView

        this.dialogMessage!!.text = message

        if (isPermissionDialog) {
            this.allowTxt!!.visibility = View.VISIBLE
            this.denyTxt!!.visibility = View.VISIBLE
            this.okTxt!!.visibility = View.GONE
            this.allowTxt!!.text = btnPositiveBtnTxt
            this.denyTxt!!.text = btnNegativeBtnTxt
        } else {
            this.allowTxt!!.visibility = View.GONE
            this.denyTxt!!.visibility = View.GONE
            this.okTxt!!.visibility = View.VISIBLE
            this.okTxt!!.text = btnCofirmTxt
        }

        customImage!!.setImageResource(customDialogImageSrc)
        initEvent()
        isCancelable = false
    }

    private fun initEvent() {
        this.allowTxt!!.setOnClickListener {
            if (allowClickListener != null) {
                allowClickListener!!.clicked()
            }
            dismiss()
        }

        this.denyTxt!!.setOnClickListener {
            if (denyClickListener != null) {
                denyClickListener!!.clicked()
            }
            dismiss()
        }

        this.okTxt!!.setOnClickListener {
            if (okClickListener != null) {
                okClickListener!!.clicked()
            }
            dismiss()
        }
    }

    fun showDialog(fm: FragmentManager, message: String, customDialogImage: Int, btnCofirmTxt: String, okClickListener: BtnOkClick) {
        val dialog = CustomDialog(message, customDialogImage, btnCofirmTxt, okClickListener)
        dialog.show(fm, "")
    }

    fun showDialog(fm: FragmentManager, message: String, customDialogImage: Int, btnPositiveBtnTxt: String, btnNegativeBtnTxt: String, allowClickListener: BtnAllowClick, denyClickListener: BtnDenyClick) {
        val dialog = CustomDialog(message, customDialogImage, btnPositiveBtnTxt, btnNegativeBtnTxt, allowClickListener, denyClickListener)
        dialog.show(fm, "")
    }

    fun showDialog(fm: FragmentManager, message: String, isError: Boolean) {
        if (customDialog!= null && customDialog!!.dialog != null && customDialog!!.dialog.isShowing) {
            return
        }
        if (isError) {
            customDialog = CustomDialog(message, 0, "OK", null)
            customDialog!!.show(fm, "")
        } else {
            customDialog = CustomDialog(message, 0, "OK", null)
            customDialog!!.show(fm, "")
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var customDialog: CustomDialog? = null
        @SuppressLint("StaticFieldLeak")
        var customImage: ImageView? = null
    }
}
