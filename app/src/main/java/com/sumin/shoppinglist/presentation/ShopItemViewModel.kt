package com.sumin.shoppinglist.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sumin.shoppinglist.data.ShopListRepositoryImpl
import com.sumin.shoppinglist.domain.AddShopItemUseCase
import com.sumin.shoppinglist.domain.EditShopItemUseCase
import com.sumin.shoppinglist.domain.GetShopItemUseCase
import com.sumin.shoppinglist.domain.ShopItem

class ShopItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository =
        ShopListRepositoryImpl(application) // Wrong: Presentation KNOWS about DATA LAYER

    private val getShopItemUseCase =
        GetShopItemUseCase(repository) // to get ShopItem on edit screen
    private val addShopListUseCase = AddShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    private val _currentShopItem = MutableLiveData<ShopItem>()
    val currentShopItem: LiveData<ShopItem>
        get() = _currentShopItem

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    fun getShopItem(shopItemId: Int) {
        val item = getShopItemUseCase.getShopItem(shopItemId)
        _currentShopItem.postValue(item)
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val isFieldsValid = validateInput(name, count)
        if (isFieldsValid) {
            addShopListUseCase.addShopItem(
                ShopItem(
                    name = name,
                    count = count,
                    enabled = true
                )
            )
            finishWork()
        }
    }

    fun editShowItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val isFieldsValid = validateInput(name, count)
        if (isFieldsValid) {
            _currentShopItem.value?.let {
                editShopItemUseCase.editShopItem(it.copy(name = name, count = count))
                finishWork()
            }
        }
    }

    fun resetErrorInputName(){
        _errorInputName.postValue(false)
    }

    fun resetErrorInputCount(){
        _errorInputCount.postValue(false)
    }

    private fun finishWork() {
        _shouldCloseScreen.postValue(Unit)
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return inputCount?.trim()?.toIntOrNull()
            ?: 0
    }

    private fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.postValue(true)
            result = false
        }
        if (count <= 0) {
            _errorInputCount.postValue(true)
            result = false
        }
        return result
    }
}