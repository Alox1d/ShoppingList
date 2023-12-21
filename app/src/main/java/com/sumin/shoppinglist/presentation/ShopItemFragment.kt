package com.sumin.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.sumin.shoppinglist.R

class ShopItemFragment : Fragment() {

    private lateinit var viewModel: ShopItemViewModel

    private lateinit var tilName: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etCount: EditText
    private lateinit var buttonSave: Button

    private var screenMode: String = UNDEFINED_SCREEN_MODE
    private var shopItemId: Int = UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ShopItemFragment", "onCreate")

        // НЕ работаем с View здесь, т.к. вызывается ДО onViewCreated, т.е. до создания View
        super.onCreate(savedInstanceState)

        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initViews(view)
        addTextChangeListeners()
        launchRightMode()
        observeViewModel()
    }

    private fun observeViewModel() {

        // Lifecycle of View can be < Lifecycle Fragment.
        //
        // Жиз. цикл this (Fragment'а) != жиз. циклу его View:
        // в  retain-фрагментах фрагмент может быть жив, а его View нет.
        // Тогда получим краш при оброщении к view-элементам из LiveData.
        // Поэтому юзаем viewLifecycleOwner
        viewModel.errorInputName.observe(viewLifecycleOwner) {
            tilName.error = if (it) getString(R.string.error_input_name) else null
        }
        viewModel.errorInputCount.observe(viewLifecycleOwner) {
            tilCount.error = if (it) getString(R.string.error_input_count) else null
        }
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {

            // activity nullable, т.к. мы можем к ней обратиться, когда она ещё НЕ ПРИКРЕПЛЕНА к активити
            activity?.onBackPressed()
            // requireActivity() throws IllegalStateException
            // requireActivity().onBackPressed()
        }
    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    private fun addTextChangeListeners() {
        etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(p0: Editable?) = Unit
        })
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(p0: Editable?) = Unit

        })
    }

    private fun launchAddMode() {
        buttonSave.setOnClickListener {
            viewModel.addShopItem(etName.text.toString(), etCount.text.toString())
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.currentShopItem.observe(viewLifecycleOwner) {
            etName.setText(it.name)
            etCount.setText(it.count.toString())
        }
        buttonSave.setOnClickListener {
            viewModel.editShowItem(etName.text.toString(), etCount.text.toString())
        }
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

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
    }

    private fun initViews(view: View) {
        tilName = view.findViewById(R.id.til_name)
        tilCount = view.findViewById(R.id.til_count)
        etName = view.findViewById(R.id.et_name)
        etCount = view.findViewById(R.id.et_count)
        buttonSave = view.findViewById(R.id.save_button)
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