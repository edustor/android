package ru.wutiarn.edustor.android.dagger.module

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.repo.DocumentRepo
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import ru.wutiarn.edustor.android.data.repo.SubjectsRepo
import ru.wutiarn.edustor.android.data.repo.realm.RealmDocumentRepo
import ru.wutiarn.edustor.android.data.repo.realm.RealmLessonRepo
import ru.wutiarn.edustor.android.data.repo.realm.RealmSubjectRepo

@Module
class RepoModule {
    @Provides
    @AppScope
    fun subjectsRepo(): SubjectsRepo {
        return RealmSubjectRepo()
    }

    @Provides
    @AppScope
    fun lessonsRepo(): LessonsRepo {
        return RealmLessonRepo()
    }

    @Provides
    @AppScope
    fun documentsRepo(): DocumentRepo {
        return RealmDocumentRepo()
    }

}
