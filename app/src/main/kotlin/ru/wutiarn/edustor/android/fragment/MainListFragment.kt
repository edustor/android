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
import ru.wutiarn.edustor.android.activity.MainActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.MainListEntityAdapter
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.MainListEntity
import ru.wutiarn.edustor.android.data.models.Tag
import ru.wutiarn.edustor.android.events.EdustorMetaSyncFinished
import ru.wutiarn.edustor.android.presenter.MainListPresenter
import ru.wutiarn.edustor.android.util.extension.makeSnack
import ru.wutiarn.edustor.android.view.MainListView

class MainListFragment : MvpLceFragment<LinearLayout, List<MainListEntity>, MainListView, MainListPresenter>(),
        MainListView, MainListEntityAdapter.MainListEntityAdapterEventsListener {
    lateinit var appComponent: AppComponent
    lateinit var entityAdapter: MainListEntityAdapter

    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var syncSwitch: Switch? = null
        set(value) {
            field = value
            configureSyncSwitch()
        }


    override fun createPresenter(): MainListPresenter {
        val application = context.applicationContext as EdustorApplication
        appComponent = application.appComponent

        val parentTagId = arguments?.getString("parent_tag_id")

        return MainListPresenter(appComponent, parentTagId)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_base_list, container, false)!!
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout?.setOnRefreshListener {
            appComponent.syncManager.requestSync(true, false)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        appComponent.eventBus.register(this)

    }

    override fun onStop() {
        super.onStop()
        appComponent.eventBus.unregister(this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureFabs()
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

    override fun setData(entities: List<MainListEntity>?) {
        entityAdapter.entities = entities ?: emptyList()
        entityAdapter.notifyDataSetChanged()
        showContent()
    }

    fun configureRecyclerView() {
        entityAdapter = MainListEntityAdapter(appComponent, this)
        base_recycler_view.layoutManager = LinearLayoutManager(context)
        base_recycler_view.adapter = entityAdapter
    }

    private fun configureSyncSwitch() {
        if (presenter.parentTagId != null) {
            syncSwitch?.isEnabled = true
            syncSwitch?.isChecked = appComponent.pdfSyncManager.getTagSyncStatus(presenter.parentTagId!!).markedForSync
            syncSwitch?.setOnCheckedChangeListener { button, b -> presenter.onSyncSwitchChanged(b) }
        }
        else {
            syncSwitch?.visibility = View.GONE
        }
    }

    fun configureFabs() {
        val calendarFab = activity.fab_calendar
        val scanExistedButton = activity.fab_scan_existed

        if (presenter.parentTagId != null) {
            calendarFab.visibility = View.VISIBLE
            scanExistedButton.visibility = View.GONE

            calendarFab.setOnClickListener {
                val now = LocalDateTime.now()
                val dialog = DatePickerDialog(context, presenter, now.year, now.monthValue - 1, now.dayOfMonth)
                dialog.show()
            }
        }
        else {
            scanExistedButton.visibility = View.VISIBLE
            calendarFab.visibility = View.GONE
        }
    }

    override fun onTagClick(tag: Tag) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("parent_tag_id", tag.id)
        startActivity(intent)
    }

    override fun onLessonClick(lesson: Lesson) {
        val intent = Intent(context, LessonDetailsActivity::class.java)
        intent.putExtra("id", lesson.id)
        startActivity(intent)
    }

    @Subscribe fun OnSyncFinished(event: EdustorMetaSyncFinished) {
        if (event.success) {
            swipeRefreshLayout?.isRefreshing = false
        } else {
            appComponent.eventBus.makeSnack("Sync finished with error: ${event.exception?.message}")
        }
    }
}
