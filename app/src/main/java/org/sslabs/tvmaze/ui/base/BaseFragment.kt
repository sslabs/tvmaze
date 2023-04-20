package org.sslabs.tvmaze.ui.base

import android.content.Context
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import org.sslabs.tvmaze.navigation.IScreensNavigator
import org.sslabs.tvmaze.ui.UICommunicationListener
import org.sslabs.tvmaze.util.menuHost
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    lateinit var uiCommunicationListener: UICommunicationListener
    protected var menuProvider: MenuProvider? = null

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

    override fun onDestroyView() {
        menuProvider?.let { menuHost().removeMenuProvider(it) }
        super.onDestroyView()
    }
}
