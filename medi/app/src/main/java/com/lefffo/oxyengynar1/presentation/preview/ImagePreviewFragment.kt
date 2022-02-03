package com.lefffo.oxyengynar1.presentation.preview

import android.view.View
import coil.load
import com.lefffo.oxyengynar1.R
import com.lefffo.oxyengynar1.ViewAnalytics
import com.lefffo.oxyengynar1.databinding.ScreenImagePreviewBinding
import com.lefffo.oxyengynar1.media.Media.Image
import com.lefffo.oxyengynar1.media.MediaMeta
import com.ortiz.touchview.OnTouchImageViewListener

class ImagePreviewFragment : BaseMediaPreviewFragment<Image>(R.layout.screen_image_preview) {

    override fun onBind(view: View, media: Image) = with(ScreenImagePreviewBinding.bind(view)) {
        imageView.load(media.contentUri)

        if (!ViewAnalytics.isZoomed(media.title)) {
            imageView.setOnTouchImageViewListener(object : OnTouchImageViewListener {
                override fun onMove() {
                    if (imageView.isZoomed) {
                        ViewAnalytics.zoomedIn(media.title, activity?.applicationContext!!)
                    }

                }
            })
        }

        imageView.onClicked(
            onSingleClick = { toggleControls() },
            onDoubleClick = { changeControlsVisibility(show = false) }
        )
    }

    companion object {
        fun newInstance(meta: MediaMeta) = ImagePreviewFragment().apply {
            arguments = args(meta)
        }
    }
}