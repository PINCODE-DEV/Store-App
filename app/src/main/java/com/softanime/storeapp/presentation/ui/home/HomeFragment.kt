package com.softanime.storeapp.presentation.ui.home

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import coil.load
import com.softanime.storeapp.R
import com.softanime.storeapp.data.model.home.ProductsCategories
import com.softanime.storeapp.data.model.home.ResponseBanners.ResponseBannersItem
import com.softanime.storeapp.data.model.home.ResponseDiscount.ResponseDiscountItem
import com.softanime.storeapp.data.model.home.ResponseProducts
import com.softanime.storeapp.data.model.home.ResponseProducts.SubCategory.Products.Data
import com.softanime.storeapp.databinding.FragmentHomeBinding
import com.softanime.storeapp.presentation.adapter.home.BannersAdapter
import com.softanime.storeapp.presentation.adapter.home.DiscountsAdapter
import com.softanime.storeapp.presentation.adapter.home.ProductsAdapter
import com.softanime.storeapp.presentation.viewModel.HomeViewModel
import com.softanime.storeapp.presentation.viewModel.ProfileViewModel
import com.softanime.storeapp.utils.AUTO_SCROLL_TIME
import com.softanime.storeapp.utils.base.BaseFragment
import com.softanime.storeapp.utils.extensions.loadImage
import com.softanime.storeapp.utils.extensions.setup
import com.softanime.storeapp.utils.extensions.showSnackBar
import com.softanime.storeapp.utils.extensions.shownLoading
import com.softanime.storeapp.utils.network.NetworkRequest
import com.todkars.shimmer.ShimmerRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    //Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel by activityViewModels<HomeViewModel>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()

    // Adapters
    @Inject
    lateinit var bannersAdapter: BannersAdapter

    @Inject
    lateinit var mobileProductsAdapter: ProductsAdapter

    @Inject
    lateinit var shoesProductsAdapter: ProductsAdapter

    @Inject
    lateinit var stationeryProductsAdapter: ProductsAdapter

    @Inject
    lateinit var laptopProductsAdapter: ProductsAdapter

    @Inject
    lateinit var discountsAdapter: DiscountsAdapter

    // Slider Position
    private var bannerIndex = 0

    // Others
    private val pagerSnapHelper by lazy { PagerSnapHelper() }
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init Views
        setupViews()
        // Load data
        loadProfileData()
        loadBannersData()
        loadDiscountData()
        loadProductData()
    }

    private fun setupViews() {
        binding.apply {
            avatarImg.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            }
        }
    }

    private fun loadProfileData() {
        binding.apply {
            profileViewModel.profileData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        avatarLoading.isVisible = true
                    }

                    is NetworkRequest.Success -> {
                        avatarLoading.isVisible = false
                        response.data?.let { data ->
                            if (data.avatar != null) {
                                avatarImg.loadImage(data.avatar)
                            } else {
                                avatarImg.load(R.drawable.placeholder_user)
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        avatarLoading.isVisible = false
                        // Show Error
                        root.showSnackBar(response.message!!)
                    }
                }
            }
        }
    }

    private fun loadBannersData() {
        binding.apply {
            viewModel.bannersData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        bannerLoading.shownLoading(true, bannerList)
                    }

                    is NetworkRequest.Success -> {
                        bannerLoading.shownLoading(false, bannerList)
                        response.data?.let { data ->
                            Log.i("LOG", "loadBannersData: ${data[0].title}")
                            if (data.isNotEmpty()) {
                                initBannerRecycler(data)
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        bannerLoading.shownLoading(false, bannerList)
                        // Show Error
                        root.showSnackBar(response.message!!)
                    }
                }
            }
        }
    }

    private fun loadDiscountData() {
        binding.apply {
            viewModel.discountData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        binding.discountList.showShimmer()
                    }

                    is NetworkRequest.Success -> {
                        binding.discountList.hideShimmer()
                        response.data?.let { data ->
                            if (data.isNotEmpty()) {
                                initDiscountRecycler(data)
                                val endTime = data[0].endTime?.let {
                                    it.split("T")[0]
                                }
                                discountTimer(endTime!!)
                                countDownTimer.start()
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        binding.discountList.hideShimmer()
                        // Show Error
                        root.showSnackBar(response.message!!)
                    }
                }
            }
        }
    }

    private fun loadProductData() {
        binding.apply {
            // Mobile
            if (mobileLay.parent != null) {
                val mobileInflate = mobileLay.inflate()
                viewModel.getProductsData(ProductsCategories.MOBILE)
                    .observe(viewLifecycleOwner) { request ->
                        handleProductsRequest(
                            request,
                            mobileProductsAdapter,
                            mobileInflate.findViewById(R.id.mobileProductsList)
                        )
                    }
            }
            // Shoes
            if (shoesLay.parent != null) {
                val shoesInflate = shoesLay.inflate()
                viewModel.getProductsData(ProductsCategories.SHOES)
                    .observe(viewLifecycleOwner) { request ->
                        handleProductsRequest(
                            request,
                            shoesProductsAdapter,
                            shoesInflate.findViewById(R.id.menShoesProductsList)
                        )
                    }
            }
            // Stationery
            if (stationeryLay.parent != null) {
                val stationeryInflate = stationeryLay.inflate()
                viewModel.getProductsData(ProductsCategories.STATIONERY)
                    .observe(viewLifecycleOwner) { request ->
                        handleProductsRequest(
                            request,
                            stationeryProductsAdapter,
                            stationeryInflate.findViewById(R.id.stationeryProductsList)
                        )
                    }
            }
            // Stationery
            if (laptopLay.parent != null) {
                val laptopInflate = laptopLay.inflate()
                viewModel.getProductsData(ProductsCategories.LAPTOP)
                    .observe(viewLifecycleOwner) { request ->
                        handleProductsRequest(
                            request,
                            laptopProductsAdapter,
                            laptopInflate.findViewById(R.id.laptopProductsList)
                        )
                    }
            }
        }
    }

    private fun handleProductsRequest(
        request: NetworkRequest<ResponseProducts>,
        adapter: ProductsAdapter,
        recyclerView: ShimmerRecyclerView
    ) {
        when (request) {
            is NetworkRequest.Loading -> {
                recyclerView.showShimmer()
            }

            is NetworkRequest.Success -> {
                recyclerView.hideShimmer()
                request.data?.let { data ->
                    data.subCategory?.let { subCategory ->
                        subCategory.products?.let { products ->
                            products.data?.let { myData ->
                                if (myData.isNotEmpty()) {
                                    initProductsRecycler(myData, recyclerView, adapter)
                                }
                            }
                        }
                    }
                }
            }

            is NetworkRequest.Error -> {
                recyclerView.hideShimmer()
                // Show Error
                binding.root.showSnackBar(request.message!!)
            }
        }
    }

    private fun initProductsRecycler(
        data: List<Data>,
        recyclerView: ShimmerRecyclerView,
        adapter: ProductsAdapter
    ) {
        adapter.setData(data)
        recyclerView.setup(
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false),
            adapter
        )
        // item click
        adapter.setOnItemClickListener { }
    }

    private fun initDiscountRecycler(data: List<ResponseDiscountItem>) {
        discountsAdapter.setData(data)
        val lm = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
        binding.discountList.setup(lm, discountsAdapter)
    }

    private fun initBannerRecycler(data: List<ResponseBannersItem>) {
        bannersAdapter.setData(data)
        binding.bannerList.apply {
            adapter = bannersAdapter
            setAlpha(true)
            setInfinite(false)
            set3DItem(true)
        }
        // item click
        //bannersAdapter.setOnItemClickListener {  }
        binding.apply {
            // Indicator
            pagerSnapHelper.attachToRecyclerView(bannerList)
            bannerIndicator.attachToRecyclerView(bannerList, pagerSnapHelper)
        }
        autoScrollBanners((data.size - 1))
    }

    private fun autoScrollBanners(size: Int) {
        // Auto Scroll
        lifecycleScope.launch {
            repeat(Int.MAX_VALUE) {
                delay(AUTO_SCROLL_TIME)

                if (bannerIndex < size)
                    bannerIndex += 1
                else
                    bannerIndex = 0

                binding.bannerList.smoothScrollToPosition(bannerIndex)
                binding.bannerIndicator.animatePageSelected(bannerIndex)
            }
        }
    }

    private fun discountTimer(fullDate: String) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date: Date = formatter.parse(fullDate) as Date
        val currentMillis = System.currentTimeMillis()
        val finalMillis = date.time - currentMillis

        countDownTimer = object : CountDownTimer(finalMillis, 1_000) {
            override fun onTick(millis: Long) {
                // Calculate time
                var timer = millis
                // Days
                val days: Long = TimeUnit.MILLISECONDS.toDays(timer)
                timer -= TimeUnit.DAYS.toMillis(days)
                // Hours
                val hours: Long = TimeUnit.MILLISECONDS.toHours(timer)
                timer -= TimeUnit.HOURS.toMillis(hours)
                // Minutes
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(timer)
                timer -= TimeUnit.MINUTES.toMillis(minutes)
                // Seconds
                val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(timer)
                timer -= TimeUnit.SECONDS.toMillis(seconds)

                // Views
                try {
                    binding.timerLay.apply {
                        if (days > 0) {
                            dayTxt.isVisible = true
                            dayTxt.text = days.toString()
                        } else
                            dayTxt.isVisible = false

                        if (hours > 0) {
                            hourTxt.isVisible = true
                            hourTxt.text = hours.toString()
                        } else
                            hourTxt.isVisible = false

                        if (minutes > 0) {
                            minuteTxt.isVisible = true
                            minuteTxt.text = minutes.toString()
                        } else
                            minuteTxt.isVisible = false

                        secondTxt.text = seconds.toString()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {}
        }
    }

    override fun onStop() {
        countDownTimer.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}