package org.sslabs.tvmaze.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import org.sslabs.tvmaze.navigation.IScreensNavigator
import org.sslabs.tvmaze.ui.UICommunicationListener
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    lateinit var uiCommunicationListener: UICommunicationListener

    @Inject
    lateinit var screensNavigator: IScreensNavigator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Timber.e("$context must implement UICommunicationListener")
        }
    }
}
