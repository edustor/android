package ru.wutiarn.edustor.android.dagger.groups

import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import ru.wutiarn.edustor.android.data.repo.PageRepo
import ru.wutiarn.edustor.android.data.repo.TagRepo

class RepoGroup(
        val pages: PageRepo,
        val lessons: LessonsRepo,
        val tag: TagRepo
)