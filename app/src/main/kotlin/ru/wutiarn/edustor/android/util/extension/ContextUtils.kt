package ru.wutiarn.edustor.android.util.extension

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.squareup.otto.Bus
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.dagger.component.DaggerAppComponent
import ru.wutiarn.edustor.android.dagger.module.LocalStorageModule
import ru.wutiarn.edustor.android.events.RequestSnackbarEvent

fun Bus.makeSnack(str: String, length: Int = Snackbar.LENGTH_SHORT) {
    this.post(RequestSnackbarEvent(str, length))
}

fun Context.makeToast(str: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, str, duration).show()
}

fun Context.startActivity(activityClass: Class<out AppCompatActivity>, clearStack: Boolean = false) {
    val intent = Intent(this, activityClass)
    if (clearStack) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    this.startActivity(intent)
}

fun Context.initializeNewAppComponent(): AppComponent {
    return DaggerAppComponent.builder()
            .localStorageModule(LocalStorageModule(this))
            .build()
}