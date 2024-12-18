package com.example.bolatix.di

import com.example.bolatix.BuildConfig
import com.example.bolatix.data.remote.network.CloudService
import com.example.bolatix.data.remote.network.RetryInterceptor
import com.example.bolatix.data.repository.AuthRepository
import com.example.bolatix.data.repository.FootballRepository
import com.example.bolatix.preference.UserPreferences
import com.example.bolatix.ui.viewmodels.AuthViewModel
import com.example.bolatix.ui.viewmodels.ProfileViewModel
import com.example.bolatix.ui.viewmodels.RecomendedViewModel
import com.example.bolatix.ui.viewmodels.StandingsFragmentViewModel
import com.example.bolatix.ui.viewmodels.UpcomingTicketFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE

    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(RetryInterceptor(maxRetry = 5))
        .build()

    single<CloudService> {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CloudService::class.java)
    }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { UserPreferences(get()) }
    single { AuthRepository(get(), get(), get()) }
    single { FootballRepository(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { UpcomingTicketFragmentViewModel(get()) }
    viewModel { StandingsFragmentViewModel(get()) }
    viewModel { RecomendedViewModel(get()) }
}