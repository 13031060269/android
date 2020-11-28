package com.hfax.ucard.modules.user.fragment

import android.os.Bundle
import android.view.View
import com.hfax.ucard.R
import com.hfax.ucard.base.BaseNetworkFragment
import com.hfax.ucard.bean.LoginBean
import com.hfax.ucard.modules.home.MainActivity
import com.hfax.ucard.utils.MVPUtils
import com.hfax.ucard.utils.mvp.NetworkAddress
import com.hfax.ucard.utils.mvp.RequestMap
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl
import com.hfax.ucard.widget.codes.CodeCountDownTimer
import kotlinx.android.synthetic.main.base_title.*
import kotlinx.android.synthetic.main.fragment_unsubscribe_real.*

class RealUnsubscribeFragment : BaseNetworkFragment<Any>() {
    override fun getLayoutRes(): Int = R.layout.fragment_unsubscribe_real
    override fun initData() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text = "账户注销"
        tv_phone.text = LoginBean.getMobile()

        tv_submit.setOnClickListener {
            val id = et_id.text.toString()
            if (!id.matches(Regex("\\d{15}(\\d{2}[0-9xX])?"))) {
                showToast("身份证非法")
                return@setOnClickListener
            }
            val smsText = tv_code.text.toString()
            if (smsText.isNullOrBlank()) {
                showToast("请输入验证码！")
                return@setOnClickListener
            }
            showLoadingDialog()
            val map = RequestMap(NetworkAddress.CANCELLATION)
            map["mobile"] = LoginBean.getMobile()
            map["idcard-no"] = id
            map["sms-text"] = smsText
            mNetworkAdapter.request(map, MVPUtils.Method.POST, object : SimpleViewImpl<Any?>() {
                override fun onSuccess(t: Any?) {
                    showToast("注销成功")
                    dismissLoadingDialog()
                    LoginBean.clear()
                    MainActivity.start(getActivity())
                }

                override fun onFail(code: Int, msg: String?) {
                    showToast(msg)
                    dismissLoadingDialog()
                }
            })
        }

        cv_code.setOnClickListener {
            showLoadingDialog()
            val map = RequestMap(NetworkAddress.GEN_CANCEL_SMS)
            map["mobile"] = LoginBean.getMobile()
            map["idcard-no"] = et_id.text.toString()
            mNetworkAdapter.request(map, MVPUtils.Method.POST, object : SimpleViewImpl<Any?>() {
                override fun onSuccess(t: Any?) {
                    dismissLoadingDialog()
                    CodeCountDownTimer(60, cv_code).start()
                }

                override fun onFail(code: Int, msg: String?) {
                    showToast(msg)
                    dismissLoadingDialog()
                }
            })
        }

    }

    override fun onSuccess(t: Any) {
    }

    override fun onFail(code: Int, msg: String?) {
    }
}