package com.example.crud_firebase_kotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter( private var empList: ArrayList<Data>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    private lateinit var EliminarListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onItemDeleteClick(position: Int)
        //fun onItemMenuClick(position: Int)
        fun onItemMenuClick(position: Int, view: View)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = empList[position]
        holder.tvEmpName.text = currentEmp.text
        Glide.with(holder.recImage.context)
            .load(currentEmp.imageUrl)
            .into(holder.recImage)
    }

    override fun getItemCount(): Int {
        return empList.size
    }


    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val recImage: ImageView = itemView.findViewById(R.id.recImage)
        val tvEmpName : TextView = itemView.findViewById(R.id.recTitle)
        val Eliminar : ImageView = itemView.findViewById(R.id.idEliminar)
        val mMenus : ImageView = itemView.findViewById(R.id.mMenus)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
            Eliminar.setOnClickListener {
                clickListener.onItemDeleteClick(adapterPosition)
            }
            mMenus.setOnClickListener {
                clickListener.onItemMenuClick(adapterPosition, it)
            }
        }

    }

    fun deleteItem(position: Int) {
        empList.removeAt(position)
        notifyItemRemoved(position)
    }
    fun searchDataList(searchList: List<Data>) {
        empList = searchList as ArrayList<Data>
        notifyDataSetChanged()
    }

}