package app.task.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.task.R
import butterknife.BindView
import butterknife.ButterKnife
import java.util.*

class FavAdapter(private val context: Context, // vars
                 private val list: ArrayList<String>) : RecyclerView.Adapter<FavAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fav_item, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_country!!.text = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_country: TextView? = null

        init {
            tv_country = itemView.findViewById(R.id.tv_country) as? TextView
        }
    }

}