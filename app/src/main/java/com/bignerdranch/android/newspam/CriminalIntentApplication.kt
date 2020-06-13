package com.bignerdranch.android.newspam

import android.app.Application

class NewsPamApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NewsRepository.initialize(this)
    }
}