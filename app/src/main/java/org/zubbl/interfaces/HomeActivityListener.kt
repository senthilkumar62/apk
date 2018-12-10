package org.zubbl.interfaces

import android.os.Bundle

interface HomeActivityListener {
    fun switchFragments(fragment: String, bundle: Bundle?, isTransition: Boolean, isClearStack: Boolean)

    fun setTitle(title: String)

    fun serLeftIcon(lefticon: Int)

    fun navigateBack()

    fun setRightIcon(rightIcon: Int)

    fun setActionBarListener(listener: ActionBarIconlistener)

    fun showSideNavigationDrawer()

    fun hideTopActionBar()

    fun showTopActionBar()
}