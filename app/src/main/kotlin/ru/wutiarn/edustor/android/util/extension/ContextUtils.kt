package ru.wutiarn.edustor.android.util.extension

import android.content.Context
import android.widget.Toast

fun Context.makeToast(str: String) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}