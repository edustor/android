package ru.wutiarn.edustor.android.util.extension

import io.realm.Realm
import ru.wutiarn.edustor.android.activity.InitSyncActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent

fun AppComponent.assertSynced(): Boolean {
    val realm = Realm.getDefaultInstance()

    if (!activeSession.isLoggedIn) {
        activeSession.logout()
    } else if (realm.isEmpty) {
        application.startActivity(InitSyncActivity::class.java, true)
    } else {
        return true
    }

    return false
}