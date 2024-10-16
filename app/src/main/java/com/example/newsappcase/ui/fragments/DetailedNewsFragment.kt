package com.example.newsappcase.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.newsappcase.R
import com.example.newsappcase.databinding.FragmentDetailedNewsBinding
import com.example.newsappcase.extensions.setSingleOnClickListener
import com.example.newsappcase.extensions.showToast
import com.example.newsappcase.ui.viewmodel.DetailedNewsViewModel
import com.example.newsappcase.util.FavoriteEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailedNewsFragment : Fragment() {
    private var _binding: FragmentDetailedNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailedNewsViewModel by viewModels()

    private val args: DetailedNewsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article = args.article

        article.let {
            binding.webView.apply {
                webViewClient = WebViewClient()
                it.url?.let { it1 -> loadUrl(it1) }
            }
        }

        binding.actionButton.setSingleOnClickListener {
            viewModel.favoriteButtonClicked(article)
        }

        observeViewModel()
        viewModel.checkIsArticleSaved(article)
    }

    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteStatus.collectLatest { isSaved ->
                if (isSaved) {
                    binding.actionButton.setImageResource(R.drawable.ic_favorite)
                } else {
                    binding.actionButton.setImageResource(R.drawable.ic_not_favorite)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteEvent.collectLatest { event ->
                when (event) {
                    FavoriteEvent.AddedFavorite -> {
                        context?.showToast(getString(R.string.article_saved))
                        binding.actionButton.setImageResource(R.drawable.ic_favorite)
                    }

                    FavoriteEvent.RemovedFavorite -> {
                        context?.showToast(getString(R.string.article_removed))
                        binding.actionButton.setImageResource(R.drawable.ic_not_favorite)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}