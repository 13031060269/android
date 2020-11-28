package com.hfax.ucard.modules.user.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hfax.app.h5.H5Activity
import com.hfax.ucard.R
import com.hfax.ucard.utils.UCardUtil
import com.hfax.ucard.utils.mvp.DataChange
import com.hfax.ucard.utils.mvp.NetworkAddress

class LoginContractDialog(context: Context, dataChange: DataChange<Boolean>) {
    private val alertDialog: AlertDialog = AlertDialog.Builder(context).setView(R.layout.dialog_login_contract).create()
    val tvContent: TextView

    init {
        alertDialog.show()
        tvContent = alertDialog.findViewById(R.id.tv_content)!!
        val text1 = "服务协议"
        val text2 = "隐私协议"
        val text = "    为了保证您的信息安全与个人隐私，请您仔细阅读并确认惠域U卡“服务协议”与“隐私协议”，内容明确了用户个人信息获取场景与使用原则，我们会严格基于协议内容为您提供产品服务，保护您的信息安全。如您同意以上协议，请点击“同意”开始使用惠域U卡。"
        val indexOf1 = text.indexOf(text1)
        val indexOf2 = text.indexOf(text2)
        val spannable = SpannableStringBuilder(text)
        //文字点击
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                H5Activity.startActivity(widget.context, UCardUtil.getH5Url(NetworkAddress.H5_CONTRACT_SERVICE))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = 0xff3A80FF.toInt()
            }

        }, indexOf1, indexOf1 + text1.length
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                H5Activity.startActivity(widget.context, UCardUtil.getH5Url(NetworkAddress.H5_CONTRACT_PRIVACY))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = 0xff3A80FF.toInt()
            }

        }, indexOf2, indexOf2 + text2.length
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvContent.text = spannable
        //一定要记得设置，不然点击不生效
        tvContent.movementMethod = LinkMovementMethod.getInstance()
        tvContent.highlightColor = 0
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        alertDialog.setCancelable(false)
        alertDialog.findViewById<View>(R.id.bt_no)?.setOnClickListener {
            alertDialog.cancel()
        }
        alertDialog.findViewById<View>(R.id.bt_yes)?.setOnClickListener {
            alertDialog.dismiss()
            dataChange.onChange(true)
        }
    }
}