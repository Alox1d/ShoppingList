package com.sumin.shoppinglist.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.sumin.shoppinglist.domain.ShopItem
import com.sumin.shoppinglist.domain.ShopListRepository
import javax.inject.Inject

class ShopListRepositoryImpl @Inject constructor(
    private val shopListDao: ShopListDao,
    private val mapper: ShopListMapper
) : ShopListRepository {

    override suspend fun addShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(mapper.mapDomainToDbModel(shopItem))
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        addShopItem(shopItem)
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        shopListDao.deleteShopItem(shopItem.id)
    }

    override suspend fun getShopItem(id: Int): ShopItem {
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