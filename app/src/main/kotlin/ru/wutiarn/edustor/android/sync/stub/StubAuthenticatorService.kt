package ru.wutiarn.edustor.android.sync.stub

import android.app.Service
import android.content.Intent
import android.os.IBinder

class StubAuthenticatorService : Service() {
    private lateinit var authenticator: SyncAuthenticator

    override fun onCreate() {
        authenticator = SyncAuthenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return authenticator.iBinder
    }

}