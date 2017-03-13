package ru.wutiarn.edustor.android.presenter

import android.app.DatePickerDialog
import android.widget.DatePicker
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import io.realm.Realm
import org.threeten.bp.LocalDate
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.MainListEntity
import ru.wutiarn.edustor.android.data.models.Tag
import ru.wutiarn.edustor.android.view.MainListView

class MainListPresenter(val appComponent: AppComponent, val parentTagId: String?) : MvpPresenter<MainListView>,
        DatePickerDialog.OnDateSetListener {
    var view: MainListView? = null

    private enum class EntityType {
        TAGS,
        LESSONS
    }

    override fun detachView(p0: Boolean) {
        view = null
        appComponent.eventBus.unregister(this)
    }

    override fun attachView(p0: MainListView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    fun loadData() {
        parentTagId?.let {
            val tag = appComponent.repo.tag.byId(parentTagId)
            view?.setTitle(tag.name)
        }


        val entities = mutableListOf<MainListEntity>()

        EntityType.values().forEach { entityType ->
            val typeEntities = when (entityType) {
                EntityType.TAGS -> appComponent.repo.tag.byParentTagId(parentTagId).sortedBy(Tag::name)
                EntityType.LESSONS -> {
                    parentTagId ?: return@forEach
                    appComponent.repo.lessons.byTagId(parentTagId)
                            .filter { it.pages.size > 0 }
                            .sortedByDescending(Lesson::date)
                }
            }

            @Suppress("UNCHECKED_CAST")
            entities.addAll(typeEntities as List<MainListEntity>)
        }

        view?.setData(entities)
    }


    fun onSyncSwitchChanged(b: Boolean) {
        val tagSyncStatus = appComponent.pdfSyncManager.getTagSyncStatus(parentTagId!!)

        Realm.getDefaultInstance().use {
            it.executeTransaction {
                tagSyncStatus.markedForSync = b
            }
        }
        appComponent.syncManager.requestSync(true, false)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        val date = LocalDate.of(year, month + 1, day)
        val lesson = appComponent.repo.lessons.byDate(parentTagId!!, date.toEpochDay())
        view?.onLessonClick(lesson)
    }
}