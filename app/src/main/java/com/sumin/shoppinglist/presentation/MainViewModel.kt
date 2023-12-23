package com.sumin.shoppinglist.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sumin.shoppinglist.data.ShopListRepositoryImpl
import com.sumin.shoppinglist.domain.DeleteShopItemUseCase
import com.sumin.shoppinglist.domain.EditShopItemUseCase
import com.sumin.shoppinglist.domain.GetShopListUseCase
import com.sumin.shoppinglist.domain.ShopItem
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository =
        ShopListRepositoryImpl(application) // Wrong: Presentation KNOWS about DATA LAYER

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopList = getShopListUseCase.getShopList()

    fun deleteShopItem(shopItem: ShopItem) {
        viewModelScope.launch {
            deleteShopItemUseCase.deleteShopItem(shopItem)
        }
    }

    fun changeEnableState(shopItem: ShopItem) {
        viewModelScope.launch {
            editShopItemUseCase.editShopItem(
                shopItem = shopItem.copy(
                    enabled = !shopItem.enabled
                )
            )
        }
    }

    //    fun getShopList() {
    //
    //        // Wrong: needs LiveData
    //        // LiveData: allows Activity to subscribe on data, e.g. LiveData<List<ShopItem<>>
    //        // return getShopListUseCase.getShopList()
    //        val list = getShopListUseCase.getShopList()
    //
    //        // setValue: требует вызова из main-потока
    //        // postValue: может быть вызван вне main, автоматически переключает на main
    //        shopList.postValue(list)
    //    }
}