package com.hfax.ucard.modules.user.adapter

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.hfax.ucard.R
import com.hfax.ucard.utils.PermissionUtils

data class PermissionBean(val name: String, val value: List<String>)
class PermissionAdapter(var list: List<PermissionBean>) : RecyclerView.Adapter<PermissionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_permission, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val permission = list[position]
        holder.tvName.text = permission.name

        if (PermissionUtils.isPermissionGranted(permission.value)) {
            holder.tvAct.text = "已开启"
            holder.ivGo.visibility = View.INVISIBLE
            holder.itemView.setOnClickListener { }
        } else {
            holder.tvAct.text = "去设置"
            holder.ivGo.visibility = View.VISIBLE
            holder.itemView.setOnClickListener {
                val localIntent = Intent()
                val context = it.context
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                localIntent.data = Uri.fromParts("package", context.packageName, null)
                context?.startActivity(localIntent)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.tv_name)
        lateinit var tvName: TextView

        @BindView(R.id.tv_act)
        lateinit var tvAct: TextView

        @BindView(R.id.iv_go)
        lateinit var ivGo: View

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
