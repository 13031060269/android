package com.hfax.ucard.modules.home

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.hfax.app.h5.H5Activity
import com.hfax.ucard.R
import com.hfax.ucard.utils.UCardUtil
import com.hfax.ucard.utils.mvp.NetworkAddress

class Deny(text: String?, context: Context) {
    init {
        val alertDialog: AlertDialog = AlertDialog.Builder(context).setView(R.layout.dialog_home_drainage).create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        alertDialog.findViewById<View>(R.id.iv_close)?.setOnClickListener { alertDialog.dismiss() }
        alertDialog.findViewById<View>(R.id.btn_go)?.setOnClickListener {
            H5Activity.startActivity(context, UCardUtil.getH5Url(NetworkAddress.H5_MORE_BORROW_MONEY_WAYS))
            alertDialog.dismiss()
        }
        (alertDialog.findViewById<View>(R.id.tv_content) as TextView?)?.text = text
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
    }
}