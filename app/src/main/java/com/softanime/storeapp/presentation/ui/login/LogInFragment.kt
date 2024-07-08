package com.softanime.storeapp.presentation.ui.login

import academy.nouri.storeapp.data.models.login.BodyLogin
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.softanime.storeapp.databinding.FragmentLogInBinding
import com.softanime.storeapp.presentation.viewModel.LogInViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class LogInFragment : Fragment() {
    //Binding
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    // ViewModel
    private val viewModel by viewModels<LogInViewModel>()
    // Models
    @Inject
    lateinit var logInBody: BodyLogin

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
            sendPhoneBtn.setOnClickListener {
                logInBody.login = phoneEdt.text.toString()
                logInBody.hashCode = "sdgfrhwodwe"
                viewModel.postLogin(logInBody)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}