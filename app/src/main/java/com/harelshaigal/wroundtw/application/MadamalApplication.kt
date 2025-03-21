package com.harelshaigal.aroundtw.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.harelshaigal.aroundtw.helpers.LocationHelper
import com.google.firebase.FirebaseApp

class AroundTwApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        LocationHelper.initialize(this)
        FirebaseApp.initializeApp(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

}