package com.squillaci.autodiag.di

import com.squillaci.autodiag.data.repository.ObdRepositoryImpl
import com.squillaci.autodiag.domain.repository.ObdRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindObdRepository(impl: ObdRepositoryImpl): ObdRepository
}
