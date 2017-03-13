package ru.wutiarn.edustor.android.data.repo.realm

import io.realm.Realm
import ru.wutiarn.edustor.android.data.models.Tag
import ru.wutiarn.edustor.android.data.repo.TagRepo
import ru.wutiarn.edustor.android.util.extension.copyFromRealm
import rx.Observable

class RealmTagRepo : TagRepo {

    override val all: List<Tag>
        get() {
            return Realm.getDefaultInstance().where(Tag::class.java)
                    .findAll()
                    .map { it.copyFromRealm<Tag>() }
        }

    override fun byParentTagId(parentTagId: String?): List<Tag> {
        return Realm.getDefaultInstance().where(Tag::class.java)
                .equalTo("parent.id", parentTagId)
                .findAll()
                .map { it.copyFromRealm<Tag>() }
    }

    override fun byId(id: String): Tag {
        return Realm.getDefaultInstance().where(Tag::class.java)
                .equalTo("id", id)
                .findFirst()
                .copyFromRealm()
    }
}