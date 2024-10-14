package com.example.newsappcase.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappcase.R
import com.example.newsappcase.adapters.NewsAdapter
import com.example.newsappcase.databinding.FragmentSearchNewsBinding
import com.example.newsappcase.ui.NewsViewModel
import com.example.newsappcase.util.Constants
import com.example.newsappcase.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {
    val TAG = "SearchNewsFragment"
    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by viewModels()
    lateinit var newsAdapter: NewsAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
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
                if(binding.etSearch.text.isNotEmpty()){
                    viewModel.searchNews(binding.etSearch.text.toString(), true)
                } else {
                    viewModel.getNews()
                }
                isScrolling = false
            } else {
                binding.rvSearchNews.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
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
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_detailedNewsFragment,bundle)
        }
        var searchJob: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(Constants.SEARCH_DELAY)
                searchJob = MainScope().launch {
                    editable?.let {
                        if (editable.toString().isNotEmpty()) {
                            viewModel.searchNews(editable.toString(), false)
                        } else{
                            viewModel.getNews()
                        }
                    }
                }

            }
        }
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
                        newsAdapter.differ.submitList(newsResponse.results.toList())
                        val totalPages = newsResponse.results.size / Constants.SINGLE_QUERY_ITEM_SIZE + 2
                        isLastPage = viewModel.newsOffset == totalPages// bu yanlıs buraya bi bakıcam
                        if (isLastPage) {
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
            }
        }
    }



    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
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