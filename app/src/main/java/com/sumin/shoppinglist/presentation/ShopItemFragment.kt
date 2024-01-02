package com.sumin.shoppinglist.presentation

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sumin.shoppinglist.databinding.FragmentShopItemBinding
import javax.inject.Inject
import kotlin.concurrent.thread

class ShopItemFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val component by lazy {
        (requireActivity().application as ShopApp).component
    }
    private val viewModel: ShopItemViewModel by viewModels { viewModelFactory }

    private lateinit var onEditingFinishListener: OnEditingFinishedListener

    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding == null")

    private var screenMode: String = UNDEFINED_SCREEN_MODE
    private var shopItemId: Int = UNDEFINED_ID

    override fun onAttach(context: Context) {
        component.inject(this)

        super.onAttach(context)
        if (context is OnEditingFinishedListener)
            onEditingFinishListener = context
        else
            throw RuntimeException("Activity must implement OnEditingFinishListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ShopItemFragment", "onCreate, to check recreation on rotation")

        // НЕ работаем с View здесь, т.к. вызывается ДО onViewCreated, т.е. до создания View
        super.onCreate(savedInstanceState)

        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        addTextChangeListeners()
        launchRightMode()
        observeViewModel()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun observeViewModel() {

        // Lifecycle of View can be < Lifecycle Fragment.
        //
        // Жиз. цикл this (Fragment'а) != жиз. циклу его View:
        // в  retain-фрагментах фрагмент может быть жив, а его View нет.
        // Тогда получим краш при оброщении к view-элементам из LiveData.
        // Поэтому юзаем viewLifecycleOwner

        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishListener.onEditingFinished()

            // OLD onBackPressed
            // activity nullable, т.к. мы можем к ней обратиться, когда она ещё НЕ ПРИКРЕПЛЕНА к активити
            // activity?.onBackPressed()

            // requireActivity() throws IllegalStateException
            // requireActivity().onBackPressed()
        }
        // Without DataBinding
        // viewModel.errorInputName.observe(viewLifecycleOwner) {
        //     binding.tilName.error = if (it) getString(R.string.error_input_name) else null
        // }
        // viewModel.errorInputCount.observe(viewLifecycleOwner) {
        //     binding.tilCount.error = if (it) getString(R.string.error_input_count) else null
        // }
    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    private fun addTextChangeListeners() {
        binding.etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(p0: Editable?) = Unit
        })
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(p0: Editable?) = Unit

        })
    }

    private fun launchAddMode() {
        binding.saveButton.setOnClickListener {
            // add to DB by viewModel
            // viewModel.addShopItem(binding.etName.text.toString(), binding.etCount.text.toString())

            thread {
                context?.contentResolver?.insert(
                    Uri.parse("content://com.sumin.shoppinglist/shop_items"),
                    ContentValues().apply {
                        put("id", 0)
                        put("name", binding.etName.text.toString())
                        put("count", binding.etCount.text.toString().toInt())
                        put("enabled", true)
                    }
                )
            }
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        binding.saveButton.setOnClickListener {
            viewModel.editShowItem(binding.etName.text.toString(), binding.etCount.text.toString())
        }
        // Observe Without DataBinding
        // viewModel.currentShopItem.observe(viewLifecycleOwner) {
        //     binding.etName.setText(it.name)
        //     binding.etCount.setText(it.count.toString())
        // }
    }

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(ARG_SCREEN_MODE)) throw RuntimeException("Param Screen Mode is absent")
        val mode = args.getString(ARG_SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(ARG_SHOT_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = args.getInt(
                ARG_SHOT_ITEM_ID,
                UNDEFINED_ID
            )
        }
    }

    fun interface OnEditingFinishedListener {
        fun onEditingFinished()
    }

    companion object {
        private const val ARG_SCREEN_MODE = "extra_mode"
        private const val ARG_SHOT_ITEM_ID = "extra_shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"

        private const val UNDEFINED_ID = -1
        private const val UNDEFINED_SCREEN_MODE = ""

        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SCREEN_MODE, MODE_EDIT)
                    putInt(ARG_SHOT_ITEM_ID, shopItemId)
                }
            }
        }
    }
}