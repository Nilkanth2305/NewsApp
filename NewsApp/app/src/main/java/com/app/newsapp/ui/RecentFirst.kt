package com.app.newsapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.newsapp.R
import com.app.newsapp.adapter.NewsAdapter
import com.app.newsapp.model.APIData
import com.app.newsapp.model.Articles
import com.app.newsapp.model.Source
import com.app.newsapp.network.RetrofitClient
import com.app.newsapp.utils.toast
import com.app.newsapp.utils.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_recent_first.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecentFirst : Fragment() {

    val TAG = RecentFirst::class.java.name
    var recentList: ArrayList<Articles<Source>> = ArrayList()
    var adapter: NewsAdapter? = null
    var isData: Boolean = true
    var pageCount = 1
    var searchKey: String = "apple"
    var progressBar: ProgressBar? = null
    var count = 0
    var tvCount: TextView? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = LayoutInflater.from(context).inflate(
            R.layout.activity_recent_first,
            container,
            false
        )

        progressBar = v.findViewById(R.id.progressbar)
        tvCount = v.findViewById(R.id.tvCount)
        adapter = NewsAdapter(requireContext(), recentList)
        v.rv.layoutManager = LinearLayoutManager(requireContext())
        v.rv.adapter = adapter


        v.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    Log.wtf(TAG, "Scroll Page: Last Pos")
                    if (isData) {
                        getRecentNews(searchKey, pageCount, false)
                    }
                }
            }
        })

        getRecentNews(searchKey, pageCount, true)

        return v
    }

    private fun getRecentNews(q: String, page: Int, isFirst: Boolean) {
        if (isFirst) {
            Utils.showLoader(context)
        } else {
            progressBar?.visibility = View.VISIBLE
        }

        Log.wtf(TAG, "PageCount: $page : $q")
        val data: HashMap<String, String> = HashMap()
        data["domains"] = "wsj.com"
        data["pageSize"] = "15"
        data["page"] = page.toString()
        data["apiKey"] = Constant.API_KEY
        pageCount++

        RetrofitClient.instance.GetAllArticles(data)
            .enqueue(object : Callback<APIData<Articles<Source>>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<APIData<Articles<Source>>>,
                    response: Response<APIData<Articles<Source>>>
                ) {
                    if (isFirst) {
                        Utils.hideLoader()
                    } else {
                        Utils.hideLoader()
                        progressBar?.visibility = View.GONE
                    }
                    if (response.isSuccessful) {

                        Log.wtf(TAG, "Success: " + Gson().toJson(response.body()))

                        if (response.body()?.articles?.isEmpty()!!) {
                            isData = false
                        } else {
                            isData = true
                            recentList.addAll(response.body()?.articles!!)
                            adapter?.notifyDataSetChanged()
                            count+= response.body()?.articles!!.size
                            tvCount?.text="Count($count)"
                        }
                    } else {
                        Log.wtf(
                            TAG,
                            "Error: " + Gson().toJson(response.errorBody()) + " : " + Gson().toJson(
                                response.body()
                            )
                        )
                        context?.toast("Error: " + response.errorBody())
                    }
                }

                override fun onFailure(call: Call<APIData<Articles<Source>>>, t: Throwable) {
                    if (isFirst) {
                        Utils.hideLoader()
                    } else {
                        Utils.hideLoader()
                        progressBar?.visibility = View.GONE
                    }
                    Log.wtf(TAG, "Failed: " + t.message)
                    context?.toast("Failed: " + t.message)
                }
            })
    }
}