package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.MvpView
import ru.wutiarn.edustor.android.presenter.LessonPresenter

/**
 * Created by wutiarn on 05.03.16.
 */
interface LessonDetailsActivityView : MvpView {
    val fragmentPresenter: LessonPresenter?
}