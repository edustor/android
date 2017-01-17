package ru.wutiarn.edustor.android.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.fragment_base_list.*
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.activity.LessonsListActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.TagAdapter
import ru.wutiarn.edustor.android.data.models.Tag
import ru.wutiarn.edustor.android.events.EdustorMetaSyncFinished
import ru.wutiarn.edustor.android.presenter.TagListPresenter
import ru.wutiarn.edustor.android.util.extension.makeSnack
import ru.wutiarn.edustor.android.view.TagListView

class TagListFragment : MvpLceFragment<LinearLayout, List<Tag>, TagListView, TagListPresenter>(),
        TagListView, TagAdapter.TagAdapterEventsListener {
    lateinit var appComponent: AppComponent
    lateinit var adapter: TagAdapter

    var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun createPresenter(): TagListPresenter {
        val application = context.applicationContext as EdustorApplication
        appComponent = application.appComponent

        val parentTagId = arguments?.getString("parent_tag_id")

        return TagListPresenter(appComponent, parentTagId)
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

    override fun setData(lessons: List<Tag>?) {
        adapter.tags = lessons ?: emptyList()
        adapter.notifyDataSetChanged()
        showContent()
    }

    fun configureRecyclerView() {
        adapter = TagAdapter(appComponent, this)
        base_recycler_view.layoutManager = LinearLayoutManager(context)
        base_recycler_view.adapter = adapter
    }

    override fun onTagClick(tag: Tag) {
        val intent = Intent(context, LessonsListActivity::class.java)
        intent.putExtra("tag_id", tag.id)
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
