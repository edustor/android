package ru.wutiarn.edustor.android.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import kotlinx.android.synthetic.main.fragment_lesson.*
import kotlinx.android.synthetic.main.lesson_info.*
import org.threeten.bp.format.DateTimeFormatter
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.DocumentsAdapter
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.presenter.LessonPresenter
import ru.wutiarn.edustor.android.view.LessonView


class LessonFragment : MvpLceFragment<LinearLayout, Lesson, LessonView, LessonPresenter>(), LessonView {

    lateinit var appComponent: AppComponent

    override fun makeSnackbar(msg: String) {
        view?.let {
            Snackbar.make(view!!, msg, Snackbar.LENGTH_LONG).show()
        }
    }

    lateinit var documentsAdapter: DocumentsAdapter
    lateinit var wrappedDocumentsAdapter: RecyclerView.Adapter<*>

    override fun createPresenter(): LessonPresenter? {
        val application = context.applicationContext as Application
        appComponent = application.appComponent
        return LessonPresenter(appComponent, arguments)
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

        documentsAdapter.documents = lesson?.documents ?: mutableListOf()

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
        documentsAdapter = DocumentsAdapter(presenter, appComponent)

        val recyclerViewTouchActionGuardManager = RecyclerViewTouchActionGuardManager()
        recyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true)
        recyclerViewTouchActionGuardManager.isEnabled = true

        val recyclerViewDragDropManager = RecyclerViewDragDropManager()
        recyclerViewDragDropManager.setInitiateOnMove(false)
        recyclerViewDragDropManager.setInitiateOnLongPress(true)
        recyclerViewDragDropManager.setLongPressTimeout(1000)

        val recyclerViewSwipeManager = RecyclerViewSwipeManager()


        val animator = SwipeDismissItemAnimator()
        animator.supportsChangeAnimations = false

        wrappedDocumentsAdapter = recyclerViewDragDropManager.createWrappedAdapter(documentsAdapter)
        wrappedDocumentsAdapter = recyclerViewSwipeManager.createWrappedAdapter(wrappedDocumentsAdapter)


        documents_recycler_view.layoutManager = LinearLayoutManager(this.context)
        documents_recycler_view.adapter = wrappedDocumentsAdapter
        documents_recycler_view.itemAnimator = animator

        recyclerViewTouchActionGuardManager.attachRecyclerView(documents_recycler_view)
        recyclerViewSwipeManager.attachRecyclerView(documents_recycler_view)
        recyclerViewDragDropManager.attachRecyclerView(documents_recycler_view)

        documents_recycler_view.addItemDecoration(SimpleListDividerDecorator(ContextCompat.getDrawable(context, R.drawable.list_divider_h), true))

        //        val header = RecyclerViewHeader.fromXml(context, R.layout.lesson_info)
        //        header.attachTo(documents_recycler_view)
    }
}