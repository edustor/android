package ru.wutiarn.edustor.android.presenter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.util.Log
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.squareup.otto.Subscribe
import io.realm.Realm
import ru.wutiarn.edustor.android.R
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.Lesson
import ru.wutiarn.edustor.android.data.models.util.sync.PdfSyncStatus
import ru.wutiarn.edustor.android.events.PdfSyncProgressEvent
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent
import ru.wutiarn.edustor.android.util.extension.*
import ru.wutiarn.edustor.android.view.LessonDetailsView
import rx.Subscription

class LessonDetailsPresenter(val appComponent: AppComponent, val context: Context, arguments: Bundle) : MvpPresenter<LessonDetailsView> {

    val TAG: String = LessonDetailsPresenter::class.java.name

    var view: LessonDetailsView? = null
    var activeSubscription: Subscription? = null

    val lessonId: String = arguments.getString("id")

    var lesson: Lesson? = null

    var openPdfAfterSyncFinished = false

    override fun detachView(p0: Boolean) {
        appComponent.eventBus.unregister(this)
        activeSubscription?.unsubscribe()
        view = null
    }

    override fun attachView(p0: LessonDetailsView?) {
        appComponent.eventBus.register(this)
        view = p0
    }

    @Subscribe fun onPdfSyncProgress(event: PdfSyncProgressEvent) {
        if (event.lessonId != lesson?.id) return
        val status: String

        if (event.done) {
            if (openPdfAfterSyncFinished) {
                openPdfAfterSyncFinished = false
                openSyncedPdf()
            }
            status = "Synced"
        } else {
            status = "${event.percent}%"
        }

        view?.setPdfSyncStatus(status)
    }

    fun loadData() {
        activeSubscription?.unsubscribe()

        activeSubscription = appComponent.repo.lessons.byId(lessonId)
                .setUpSyncState(appComponent.pdfSyncManager)
                .linkToLCEView(view, { lesson = it })
    }

    fun onGetPdfClicked() {
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                lesson?.syncStatus!!.setShouldBeSynced(true)  // Update realmValidUntil even if page is already synced
            }
        }
        if (lesson!!.syncStatus!!.getStatus(lesson!!, appComponent.context) == PdfSyncStatus.SyncStatus.SYNCED) {
            openSyncedPdf()
        } else {
            openPdfAfterSyncFinished = true
            appComponent.syncManager.requestSync(manual = true, pdfOnly = true)
            appComponent.eventBus.makeSnack("Pdf sync requested")
        }
    }

    private fun openSyncedPdf() {
        val file = lesson?.getCacheFile(appComponent.context) ?: let {
            Log.i(TAG, "Failed to get cache file for lesson ${lesson?.id}")
            return
        }
        val fileUri = FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), file)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileUri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.startActivity(intent)
    }

    fun onCopyUrlClicked() {
        val uri = lesson?.getPdfUrl(appComponent.constants.pdf_url)
        val clipboardManager = appComponent.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = ClipData.newPlainText(uri, uri)
        appComponent.eventBus.makeSnack("Copied: $uri")
    }

    fun onSyncSwitchChanged(isEnabled: Boolean) {
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                lesson?.syncStatus?.markedForSync = isEnabled
            }
            appComponent.syncManager.requestSync(true, false)
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

    fun onQrCodeScanned(result: String) {

        val (type, id) = EdustorURIParser.parse(result)

        if (type != EdustorURIParser.URIType.PAGE) {
            appComponent.eventBus.post(RequestSnackbarEvent("Error: incorrect QR code payload")); return
        }

        if (lesson == null) {
            appComponent.eventBus.post(RequestSnackbarEvent("Error: lesson uuid is not found")); return
        }

        appComponent.repo.pages.activateQR(id, lesson?.id!!).subscribe({
            appComponent.eventBus.post(RequestSnackbarEvent("Done ${it.shortQR}! ID: ${it.id}"))
        }, {
            Log.w("LessonDetailsPresenter", "Error while creating page", it)
            appComponent.eventBus.post(RequestSnackbarEvent("Error: ${it.message}"))
        })
    }
}