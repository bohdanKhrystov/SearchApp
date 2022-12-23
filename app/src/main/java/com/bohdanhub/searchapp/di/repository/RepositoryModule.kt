package com.bohdanhub.searchapp.di.repository

import com.bohdanhub.searchapp.data.ParserImpl
import com.bohdanhub.searchapp.data.UrlFetcherImpl
import com.bohdanhub.searchapp.domain.Parser
import com.bohdanhub.searchapp.domain.UrlFetcher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindUrlFetcher(fetcherImpl: UrlFetcherImpl): UrlFetcher

    @Binds
    abstract fun bindParser(parserImpl: ParserImpl): Parser
}