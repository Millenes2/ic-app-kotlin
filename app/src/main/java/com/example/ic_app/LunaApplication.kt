package com.example.ic_app

import android.app.Application
import com.example.ic_app.data.remote.RetrofitClient

class LunaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}
