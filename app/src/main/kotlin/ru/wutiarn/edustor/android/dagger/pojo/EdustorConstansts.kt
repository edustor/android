package ru.wutiarn.edustor.android.dagger.pojo

import android.accounts.Account
import android.content.Context
import ru.wutiarn.edustor.android.R

class EdustorConstants(
        context: Context
) {
    val GOOGLE_BACKEND_CLIENT_ID = "99685742253-41uieqd0vl3e03l62c7t3impd38gdt4q.apps.googleusercontent.com"
    var URL: String = context.getString(R.string.EDUSTOR_URL)
    val syncAccount = Account("Edustor sync", "edustor.ru")
}