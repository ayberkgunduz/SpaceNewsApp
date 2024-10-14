package com.example.newsappcase.core.usecase

import kotlinx.coroutines.flow.Flow

interface UseCase<Param, Result> {
    suspend operator fun invoke(param: Param?): Result
}

interface FlowUseCase<Param, Result> : UseCase<Param, Flow<Result>>