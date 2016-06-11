package ru.wutiarn.edustor.android.presenter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import ru.wutiarn.edustor.android.activity.SubjectsListActivity
import ru.wutiarn.edustor.android.dagger.component.AppComponent
import ru.wutiarn.edustor.android.data.api.LoginView
import ru.wutiarn.edustor.android.util.extension.configureAsync
import ru.wutiarn.edustor.android.util.extension.makeToast

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
    }

    fun onLoggedIn() {
        val intent = Intent(activity, SubjectsListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
    }

    fun onLogin() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(gapi)
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun onGoogleSignIn(result: GoogleSignInResult) {
        appComponent.loginApi.login(result.signInAccount.idToken)
                .configureAsync()
                .subscribe ({
                    activity.makeToast("Successfully logged in as ${result.signInAccount.displayName}")
                    appComponent.preferences.token = it.token
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
                    activity.makeToast("Signing in failed")
                }
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult?) {
        throw UnsupportedOperationException()
    }

    override fun detachView(retainInstance: Boolean) {
        this.view = null
    }

    override fun attachView(view: LoginView?) {
        this.view = view
    }
}