package ru.wutiarn.edustor.android.util.extension

object EdustorURIParser {
    enum class URIType(val type: String) {
        PAGE("d")
    }

    data class EdustorURI(val type: URIType, val id: String)

    private val UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}".toRegex()

    fun parse(string: String): EdustorURI {
        // Regex is simplified to be compatible with qr format v3 and older
        val qrUuid = UUID_REGEX.find(string)?.value ?: throw IllegalAccessException("Unknown qr code")
        return EdustorURI(URIType.PAGE, qrUuid)
    }
}