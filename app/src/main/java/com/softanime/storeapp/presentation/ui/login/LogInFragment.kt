package com.softanime.storeapp.presentation.ui.login

import academy.nouri.storeapp.data.models.login.BodyLogin
import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.softanime.storeapp.R
import com.softanime.storeapp.databinding.FragmentLogInBinding
import com.softanime.storeapp.presentation.ui.MainActivity
import com.softanime.storeapp.presentation.viewModel.LogInViewModel
import com.softanime.storeapp.utils.IS_CALLED_VERIFY
import com.softanime.storeapp.utils.base.BaseFragment
import com.softanime.storeapp.utils.extensions.enableLoading
import com.softanime.storeapp.utils.extensions.hideKeyboard
import com.softanime.storeapp.utils.extensions.showSnackBar
import com.softanime.storeapp.utils.network.NetworkRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class LogInFragment : BaseFragment() {
    //Binding
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel by viewModels<LogInViewModel>()

    // Models
    @Inject
    lateinit var logInBody: BodyLogin

    // Parent Activity
    private val parentActivity by lazy { activity as MainActivity }

    // Others
    private var phone = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Init views
        binding.apply {
            // Set bg
            bottomImg.load(R.drawable.bg_circle)
            // Fill Hashcode
            logInBody.hashCode = parentActivity.hashCode
            // Click
            sendPhoneBtn.setOnClickListener {
                // Hide Keyboard
                root.hideKeyboard()
                // Fill phone
                logInBody.login = phoneEdt.text.toString()
                phone = phoneEdt.text.toString()
                if (phone.length == 11) {
                    // Call api
                    if (isNetworkAvailable) {
                        IS_CALLED_VERIFY = true
                        viewModel.callLogInApi(logInBody)
                    }
                }
            }
        }
        loadLoginData()
        // Handle Animation
        handleAnimation()
    }

    private fun handleAnimation() {
        binding.animationView.apply {
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {}
                override fun onAnimationEnd(p0: Animator) {
                    lifecycleScope.launch {
                        delay(2000)
                        playAnimation()
                    }
                }

                override fun onAnimationCancel(p0: Animator) {}
                override fun onAnimationRepeat(p0: Animator) {}
            })
        }
    }

    private fun loadLoginData() {
        binding.apply {
            viewModel.loginData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        sendPhoneBtn.enableLoading(true)
                    }
                    is NetworkRequest.Success -> {
                        sendPhoneBtn.enableLoading(false)
                        response.data?.let {
                            // Go to Verify
                            if (IS_CALLED_VERIFY){
                                val direction = VerifyFragmentDirections.actionLogInFragmentToVerifyFragment(phone)
                                findNavController().navigate(direction)
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        sendPhoneBtn.enableLoading(false)
                        // Show Error
                        root.showSnackBar(response.message!!)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}