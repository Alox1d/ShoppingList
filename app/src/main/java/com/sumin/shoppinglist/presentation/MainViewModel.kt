package com.sumin.shoppinglist.presentation

import androidx.lifecycle.ViewModel
import com.sumin.shoppinglist.data.ShopListRepositoryImpl
import com.sumin.shoppinglist.domain.DeleteShopItemUseCase
import com.sumin.shoppinglist.domain.EditShopItemUseCase
import com.sumin.shoppinglist.domain.GetShopListUseCase
import com.sumin.shoppinglist.domain.ShopItem

class MainViewModel : ViewModel() {
    private val repository = ShopListRepositoryImpl // Wrong: Presentation KNOWS about DATA LAYER

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopList = getShopListUseCase.getShopList()

    fun deleteShopItem(shopItem: ShopItem) {
        deleteShopItemUseCase.deleteShopItem(shopItem)
    }

    fun changeEnableState(shopItem: ShopItem) {
        editShopItemUseCase.editShopItem(
            shopItem = shopItem.copy(
                enabled = !shopItem.enabled
            )
        )
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