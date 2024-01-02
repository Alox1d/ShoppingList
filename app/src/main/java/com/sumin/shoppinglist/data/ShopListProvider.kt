package com.sumin.shoppinglist.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.sumin.shoppinglist.domain.ShopItem
import com.sumin.shoppinglist.presentation.ShopApp
import javax.inject.Inject

class ShopListProvider : ContentProvider() {

    private val component by lazy {
        (context as ShopApp).component
    }

    @Inject
    lateinit var shopListDao: ShopListDao

    @Inject
    lateinit var mapper: ShopListMapper

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI("com.sumin.shoppinglist", "shop_items", GET_SHOP_ITEMS_QUERY)
        addURI("com.sumin.shoppinglist", "shop_items/#", GET_SHOP_ITEMS_QUERY_BY_ID)
    }

    override fun onCreate(): Boolean {
        component.inject(this)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d("ShopListProvider", "query: $uri")

        return when (uriMatcher.match(uri)) {
            GET_SHOP_ITEMS_QUERY -> {
                shopListDao.getShopListCursor()
            }

            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            GET_SHOP_ITEMS_QUERY -> {
                if (values == null) return null
                val id = values.getAsInteger("id")
                val name = values.getAsString("name")
                val count = values.getAsInteger("count")
                val enabled = values.getAsBoolean("enabled")
                val shopItem = ShopItem(name = name, count = count, enabled = enabled, id = id)

                shopListDao.addShopItemSync(mapper.mapDomainToDbModel(shopItem))
            }
        }
        return null
    }

    // Selection = WHERE (in SQL)
    // selection = "id = ? AND name = ?"
    // selectionArgs = "5", "TWIX"
    //
    // returns int - How many strokes were deleted
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        when (uriMatcher.match(uri)) {
            GET_SHOP_ITEMS_QUERY -> {
                val id = selectionArgs?.get(0)?.toInt() ?: -1
                return shopListDao.deleteShopItemSync(id)
            }
        }
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        when (uriMatcher.match(uri)) {
            GET_SHOP_ITEMS_QUERY -> {
                if (values == null) return 0
                val id = values.getAsInteger("id")
                val name = values.getAsString("name")
                val count = values.getAsInteger("count")
                val enabled = values.getAsBoolean("enabled")
                val shopItem = ShopItem(name = name, count = count, enabled = enabled, id = id)

                shopListDao.addShopItemSync(mapper.mapDomainToDbModel(shopItem))
            }
        }
        return 0
    }

    companion object {
        private const val GET_SHOP_ITEMS_QUERY = 100
        private const val GET_SHOP_ITEMS_QUERY_BY_ID = 101
    }
}