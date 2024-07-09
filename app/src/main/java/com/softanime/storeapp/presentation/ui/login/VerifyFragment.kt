package com.softanime.storeapp.presentation.ui.login

import academy.nouri.storeapp.data.models.login.BodyLogin
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.goodiebag.pinview.Pinview
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.softanime.storeapp.R
import com.softanime.storeapp.databinding.FragmentLogInBinding
import com.softanime.storeapp.databinding.FragmentVerifyBinding
import com.softanime.storeapp.presentation.viewModel.LogInViewModel
import com.softanime.storeapp.utils.otp.SMSBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class VerifyFragment : Fragment() {
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
    private var intentFilter : IntentFilter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Args
        args.let {
            logInBody.login = it.phone
        }
        // Handle Animation
        handleAnimation()
        // Init BroadCastReceiver
        initSmsReceiver()
        smsListener()

        binding.apply {
            bottomImg.load(R.drawable.bg_circle)
            // Custom TextColor for PinView
            pinView.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))

            pinView.setPinViewEventListener(object : Pinview.PinViewEventListener{
                override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {
                    logInBody.code = pinview?.value?.toInt()
                    viewModel.callLogInVerifyApi(logInBody)
                }
            })
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

    private fun initSmsReceiver() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver.onReceiveMessage {
            val code = it.split(":")[1].trim().subSequence(0,4)
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
        requireContext().registerReceiver(smsReceiver,intentFilter, RECEIVER_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(smsReceiver)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}