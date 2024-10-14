package com.example.newsappcase.domain.usecase

import com.example.newsappcase.api.NewsRemoteRepository
import com.example.newsappcase.core.exception.NullParamsException
import com.example.newsappcase.core.usecase.FlowUseCase
import com.example.newsappcase.model.NewsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

enum class GetNewsType {
    GET_NEWS,
    SEARCH_NEWS,
}

data class SendAuthCodeParam(
    val type: GetNewsType,
    val limit: Int? = null,
    val offset: Int? = null,
    val searchText: String? = null,
)
class GetNewsUseCase @Inject
constructor(
    private val remoteRepository: NewsRemoteRepository,
) : FlowUseCase<SendAuthCodeParam, Response<NewsResponse>> {

    override suspend fun invoke(param: SendAuthCodeParam?): Flow<Response<NewsResponse>> = flow {
        if (param == null) {
            throw NullParamsException()
        }
        val response = when (param.type) {
            GetNewsType.GET_NEWS -> getNews(param.limit, param.offset)
            GetNewsType.SEARCH_NEWS -> searchNews(param.searchText, param.limit, param.offset)
        }
        emit(response)
    }

    private suspend fun getNews(limit: Int?, offset: Int?): Response<NewsResponse> {
        return remoteRepository.getNews(limit, offset)
    }

    private suspend fun searchNews(searchQuery: String?, limit: Int?, offset: Int?): Response<NewsResponse> {
        return remoteRepository.searchNews(searchQuery, limit, offset)
    }
}
