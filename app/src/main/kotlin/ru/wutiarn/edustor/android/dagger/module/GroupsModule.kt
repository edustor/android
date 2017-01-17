package ru.wutiarn.edustor.android.dagger.module

import dagger.Module
import dagger.Provides
import ru.wutiarn.edustor.android.dagger.annotation.AppScope
import ru.wutiarn.edustor.android.dagger.groups.ApiGroup
import ru.wutiarn.edustor.android.dagger.groups.RepoGroup
import ru.wutiarn.edustor.android.data.api.AccountsApi
import ru.wutiarn.edustor.android.data.api.SyncApi
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import ru.wutiarn.edustor.android.data.repo.PageRepo
import ru.wutiarn.edustor.android.data.repo.TagRepo

@Module
class GroupsModule {
    @Provides
    @AppScope
    fun apiGroup(accountsApi: AccountsApi,
                 syncApi: SyncApi): ApiGroup {
        return ApiGroup(accountsApi, syncApi)
    }

    @Provides
    @AppScope
    fun repoGroup(pageRepo: PageRepo,
                  lessonsRepo: LessonsRepo,
                  tagRepo: TagRepo): RepoGroup {
        return RepoGroup(pageRepo, lessonsRepo, tagRepo)
    }
}