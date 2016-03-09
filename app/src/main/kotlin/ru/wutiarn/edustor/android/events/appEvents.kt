package ru.wutiarn.edustor.android.events

import android.support.design.widget.Snackbar

/**
 * Created by wutiarn on 09.03.16.
 */
data class RequestSnackbarEvent(val message: String, val length: Int = Snackbar.LENGTH_LONG)