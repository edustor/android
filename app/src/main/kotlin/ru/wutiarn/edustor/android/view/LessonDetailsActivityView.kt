package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.MvpView
import ru.wutiarn.edustor.android.presenter.LessonPresenter

interface LessonDetailsActivityView : MvpView {
    val fragmentPresenter: LessonPresenter?
}