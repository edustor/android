package ru.wutiarn.edustor.android.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import kotlinx.android.synthetic.main.fragment_base_list.*
import org.threeten.bp.LocalDateTime
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.activity.LessonDetailsActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.LessonsAdapter
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.presenter.LessonListPresenter
import ru.wutiarn.edustor.android.util.EndlessRecyclerViewScrollListener
import ru.wutiarn.edustor.android.view.LessonsListView

class LessonsListFragment : MvpLceFragment<LinearLayout, MutableList<Lesson>, LessonsListView, LessonListPresenter>(),
        LessonsListView, LessonsAdapter.LessonsAdapterEventsListener {
    lateinit var appComponent: AppComponent
    lateinit var lessonsAdapter: LessonsAdapter

    override fun createPresenter(): LessonListPresenter {
        val application = context.applicationContext as EdustorApplication
        appComponent = application.appComponent
        return LessonListPresenter(appComponent, arguments)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_base_list, container, false)
        if (arguments?.getBoolean("allowDatePick") ?: false == true) {
            val datePicker = inflater?.inflate(R.layout.lesson_date_picker, view?.findViewById(R.id.list_header) as FrameLayout, true)
            val pickerButton = datePicker?.findViewById(R.id.date_picker_button) as Button

            pickerButton.setOnClickListener {
                val now = LocalDateTime.now()
                val dialog = DatePickerDialog(context, presenter, now.year, now.monthValue - 1, now.dayOfMonth)
                dialog.show()
            }
        }

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureRecyclerView()
        loadData(false)
    }

    override fun getErrorMessage(p0: Throwable?, p1: Boolean): String? {
        return p0?.message
    }

    override fun loadData(isPullRefresh: Boolean) {
        loadData(isPullRefresh, 0)
    }

    fun loadData(isPullRefresh: Boolean, page: Int) {
        showLoading(isPullRefresh)
        presenter.loadData(page)
    }

    override fun setData(lessons: MutableList<Lesson>?) {
        lessonsAdapter.lessons = presenter.lessons
        lessonsAdapter.notifyDataSetChanged()
        lessons?.firstOrNull()?.subject?.name?.let {
            activity.title = it
        }
        showContent()
    }

    override fun onLessonClick(lesson: Lesson) {
        val intent = Intent(context, LessonDetailsActivity::class.java)
        intent.putExtra("id", lesson.id)
        startActivity(intent)
    }

    fun configureRecyclerView() {
        lessonsAdapter = LessonsAdapter(appComponent, this)
        val layoutManager = LinearLayoutManager(context)

        val scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int) {
                presenter.loadData(page)
            }
        }

        base_recycler_view.layoutManager = layoutManager
        base_recycler_view.addOnScrollListener(scrollListener)
        base_recycler_view.adapter = lessonsAdapter
    }
}
