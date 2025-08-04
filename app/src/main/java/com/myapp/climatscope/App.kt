package com.myapp.climatscope

import android.app.Application
import com.myapp.climatscope.di.DependencyContainer

class App : Application() {

    lateinit var dependencyContainer: DependencyContainer
        private set

    override fun onCreate() {
        super.onCreate()
        dependencyContainer = DependencyContainer(this)
    }
}