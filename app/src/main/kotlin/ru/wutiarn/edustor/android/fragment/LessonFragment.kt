package ru.wutiarn.edustor.android.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.fragment_lesson.*
import kotlinx.android.synthetic.main.lesson_info.*
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.data.models.Document
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.presenter.LessonPresenter
import ru.wutiarn.edustor.android.view.LessonView


class LessonFragment : MvpLceFragment<LinearLayout, Lesson, LessonView, LessonPresenter>(), LessonView {

    lateinit var documentsAdapter: FlexibleAdapter<Document>


    override fun createPresenter(): LessonPresenter? {
        val application = context.applicationContext as Application
        return LessonPresenter(application.appComponent, arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        loadData(false)
    }

    override fun setData(lesson: Lesson?) {

        subject.text = lesson?.subject?.name
        date.text = lesson?.date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
        start_time.text = lesson?.start?.format(DateTimeFormatter.ISO_LOCAL_TIME)
        end_time.text = lesson?.end?.format(DateTimeFormatter.ISO_LOCAL_TIME)

        documentsAdapter.updateDataSet(lesson?.documents ?: emptyList())

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

    fun configureRecyclerView() {
        documents_recycler_view.layoutManager = LinearLayoutManager(this.context)
        documentsAdapter = FlexibleAdapter<Document>(emptyList())
        documents_recycler_view.adapter = documentsAdapter

        val header = RecyclerViewHeader.fromXml(context, R.layout.lesson_info)
        header.attachTo(documents_recycler_view)
    }
}