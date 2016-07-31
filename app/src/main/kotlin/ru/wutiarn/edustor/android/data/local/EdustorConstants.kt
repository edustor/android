package ru.wutiarn.edustor.android.data.local

import android.accounts.Account
import android.content.Context
import ru.wutiarn.edustor.android.R

class EdustorConstants(
        context: Context
) {
    val GOOGLE_BACKEND_CLIENT_ID: String = context.getString(R.string.google_oauth_client_id)
    var URL: String = context.getString(R.string.edustor_url)
    val syncAccount = Account(context.getString(R.string.sync_account_name),
            context.getString(R.string.account_type))
    val syncContentProviderAuthority: String = context.getString(R.string.sync_content_provider_authority)
    val pdfContentProviderAuthority: String = context.getString(R.string.pdf_sync_content_provider_authority)
}