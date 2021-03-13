package com.app.newsapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.newsapp.R
import com.app.newsapp.adapter.NewsAdapter
import com.app.newsapp.model.APIData
import com.app.newsapp.model.APIDataList
import com.app.newsapp.model.Articles
import com.app.newsapp.model.Source
import com.app.newsapp.network.RetrofitClient
import com.app.newsapp.utils.toast
import com.app.newsapp.utils.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_head_lines_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HeadLinesFragment : Fragment() {

    val TAG = HeadLinesFragment::class.java.name
    var headLinesList: ArrayList<Articles<Source>> = ArrayList()
    var adapter: NewsAdapter? = null
    var isData: Boolean = true
    var pageCount = 1
    var searchKey: String = "general"
    var progressBar: ProgressBar? = null
    var mapCategory: HashMap<String, String>? = null
    var adapterCategory: ArrayAdapter<String>? = null
    var spinnerCategory:Spinner?=null
    var tvCount: TextView?=null
    var count=0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = LayoutInflater.from(context).inflate(
            R.layout.activity_head_lines_fragment,
            container,
            false
        )

        progressBar = v.findViewById(R.id.progressbar)
        tvCount = v.findViewById(R.id.tvCount)
        spinnerCategory = v.findViewById(R.id.spinnerCategory)
        adapter = NewsAdapter(requireContext(), headLinesList)
        v.rv.layoutManager = LinearLayoutManager(requireContext())
        v.rv.adapter = adapter

        getCategoryList()

        v.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    Log.wtf(TAG, "Scroll Page: Last Pos")
                    if (isData) {
                        getHeadLines(searchKey, pageCount, false)
                    }
                }
            }
        })

        getHeadLines(searchKey, pageCount, true)

        return v
    }


    private fun getHeadLines(q: String, page: Int, isFirst: Boolean) {
        if (isFirst) {
            Utils.showLoader(context)
        } else {
            progressBar?.visibility = View.VISIBLE
        }

        val data: HashMap<String, String> = HashMap()
        data["country"] = "us"
        data["category"] = q
        data["pageSize"] = "15"
        data["page"] = page.toString()
        data["apiKey"] = Constant.API_KEY
        pageCount++

        RetrofitClient.instance.GetHeadLines(data)
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
                            headLinesList.addAll(response.body()?.articles!!)
                            adapter?.notifyDataSetChanged()
                            count+= response.body()?.articles!!.size
                            tvCount?.text="Count($count)"
                        }
                    } else {
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

    fun getCategoryList() {

        val data: HashMap<String, String> = HashMap()
        data["apiKey"] = Constant.API_KEY

        RetrofitClient.instance.GetSources(data)
            .enqueue(object : Callback<APIDataList<Source>>{
                override fun onResponse(
                    call: Call<APIDataList<Source>>,
                    response: Response<APIDataList<Source>>
                ) {

                        Utils.hideLoader()

                    if (response.isSuccessful) {
                        mapCategory= HashMap()
                        Log.wtf(TAG, "Success: " + Gson().toJson(response.body()))
                        for (i in 0 until response.body()?.sources?.size!!) {
                            mapCategory!![response.body()?.sources?.get(i)?.category.toString()] =
                                    response.body()?.sources?.get(i)?.category.toString()
                        }
                        adapterCategory = context?.let { ArrayAdapter(it,android.R.layout.simple_spinner_dropdown_item,mapCategory!!.values.toList()) }

                        spinnerCategory?.adapter = adapterCategory


                        spinnerCategory?.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    Log.wtf(TAG, "Selected Item: Nothing general")
                                    searchKey = "general"
                                }

                                @SuppressLint("SetTextI18n")
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    searchKey = Utils.getKey(mapCategory!!, parent?.getItemAtPosition(position)).toString()
                                    Log.d(TAG, "Category Selected: $searchKey")
                                    headLinesList.clear()
                                    pageCount = 1
                                    count=0
                                    tvCount?.text="Count($count)"
                                    getHeadLines(searchKey, pageCount, true)

                                }
                            }

                    }else{
                        context?.toast("Error: " + response.errorBody())
                    }
                }

                override fun onFailure(call: Call<APIDataList<Source>>, t: Throwable) {
                    Utils.hideLoader()
                    Log.wtf(TAG, "Failed: " + t.message)
                    context?.toast("Failed: " + t.message)
                }
            })
    }
}