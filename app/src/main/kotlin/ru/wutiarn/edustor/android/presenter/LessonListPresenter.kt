package ru.wutiarn.edustor.android.presenter

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.LessonsListView

/**
 * Created by wutiarn on 10.03.16.
 */
class LessonListPresenter(val appComponent: AppComponent, val arguments: Bundle?) : MvpPresenter<LessonsListView>,
        DatePickerDialog.OnDateSetListener {

    var subjectId: String? = null

    var view: LessonsListView? = null
    var lessons: MutableList<Lesson>? = null


    init {
        subjectId = arguments?.getString("subject_id")
    }

    override fun detachView(p0: Boolean) {
        view = null
    }

    override fun attachView(p0: LessonsListView?) {
        view = p0
    }

    fun loadData() {
        if (subjectId == null) {
            appComponent.lessonsApi.today()
                    .map { it.toMutableList() }
                    .configureAsync()
                    .linkToLCEView(view, { lessons = it })
        } else {
            appComponent.lessonsApi.bySubjectId(subjectId!!)
                    .map { it.toMutableList() }
                    .configureAsync()
                    .linkToLCEView(view, { lessons = it })
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        
    }
}