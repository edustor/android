package ru.wutiarn.edustor.android.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment
import kotlinx.android.synthetic.main.fragment_base_list.*
import ru.wutiarn.edustor.android.Application
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.activity.LessonsListActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.adapter.SubjectsAdapter
import ru.wutiarn.edustor.android.data.models.Subject
import ru.wutiarn.edustor.android.presenter.SubjectListPresenter
import ru.wutiarn.edustor.android.view.SubjectsListView

/**
 * Created by wutiarn on 13.03.16.
 */
class SubjectsListFragment : MvpLceFragment<LinearLayout, List<Subject>, SubjectsListView, SubjectListPresenter>(), SubjectsListView, SubjectsAdapter.SubjectsAdapterEventsListener {
    lateinit var appComponent: AppComponent
    lateinit var adapter: SubjectsAdapter

    override fun createPresenter(): SubjectListPresenter? {
        val application = context.applicationContext as Application
        appComponent = application.appComponent
        return SubjectListPresenter(appComponent)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_base_list, container, false)
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

    override fun setData(lessons: List<Subject>?) {
        adapter.subjects = lessons ?: emptyList()
        showContent()
    }

    fun configureRecyclerView() {
        adapter = SubjectsAdapter(appComponent, this)
        base_recycler_view.layoutManager = LinearLayoutManager(context)
        base_recycler_view.adapter = adapter
    }

    override fun onSubjectClick(subject: Subject) {
        val intent = Intent(context, LessonsListActivity::class.java)
        intent.putExtra("subject_id", subject.id)
        startActivity(intent)
    }
}