package ru.wutiarn.edustor.android.events

import android.support.design.widget.Snackbar

/**
 * @author wutiarn <wutiarn@gmail.com>
 * @since
 */
data class RequestSnackbarEvent(val message: String, val length: Int = Snackbar.LENGTH_LONG)

class RealmSyncFinishedEvent()

data class PdfSyncProgressEvent(val lessonId: String, val percent: Int, val done: Boolean)