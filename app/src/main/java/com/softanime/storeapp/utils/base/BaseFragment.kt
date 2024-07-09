package com.softanime.storeapp.utils.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.softanime.storeapp.R
import com.softanime.storeapp.utils.network.NetworkChecker
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseFragment : Fragment() {
    // Network Checker
    @Inject
    lateinit var networkChecker: NetworkChecker

    // Other
    var isNetworkAvailable = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            networkChecker.checkNetwork().collect {
                isNetworkAvailable = it
                if (!it)
                    Toast.makeText(requireContext(), R.string.checkYourNetwork, Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }
}