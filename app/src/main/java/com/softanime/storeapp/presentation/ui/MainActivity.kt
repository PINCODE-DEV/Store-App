package com.softanime.storeapp.presentation.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.softanime.storeapp.R
import com.softanime.storeapp.databinding.ActivityMainBinding
import com.softanime.storeapp.utils.otp.AppSignatureHelper
import dagger.hilt.android.AndroidEntryPoint
import io.github.inflationx.viewpump.ViewPumpContextWrapper
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
                R.id.homeFragment -> { binding.bottomNav.isVisible = true }
                R.id.cartFragment -> { binding.bottomNav.isVisible = true }
                R.id.categoriesFragment -> { binding.bottomNav.isVisible = true }
                R.id.profileFragment -> { binding.bottomNav.isVisible = true }
                else ->
                    binding.bottomNav.isVisible = false
            }
        }

        // Generate Hash code
        generateHashCode()
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

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}