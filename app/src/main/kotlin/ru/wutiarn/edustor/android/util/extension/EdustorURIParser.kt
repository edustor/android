package ru.wutiarn.edustor.android.util.extension

object EdustorURIParser {
    enum class URIType(val type: String) {
        DOCUMENT("d")
    }

    data class EdustorURI(val type: URIType, val id: String)

    private val regex = "edustor://(.+?)/(.+)".toRegex()

    fun parse(string: String): EdustorURI {

        val matchResult = regex.matchEntire(string) ?: return EdustorURI(URIType.DOCUMENT, string)

        val groups = matchResult.groupValues

        val type = URIType.values().firstOrNull { it.type == groups[1] } ?: throw IllegalAccessException("Unsupported type ${groups[1]}")
        return EdustorURI(type, groups[2])
    }
}