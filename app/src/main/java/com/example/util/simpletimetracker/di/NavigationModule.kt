package com.example.util.simpletimetracker.di

import com.example.util.simpletimetracker.navigation.ActionResolver
import com.example.util.simpletimetracker.navigation.ActionResolverImpl
import com.example.util.simpletimetracker.navigation.NotificationResolver
import com.example.util.simpletimetracker.navigation.NotificationResolverImpl
import com.example.util.simpletimetracker.navigation.Router
import com.example.util.simpletimetracker.navigation.RouterImpl
import com.example.util.simpletimetracker.navigation.ScreenFactory
import com.example.util.simpletimetracker.navigation.ScreenFactoryImpl
import com.example.util.simpletimetracker.navigation.ScreenResolver
import com.example.util.simpletimetracker.navigation.ScreenResolverImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @Singleton
    fun ScreenResolverImpl.bindScreenResolver(): ScreenResolver

    @Binds
    @Singleton
    fun ScreenFactoryImpl.bindScreenFactory(): ScreenFactory

    @Binds
    @Singleton
    fun ActionResolverImpl.bindActionResolver(): ActionResolver

    @Binds
    @Singleton
    fun NotificationResolverImpl.bindNotificationResolver(): NotificationResolver

    @Binds
    @Singleton
    fun RouterImpl.bindRouter(): Router
}