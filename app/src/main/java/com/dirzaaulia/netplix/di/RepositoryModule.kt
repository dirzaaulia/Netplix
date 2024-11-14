package com.dirzaaulia.netplix.di

import com.dirzaaulia.netplix.network.KtorClient
import com.dirzaaulia.netplix.repository.NetworkRepository
import com.dirzaaulia.netplix.repository.NetworkRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
class RepositoryModule {

    @Provides
    @ViewModelScoped
    fun proviewNetworkRepository(
        ktor: KtorClient
    ): NetworkRepository {
        return NetworkRepositoryImpl(ktor)
    }
}