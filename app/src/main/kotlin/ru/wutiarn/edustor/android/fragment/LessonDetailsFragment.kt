package ru.wutiarn.edustor.android.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import kotlinx.android.synthetic.main.fragment_lesson_details.*
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.DocumentsAdapter
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.DocumentAddedEvent
import ru.wutiarn.edustor.android.events.DocumentChangedEvent
import ru.wutiarn.edustor.android.events.DocumentMovedEvent
import ru.wutiarn.edustor.android.events.DocumentRemovedEvent
import ru.wutiarn.edustor.android.presenter.LessonPresenter
import ru.wutiarn.edustor.android.util.EdustorItemTouchHelperCallback
import ru.wutiarn.edustor.android.util.extension.makeToast
import ru.wutiarn.edustor.android.view.LessonDetailsView


class LessonDetailsFragment : MvpLceFragment<LinearLayout, Lesson, LessonDetailsView, LessonPresenter>(), LessonDetailsView {

    lateinit var appComponent: AppComponent

    lateinit var documentsAdapter: DocumentsAdapter
    lateinit var wrappedDocumentsAdapter: RecyclerView.Adapter<*>

    override fun createPresenter(): LessonPresenter {
        val application = context.applicationContext as EdustorApplication
        appComponent = application.appComponent
        return LessonPresenter(appComponent, arguments)
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

        getPdf.setOnClickListener {
            val uri = Uri.parse(appComponent.constants.URL + "pdf/${lesson?.id}.pdf")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        copyUrl.setOnClickListener {
            val uri = appComponent.constants.URL + "pdf/${lesson?.id}"
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.primaryClip = ClipData.newPlainText(uri, uri)
            context.makeToast("Copied: $uri")
        }

        documentsAdapter.lesson = lesson

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
        return inflater?.inflate(R.layout.fragment_lesson_details, container, false)
    }

    fun configureRecyclerView() {
        documentsAdapter = DocumentsAdapter(context, appComponent)


        documents_recycler_view.adapter = documentsAdapter
        documents_recycler_view.layoutManager = LinearLayoutManager(context)

        val cb = EdustorItemTouchHelperCallback(context)
        val itemTouchHelper = ItemTouchHelper(cb)

        itemTouchHelper.attachToRecyclerView(documents_recycler_view)
//        documents_recycler_view.addItemDecoration(SimpleListDividerDecorator(ContextCompat.getDrawable(context, R.drawable.list_divider_h), true))
    }

    override fun notifyDocumentsChanged(event: DocumentChangedEvent) {
        val adapter = this.documentsAdapter
        when (event) {
            is DocumentMovedEvent -> {
                adapter.notifyItemMoved(event.fromPos, event.toPos)
            }
            is DocumentRemovedEvent -> {
                adapter.notifyItemRemoved(event.position)
            }
            is DocumentAddedEvent -> {
                adapter.notifyItemInserted(event.insertedPosition)
            }
            else -> {
                adapter.notifyDataSetChanged()
            }
        }
    }
}