package ru.wutiarn.edustor.android

import android.accounts.AccountManager
import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.threetenabp.AndroidThreeTen
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.util.extension.initializeNewAppComponent

class EdustorApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        val realmConfig = RealmConfiguration.Builder(applicationContext)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfig)

        appComponent = initializeNewAppComponent()

//        Realm.getDefaultInstance().executeTransaction { it.deleteAll() }

        val accountManager = getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        accountManager.addAccountExplicitly(appComponent.constants.syncAccount, null, null)

        Log.i("EdustorApplication", "Edustor token: ${appComponent.preferences.token}")
        Log.i("EdustorApplication", "Firebase token: ${FirebaseInstanceId.getInstance().token}")
    }
}
