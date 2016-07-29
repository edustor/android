package ru.wutiarn.edustor.android.events

import android.support.design.widget.Snackbar

/**
 * @author wutiarn <wutiarn@gmail.com>
 * @since
 */
data class RequestSnackbarEvent(val message: String, val length: Int = Snackbar.LENGTH_LONG)

class RealmSyncFinishedEvent()