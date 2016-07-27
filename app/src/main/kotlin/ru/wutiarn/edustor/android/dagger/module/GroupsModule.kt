package ru.wutiarn.edustor.android.dagger.module

import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.dagger.groups.ApiGroup
import ru.wutiarn.edustor.android.dagger.groups.RepoGroup
import ru.wutiarn.edustor.android.data.api.LoginApi
import ru.wutiarn.edustor.android.data.api.SyncApi
import ru.wutiarn.edustor.android.data.repo.DocumentRepo
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import ru.wutiarn.edustor.android.data.repo.SubjectsRepo

@Module
class GroupsModule {
    @Provides
    @AppScope
    fun apiGroup(loginApi: LoginApi,
                 syncApi: SyncApi): ApiGroup {
        return ApiGroup(loginApi, syncApi)
    }

    @Provides
    @AppScope
    fun repoGroup(documentsRepo: DocumentRepo,
                  lessonsRepo: LessonsRepo,
                  subjectsRepo: SubjectsRepo): RepoGroup {
        return RepoGroup(documentsRepo, lessonsRepo, subjectsRepo)
    }
}