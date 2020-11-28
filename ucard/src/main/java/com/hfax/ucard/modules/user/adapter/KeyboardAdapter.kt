package com.hfax.ucard.modules.user.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hfax.ucard.R

class KeyboardAdapter(showX: Boolean = false) : RecyclerView.Adapter<ViewHolder>() {
    var keyboardListener: KeyboardListener? = null
    val showX = showX
    val data = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "X", "0", "DEL");
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context)?.inflate(R.layout.item_keyboard, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder?.tv?.setBackgroundResource(0)
        holder?.iv?.visibility = View.GONE
        holder?.tv?.visibility = View.VISIBLE
        if (item == "DEL") {
            holder?.tv?.visibility = View.GONE
            holder?.iv?.visibility = View.VISIBLE
            holder?.iv?.setOnClickListener {
                keyboardListener?.onDelete()
            }
        } else if (item == "X") {
            if (showX) {
                holder?.tv?.text = item
                holder?.tv?.setOnClickListener {
                    keyboardListener?.onSelectX(item)
                }
            }
        } else {
            holder?.tv?.setBackgroundResource(R.drawable.selector_keyboard)
            holder?.tv?.text = item
            holder?.tv?.setOnClickListener {
                keyboardListener?.onSelect(item)
            }
        }
    }
}

class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val tv = itemView?.findViewById<TextView>(R.id.tv)
    val iv = itemView?.findViewById<ImageView>(R.id.iv)
}

interface KeyboardListener {
    fun onDelete()
    fun onSelect(num: String)
    fun onSelectX(x: String)
}