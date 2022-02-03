package com.lefffo.oxyengynar1.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.fetch.VideoFrameUriFetcher
import coil.load
import coil.request.videoFrameMillis
import com.lefffo.oxyengynar1.databinding.ItemImageSimpleBinding
import com.lefffo.oxyengynar1.databinding.ItemVideoSimpleBinding
import com.lefffo.oxyengynar1.media.Media
import com.lefffo.oxyengynar1.media.Thumbnail

private const val VIEW_TYPE_IMAGE = 1
private const val VIEW_TYPE_VIDEO = 2

class HomeAdapter(
    private val onMediaClicked: (media: Media) -> Unit
) : ListAdapter<Media, HomeAdapter.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Media.Image -> VIEW_TYPE_IMAGE
            is Media.Video -> VIEW_TYPE_VIDEO
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_IMAGE -> ImageViewHolder(ItemImageSimpleBinding.inflate(inflater))
            VIEW_TYPE_VIDEO -> VideoViewHolder(ItemVideoSimpleBinding.inflate(inflater))
            else -> throw IllegalArgumentException("Unsupported viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val media = getItem(position)) {
            is Media.Image -> (holder as ImageViewHolder).bind(media)
            is Media.Video -> (holder as VideoViewHolder).bind(media)
        }
        holder.itemView.setOnClickListener {
            onMediaClicked(getItem(position))
        }
    }

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ImageViewHolder(private val binding: ItemImageSimpleBinding) : ViewHolder(binding.root) {

        fun bind(image: Media.Image) = with(binding) {
            ivThumb.load(image.thumbnail.uri)
        }
    }

    class VideoViewHolder(private val binding: ItemVideoSimpleBinding) : ViewHolder(binding.root) {

        fun bind(video: Media.Video) = with(binding) {
            ivThumb.load(video.thumbnail.uri) {
                if (video.thumbnail is Thumbnail.Video) {
                    videoFrameMillis(500)
                    fetcher(VideoFrameUriFetcher(binding.root.context))
                }
            }
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean =
            oldItem == newItem
    }
}