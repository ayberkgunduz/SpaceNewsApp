package com.example.newsappcase.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappcase.R
import com.example.newsappcase.adapters.NewsAdapter
import com.example.newsappcase.databinding.FragmentNewsBinding
import com.example.newsappcase.extensions.invisible
import com.example.newsappcase.extensions.showToast
import com.example.newsappcase.extensions.visible
import com.example.newsappcase.ui.viewmodel.NewsViewModel
import com.example.newsappcase.util.Constants
import com.example.newsappcase.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsFragment : Fragment() {

    val TAG = "NewsFragment"
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by viewModels()
    lateinit var newsAdapter: NewsAdapter
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndOnNotLastPage = !isLoading && !isLastPage
            val isLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.SINGLE_QUERY_ITEM_SIZE
            val shouldPaginate = isNotLoadingAndOnNotLastPage && isLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getNews()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

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
        setupNewsAdapter()

        newsAdapter.setOnItemClickListener {
            val direction = NewsFragmentDirections.actionNewsFragmentToDetailedNewsFragment(it)
            findNavController().navigate(direction)
        }
        observeViewModel()
        viewModel.initNews()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newsData.collectLatest { newsResponse ->
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
                            newsAdapter.differ.submitList(newsResponse.results.toList())
                            val totalPages =
                                newsResponse.count / Constants.SINGLE_QUERY_ITEM_SIZE + 2
                            isLastPage =
                                viewModel.newsOffset == totalPages
                            if (isLastPage) {
                                binding.rvBreakingNews.setPadding(0, 0, 0, 0)
                            }
                        }
                    }

                    is Resource.NoConnection -> {
                        hideProgressBar()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.offlineNewsData.collectLatest { response ->
                when (response) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {}
                    is Resource.NoConnection -> {
                        hideProgressBar()
                        newsAdapter.differ.submitList(response.data)
                        requireContext().showToast(
                            getString(R.string.offline_mode),
                            Toast.LENGTH_SHORT
                        )
                    }
                }
            }
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.invisible()
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visible()
        isLoading = true
    }

    private fun setupNewsAdapter() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@NewsFragment.scrollListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





