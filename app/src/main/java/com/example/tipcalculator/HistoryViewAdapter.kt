package com.example.tipcalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryViewAdapter(val tipHistory: MutableList<TipData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val historyItemView = inflater.inflate(R.layout.view_history, parent, false)
        return HistoryViewHolder(historyItemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vhHistory = holder as HistoryViewHolder
        vhHistory.bind(tipHistory.get(position))
        vhHistory.btn_delete.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                val pos = tipHistory.indexOf(vhHistory.data)
                tipHistory.removeAt(pos)
                this@HistoryViewAdapter.notifyItemRemoved(pos)
            }

        })
    }

    override fun getItemCount(): Int {
        return tipHistory.size
    }

    inner class HistoryViewHolder(itemView: View): RecyclerView.ViewHolder(
        itemView
    ) {
        lateinit var data: TipData
        val tv_base: TextView = itemView.findViewById(R.id.tv_base)
        val tv_tip: TextView = itemView.findViewById(R.id.tv_tip)
        val tv_total: TextView = itemView.findViewById(R.id.tv_total)
        val tv_person: TextView = itemView.findViewById(R.id.tv_person)
        val tv_per_person: TextView = itemView.findViewById(R.id.tv_per_person)
        val btn_delete: Button = itemView.findViewById(R.id.btn_delete)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)

        fun bind(tipData: TipData) {
            data = tipData
            tv_base.text = "$${tipData.baseAmount}"
            tv_tip.text = "$%.2f".format(tipData.tip * tipData.baseAmount / 100.0)
            tv_total.text = "$${tipData.totalAmount}"
            tv_person.text = "${tipData.splitNum}"
            tv_per_person.text = "$%.2f".format(tipData.totalAmount / tipData.splitNum)
            tvDateTime.text = "${tipData.dateTime}"
        }

    }

}