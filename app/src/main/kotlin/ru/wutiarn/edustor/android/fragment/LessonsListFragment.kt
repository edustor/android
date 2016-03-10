package ru.wutiarn.edustor.android.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import kotlinx.android.synthetic.main.fragment_lessons_list.*
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.LessonsAdapter
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.presenter.LessonListPresenter
import ru.wutiarn.edustor.android.view.LessonsListView

/**
 * Created by wutiarn on 10.03.16.
 */
class LessonsListFragment : MvpLceFragment<LinearLayout, MutableList<Lesson>, LessonsListView, LessonListPresenter>(), LessonsListView {
    lateinit var appComponent: AppComponent
    lateinit var lessonsAdapter: LessonsAdapter

    override fun createPresenter(): LessonListPresenter? {
        val application = context.applicationContext as Application
        appComponent = application.appComponent
        return LessonListPresenter(appComponent)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_lessons_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureRecyclerView()
        loadData(false)
    }

    override fun getErrorMessage(p0: Throwable?, p1: Boolean): String? {
        return p0?.message
    }

    override fun loadData(p0: Boolean) {
        showLoading(p0)
        presenter.loadData()
    }

    override fun setData(lessons: MutableList<Lesson>?) {
        lessonsAdapter.lessons = lessons ?: mutableListOf()
        showContent()
    }

    fun configureRecyclerView() {
        lessonsAdapter = LessonsAdapter(appComponent)
        lessons_recycler_view.layoutManager = LinearLayoutManager(context)
        lessons_recycler_view.adapter = lessonsAdapter
    }
}
