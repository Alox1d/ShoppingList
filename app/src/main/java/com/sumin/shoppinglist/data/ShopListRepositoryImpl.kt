package com.sumin.shoppinglist.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.sumin.shoppinglist.domain.ShopItem
import com.sumin.shoppinglist.domain.ShopListRepository

class ShopListRepositoryImpl(application: Application) : ShopListRepository {

    private val shopListDao = AppDatabase.getInstance(application).shopListDao()
    private val mapper = ShopListMapper()

    override fun addShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(mapper.mapDomainToDbModel(shopItem))
    }

    override fun editShopItem(shopItem: ShopItem) {
        addShopItem(shopItem)
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopListDao.deleteShopItem(shopItem.id)
    }

    override fun getShopItem(id: Int): ShopItem {
        val dbModel = shopListDao.getShopItem(id)
        return mapper.mapDbModelToDomain(dbModel)
    }

    override fun getShopList(): LiveData<List<ShopItem>> =
        shopListDao.getShopList().map { mapper.mapListDbModelToListDomain(it) }
    // Map via MediatorLiveData
    /*
    MediatorLiveData<List<ShopItem>>().apply {
        addSource(shopListDao.getShopList()){
            value = mapper.mapListDbModelToListDomain(it)
        }
    }
     */
}