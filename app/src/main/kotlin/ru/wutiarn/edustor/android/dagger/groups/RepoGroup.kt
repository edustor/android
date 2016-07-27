package ru.wutiarn.edustor.android.dagger.groups

import ru.wutiarn.edustor.android.data.repo.DocumentRepo
import ru.wutiarn.edustor.android.data.repo.LessonsRepo
import ru.wutiarn.edustor.android.data.repo.SubjectsRepo

class RepoGroup(
        val documents: DocumentRepo,
        val lessons: LessonsRepo,
        val subjects: SubjectsRepo
)