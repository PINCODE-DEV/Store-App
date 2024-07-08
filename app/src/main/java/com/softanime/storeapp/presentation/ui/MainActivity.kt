package com.softanime.storeapp.presentation.ui

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.softanime.storeapp.R
import com.softanime.storeapp.databinding.ActivityMainBinding
import com.softanime.storeapp.utils.otp.AppSignatureHelper
import com.softanime.storeapp.utils.otp.SMSBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // OTP
    @Inject
    lateinit var signatureHelper: AppSignatureHelper
    var hashCode = ""
    @Inject
    lateinit var smsReceiver: SMSBroadcastReceiver
    private var intentFilter : IntentFilter? = null

    // Other
    private lateinit var navHost: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Init
        setupViews()
    }

    private fun setupViews() {
        navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment

        binding.bottomNav.apply {
            setupWithNavController(navHost.navController)
            // Disable double click on item
            setOnNavigationItemReselectedListener { }
        }
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                //R.id.splashFragment -> { binding.bottomNav.isVisible = true }
                else -> {
                    binding.bottomNav.isVisible = false
                }
            }
        }

        // Generate Hash code
        generateHashCode()

        // Init BroadCastReceiver
        initSmsReceiver()
        smsListener()
    }

    private fun smsListener() {
        val client = SmsRetriever.getClient(this)
        client.startSmsRetriever()
    }

    private fun initSmsReceiver() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver.onReceiveMessage {
            Log.i("OTP", "SMS: $it")
        }
    }

    private fun generateHashCode() {
        signatureHelper.appSignatures.forEach {
            hashCode = it
            Log.i("OTP", "Hash code: $hashCode")
        }
    }

    override fun onNavigateUp(): Boolean {
        return navHost.navController.navigateUp() || super.onNavigateUp()
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        applicationContext.registerReceiver(smsReceiver,intentFilter, RECEIVER_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        applicationContext.unregisterReceiver(smsReceiver)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}