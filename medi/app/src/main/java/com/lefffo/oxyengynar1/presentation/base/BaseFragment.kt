package com.lefffo.oxyengynar1.presentation.base

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.lefffo.oxyengynar1.MainActivity
import com.lefffo.oxyengynar1.Navigator

open class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId),
    Navigator {

    private lateinit var navigator: Navigator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        require(context is Navigator) { "$context must implement Navigator" }
        navigator = context
    }

    override fun navigateTo(fragment: Fragment) {
        navigator.navigateTo(fragment)
    }

    override fun navigateBack() {
        navigator.navigateBack()
    }

    protected fun exitKioskMode() {
        (requireActivity() as MainActivity).showExitKioskModeDialog()
    }
}