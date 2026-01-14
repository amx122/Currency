package com.example.kursova_mobile.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.kursova_mobile.R
import com.example.kursova_mobile.databinding.ActivityMainBinding
import com.example.kursova_mobile.utils.ContextUtils
import com.example.kursova_mobile.utils.PrefsManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun attachBaseContext(newBase: Context) {
        val lang = PrefsManager.getLanguage(newBase)
        super.attachBaseContext(ContextUtils.updateLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val isDark = PrefsManager.isDarkMode(this)
        val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}