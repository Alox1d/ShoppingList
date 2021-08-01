package com.sumin.shoppinglist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import com.sumin.shoppinglist.R
import com.sumin.shoppinglist.databinding.ItemShopDisabledBinding
import com.sumin.shoppinglist.databinding.ItemShopEnabledBinding
import com.sumin.shoppinglist.domain.ShopItem

class ShopListAdapter :
    ListAdapter<ShopItem, ShopItemViewHolder>(ShopItemDiffCallback()) {

    var onShopItemLongClickListener: ((shopItem: ShopItem) -> Unit)? = null
    var onShopItemClickListener: ((shopItem: ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {

        val layoutId = when (viewType) {
            ShopStatus.ACTIVE.value -> R.layout.item_shop_enabled
            ShopStatus.DISABLED.value -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Unknown ViewType: $viewType") // TODO: throw in debug (no)
        }
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )
        return ShopItemViewHolder(binding)
    }


    override fun onBindViewHolder(viewHolder: ShopItemViewHolder, position: Int) {
        val shopItem = getItem(position)
        val binding = viewHolder.binding
        binding.root.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(shopItem)
            true
        }
        binding.root.setOnClickListener {
            onShopItemClickListener?.invoke(shopItem)
        }
        when (binding) {
            is ItemShopDisabledBinding -> {
                binding.tvName.text = shopItem.name
                binding.tvCount.text = shopItem.count.toString()
            }
            is ItemShopEnabledBinding -> {
                binding.tvName.text = shopItem.name
                binding.tvCount.text = shopItem.count.toString()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).enabled) ShopStatus.ACTIVE.value else ShopStatus.DISABLED.value
    }

    /* Used for RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>()
    var shopList = listOf<ShopItem>()
    set(value) {
        val callback = ShopListDiffCallback(field, value)
        val diffResult = DiffUtil.calculateDiff(callback)
        diffResult.dispatchUpdatesTo(this)
        field = value
    }
    override fun getItemCount(): Int = itemCount
     */

    companion object {
        // Из-за появления 2-го ViewType'а при большом кол-ве элементов (>100)
        // у нас начинают создавать новые ViewHolder'ы в пуле.
        // Это происходит по той причине, что изначальный пул небольшой (DEFAULT_MAX_SCRAP = 5),
        // в нём копятся ViewHolder'ы одного и того же ViewType'а (например их как раз 5 шт.)
        // и ViewHolder'а для другого ViewType'а просто нет.
        // Поэтому его приходится создавать - вызывается onCreateViewHolder
        // При этом:
        // 1. На устройствах с очень большим экраном его придётся увеличивать.
        // 2. Слишком большой пул лучше не делать - они будут просто висеть в памяти
        const val MAX_POOL_SIZE = 15
    }
}