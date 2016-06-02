package ru.wutiarn.edustor.android.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_login.*
import ru.wutiarn.edustor.android.EdustorApplication
import ru.wutiarn.edustor.android.R

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private lateinit var gapi: GoogleApiClient
    private val RC_SIGN_IN = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val appComponent = (application as EdustorApplication).appComponent

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(appComponent.constants.GOOGLE_BACKEND_CLIENT_ID)
                .build()

        gapi = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        sign_in_button.setScopes(gso.scopeArray)
        sign_in_button.setOnClickListener { onLogin() }
    }

    override fun onConnectionFailed(p0: ConnectionResult?) {
        throw UnsupportedOperationException()
    }

    fun onLogin() {
        Toast.makeText(this, "Logging in", Toast.LENGTH_SHORT).show()

        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(gapi)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if (result.isSuccess) {
                    onLoggedIn(result)
                }
            }
        }
    }

    fun onLoggedIn(result: GoogleSignInResult) {
        Toast.makeText(this, "Welcome, ${result.signInAccount.displayName}", Toast.LENGTH_SHORT).show()
    }
}