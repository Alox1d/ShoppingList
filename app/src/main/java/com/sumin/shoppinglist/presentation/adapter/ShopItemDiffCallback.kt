package com.sumin.shoppinglist.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.sumin.shoppinglist.domain.ShopItem

/**
 * Doing calculations in a worker thread, instead of main via DiffUtil.Callback(), when calling adapter.submitList(list).
 */
class ShopItemDiffCallback : DiffUtil.ItemCallback<ShopItem>() {
    override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
        return oldItem == newItem
    }
}