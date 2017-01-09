package ru.wutiarn.edustor.android.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_base_list.*
import org.threeten.bp.LocalDateTime
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.activity.LessonDetailsActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.LessonsAdapter
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.Subject
import ru.wutiarn.edustor.android.events.EdustorMetaSyncFinished
import ru.wutiarn.edustor.android.presenter.LessonListPresenter
import ru.wutiarn.edustor.android.util.extension.makeSnack
import ru.wutiarn.edustor.android.view.LessonsListView

class LessonsListFragment() : MvpLceFragment<LinearLayout, List<Lesson>, LessonsListView, LessonListPresenter>(),
        LessonsListView, LessonsAdapter.LessonsAdapterEventsListener {
    lateinit var appComponent: AppComponent
    lateinit var lessonsAdapter: LessonsAdapter
    var switch: Switch? = null
    var subject: Subject? = null

    var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun createPresenter(): LessonListPresenter {
        val application = context.applicationContext as EdustorApplication
        appComponent = application.appComponent
        return LessonListPresenter(appComponent, arguments)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_base_list, container, false)!!
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout?.setOnRefreshListener {
            appComponent.syncManager.requestSync(true, false)
        }

        if (arguments?.getBoolean("allowDatePick") ?: false) {
            val calendarFab = activity.fab_calendar
            calendarFab.visibility = View.VISIBLE

            calendarFab.setOnClickListener {
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

    override fun onStart() {
        super.onStart()
        appComponent.eventBus.register(this)

    }

    override fun onStop() {
        super.onStop()
        appComponent.eventBus.unregister(this)
    }

    override fun getErrorMessage(p0: Throwable?, p1: Boolean): String? {
        return p0?.message
    }

    override fun loadData(isPullRefresh: Boolean) {
        showLoading(isPullRefresh)
        presenter.loadData()
    }

    override fun setData(lessons: List<Lesson>?) {
        lessonsAdapter.lessons = lessons ?: emptyList()
        lessonsAdapter.notifyDataSetChanged()
        lessons?.firstOrNull()?.subject?.let {
            subject = it
            activity?.title = it.name
            initializeSwitch(it)
        }
        if (isResumed) showContent()
    }

    fun setSyncSwitch(switch: Switch) {
        this.switch = switch
        if (subject != null) initializeSwitch(subject!!)
    }

    private fun initializeSwitch(subject: Subject) {
        switch?.isEnabled = true
        switch?.isChecked = appComponent.pdfSyncManager.getSubjectSyncStatus(subject.id).markedForSync
        switch?.setOnCheckedChangeListener { button, b -> presenter.onSyncSwitchChanged(b) }
    }

    override fun onLessonClick(lesson: Lesson) {
        val intent = Intent(context, LessonDetailsActivity::class.java)
        intent.putExtra("id", lesson.id)
        startActivity(intent)
    }

    fun configureRecyclerView() {
        lessonsAdapter = LessonsAdapter(appComponent, this)
        val layoutManager = LinearLayoutManager(context)

        base_recycler_view.layoutManager = layoutManager
        base_recycler_view.adapter = lessonsAdapter
    }

    @Subscribe fun OnSyncFinished(event: EdustorMetaSyncFinished) {
        if (event.success) {
            swipeRefreshLayout?.isRefreshing = false
        } else {
            appComponent.eventBus.makeSnack("Sync finished with error: ${event.exception?.message}")
        }
    }
}
