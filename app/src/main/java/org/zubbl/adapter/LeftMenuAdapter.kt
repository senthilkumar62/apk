package org.zubbl.adapter

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.zubbl.R
import org.zubbl.interfaces.SideMenuInterface

class LeftMenuAdapter (ctx: Context, menU_TITLE: Array<String>, private var sideMenuInterface: SideMenuInterface) : RecyclerView.Adapter<LeftMenuAdapter.ViewHolder>() {
    var context: Context = ctx
    var list: Array<String> = menU_TITLE
    private var inflater: LayoutInflater
    private var selectedPos=0

    init {
        inflater = LayoutInflater.from(context)
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.content_sidemenu, parent, false))
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
       // holder.tvId.isSelected = selectedPos == position;

        if (position==1) {
            holder.tvZubbl.visibility=View.VISIBLE
        }
        else
        {
            holder.tvZubbl.visibility=View.GONE
        }
        holder.tvId.text = list[position]


        holder.itemView.setOnClickListener({
            sideMenuInterface.sideMenuClick(holder.adapterPosition)
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvId: AppCompatTextView = itemView.findViewById(R.id.tv_title_sidemenu)
        internal var tvZubbl:AppCompatTextView=itemView.findViewById(R.id.tv_zubbl_sidemenu)
    }
}