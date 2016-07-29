package ru.wutiarn.edustor.android.data.repo

import org.threeten.bp.Instant
import retrofit2.http.*
import ru.wutiarn.edustor.android.data.models.Document
import rx.Observable

interface DocumentRepo {
    @POST("documents/uuid/activate")
    @FormUrlEncoded
    fun activateUUID(@Field("uuid") uuid: String,
                     @Field("lesson") lessonId: String,
                     @Field("instant") instant: Instant = Instant.now()
    ): Observable<Document>

    @DELETE("documents/{document}")
    fun delete(@Path("document") documentId: String): Observable<Unit>
}