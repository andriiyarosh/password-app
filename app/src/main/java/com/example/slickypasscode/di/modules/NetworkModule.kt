package com.example.slickypasscode.di.modules

import com.example.slickypasscode.network.ServerInterface
import com.example.slickypasscode.network.SlickyMockServer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideServerInterface(): ServerInterface {
        return SlickyMockServer
    }
}