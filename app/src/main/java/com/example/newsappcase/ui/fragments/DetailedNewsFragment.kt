package com.example.newsappcase.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.newsappcase.databinding.FragmentDetailedNewsBinding
import com.example.newsappcase.model.Article
import com.example.newsappcase.ui.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedNewsFragment: Fragment() {
    val TAG = "DetailedNewsFragment"
    private var _binding: FragmentDetailedNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by viewModels()

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
        val article = arguments?.getSerializable("article") as? Article


        article?.let {
            binding.webView.apply {
                webViewClient = WebViewClient()
                it.url?.let { it1 -> loadUrl(it1) }
            }

            binding.fab.setOnClickListener {
                viewModel.checkIsArticleSaved(article)
            }

        }
    }

}