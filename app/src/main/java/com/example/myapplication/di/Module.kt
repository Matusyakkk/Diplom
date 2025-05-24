package com.example.myapplication.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.metamask.androidsdk.DappMetadata
import io.metamask.androidsdk.Ethereum
import io.metamask.androidsdk.SDKOptions
import javax.inject.Singleton

var INFURA_KEY = "XXX"

@Module
@InstallIn(SingletonComponent::class)
class Module {
    @Provides
    @Singleton
    fun provideEthereum(@ApplicationContext context: Context): Ethereum {
        return Ethereum(
            context = context,
            dappMetadata = DappMetadata(
                name = context.applicationInfo.name,
                url = "https://${context.applicationInfo.name}.com"
            ),
            sdkOptions = SDKOptions(infuraAPIKey = INFURA_KEY)
        )
    }
}