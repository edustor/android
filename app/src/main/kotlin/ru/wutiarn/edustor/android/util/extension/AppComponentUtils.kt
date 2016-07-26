package ru.wutiarn.edustor.android.util.extension

import android.app.Activity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import io.realm.Realm
import ru.wutiarn.edustor.android.activity.InitSyncActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent

fun AppComponent.assertActivityCanStart(activity: Activity): Boolean {
    val realm = Realm.getDefaultInstance()

    if (!activeSession.isLoggedIn) {
        activeSession.logout()
    } else if (realm.isEmpty) {
        application.startActivity(InitSyncActivity::class.java, true)
    } else {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val servicesAvailableResult = googleApiAvailability.isGooglePlayServicesAvailable(this.application.applicationContext)
        if (servicesAvailableResult != ConnectionResult.SUCCESS) {
            val errorDialog = googleApiAvailability.getErrorDialog(activity, servicesAvailableResult, 0)
            errorDialog?.show()
        } else {
            return true
        }
    }

    return false
}