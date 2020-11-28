package com.hfax.ucard.modules.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.hfax.ucard.R
import com.hfax.ucard.bean.CouponDetailBean.CouponDetail
import com.hfax.ucard.utils.UCardUtil
import kotlinx.android.synthetic.main.item_coupon_unused.view.*

class CouponAdapter(var list: List<CouponDetail>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var reslut: View
        val vh: ViewHold
        if (convertView == null) {
            reslut = LayoutInflater.from(parent?.context).inflate(R.layout.item_coupon_unused, parent, false)
            vh = ViewHold(reslut)
            reslut.tag = vh
        } else {
            reslut = convertView
            vh = reslut.tag as ViewHold
        }
        vh.update(getItem(position))
        return reslut
    }

    override fun getItem(position: Int): CouponDetail {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getCount(): Int {
        return list.size
    }

    class ViewHold(view: View) {
        var view: View = view

        fun update(couponDetail: CouponDetail) {
            if (couponDetail.type == 2) {
                view.tv_money_label.text = "天"
                view.tv_money?.text = "${couponDetail.duration}"
            }else{
                view.tv_money?.text = UCardUtil.formatAmount(couponDetail.denomination)
            }
            when (couponDetail.status) {
                100 -> {
                    if (couponDetail.type == 2) {
                        view.iv_bg?.setImageResource(R.drawable.coupon_pic_unuse_2)
                    } else {
                        view.iv_bg?.setImageResource(R.drawable.coupon_pic_unuse)
                    }
                }
                200 -> {
                    view.iv_bg?.setImageResource(R.drawable.coupon_pic_used)
                }
                300 -> {
                    view.iv_bg?.setImageResource(R.drawable.coupon_pic_expired)
                }
            }
            view.tv_name.text = couponDetail.typeLabel ?: "优惠券"
            var text = "";
            if (!UCardUtil.isCollectionEmpty(couponDetail.description)) {
                var i = 0;
                for (str: String in couponDetail.description) {
                    if (++i > 1) {
                        text = "$text\n"
                    }
                    text = "$text$i,$str"
                }
            }
            view.tv_content?.text = text
        }
    }

}