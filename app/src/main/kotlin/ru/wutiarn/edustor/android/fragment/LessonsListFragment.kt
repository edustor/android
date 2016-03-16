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
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.activity.LessonDetailsActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.LessonsAdapter
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.presenter.LessonListPresenter
import ru.wutiarn.edustor.android.view.LessonsListView

/**
 * Created by wutiarn on 10.03.16.
 */
class LessonsListFragment : MvpLceFragment<LinearLayout, MutableList<Lesson>, LessonsListView, LessonListPresenter>(),
        LessonsListView, LessonsAdapter.LessonsAdapterEventsListener {
    lateinit var appComponent: AppComponent
    lateinit var lessonsAdapter: LessonsAdapter

    override fun createPresenter(): LessonListPresenter? {
        val application = context.applicationContext as Application
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
                val dialog = DatePickerDialog(context, presenter, now.year, now.monthValue, now.dayOfMonth)
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

    override fun loadData(p0: Boolean) {
        showLoading(p0)
        presenter.loadData()
    }

    override fun setData(lessons: MutableList<Lesson>?) {
        lessonsAdapter.lessons = lessons ?: mutableListOf()
        showContent()
    }

    override fun onLessonClick(lesson: Lesson) {
        val intent = Intent(context, LessonDetailsActivity::class.java)
        intent.putExtra("id", lesson.id)
        startActivity(intent)
    }

    fun configureRecyclerView() {
        lessonsAdapter = LessonsAdapter(appComponent, this)
        base_recycler_view.layoutManager = LinearLayoutManager(context)
        base_recycler_view.adapter = lessonsAdapter
    }
}
