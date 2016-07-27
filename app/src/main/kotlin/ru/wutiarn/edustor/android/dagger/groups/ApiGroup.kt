package ru.wutiarn.edustor.android.dagger.groups

import ru.wutiarn.edustor.android.data.api.LoginApi
import ru.wutiarn.edustor.android.data.api.SyncApi

class ApiGroup(
        val login: LoginApi,
        val sync: SyncApi
)