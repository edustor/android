package ru.wutiarn.edustor.android.presenter

import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.view.LessonsListView

/**
 * Created by wutiarn on 10.03.16.
 */
class LessonListPresenter : MvpPresenter<LessonsListView> {

    var view: LessonsListView? = null

    override fun detachView(p0: Boolean) {
        view = null
    }

    override fun attachView(p0: LessonsListView?) {
        view = p0
    }

    fun loadData() {

    }

}