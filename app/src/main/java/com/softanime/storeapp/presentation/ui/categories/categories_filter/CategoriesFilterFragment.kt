package com.softanime.storeapp.presentation.ui.categories.categories_filter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.softanime.storeapp.R
import com.softanime.storeapp.databinding.FragmentCategoriesFilterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoriesFilterFragment : BottomSheetDialogFragment() {
    //Binding
    private var _binding: FragmentCategoriesFilterBinding? = null
    private val binding get() = _binding!!

    //Theme
    override fun getTheme() = R.style.RemoveDialogBackground

    //Other
    //private val viewModel by activityViewModels<CategoryProductViewModel>()
    private var minPrice: String? = null
    private var maxPrice: String? = null
    private var sort: String? = null
    private var search: String? = null
    private var available: Boolean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoriesFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Init views
        binding.apply {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}