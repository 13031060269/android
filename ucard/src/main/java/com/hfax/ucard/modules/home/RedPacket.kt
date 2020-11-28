package com.hfax.ucard.modules.home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.hfax.ucard.R
import com.hfax.ucard.bean.LoanStatus4BarBean
import com.hfax.ucard.modules.user.CouponActivity
import com.hfax.ucard.utils.MVPUtils
import com.hfax.ucard.utils.mvp.NetworkAddress
import com.hfax.ucard.utils.mvp.OnLoadDataListener
import com.hfax.ucard.utils.mvp.RequestMap
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleModel

var dialog: Dialog? = null
fun showRedPacket(context: Context, one: LoanStatus4BarBean.Coupon) {
    dialog?.apply {
        if (isShowing) {
            return
        }
    }
    val alertDialog: AlertDialog = AlertDialog.Builder(context).setView(R.layout.dialog_red_packet).create()
    dialog = alertDialog
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.show()
    alertDialog.findViewById<View>(R.id.iv_close)?.setOnClickListener { alertDialog.dismiss() }
    alertDialog.findViewById<View>(R.id.btn_go)?.setOnClickListener {
        alertDialog.dismiss()
        context.startActivity(Intent(context, CouponActivity::class.java))
    }
    (alertDialog.findViewById<View>(R.id.tv_msg) as TextView?)?.text = one.info
    (alertDialog.findViewById<View>(R.id.tv_money) as TextView?)?.text = "¥${one.amount}"
    alertDialog.setCancelable(false)
    alertDialog.setCanceledOnTouchOutside(false)
    alertDialog.setOnDismissListener {
        val map = RequestMap(NetworkAddress.COUPON_CLICK)
        map["fingerprint"] = one.fingerprint
        SimpleModel().doLoadData<Any>(map, null, object : OnLoadDataListener<Any> {
            override fun onSuccess(data: Any?) {
            }

            override fun onFail(code: Int, msg: String?) {
            }
        }, MVPUtils.Method.POST, Any::class.java)
        dialog = null
    }
}