package me.brisson.speedtest.network.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.brisson.speedtest.network.INetworkManager
import me.brisson.speedtest.network.NetworkManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun providesNetworkManager(@ApplicationContext context: Context): INetworkManager {
        return NetworkManager(context)
    }
}
