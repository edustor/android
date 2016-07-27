package ru.wutiarn.edustor.android.util.extension

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.dagger.component.DaggerAppComponent
import ru.wutiarn.edustor.android.dagger.module.LocalStorageModule

fun Context.makeToast(str: String) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}

fun Context.startActivity(activityClass: Class<out AppCompatActivity>, clearStack: Boolean = false) {
    val intent = Intent(this, activityClass)
    if (clearStack == true) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    this.startActivity(intent)
}

fun Context.initializeNewAppComponent(): AppComponent {
    return DaggerAppComponent.builder()
            .localStorageModule(LocalStorageModule(this))
            .build()
}