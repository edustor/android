package ru.wutiarn.edustor.android.fragment

import android.widget.LinearLayout
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.presenter.LessonListPresenter
import ru.wutiarn.edustor.android.view.LessonsListView

/**
 * Created by wutiarn on 10.03.16.
 */
class LessonsListFragment : MvpLceFragment<LinearLayout, MutableList<Lesson>, LessonsListView, LessonListPresenter>() {
    override fun createPresenter(): LessonListPresenter? {
        return LessonListPresenter()
    }

    override fun getErrorMessage(p0: Throwable?, p1: Boolean): String? {
        return p0?.message
    }

    override fun loadData(p0: Boolean) {
        presenter.loadData()
    }

    override fun setData(p0: MutableList<Lesson>?) {

    }
}
