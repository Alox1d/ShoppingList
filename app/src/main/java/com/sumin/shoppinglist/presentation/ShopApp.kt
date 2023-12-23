package com.sumin.shoppinglist.presentation

import android.app.Application
import com.sumin.shoppinglist.di.DaggerAppComponent

class ShopApp : Application() {
    val component by lazy {
        DaggerAppComponent.factory().create(this)
    }
}