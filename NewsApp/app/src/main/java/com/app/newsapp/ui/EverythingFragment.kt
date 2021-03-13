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
import com.app.newsapp.utils.hideKeyboard
import com.app.newsapp.utils.toast
import com.app.newsapp.utils.Utils.hideLoader
import com.app.newsapp.utils.Utils.showLoader
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_everything_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EverythingFragment : Fragment() {

    val TAG = EverythingFragment::class.java.name
    var articlesList: ArrayList<Articles<Source>> = ArrayList()
    var adapter: NewsAdapter? = null
    var isData: Boolean = true
    var pageCount = 1
    var searchKey: String = "apple"
    var progressBar: ProgressBar? = null
    var tvCount: TextView?=null
    var count=0

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = LayoutInflater.from(context).inflate(
            R.layout.activity_everything_fragment,
            container,
            false
        )

        progressBar = v.findViewById(R.id.progressbar);
        tvCount = v.findViewById(R.id.tvCount)
        adapter = NewsAdapter(requireContext(), articlesList)
        v.rv.layoutManager = LinearLayoutManager(requireContext())
        v.rv.adapter = adapter

        v.edtSearch.setOnTouchListener { v, event ->
            v.isFocusable = true
            v.isFocusableInTouchMode = true
            false
        }


        v.btnSearch.setOnClickListener {
            context?.hideKeyboard(it)
            if (v.edtSearch.text.toString().isNotEmpty()) {
                articlesList.clear()
                count=0
                pageCount = 1
                searchKey = v.edtSearch.text.toString()
                tvCount?.text="Count($count)"
                getAllArticles(searchKey, pageCount, true)
            } else {
                context?.toast("Enter Search key")
            }
        }

        v.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    Log.wtf(TAG, "Scroll Page: Last Pos")
                    if (isData) {
                        getAllArticles(searchKey, pageCount, false)
                    }
                }
            }
        })

        getAllArticles(searchKey, pageCount, true)

        return v
    }

    private fun getAllArticles(q: String, page: Int, isFirst: Boolean) {
        if (isFirst) {
            showLoader(context)
        } else {
            progressBar?.visibility = View.VISIBLE
        }

        Log.wtf(TAG, "PageCount: $page : $q")

        val data: HashMap<String, String> = HashMap()
        data["q"] = q
        data["qInTitle"] = "bitcoin"
        data["to"] = "2021-03-11"
        data["from"] = "2021-03-11"
        data["sortBy"] = "popularity"
        data["pageSize"] = "15"
        data["page"] = page.toString()
        data["language"] = "en"
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
                        hideLoader()
                    } else {
                        hideLoader()
                        progressBar?.visibility = View.GONE
                    }
                    if (response.isSuccessful) {

                        Log.wtf(TAG, "Success: " + Gson().toJson(response.body()))

                        if (response.body()?.articles?.isEmpty()!!) {
                            isData = false
                        } else {
                            isData = true
                            articlesList.addAll(response.body()?.articles!!)
                            adapter?.notifyDataSetChanged()
                            count+= response.body()?.articles!!.size
                            tvCount?.text="Count($count)"
                        }
                    } else {
                        Log.wtf(TAG, "Error: " + Gson().toJson(response.errorBody()) + " : " + Gson().toJson(response.body()))
                        context?.toast("Error: " + response.errorBody())
                    }
                }

                override fun onFailure(call: Call<APIData<Articles<Source>>>, t: Throwable) {
                    if (isFirst) {
                        hideLoader()
                    } else {
                        hideLoader()
                        progressBar?.visibility = View.GONE
                    }
                    Log.wtf(TAG, "Failed: " + t.message)
                    context?.toast("Failed: " + t.message)
                }
            })
    }
}