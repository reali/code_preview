package com.lefffo.oxyengynar1.media

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

typealias MediaId = Long

sealed class Media(
    open val id: MediaId,
    open val title: String,
    open val contentUri: Uri,
    open val thumbnail: Thumbnail,
    open val date: Date
) {

    data class Image(
        override val id: MediaId,
        override val title: String,
        override val contentUri: Uri,
        override val thumbnail: Thumbnail,
        override val date: Date
    ) : Media(id, title, contentUri, thumbnail, date)

    data class Video(
        override val id: MediaId,
        override val title: String,
        override val contentUri: Uri,
        override val thumbnail: Thumbnail,
        val duration: Int,
        override val date: Date
    ) : Media(id, title, contentUri, thumbnail, date)

    val meta: MediaMeta
        get() = MediaMeta(id, isVideo = this is Video)
}

@Parcelize
data class MediaMeta(
    val id: MediaId,
    val isVideo: Boolean
) : Parcelable

sealed class Thumbnail(
    open val uri: Uri,
) {

    data class Image(
        override val uri: Uri
    ) : Thumbnail(uri)

    data class Video(
        override val uri: Uri
    ) : Thumbnail(uri)
}