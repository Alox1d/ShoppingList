package ru.alox1d.secondapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getShopItemsByContentResolver()
    }

    private fun getShopItemsByContentResolver() {
        thread {
            val cursor = contentResolver.query(
                Uri.parse("content://com.sumin.shoppinglist/shop_items"),
                null,
                null,
                null,
                null,
                null,
            )
            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val count = cursor.getInt(cursor.getColumnIndexOrThrow("count"))
                val enabled = cursor.getInt(cursor.getColumnIndexOrThrow("enabled")) > 0
                val shopItem = ShopItem(name = name, count = count, enabled = enabled, id = id)
                Log.d("MainActivity2", shopItem.toString())
            }
            cursor?.close()
        }
    }
}