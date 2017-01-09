package ru.wutiarn.edustor.android.data.models.util.sync

import io.realm.RealmObject
import io.realm.annotations.RealmClass

/**
 * Yes, this is exactly what you're thinking about. A WHOLE FUCKING CLASS with ONE STRING FIELD
 * But I have nothing else to do since Realm CANNOT store lists of non-RealmObject types (and yes, String is definitely
 * is _NOT_ RealmObject. Bullshit.
 *
 * @see <a href="https://github.com/realm/realm-java/issues/575">GitHub Issue</a>
 */
@RealmClass
open class PageMD5(
        var md5: String? = null
) : RealmObject()