package com.softanime.storeapp.presentation.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.utils.widget.MotionLabel
import androidx.lifecycle.lifecycleScope
import com.softanime.storeapp.R
import com.softanime.storeapp.data.stored.SessionManager
import com.softanime.storeapp.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {
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
                lifecycleScope.launch {
                    val token = sessionManager.getToken.first()
                    if (token == null){
                        // Go to Login
                    }else{
                        // Go to Home
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