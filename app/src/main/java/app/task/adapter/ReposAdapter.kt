package app.task.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import app.task.R
import app.task.model.RepositoryModel
import butterknife.BindView
import butterknife.ButterKnife
import kotlin.collections.ArrayList

class ReposAdapter(private val context: Context, // vars
                   private val list: ArrayList<RepositoryModel>) : RecyclerView.Adapter<ReposAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.repo_item, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_name!!.text = list[position].name
        holder.tv_lang!!.text = list[position].language
        val string : String  = context.getString(R.string.Views)

        holder.tv_watchers!!.text = list[position].watchers.plus(string)
        holder.tv_link!!.setOnClickListener { if (list[position].url != null) openWebPage(list[position].url) }
    }

    fun openWebPage(url: String?) {
        try {
            val webpage = Uri.parse(url)
            val myIntent = Intent(Intent.ACTION_VIEW, webpage)
            context.startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context,
                    "No application can handle this request. Please install a web browser or check your URL.", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_name: TextView? = null
        var tv_lang: TextView? = null
        var tv_watchers: TextView? = null
        var tv_link: TextView? = null

        init {
            tv_name = itemView.findViewById(R.id.tv_name) as? TextView
            tv_lang = itemView.findViewById(R.id.tv_lang) as? TextView
            tv_watchers = itemView.findViewById(R.id.tv_watchers) as? TextView
            tv_link = itemView.findViewById(R.id.tv_link) as? TextView
        }
    }

}