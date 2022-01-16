package org.sslabs.tvmaze.di.fragment

import android.R
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import org.sslabs.tvmaze.navigation.AppScreensNavigator
import org.sslabs.tvmaze.navigation.IScreensNavigator

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

    @Provides
    fun providesGlideRequestOptions() = RequestOptions
        .placeholderOf(ColorDrawable(R.attr.colorAccent))
        .error(org.sslabs.tvmaze.R.drawable.default_image)

    @Provides
    fun provideNavController(fragment: Fragment): NavController = fragment.findNavController()

    @Provides
    fun provideScreensNavigator(navController: NavController): IScreensNavigator =
        AppScreensNavigator(navController)
}