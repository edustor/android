package ru.wutiarn.edustor.android.util.extension

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import ru.wutiarn.edustor.android.activity.LoginActivity

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