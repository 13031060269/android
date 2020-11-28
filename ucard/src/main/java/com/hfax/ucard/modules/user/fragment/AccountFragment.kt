package com.hfax.ucard.modules.user.fragment

import android.os.Bundle
import android.view.View
import com.hfax.ucard.R
import com.hfax.ucard.base.BaseNetworkFragment
import com.hfax.ucard.base.MyFragmentActivity
import com.hfax.ucard.bean.CacheBean
import com.hfax.ucard.bean.RealNameAuthenticationBean
import com.hfax.ucard.modules.loan.CertificationActivity
import com.hfax.ucard.modules.user.ModifyLoginPwdActivity
import com.hfax.ucard.modules.user.RealNameAuthenticationActivity
import com.hfax.ucard.utils.Constants.UCardConstants
import kotlinx.android.synthetic.main.base_title.*
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : BaseNetworkFragment<Any>() {
    override fun getLayoutRes(): Int = R.layout.fragment_account
    override fun initData() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text = "我的账户"
        ll_change_pwd.setOnClickListener { ModifyLoginPwdActivity.start(getActivity()) }
        ll_unsubscribe.setOnClickListener { MyFragmentActivity.start(getActivity(), UnsubscribeFragment::class.java) }
        ll_real_name_authentication.setOnClickListener {
            val personalCenterBean: RealNameAuthenticationBean? = CacheBean.getCache(RealNameAuthenticationBean::class.java)
            if (personalCenterBean != null) {
                if (personalCenterBean.completeIdCard) {
                    RealNameAuthenticationActivity.start(getActivity(), personalCenterBean.name, personalCenterBean.idNo)
                } else {
                    CertificationActivity.start(getActivity(), UCardConstants.IDCARD_PERSON_CENTEL)
                }
            }
        }
    }

    override fun onLoad() {
        val personalCenterBean: RealNameAuthenticationBean = CacheBean.getCache(RealNameAuthenticationBean::class.java)
        if (personalCenterBean.completeIdCard) {
            tv_have_not.visibility = View.GONE;
            ll_have_name.visibility = View.VISIBLE;
            tv_name.text = personalCenterBean.name;
        } else {
            tv_have_not.visibility = View.VISIBLE;
            ll_have_name.visibility = View.GONE;
        }

    }

    override fun onSuccess(t: Any) {
    }

    override fun onFail(code: Int, msg: String?) {
    }
}