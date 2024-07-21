package com.softanime.storeapp.presentation.ui.categories.categories_products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.softanime.storeapp.R
import com.softanime.storeapp.data.model.home.ResponseProducts
import com.softanime.storeapp.databinding.FragmentCategoriesProductsBinding
import com.softanime.storeapp.presentation.adapter.categories.ProductsAdapter
import com.softanime.storeapp.presentation.viewModel.CategoryProductViewModel
import com.softanime.storeapp.utils.base.BaseFragment
import com.softanime.storeapp.utils.extensions.setup
import com.softanime.storeapp.utils.extensions.showSnackBar
import com.softanime.storeapp.utils.network.NetworkRequest
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesProductsFragment : BaseFragment() {
    //Binding
    private var _binding: FragmentCategoriesProductsBinding? = null
    private val binding get() = _binding!!

    // Args
    private val args by navArgs<CategoriesProductsFragmentArgs>()
    private var slug = ""

    // ViewModel
    private val viewModel by activityViewModels<CategoryProductViewModel>()

    // Adapter
    @Inject
    lateinit var productsAdapter: ProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesProductsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Args
        args.let {
            slug = it.slug
        }
        //Call api
        if (isNetworkAvailable)
            viewModel.callProductsApi(slug, viewModel.productsQueries())

        //InitViews
        binding.apply {
            //Toolbar
            toolbar.apply {
                //Back
                toolbarBackImg.setOnClickListener { findNavController().popBackStack() }
                //Filter
                toolbarOptionImg.setOnClickListener { findNavController().navigate(R.id.actionCategoryToFilter) }
            }
        }

        //Load data
        loadProductsData()
    }

    private fun loadProductsData() {
        binding.apply {
            viewModel.productData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        productsList.showShimmer()
                    }

                    is NetworkRequest.Success -> {
                        productsList.hideShimmer()
                        response.data?.let { data ->
                            //Title
                            binding.toolbar.toolbarTitleTxt.text = data.subCategory?.title
                            //Recyclerview
                            data.subCategory?.products?.let { products ->
                                if (products.data!!.isNotEmpty()) {
                                    emptyLay.isVisible = false
                                    productsList.isVisible = true
                                    //Init recycler
                                    initProductsRecycler(products.data)
                                } else {
                                    emptyLay.isVisible = true
                                    productsList.isVisible = false
                                }
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        productsList.hideShimmer()
                        root.showSnackBar(response.message!!)
                    }
                }
            }
        }
    }

    private fun initProductsRecycler(data: List<ResponseProducts.SubCategory.Products.Data>) {
        productsAdapter.setData(data)
        binding.productsList.setup(LinearLayoutManager(requireContext()), productsAdapter)
        //Click
        productsAdapter.setOnItemClickListener {

        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}