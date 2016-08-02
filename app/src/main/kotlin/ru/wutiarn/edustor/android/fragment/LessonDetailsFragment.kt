package ru.wutiarn.edustor.android.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import kotlinx.android.synthetic.main.fragment_lesson_details.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.DocumentsAdapter
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import ru.wutiarn.edustor.android.presenter.LessonDetailsPresenter
import ru.wutiarn.edustor.android.util.EdustorDocumentTouchHelperCallback
import ru.wutiarn.edustor.android.view.LessonDetailsView


class LessonDetailsFragment : MvpLceFragment<LinearLayout, Lesson, LessonDetailsView, LessonDetailsPresenter>(), LessonDetailsView {

    lateinit var appComponent: AppComponent

    lateinit var documentsAdapter: DocumentsAdapter

    override fun createPresenter(): LessonDetailsPresenter {
        val application = context.applicationContext as EdustorApplication
        appComponent = application.appComponent
        return LessonDetailsPresenter(appComponent, arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        topic_save_button.setOnClickListener { presenter.setTopic(topic.text.toString()) }
        loadData(false)
    }

    override fun setData(lesson: Lesson?) {

        subject.text = lesson?.subject?.name
        date.text = lesson?.date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
        topic.setText(lesson?.topic)


        lesson?.syncStatus?.let {
            syncSwitch.isChecked = it.markedForSync
            syncStatus.text = when (it.getStatus(lesson, context)) {
                PdfSyncStatus.SyncStatus.SYNCED -> "Synced"
                PdfSyncStatus.SyncStatus.OBSOLETE -> "Obsolete"
                PdfSyncStatus.SyncStatus.MISSING -> "Not synced"
                else -> "State in unknown"
            }
            expDate.text = if (it.realmValidUntil != null)
                LocalDate.ofEpochDay(it.realmValidUntil!!).toString() else "None"
        }

        documentsAdapter.lesson = lesson

        getPdf.setOnClickListener { presenter.onGetPdfClicked() }
        copyUrl.setOnClickListener { presenter.onCopyUrlClicked() }
        syncSwitch.setOnCheckedChangeListener { button, b -> presenter.onSyncSwitchChanged(b) }

        showContent()
    }

    override fun setPdfSyncStatus(status: String) {
        syncStatus.text = status
    }

    override fun loadData(p0: Boolean) {
        showLoading(false)
        presenter.loadData()
    }

    override fun getErrorMessage(p0: Throwable, p1: Boolean): String? {
        return p0.message
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_lesson_details, container, false)
    }

    fun configureRecyclerView() {
        documentsAdapter = DocumentsAdapter(context, appComponent)


        documents_recycler_view.adapter = documentsAdapter
        documents_recycler_view.layoutManager = LinearLayoutManager(context)

        val cb = EdustorDocumentTouchHelperCallback(context)
        val itemTouchHelper = ItemTouchHelper(cb)

        itemTouchHelper.attachToRecyclerView(documents_recycler_view)
    }
}