package com.example.newsappcase.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsappcase.adapters.NewsAdapter
import com.example.newsappcase.databinding.FragmentNewsBinding
import com.example.newsappcase.ui.MainActivity
import com.example.newsappcase.ui.NewsViewModel
import com.example.newsappcase.util.Resource

class NewsFragment: Fragment() {

    val TAG = "BreakingNewsFragment"
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupNewsAdapter()
        viewModel.newsData.observe(viewLifecycleOwner) { newsResponse ->// farklı yaptı
            when (newsResponse) {
                is Resource.Error -> {
                    hideProgressBar()
                    newsResponse.message?.let { message ->
                        Log.e(TAG, "An error occurred: $message")
                    }
                }
                is Resource.Loading -> showProgressBar()
                is Resource.Success -> {
                    hideProgressBar()
                    newsResponse.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.results)
                    }
                }
            }
        }
    }



    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupNewsAdapter() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





