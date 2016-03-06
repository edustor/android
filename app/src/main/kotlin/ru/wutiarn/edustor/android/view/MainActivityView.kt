package ru.wutiarn.edustor.android.view

import android.support.v4.app.Fragment
import com.hannesdorfmann.mosby.mvp.MvpView

/**
 * Created by wutiarn on 05.03.16.
 */
interface MainActivityView : MvpView {
    fun showSlidingPanelFragment(fragment: Fragment)
    fun detachSlidingPanelFragment()
}