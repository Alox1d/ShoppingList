//package com.sumin.shoppinglist.data.old
//
//import android.app.Application
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.sumin.shoppinglist.data.AppDatabase
//import com.sumin.shoppinglist.domain.ShopItem
//import com.sumin.shoppinglist.domain.ShopItem.Companion.UNDEFINED_ID
//import com.sumin.shoppinglist.domain.ShopListRepository
//import kotlin.random.Random
//
//class OldShopListRepositoryImpl(application: Application) : ShopListRepository {
//
//    private val shopListDao = AppDatabase.getInstance(application).shopListDao()
//
//    private val shopList = sortedSetOf<ShopItem>(
//        comparator = { item1, item2 -> item1.id.compareTo(item2.id) })
//    private val shopListLiveData = MutableLiveData(listOf<ShopItem>())
//    private var autoIncrementId = 0
//
//    init {
//        for (i in 0 until 10) {
//            val item = ShopItem("Name $i", i, Random.nextBoolean())
//            addShopItem(item)
//        }
//    }
//
//    override fun addShopItem(shopItem: ShopItem) {
//        if (shopItem.id == UNDEFINED_ID) {
//            shopItem.id = autoIncrementId++
//        }
//        shopList.add(shopItem)
//        updateList()
//    }
//
//    override fun editShopItem(shopItem: ShopItem) {
//        val oldShopItem = getShopItem(shopItem.id)
//        shopList.remove(oldShopItem)
//        addShopItem(shopItem)
//    }
//
//    override fun deleteShopItem(shopItem: ShopItem) {
//        shopList.remove(shopItem)
//        updateList()
//    }
//
//    override fun getShopItem(id: Int): ShopItem {
//        return shopList.find {
//            it.id == id
//        } ?: throw RuntimeException("Element with id = $id not found")
//    }
//
//    override fun getShopList(): LiveData<List<ShopItem>> {
//        return shopListLiveData
//    }
//
//    private fun updateList() {
//        shopListLiveData.value = shopList.toList()
//    }
//}