package ru.wutiarn.edustor.android.view

import com.hannesdorfmann.mosby.mvp.MvpView
import ru.wutiarn.edustor.android.presenter.LessonDetailsPresenter

interface LessonDetailsActivityView : MvpView {
    val fragmentPresenter: LessonDetailsPresenter?
}