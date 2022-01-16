package org.sslabs.tvmaze.ui

interface UICommunicationListener {

    fun displayProgressBar(isLoading: Boolean)

    fun setToolbarExpanded(isCollapsable: Boolean)

    fun setToolbarTitle(title: String?)
}
