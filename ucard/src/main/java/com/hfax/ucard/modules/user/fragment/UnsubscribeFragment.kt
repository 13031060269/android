package com.hfax.ucard.modules.user.fragment

import android.os.Bundle
import android.view.View
import com.hfax.ucard.R
import com.hfax.ucard.base.BaseNetworkFragment
import com.hfax.ucard.base.MyFragmentActivity
import com.hfax.ucard.utils.MVPUtils
import com.hfax.ucard.utils.mvp.NetworkAddress
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl
import kotlinx.android.synthetic.main.base_title.*
import kotlinx.android.synthetic.main.fragment_unsubscribe.*

class UnsubscribeFragment : BaseNetworkFragment<Any>() {
    override fun getLayoutRes(): Int = R.layout.fragment_unsubscribe
    companion  object {
        private var delay = 30 * 1000
    }
    private var time = delay
    override fun initData() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text = "账户注销"
        timeRun()
        tv_submit.setOnClickListener {
            showLoadingDialog()
            mNetworkAdapter.request(NetworkAddress.COULD_CANCEL, MVPUtils.Method.POST, object : SimpleViewImpl<Any?>() {
                override fun onSuccess(t: Any?) {
                    dismissLoadingDialog()
                    MyFragmentActivity.start(getActivity(), RealUnsubscribeFragment::class.java)
                }

                override fun onFail(code: Int, msg: String?) {
                    showToast(msg)
                    dismissLoadingDialog()
                }
            })
        }
    }

    private fun timeRun() {
        time -= 1000
        if (time != 0) {
            tv_submit.text = "请仔细阅读注销说明 ${time / 1000}s"
            mHandler.postDelayed({ timeRun() }, 1000)
        } else {
            tv_submit.isEnabled = true
            tv_submit.textSize = 16f
            tv_submit.text = "确认申请"
        }

    }

    override fun onSuccess(t: Any) {
    }

    override fun onFail(code: Int, msg: String?) {
    }
}