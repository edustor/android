package ru.wutiarn.edustor.android.util.extension

import android.support.design.widget.Snackbar
import android.view.View
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent

fun RequestSnackbarEvent.show(view: View?) {
    view?.let {
        Snackbar.make(view, this.message, this.length).show()
    }
}