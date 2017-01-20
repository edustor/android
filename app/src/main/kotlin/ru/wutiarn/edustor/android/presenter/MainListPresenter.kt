package ru.wutiarn.edustor.android.presenter

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import io.realm.Realm
import org.threeten.bp.LocalDate
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.MainListEntity
import ru.wutiarn.edustor.android.data.models.Tag
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.view.MainListView
import rx.Observable
import rx.subscriptions.CompositeSubscription

class MainListPresenter(val appComponent: AppComponent, val parentTagId: String?) : MvpPresenter<MainListView>,
        DatePickerDialog.OnDateSetListener {
    var view: MainListView? = null

    var activeSubscription: CompositeSubscription? = null
    private val entityCache: MutableMap<EntityType, List<MainListEntity>> = mutableMapOf()

    private enum class EntityType {
        TAGS,
        LESSONS
    }

    override fun detachView(p0: Boolean) {
        view = null
        appComponent.eventBus.unregister(this)
        activeSubscription?.unsubscribe()
    }

    override fun attachView(p0: MainListView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    fun loadData() {
        activeSubscription?.unsubscribe()
        activeSubscription = CompositeSubscription()
        entityCache.clear()

        EntityType.values().forEach { entityType ->
            @Suppress("UNCHECKED_CAST")
            val observable: Observable<List<MainListEntity>> = when (entityType) {
                EntityType.TAGS -> appComponent.repo.tag.byTagParentTagId(parentTagId).map { it.sortedBy(Tag::name) } as Observable<List<MainListEntity>>
                EntityType.LESSONS -> {
                    parentTagId ?: return@forEach
                    appComponent.repo.lessons.byTagId(parentTagId)
                            .map {
                                it.filter { it.pages.size > 0 }.sortedByDescending(Lesson::date)
                            } as Observable<List<MainListEntity>>
                }
                else -> throw IllegalStateException("Unsupported entity type") // (Almost) impossible :)
            }

            val subscription = observable
                    .map {
                        entityCache[entityType] = it

                        EntityType.values()
                                .filter { it in entityCache }
                                .flatMap { entityCache[it] ?: emptyList() }
                    }
                    .linkToLCEView(view)
            activeSubscription!!.add(subscription)
        }
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
        appComponent.repo.lessons.byDate(parentTagId!!, date.toEpochDay())
                .first()
                .subscribe(
                        { view?.onLessonClick(it) },
                        {
                            Log.w("LessonListPresenter", "Error in onDateSet", it)
                            appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}"))
                        }
                )
    }
}