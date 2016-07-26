package ru.wutiarn.edustor.android.data.repo

import org.threeten.bp.Instant
import retrofit2.http.*
import ru.wutiarn.edustor.android.data.models.Document
import rx.Completable
import rx.Observable
import rx.Single
import java.util.*

interface DocumentRepo {
    @POST("documents/uuid/activate")
    @FormUrlEncoded
    fun activateUUID(@Field("uuid") uuid: String,
                     @Field("lesson") lessonId: String,
                     @Field("instant") instant: Instant = Instant.now()
    ): Single<Document>

    @DELETE("documents/{document}")
    fun delete(@Path("document") documentId: String): Completable
}