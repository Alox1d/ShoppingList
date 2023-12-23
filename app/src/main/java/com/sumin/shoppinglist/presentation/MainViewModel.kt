package com.sumin.shoppinglist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumin.shoppinglist.domain.DeleteShopItemUseCase
import com.sumin.shoppinglist.domain.EditShopItemUseCase
import com.sumin.shoppinglist.domain.GetShopListUseCase
import com.sumin.shoppinglist.domain.ShopItem
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getShopListUseCase: GetShopListUseCase,
    private val deleteShopItemUseCase: DeleteShopItemUseCase,
    private val editShopItemUseCase: EditShopItemUseCase,
) : ViewModel() {
    // Wrong: Presentation KNOWS about DATA LAYER
    // private val repository = ShopListRepositoryImpl(application)

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