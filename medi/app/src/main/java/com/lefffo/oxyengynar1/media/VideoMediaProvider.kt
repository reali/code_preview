package com.lefffo.oxyengynar1.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.lefffo.oxyengynar1.media.Media.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class VideoMediaProvider(private val context: Context) {

    suspend fun getVideos(): List<Video> = withContext(Dispatchers.IO) {
        val externalCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val internalCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_INTERNAL
            )
        } else {
            MediaStore.Video.Media.INTERNAL_CONTENT_URI
        }

        (getVideos(externalCollection) + getVideos(internalCollection))
    }

    private suspend fun getVideos(
        collection: Uri
    ): List<Video> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Video>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_MODIFIED,
        )

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
            val dateColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                try {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val duration = cursor.getInt(durationColumn)
                    val date = cursor.getLong(dateColumn) * 1_000
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                    )

                    list += Video(
                        id = id,
                        title = name.removeFileExtension(),
                        contentUri = contentUri,
                        thumbnail = Thumbnail.Video(contentUri),
                        duration = duration,
                        date = Date(date)
                    )
                } catch (error: Throwable) {
                    Timber.e(error, "Failed to parse video")
                }
            }
        }

        list
    }
}