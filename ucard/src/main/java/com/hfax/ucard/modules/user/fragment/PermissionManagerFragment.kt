package com.hfax.ucard.modules.user.fragment

import android.Manifest
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hfax.ucard.R
import com.hfax.ucard.base.BaseNetworkFragment
import com.hfax.ucard.modules.user.adapter.PermissionAdapter
import com.hfax.ucard.modules.user.adapter.PermissionBean
import kotlinx.android.synthetic.main.base_title.*
import kotlinx.android.synthetic.main.fragment_permission_manager.*

class PermissionManagerFragment : BaseNetworkFragment<Any>() {

    override fun getLayoutRes(): Int = R.layout.fragment_permission_manager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title?.text = "权限授权管理"
        recycle_permission?.layoutManager = LinearLayoutManager(getActivity())
    }

    override fun onResume() {
        super.onResume()
        if (recycle_permission?.adapter == null) {
            val list = mutableListOf<PermissionBean>()
            list.add(PermissionBean("通讯录权限", listOf(Manifest.permission.READ_CONTACTS)))
            list.add(PermissionBean("定位权限", listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)))
            list.add(PermissionBean("相机权限", listOf(Manifest.permission.CAMERA)))
            list.add(PermissionBean("其他权限", listOf("其他权限")))
            recycle_permission?.adapter = PermissionAdapter(list)
        } else {
            recycle_permission?.adapter?.notifyDataSetChanged()
        }
    }

    override fun onSuccess(t: Any?) {
    }

    override fun onFail(code: Int, msg: String?) {
    }

}