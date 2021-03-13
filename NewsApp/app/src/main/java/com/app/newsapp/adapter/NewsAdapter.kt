package com.app.newsapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.newsapp.model.Articles
import com.app.newsapp.model.Source
import com.app.newsapp.network.RetrofitClient
import com.app.newsapp.R
import com.app.newsapp.ui.Constant
import com.app.newsapp.ui.SingleNews
import com.app.newsapp.utils.showURLImage

class NewsAdapter(private val context: Context,list: ArrayList<Articles<Source>>) : RecyclerView.Adapter<NewsAdapter.Holder>(){

    val TAG=NewsAdapter::class.java.name
    var articleList:List<Articles<Source>>? =null

    init {
        articleList = list
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var progressBar: ProgressBar =itemView.findViewById(R.id.progressbar)
        var tvTitle:TextView=itemView.findViewById(R.id.tvTitle)
        var ivNews:ImageView=itemView.findViewById(R.id.ivNews)
        var tvContent:TextView=itemView.findViewById(R.id.tvContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.activity_news_adapter,parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.tvTitle.text = articleList!![position].title
        holder.tvContent.text = articleList!![position].description

        articleList!![position].urlToImage?.let {
            showURLImage(context, it,holder.ivNews,holder.progressBar)
        } ?: run {
            holder.progressBar.visibility = View.GONE
            holder.ivNews.setImageResource(R.drawable.ic_launcher_background)
        }

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context,SingleNews::class.java).putExtra(Constant.URL,articleList!![position].url))
        }
    }

    override fun getItemCount(): Int {
        Log.wtf(TAG,"Value : List Size: ${articleList?.size}")
        return articleList?.size!!
    }
}