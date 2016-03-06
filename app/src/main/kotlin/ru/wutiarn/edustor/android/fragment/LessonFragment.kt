package ru.wutiarn.edustor.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import kotlinx.android.synthetic.main.fragment_lesson.*
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.presenter.LessonPresenter
import ru.wutiarn.edustor.android.view.LessonView


class LessonFragment : MvpLceFragment<LinearLayout, Lesson, LessonView, LessonPresenter>(), LessonView {

    override fun createPresenter(): LessonPresenter? {
        val uuid = arguments.getString("uuid")
        val application = context.applicationContext as Application
        return LessonPresenter(application.appComponent, uuid = uuid)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData(false)
    }

    override fun setData(p0: Lesson) {
        subject.text = p0.subject?.name
        lesson_date.text = p0.date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
        showContent()
    }

    override fun loadData(p0: Boolean) {
        showLoading(false)
        presenter.loadData()
    }

    override fun getErrorMessage(p0: Throwable, p1: Boolean): String? {
        return p0.message
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_lesson, container, false)
    }
}