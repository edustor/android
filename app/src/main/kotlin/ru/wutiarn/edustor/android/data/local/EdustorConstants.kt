package ru.wutiarn.edustor.android.data.local

import android.accounts.Account
import android.content.Context
import ru.wutiarn.edustor.android.R

class EdustorConstants(
        context: Context
) {
    val GOOGLE_BACKEND_CLIENT_ID: String = context.getString(R.string.google_oauth_client_id)
    val syncAccount = Account(context.getString(R.string.sync_account_name),
            context.getString(R.string.account_type))
    val syncContentProviderAuthority: String = context.getString(R.string.sync_content_provider_authority)
    val pdfContentProviderAuthority: String = context.getString(R.string.pdf_sync_content_provider_authority)

    val core_url: String = context.getString(R.string.edustor_core_url)
    val accounts_url: String = context.getString(R.string.edustor_accounts_url)
    val ui_url: String = context.getString(R.string.edustor_ui_url)
    val pdf_url: String = context.getString(R.string.edustor_pdf_url)

}