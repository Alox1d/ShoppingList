package com.sumin.shoppinglist.data

import com.sumin.shoppinglist.domain.ShopItem

class ShopListMapper {
    fun mapDomainToDbModel(shopItem: ShopItem) = ShopItemDbModel(
        id = shopItem.id,
        name = shopItem.name,
        count = shopItem.count,
        enabled = shopItem.enabled
    )

    fun mapDbModelToDomain(shopItemDbModel: ShopItemDbModel) = ShopItem(
        id = shopItemDbModel.id,
        name = shopItemDbModel.name,
        count = shopItemDbModel.count,
        enabled = shopItemDbModel.enabled
    )

    fun mapListDbModelToListDomain(list: List<ShopItemDbModel>) =
        list.map { mapDbModelToDomain(it) }
}