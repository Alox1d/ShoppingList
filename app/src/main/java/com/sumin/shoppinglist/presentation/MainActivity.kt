package com.sumin.shoppinglist.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sumin.shoppinglist.R
import com.sumin.shoppinglist.databinding.ActivityMainBinding
import com.sumin.shoppinglist.presentation.adapter.ShopListAdapter
import com.sumin.shoppinglist.presentation.adapter.ShopListAdapter.Companion.MAX_POOL_SIZE
import com.sumin.shoppinglist.presentation.adapter.ShopStatus
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val component by lazy {
        (application as ShopApp).component
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var binding: ActivityMainBinding

    // Adding by LinearLayout instead of RecyclerView
    // private lateinit var llShopList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Adding by LinearLayout
        // llShopList = findViewById(R.id.ll_shop_list)
        setupRecyclerView()

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        //Same: viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.shopList.observe(this) {
            Log.i("TAG", "onCreate: $it")

            /**
             * Doing calculations in a worker thread, instead of main via DiffUtil.Callback(), when calling adapter.submitList(list).
             */
            shopListAdapter.submitList(it)
        }

        binding.buttonAddShopItem.setOnClickListener {
            if (isOnePaneMode()) {
                startActivity(ShopItemActivity.newIntentAddItem(this))
            } else {
                launchFragment(ShopItemFragment.newInstanceAddItem())
            }
        }
    }

    override fun onEditingFinished() {
        Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
        supportFragmentManager.popBackStack()
    }

    private fun setupRecyclerView() {
        with(binding.rvShopList) {
            shopListAdapter = ShopListAdapter()
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(
                ShopStatus.ACTIVE.value,
                MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                ShopStatus.DISABLED.value,
                MAX_POOL_SIZE
            )
        }
        setupLongClickListener()
        setupClickListener()
        setupSwipeListener(binding.rvShopList)
    }

    private fun setupSwipeListener(rvShopList: RecyclerView) {
        val callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = shopListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }

    private fun setupClickListener() {
        shopListAdapter.onShopItemClickListener = { shopItem ->
            Log.i("TAG", "onShopItemClickListener: $shopItem")

            if (isOnePaneMode()) {
                startActivity(ShopItemActivity.newIntentEditItem(this, shopItem.id))
            } else {
                launchFragment(ShopItemFragment.newInstanceEditItem(shopItem.id))
            }
        }
    }

    private fun isOnePaneMode() = binding.shopItemContainer == null

    private fun launchFragment(shopItemFragment: ShopItemFragment) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            // We should use replace instead of add, becuase otherwise fragments will be increased with each rotation
            // .add(R.id.shop_item_container, fragment)
            .replace(R.id.shop_item_container, shopItemFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupLongClickListener() {
        shopListAdapter.onShopItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
    }

    // Adding by LinearLayout, instead of RecyclerView
    /*
    private fun showList(list: List<ShopItem>) {
        llShopList.removeAllViews()
        for (shopItem in list) { // Создаются вьюшки для КАЖДОГО элемента списка (даже если 10 000)
            val layoutId = if (shopItem.enabled) {
                R.layout.item_shop_enabled
            } else {
                R.layout.item_shop_disabled
            }
            val view = LayoutInflater.from(this).inflate(layoutId, llShopList, false) // Ресурсозатратно
            val tvName = view.findViewById<TextView>(R.id.tv_name) // Ресурсозатратно
            val tvCount = view.findViewById<TextView>(R.id.tv_count) // Ресурсозатратно
            tvName.text = shopItem.name
            tvCount.text = shopItem.count.toString()
            view.setOnLongClickListener {
                viewModel.changeEnableState(shopItem)
            }
            llShopList.addView(view)
        }
    }
     */
}