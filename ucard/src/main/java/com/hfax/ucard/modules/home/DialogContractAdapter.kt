package com.hfax.ucard.modules.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.hfax.ucard.R
import com.hfax.ucard.bean.HomeWindowStatusBean

class DialogContractAdapter(val list: List<HomeWindowStatusBean.Contract>) : BaseAdapter() {
    var checked: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vh: VH
        val contract: HomeWindowStatusBean.Contract = getItem(position)
        if (convertView == null) {
            vh = VH(parent?.context!!)
        } else {
            vh = convertView.tag as VH
        }
        if (checked) {
            vh.iv.visibility = View.VISIBLE
        } else {
            vh.iv.visibility = View.INVISIBLE
        }
        vh.tvName.text = contract.contractName
        return vh.rootView
    }

    override fun getItem(position: Int): HomeWindowStatusBean.Contract = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size

}

class VH {
    var rootView: View
    var iv: ImageView
    var tvName: TextView

    constructor(context: Context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.item_contract, null)
        rootView.tag = this
        iv = rootView.findViewById(R.id.iv)
        tvName = rootView.findViewById(R.id.tv_name)
    }
}
