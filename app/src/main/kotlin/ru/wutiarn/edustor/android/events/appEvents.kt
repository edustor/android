package ru.wutiarn.edustor.android.events

import android.support.design.widget.Snackbar
import ru.wutiarn.edustor.android.sync.SyncException

/**
 * @author wutiarn <wutiarn@gmail.com>
 * @since
 */
data class RequestSnackbarEvent(val message: String, val length: Int = Snackbar.LENGTH_LONG)

data class EdustorMetaSyncFinished(val success: Boolean, val exception: SyncException? = null)

data class PdfSyncProgressEvent(val lessonId: String, val percent: Int, val done: Boolean)