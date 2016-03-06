package ru.wutiarn.edustor.android.view

import android.support.v4.app.Fragment
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import ru.wutiarn.edustor.android.data.models.Lesson

/**
 * Created by wutiarn on 05.03.16.
 */
interface MainActivityView : MvpLceView<Lesson> {
    fun showSlidingPanelFragment(fragment: Fragment)
    fun detachSlidingPanelFragment()
    fun makeSnackbar(string: String)
}