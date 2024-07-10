package com.softanime.storeapp.presentation.ui.login

import academy.nouri.storeapp.data.models.login.BodyLogin
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.goodiebag.pinview.Pinview
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.softanime.storeapp.R
import com.softanime.storeapp.data.stored.SessionManager
import com.softanime.storeapp.databinding.FragmentLogInBinding
import com.softanime.storeapp.databinding.FragmentVerifyBinding
import com.softanime.storeapp.presentation.viewModel.LogInViewModel
import com.softanime.storeapp.utils.IS_CALLED_VERIFY
import com.softanime.storeapp.utils.base.BaseFragment
import com.softanime.storeapp.utils.extensions.enableLoading
import com.softanime.storeapp.utils.extensions.hideKeyboard
import com.softanime.storeapp.utils.extensions.showSnackBar
import com.softanime.storeapp.utils.network.NetworkRequest
import com.softanime.storeapp.utils.otp.SMSBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class VerifyFragment : BaseFragment() {
    //Binding
    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel by viewModels<LogInViewModel>()

    // Args
    private val args by navArgs<VerifyFragmentArgs>()

    // Models
    @Inject
    lateinit var logInBody: BodyLogin

    // SMS Receiver
    @Inject
    lateinit var smsReceiver: SMSBroadcastReceiver
    private var intentFilter: IntentFilter? = null

    // Stored
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Disable Flag
        IS_CALLED_VERIFY = false
        // Args
        args.let {
            logInBody.login = it.phone
        }
        // Handle Animation
        handleAnimation()
        // Init BroadCastReceiver
        initSmsReceiver()
        smsListener()
        // load login data
        loadLoginData()
        // load verify data
        loadVerifyData()

        binding.apply {
            bottomImg.load(R.drawable.bg_circle)
            // Custom TextColor for PinView
            pinView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            pinView.setPinViewEventListener(object : Pinview.PinViewEventListener {
                override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {
                    logInBody.code = pinview?.value?.toInt()
                    if (isNetworkAvailable)
                        viewModel.callLogInVerifyApi(logInBody)
                }
            })
        }
        // Start Timer
        lifecycleScope.launch {
            delay(500)
            handleTimer().start()
        }

        // Send Again SMS
        binding.sendAgainBtn.setOnClickListener {
            if (isNetworkAvailable)
                viewModel.callLogInApi(logInBody)
            handleTimer().cancel()
        }
    }

    private fun handleTimer(): CountDownTimer {
        binding.apply {
            return object : CountDownTimer(60_000, 1_000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(p0: Long) {
                    sendAgainBtn.isVisible = false
                    timerTxt.isVisible = true
                    timerProgress.isVisible = true
                    timerTxt.text = "${p0 / 1000} ${getString(R.string.second)}"
                    timerProgress.setProgressCompat((p0 / 1000).toInt(), true)
                    if (p0 < 1000)
                        timerProgress.progress = 0
                }

                override fun onFinish() {
                    sendAgainBtn.isVisible = true
                    timerTxt.isVisible = false
                    timerProgress.isVisible = false
                }
            }
        }
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
                        sendAgainBtn.enableLoading(true)
                    }

                    is NetworkRequest.Success -> {
                        sendAgainBtn.enableLoading(false)
                        response.data?.let {
                            handleTimer().start()
                        }
                    }

                    is NetworkRequest.Error -> {
                        sendAgainBtn.enableLoading(false)
                        // Show Error
                        root.showSnackBar(response.message!!)
                    }
                }
            }
        }
    }

    private fun loadVerifyData() {
        binding.apply {
            viewModel.verifyData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        timerLay.alpha = 0.3f
                    }

                    is NetworkRequest.Success -> {
                        timerLay.alpha = 1.0f
                        response.data?.let { data ->
                            lifecycleScope.launch {
                                sessionManager.saveToken(data.accessToken.toString())
                            }
                        }
                        root.hideKeyboard()
                        findNavController().popBackStack(R.id.logInFragment, true)
                        findNavController().popBackStack(R.id.verifyFragment, true)
                        // Go to Home
                        findNavController().navigate(R.id.action_to_nav_main)
                    }

                    is NetworkRequest.Error -> {
                        // Show Error
                        root.showSnackBar(response.message!!)
                    }
                }
            }
        }
    }

    private fun initSmsReceiver() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver.onReceiveMessage {
            val code = it.split(":")[1].trim().subSequence(0, 4)
            binding.pinView.value = code.toString()
        }
    }

    private fun smsListener() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsRetriever()
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(smsReceiver, intentFilter, RECEIVER_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(smsReceiver)
        handleTimer().cancel()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}