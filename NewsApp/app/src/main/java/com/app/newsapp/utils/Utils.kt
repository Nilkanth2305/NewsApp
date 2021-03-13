package com.app.newsapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.app.newsapp.R

object Utils{

    var ProgressDialog: Dialog?=null

    fun showLoader(context: Context?) {
        ProgressDialog =
            Dialog(context!!, R.style.PauseDialog)
        ProgressDialog?.setCancelable(true)
        ProgressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ProgressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        ProgressDialog?.setContentView(R.layout.progressbar_dialog)
        if (!ProgressDialog!!.isShowing) {
            ProgressDialog?.show()
        }
    }

    fun hideLoader() {
        if (ProgressDialog!!.isShowing) {
            ProgressDialog?.dismiss()
        }
    }

    fun <K, V> getKey(map: Map<K, V>, target: V): K? {
        for ((key, value) in map) {
            if (target == value) {
                return key
            }
        }
        return null
    }
}