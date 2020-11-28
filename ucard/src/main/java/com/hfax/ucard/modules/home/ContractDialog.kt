package com.hfax.ucard.modules.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import com.hfax.app.h5.H5Activity
import com.hfax.app.h5.H5Fragment
import com.hfax.app.widget.LoadingView
import com.hfax.ucard.R
import com.hfax.ucard.bean.ContractBean
import com.hfax.ucard.bean.HomeWindowStatusBean
import com.hfax.ucard.utils.MVPUtils
import com.hfax.ucard.utils.UCardUtil
import com.hfax.ucard.utils.mvp.DataChange
import com.hfax.ucard.utils.mvp.NetworkAddress
import com.hfax.ucard.utils.mvp.RequestMap
import com.hfax.ucard.utils.mvp.simpleImpl.SimplePresent
import com.hfax.ucard.utils.mvp.simpleImpl.SimpleViewImpl

class ContractDialog(context: Context, list: List<HomeWindowStatusBean.Contract>, dataChange: DataChange<Boolean>) {
    private val alertDialog: AlertDialog = AlertDialog.Builder(context).setView(R.layout.dialog_home_goloan).create()
    private val loadingView: LoadingView?
    private val present = SimplePresent()
    private var tvToast: TextView? = null
    private var change: DataChange<Boolean>? = dataChange

    private inline fun <reified T> request(map: RequestMap, method: MVPUtils.Method, view: SimpleViewImpl<T>) {
        present.request(map, this, method, view)
    }

    fun showLoading() {
        loadingView?.startLoading()
    }

    fun hideLoading(msg: String? = null) {
        loadingView?.visibility=View.INVISIBLE
        if (!msg.isNullOrEmpty()) {
            tvToast?.text = msg
            tvToast?.visibility = View.VISIBLE
            loadingView?.removeCallbacks(run)
            loadingView?.postDelayed(run, 2000)
        }
    }

    val run = {
        tvToast?.visibility = View.INVISIBLE
    }

    init {
        alertDialog.setOnDismissListener {
            present.destroyView()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        alertDialog.show()
        alertDialog.setCancelable(false)
        loadingView = alertDialog.findViewById(R.id.loading_view)
        tvToast = alertDialog.findViewById(R.id.tv_toast)
        val dialogContractAdapter = DialogContractAdapter(list)
        val lv = alertDialog.findViewById<ListView>(R.id.lv_dialog)
        lv?.apply {
            adapter = dialogContractAdapter
            onItemClickListener = OnItemClickListener { _, _, position, _ ->
                showLoading()
                val contract = list[position]
                val map = RequestMap(NetworkAddress.QUERY_CONTRACT_CONFIRM)
                map["templateId"] = contract.templateId
                request(map, MVPUtils.Method.GET, object : SimpleViewImpl<ContractBean>() {
                    override fun onSuccess(bean: ContractBean?) {
                        hideLoading()
                        val intent = Intent(context, H5Activity::class.java)
                        intent.putExtra(H5Fragment.KEY_URL, NetworkAddress.BASE_URL)
                        intent.putExtra(H5Fragment.KEY_TITLE, " ")
                        intent.putExtra(H5Fragment.KEY_IS_LOAD_HTML_SOURCE, "true")
                        intent.putExtra(H5Fragment.KEY_HTML_SOURCE, bean?.content)
                        UCardUtil.startActivity(context, intent)
                    }

                    override fun onFail(code: Int, msg: String?) {
                        hideLoading(msg)
                    }
                })
            }
        }
        val cb = alertDialog.findViewById<CheckBox>(R.id.cb)
        cb?.apply {
            isChecked = false
            setOnCheckedChangeListener { _, isChecked -> dialogContractAdapter.checked = isChecked }
        }
        alertDialog.findViewById<View>(R.id.iv_close)?.setOnClickListener {
            alertDialog.dismiss()
            change?.onChange(false)
        }
        alertDialog.findViewById<View>(R.id.submit)?.setOnClickListener {
            if (cb?.isChecked != true) {
                hideLoading("请先勾选协议")
                return@setOnClickListener
            }
            showLoading()
            request(RequestMap(NetworkAddress.SIGN_CONTRACT_CONFIRM), MVPUtils.Method.POST, object : SimpleViewImpl<Any>() {
                override fun onSuccess(t: Any?) {
                    change?.onChange(true)
                    change = null
                    alertDialog.dismiss()
                }

                override fun onFail(code: Int, msg: String?) {
                    hideLoading(msg)
                }
            })
        }
    }
}