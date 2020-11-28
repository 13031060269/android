package com.hfax.ucard.modules.user

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.hfax.ucard.R
import com.hfax.ucard.base.BaseNetworkActivity
import com.hfax.ucard.bean.CouponDetailBean
import com.hfax.ucard.modules.user.fragment.CouponFragment
import com.hfax.ucard.utils.MVPUtils
import com.hfax.ucard.utils.mvp.NetworkAddress
import com.hfax.ucard.utils.mvp.RequestMap
import kotlinx.android.synthetic.main.activity_coupon.*
import kotlinx.android.synthetic.main.base_title.*

class CouponActivity() : BaseNetworkActivity<CouponDetailBean>() {
    val fs = arrayListOf<Fragment>()
    override fun getLayoutRes(): Int {
        return R.layout.activity_coupon;
    }

    override fun initData() {
        tv_title?.text = "优惠券"
    }

    override fun onLoad() {
        super.onLoad()
        showLoadingDialog()
        val map = RequestMap(NetworkAddress.QUERY_COUPONQUERY_COUPON_LIST_LIST)
        mNetworkAdapter.request(map, MVPUtils.Method.POST)
    }

    override fun initListener() {
        iv_title_return.setOnClickListener {
            finish()
        }
        vp_coupon.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                return fs[position]
            }

            override fun getCount(): Int {
                return fs.size
            }
        }
        tab_strip.setViewPager(vp_coupon)
    }

    override fun onSuccess(t: CouponDetailBean?) {
        fs.clear()
        fs.add(CouponFragment.create(t?.unused))
        fs.add(CouponFragment.create(t?.used))
        fs.add(CouponFragment.create(t?.expired))
        vp_coupon.adapter?.notifyDataSetChanged()
        dismissLoadingDialog()
        showContentView()
    }

    override fun onFail(code: Int, msg: String?) {
        dismissLoadingDialog()
        showErrorView()
//        val couponDetailBean = Gson().fromJson<CouponDetailBean>("{\"unused\":[{\"couponNo\":\"JX20191203183655NJ5\",\"type\":1,\"loanAmount\":2000000,\"denomination\":30,\"startTime\":\"2019-12-01 00:00:00\",\"endTime\":\"2019-12-31 00:00:00\",\"status\":100,\"description\":[\"审批金额满xxx元还款时可用\",\"逾期不可用\",\"有效期限：YYYY.MM.DD -YYYY.MM.DD\"]},{\"couponNo\":\"JX20191203183655NJ6\",\"type\":1,\"loanAmount\":2000000,\"denomination\":30,\"startTime\":\"2019-12-01 00:00:00\",\"endTime\":\"2019-12-31 00:00:00\",\"status\":100,\"description\":[\"审批金额满xxx元还款时可用\",\"逾期不可用\",\"有效期限：YYYY.MM.DD -YYYY.MM.DD\"]}],\"used\":[{\"couponNo\":\"JX20191203183655NJ5\",\"type\":1,\"loanAmount\":2000000,\"denomination\":30,\"startTime\":\"2019-12-01 00:00:00\",\"endTime\":\"2019-12-31 00:00:00\",\"status\":200,\"description\":[\"审批金额满xxx元还款时可用\",\"逾期不可用\",\"有效期限：YYYY.MM.DD -YYYY.MM.DD\"]}],\"expired\":[]}", CouponDetailBean::class.java)
//
//        onSuccess(couponDetailBean)
    }
}
