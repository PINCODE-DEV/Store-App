package com.softanime.storeapp.presentation.ui.splash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.softanime.storeapp.R
import com.softanime.storeapp.data.stored.SessionManager
import com.softanime.storeapp.databinding.FragmentSplashBinding
import com.softanime.storeapp.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment() {
    //Binding
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    // Session
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Init views
        binding.apply {
            //Version
            versionTxt.text = getString(R.string.version)
        }
        checkSession()
    }

    private fun checkSession() {
        binding.motionLay.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                //Check user
                lifecycleScope.launch {
                    val token = sessionManager.getToken.first()
                    findNavController().popBackStack(R.id.splashFragment, true)
                    Log.i("LOG", "Token: $token")
                    if (token == null) {
                        findNavController().navigate(R.id.action_splashFragment_to_loginNav)
                    } else {
                        findNavController().navigate(R.id.action_to_nav_main)
                    }
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {}

        })
    }
}