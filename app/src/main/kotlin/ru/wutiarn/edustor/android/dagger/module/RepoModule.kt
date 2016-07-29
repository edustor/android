package ru.wutiarn.edustor.android.dagger.module

import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.local.SyncManager
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
    fun lessonsRepo(syncTasksManager: SyncManager): LessonsRepo {
        return RealmLessonRepo(syncTasksManager)
    }

    @Provides
    @AppScope
    fun documentsRepo(lessonRepo: LessonsRepo, syncTasksManager: SyncManager): DocumentRepo {
        return RealmDocumentRepo(lessonRepo, syncTasksManager)
    }

}