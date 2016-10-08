package ru.wutiarn.edustor.android.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.iid.FirebaseInstanceId
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.activity.InitSyncActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.models.util.sync.SyncTask
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.util.extension.makeToast
import ru.wutiarn.edustor.android.view.LoginView

class LoginPresenter(val appComponent: AppComponent, val activity: AppCompatActivity) : MvpPresenter<LoginView>, GoogleApiClient.OnConnectionFailedListener {
    val TAG = LoginPresenter::class.java.simpleName

    private val RC_SIGN_IN = 0
    val gapi: GoogleApiClient

    var view: LoginView? = null

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(appComponent.constants.GOOGLE_BACKEND_CLIENT_ID)
                .build()

        gapi = GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        appComponent.syncManager.syncEnabled = false
        appComponent.pdfSyncManager.syncEnabled = false
    }

    fun onLoggedIn() {
        val syncTask = SyncTask("account/FCMToken/put", mapOf(
                "token" to FirebaseInstanceId.getInstance().token
        ))
        appComponent.syncManager.addTask(syncTask)
        appComponent.syncManager.syncEnabled = true
        appComponent.pdfSyncManager.syncEnabled = true

        val intent = Intent(activity, InitSyncActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
    }

    fun onLogin() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(gapi)
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun onGoogleSignIn(result: GoogleSignInResult) {
        val googleIdToken: String = result.signInAccount!!.idToken!!
        appComponent.api.accounts.token("password", username = "@google", password = googleIdToken, scope = "offline")
                .configureAsync()
                .subscribe({
                    activity.makeToast("Successfully logged in as ${result.signInAccount!!.displayName}")
                    appComponent.activeSession.setFromOAuthTokenResult(it)
                    onLoggedIn()
                }, {
                    activity.makeToast("Error logging in: ${it.message}")
                    Log.w(TAG, it)
                })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_SIGN_IN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if (result.isSuccess) {
                    onGoogleSignIn(result)
                } else {
                    activity.makeToast("Signing in failed: ${result.status.statusCode}")
                }
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        activity.makeToast("Google connection failed: ${p0.errorMessage}")
    }

    override fun detachView(retainInstance: Boolean) {
        this.view = null
    }

    override fun attachView(view: LoginView?) {
        this.view = view
    }
}