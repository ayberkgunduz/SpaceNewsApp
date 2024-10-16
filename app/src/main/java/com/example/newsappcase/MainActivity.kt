package com.example.newsappcase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newsappcase.databinding.ActivityMainBinding
import com.example.newsappcase.extensions.gone
import com.example.newsappcase.extensions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailedNewsFragment -> {
                    binding.bottomNavigationView.gone()
                    binding.textTitle.text = getString(R.string.detailed_news)
                    binding.buttonBack.visible()

                }

                R.id.savedNewsFragment -> {
                    binding.bottomNavigationView.visible()
                    binding.textTitle.text = getString(R.string.favorite_news)
                    binding.buttonBack.gone()
                }

                R.id.searchNewsFragment -> {
                    binding.bottomNavigationView.visible()
                    binding.textTitle.text = getString(R.string.search_news)
                    binding.buttonBack.gone()

                }

                R.id.newsFragment -> {
                    binding.bottomNavigationView.visible()
                    binding.textTitle.text = getString(R.string.space_news)
                    binding.buttonBack.gone()
                }
            }
        }

        binding.buttonBack.setOnClickListener {
            navController.navigateUp()
        }

    }
}
