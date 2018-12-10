package org.zubbl.adapter

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import android.widget.ToggleButton
import org.zubbl.R
import org.zubbl.interfaces.SideMenuInterface

class SettingAdapter (ctx: Context, menU_TITLE: Array<String>, iconsList:Array<Int>,private var sideMenuInterface: SideMenuInterface) : RecyclerView.Adapter<SettingAdapter.ViewHolder>() {
    var context: Context = ctx
    var list: Array<String> = menU_TITLE
    private var iconsList=iconsList
    private var inflater: LayoutInflater


    init {
        inflater = LayoutInflater.from(context)
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.content_settings, parent, false))
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        if(position==2){
            holder.ivIcon.visibility=View.GONE
            holder.toggle.visibility=View.VISIBLE
            holder.toggle.isClickable=true
            if(holder.toggle.isChecked){
                Toast.makeText(context,"on",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"off",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            holder.ivIcon.visibility=View.VISIBLE
            holder.toggle.visibility=View.GONE
        }

        holder.tvName.text=list[position]
        holder.ivIcon.setImageResource(iconsList[position])

        holder.ivIcon.setOnClickListener({
            sideMenuInterface.sideMenuClick(holder.adapterPosition)
        })

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvName: AppCompatTextView = itemView.findViewById(R.id.tv_name_setting)
        internal var ivIcon: ImageView=itemView.findViewById(R.id.iv_icon_setting)
        internal var toggle:CheckBox=itemView.findViewById(R.id.toggle_settings)
    }
}