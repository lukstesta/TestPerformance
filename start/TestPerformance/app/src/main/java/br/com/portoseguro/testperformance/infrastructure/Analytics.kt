package br.com.portoseguro.testperformance.infrastructure

import android.util.Log

class Analytics {

    fun trackState(contentData: Map<String, String>) {
        Log.d("Log Analytics", contentData.toString())
    }
}