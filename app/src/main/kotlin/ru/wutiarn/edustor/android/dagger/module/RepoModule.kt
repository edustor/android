package ru.wutiarn.edustor.android.dagger.module

import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.data.local.PdfSyncManager
import ru.wutiarn.edustor.android.data.local.SyncManager
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import ru.wutiarn.edustor.android.data.repo.PageRepo
import ru.wutiarn.edustor.android.data.repo.TagRepo
import ru.wutiarn.edustor.android.data.repo.realm.RealmLessonRepo
import ru.wutiarn.edustor.android.data.repo.realm.RealmPageRepo
import ru.wutiarn.edustor.android.data.repo.realm.RealmTagRepo

@Module
class RepoModule {
    @Provides
    @AppScope
    fun tagRepo(): TagRepo {
        return RealmTagRepo()
    }

    @Provides
    @AppScope
    fun lessonsRepo(syncTasksManager: SyncManager, pdfSyncManager: PdfSyncManager): LessonsRepo {
        return RealmLessonRepo(syncTasksManager, pdfSyncManager)
    }

    @Provides
    @AppScope
    fun pageRepo(lessonRepo: LessonsRepo, syncTasksManager: SyncManager): PageRepo {
        return RealmPageRepo(lessonRepo, syncTasksManager)
    }

}
