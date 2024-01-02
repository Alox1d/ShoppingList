package com.sumin.shoppinglist.di

import android.app.Application
import com.sumin.shoppinglist.data.ShopListProvider
import com.sumin.shoppinglist.presentation.MainActivity
import com.sumin.shoppinglist.presentation.ShopItemFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(shopItemFragment: ShopItemFragment)
    fun inject(shopListProvider: ShopListProvider)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }
}