package com.example.newsappcase.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappcase.R
import com.example.newsappcase.adapters.NewsAdapter
import com.example.newsappcase.databinding.FragmentSearchNewsBinding
import com.example.newsappcase.extensions.gone
import com.example.newsappcase.extensions.invisible
import com.example.newsappcase.extensions.showToast
import com.example.newsappcase.extensions.visible
import com.example.newsappcase.model.NewsResponse
import com.example.newsappcase.ui.viewmodel.SearchNewsViewModel
import com.example.newsappcase.util.Constants
import com.example.newsappcase.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    val TAG = "SearchNewsFragment"
    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchNewsViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter

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
                if (binding.etSearch.text.isNotEmpty()) {
                    viewModel.searchNews(binding.etSearch.text.toString(), true)
                }
                isScrolling = false
            } else {
                binding.rvSearchNews.setPadding(0, 0, 0, 0)
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
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNewsAdapter()
        newsAdapter.setOnItemClickListener {
            val direction =
                SearchNewsFragmentDirections.actionSearchNewsFragmentToDetailedNewsFragment(it)
            findNavController().navigate(direction)
        }
        var searchJob: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(Constants.SEARCH_DELAY)
                searchJob = MainScope().launch {
                    editable?.let {
                        if (editable.toString().isNotEmpty()) {
                            binding.layoutSearch.gone()
                            viewModel.searchNews(editable.toString(), false)
                        } else {
                            newsAdapter.differ.submitList(emptyList())
                            binding.tvSearchInfo.text = getString(R.string.search_for_news)
                            binding.layoutSearch.visible()
                        }
                    }
                }

            }
        }
        observeViewModel()

    }

    private fun observeViewModel(){
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
                            handleNewsResponse(newsResponse)
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
                if (response is Resource.NoConnection) {
                    hideProgressBar()
                    requireContext().showToast(
                        getString(R.string.no_internet_connection),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }
    }

    private fun handleNewsResponse(newsResponse: NewsResponse) {
        if(binding.etSearch.text.isNotEmpty())
            newsAdapter.differ.submitList(newsResponse.results.toList())
        if(newsResponse.results.isEmpty()){
            binding.tvSearchInfo.text = getString(R.string.nothing_found)
            binding.layoutSearch.visible()
        }
        val totalPages =
            newsResponse.results.size / Constants.SINGLE_QUERY_ITEM_SIZE + 2
        isLastPage =
            viewModel.newsOffset == totalPages
        if (isLastPage) {
            binding.rvSearchNews.setPadding(0, 0, 0, 0)
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
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}