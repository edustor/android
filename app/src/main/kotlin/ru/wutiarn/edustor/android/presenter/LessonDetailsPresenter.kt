package ru.wutiarn.edustor.android.presenter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.squareup.otto.Subscribe
import io.realm.Realm
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.events.PdfSyncProgressEvent
import ru.wutiarn.edustor.android.events.RealmSyncFinishedEvent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.util.extension.getPdfUrl
import ru.wutiarn.edustor.android.util.extension.linkToLCEView
import ru.wutiarn.edustor.android.util.extension.makeSnack
import ru.wutiarn.edustor.android.util.extension.setUpSyncState
import ru.wutiarn.edustor.android.view.LessonDetailsView
import rx.Subscription

class LessonDetailsPresenter(val appComponent: AppComponent, arguments: Bundle) : MvpPresenter<LessonDetailsView> {

    var view: LessonDetailsView? = null
    var activeSubscription: Subscription? = null

    var uuid: String? = null
    var subjectId: String? = null
    var lessonEpochDay: Long? = null

    var lesson: Lesson? = null

    init {
        uuid = arguments.getString("uuid")
        subjectId = arguments.getString("subject")
        lessonEpochDay = arguments.getLong("date")
    }

    override fun detachView(p0: Boolean) {
        appComponent.eventBus.unregister(this)
        activeSubscription?.unsubscribe()
        view = null
    }

    override fun attachView(p0: LessonDetailsView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    @Subscribe fun onSyncFinished(event: RealmSyncFinishedEvent) {
        loadData()
    }

    @Subscribe fun onPdfSyncProgress(event: PdfSyncProgressEvent) {
        if (event.lessonId != lesson?.id) return
        val status = if (event.done) "Synced" else "${event.percent}%"
        view?.setPdfSyncStatus(status)
    }

    fun loadData() {
        activeSubscription?.unsubscribe()
        when {
            uuid != null -> {
                activeSubscription = appComponent.repo.lessons.byUUID(uuid!!)
                        .setUpSyncState(appComponent.pdfSyncManager)
                        .linkToLCEView(view, { lesson = it })
            }
            subjectId != null -> {
                activeSubscription =
                        appComponent.repo.lessons.byDate(subjectId!!, lessonEpochDay!!)
                                .setUpSyncState(appComponent.pdfSyncManager)
                                .linkToLCEView(view, { lesson = it })
            }
        }
    }

    fun onGetPdfClicked() {
        val uri = Uri.parse(appComponent.constants.URL + "pdf/${lesson?.id}.pdf")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        appComponent.context.startActivity(intent)
    }

    fun onCopyUrlClicked() {
        val uri = lesson?.getPdfUrl(appComponent.constants.URL)
        val clipboardManager = appComponent.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = ClipData.newPlainText(uri, uri)
        appComponent.eventBus.makeSnack("Copied: $uri")
    }

    fun onSyncSwitchChanged(isEnabled: Boolean) {
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                lesson?.syncStatus?.markedForSync = isEnabled
            }
            appComponent.pdfSyncManager.requestSync(true)
        }
    }

    fun setTopic(topic: String) {
        lesson?.let {
            appComponent.repo.lessons.setTopic(lesson?.id!!, topic)
                    .subscribe(
                            { appComponent.eventBus.post(RequestSnackbarEvent("Successfully renamed")) },
                            { appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}")) }
                    )
        }
    }

    fun onQrCodeScanned(uuid: String) {

        if (lesson == null) {
            appComponent.eventBus.post(RequestSnackbarEvent("Error: lesson id is not found")); return
        }

        appComponent.repo.documents.activateUUID(uuid, lesson?.id!!).subscribe({
            appComponent.eventBus.post(RequestSnackbarEvent("Done ${it.shortUUID}! ID: ${it.id}"))
        }, {
            Log.w("LoginPresenter", "Error while creating document", it)
            appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}"))
        })
    }
}