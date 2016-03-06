package ru.wutiarn.edustor.android.presenter

import android.os.Bundle
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.fragment.LessonFragment
import ru.wutiarn.edustor.android.view.MainActivityView

/**
 * Created by wutiarn on 06.03.16.
 */
class MainActivityPresenter : MvpPresenter<MainActivityView> {
    private var view: MainActivityView? = null

    override fun attachView(p0: MainActivityView?) {
        view = p0
    }

    override fun detachView(p0: Boolean) {
        view = null
    }

    fun showLessonInfo(uuid: String) {
        val documentInfoFragment = LessonFragment()
        val fragmentBundle = Bundle()
        fragmentBundle.putString("uuid", uuid)
        documentInfoFragment.arguments = fragmentBundle

        view?.showSlidingPanelFragment(documentInfoFragment)
    }
}