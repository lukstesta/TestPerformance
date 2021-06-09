package br.com.portoseguro.testperformance.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val products = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView.findViewById<TextView>(android.R.id.text1)

        itemView.text = products[position]
    }

    fun update(list: List<String>) {
        products.clear()
        products.addAll(list)
        notifyDataSetChanged()
    }

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)
}