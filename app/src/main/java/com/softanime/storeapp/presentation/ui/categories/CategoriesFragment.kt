package com.softanime.storeapp.presentation.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.softanime.storeapp.R
import com.softanime.storeapp.data.model.categories.ResponseCategories.ResponseCategoriesItem
import com.softanime.storeapp.databinding.FragmentCategoriesBinding
import com.softanime.storeapp.presentation.adapter.categories.CategoryAdapter
import com.softanime.storeapp.presentation.ui.categories.CategoriesFragmentDirections
import com.softanime.storeapp.presentation.viewModel.CategoriesViewModel
import com.softanime.storeapp.utils.extensions.setup
import com.softanime.storeapp.utils.extensions.showSnackBar
import com.softanime.storeapp.utils.network.NetworkRequest
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFragment : Fragment() {
    //Binding
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    // Adapter
    @Inject
    lateinit var categoryAdapter: CategoryAdapter

    // ViewModel
    private val viewModel by activityViewModels<CategoriesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Init views
        binding.apply {
            //Toolbar
            toolbar.apply {
                //Visibility
                toolbarBackImg.isVisible = false
                toolbarOptionImg.isVisible = false
                //Title
                toolbarTitleTxt.text = getString(R.string.categories)
            }
        }
        //Load data
        loadCategoriesData()
    }

    private fun loadCategoriesData() {
        binding.apply {
            viewModel.categoriesData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        categoriesList.showShimmer()
                    }

                    is NetworkRequest.Success -> {
                        categoriesList.hideShimmer()
                        response.data?.let { data ->
                            initCategoriesRecycler(data)
                        }
                    }

                    is NetworkRequest.Error -> {
                        categoriesList.hideShimmer()
                        root.showSnackBar(response.message!!)
                    }
                }
            }
        }
    }

    private fun initCategoriesRecycler(data: List<ResponseCategoriesItem>) {
        categoryAdapter.setData(data.dropLast(1))
        binding.categoriesList.setup(LinearLayoutManager(requireContext()), categoryAdapter)
        //Click
        categoryAdapter.setOnItemClickListener {
            val direction = CategoriesFragmentDirections.actionToCategoriesProducts(it)
            findNavController().navigate(direction)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}