package com.lefffo.oxyengynar1.presentation.home

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.lefffo.oxyengynar1.R
import com.lefffo.oxyengynar1.ThemeHelper
import com.lefffo.oxyengynar1.ViewAnalytics
import com.lefffo.oxyengynar1.databinding.ScreenHomeBinding
import com.lefffo.oxyengynar1.presentation.base.BaseFragment
import com.lefffo.oxyengynar1.presentation.preview.MediaPreviewFragment
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import java.io.File
import android.util.DisplayMetrics





class HomeFragment : BaseFragment(R.layout.screen_home) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HomeAdapter {
//            ViewAnalytics.addView(it.title, activity?.applicationContext!!)
            ViewAnalytics.resetSwipeId()
            navigateTo(MediaPreviewFragment.newInstance(it.id))
        }
        with(ScreenHomeBinding.bind(view)) {

            if (ThemeHelper.isExist) {

                val mainTextColor = Color.parseColor(ThemeHelper.myTheme?.mainTextColor) //ffffff
                val mainColor = Color.parseColor(ThemeHelper.myTheme?.mainColor) //142234
                val backgroundColor = Color.parseColor(ThemeHelper.myTheme?.backgroundColor) //000000

                //accent FF8F08

                rootLayout.setBackgroundColor(backgroundColor)
                headerLayout.findViewById<View>(R.id.header).setBackgroundColor(mainColor)
                //Tap on any item below to view.
                headerLayout.findViewById<AppCompatTextView>(R.id.btnTabToView).text = ThemeHelper.myTheme?.headRightText
                headerLayout.findViewById<AppCompatTextView>(R.id.btnTabToView).setTextColor(mainTextColor)

                val leftHead = headerLayout.findViewById<View>(R.id.headerLayout)

                //Fox Animated Engineering
                leftHead.findViewById<AppCompatTextView>(R.id.tvTitle).text = ThemeHelper.myTheme?.headLeftTopText
                ThemeHelper.myTheme?.headLeftTopTextSize?.let {
                    leftHead.findViewById<AppCompatTextView>(R.id.tvTitle).setTextSize(TypedValue.COMPLEX_UNIT_SP, it)
                }

                //Science and Engineering Graphic Studio
                leftHead.findViewById<AppCompatTextView>(R.id.tvSubtitle).text = ThemeHelper.myTheme?.headLeftBottomText

                leftHead.findViewById<AppCompatTextView>(R.id.tvTitle).setTextColor(mainTextColor)
                leftHead.findViewById<AppCompatTextView>(R.id.tvSubtitle).setTextColor(mainTextColor)


                val footer = footerLayout.findViewById<View>(R.id.rootLayout)

                footer.setBackgroundColor(mainColor)

                //(573) 279–2500
                footer.findViewById<AppCompatTextView>(R.id.leftText).text = ThemeHelper.myTheme?.footerLeftText
                //info@fox-ae.com
                footer.findViewById<AppCompatTextView>(R.id.middleText).text = ThemeHelper.myTheme?.footerMiddleText
                //www.fox-ae.com
                footer.findViewById<AppCompatTextView>(R.id.rightText).text = ThemeHelper.myTheme?.footerRightText

                footer.findViewById<AppCompatTextView>(R.id.leftText).setTextColor(mainTextColor)
                footer.findViewById<AppCompatTextView>(R.id.middleText).setTextColor(mainTextColor)
                footer.findViewById<AppCompatTextView>(R.id.rightText).setTextColor(mainTextColor)


                val path = context?.getExternalFilesDir("")

                if (!ThemeHelper.myTheme!!.new_logo.isEmpty()) {
                    val file = File("$path/${ThemeHelper.myTheme!!.new_logo}")

                    if (file.exists()) {
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        leftHead.findViewById<AppCompatImageView>(R.id.ivLogo).setImageBitmap(bmp)
                    }
                }

                if (!ThemeHelper.myTheme!!.gear_logo.isEmpty()) {

                    val file = File("$path/${ThemeHelper.myTheme!!.gear_logo}")

                    if (file.exists()) {
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        btnExitKiosk.setImageBitmap(bmp)
                    }

                }

                if (!ThemeHelper.myTheme!!.f_left_logo.isEmpty()) {

                    val file = File("$path/${ThemeHelper.myTheme!!.f_left_logo}")

                    if (file.exists()) {
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        footer.findViewById<AppCompatImageView>(R.id.leftIcon).setImageBitmap(bmp)
                    }

                }

                if (!ThemeHelper.myTheme!!.f_mid_logo.isEmpty()) {

                    val file = File("$path/${ThemeHelper.myTheme!!.f_mid_logo}")

                    if (file.exists()) {
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        footer.findViewById<AppCompatImageView>(R.id.midIcon).setImageBitmap(bmp)
                    }

                }

                if (!ThemeHelper.myTheme!!.f_right_logo.isEmpty()) {

                    val file = File("$path/${ThemeHelper.myTheme!!.f_right_logo}")

                    if (file.exists()) {
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        footer.findViewById<AppCompatImageView>(R.id.rightIcon).setImageBitmap(bmp)
                    }

                }

                if (!ThemeHelper.myTheme!!.gear_color.isEmpty()) {

                    val gear_color = Color.parseColor(ThemeHelper.myTheme?.gear_color)

                    val unwrappedDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_settings)
                    val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
                    DrawableCompat.setTint(wrappedDrawable, gear_color)

                    btnExitKiosk.setImageDrawable(wrappedDrawable)

                }

                if (!ThemeHelper.myTheme!!.f_left_color.isEmpty()) {

                    val f_left_color = Color.parseColor(ThemeHelper.myTheme?.f_left_color)

                    val unwrappedDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_call)
                    val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
                    DrawableCompat.setTint(wrappedDrawable, f_left_color)

                    footer.findViewById<AppCompatImageView>(R.id.leftIcon).setImageDrawable(wrappedDrawable)
                }

                if (!ThemeHelper.myTheme!!.f_mid_color.isEmpty()) {

                    val f_mid_color = Color.parseColor(ThemeHelper.myTheme?.f_mid_color)

                    val unwrappedDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_mail)
                    val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
                    DrawableCompat.setTint(wrappedDrawable, f_mid_color)

                    footer.findViewById<AppCompatImageView>(R.id.midIcon).setImageDrawable(wrappedDrawable)

                }

                if (!ThemeHelper.myTheme!!.f_right_color.isEmpty()) {

                    val f_right_color = Color.parseColor(ThemeHelper.myTheme?.f_right_color)

                    val unwrappedDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_site)
                    val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
                    DrawableCompat.setTint(wrappedDrawable, f_right_color)

                    footer.findViewById<AppCompatImageView>(R.id.rightIcon).setImageDrawable(wrappedDrawable)

                }

                if (!ThemeHelper.myTheme!!.arrow_color.isEmpty()) {

                }

                if (ThemeHelper.myTheme!!.f_left_hide) {
                    footer.findViewById<View>(R.id.leftIcon).visibility = View.GONE
                }

                if (ThemeHelper.myTheme!!.f_mid_hide) {
                    footer.findViewById<View>(R.id.midIcon).visibility = View.GONE
                }

                if (ThemeHelper.myTheme!!.f_right_hide) {
                    footer.findViewById<View>(R.id.rightIcon).visibility = View.GONE
                }


            }

//            headerLayout.setBackgroundColor(Color.parseColor("#f56fff"))
//            btnTabToView

            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                HomeItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.homeItemSpacing)
                )
            )
            recyclerView.enableFastScrolling()

            btnExitKiosk.setOnClickListener { exitKioskMode() }
        }

        viewModel.media.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    private fun RecyclerView.enableFastScrolling() {

        var drawable : Drawable? = null

        if (ThemeHelper.isExist) {

            val scrollColor = Color.parseColor(ThemeHelper.myTheme?.scrollColor) //000000

            val unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.scroll_thumb)
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
            DrawableCompat.setTint(wrappedDrawable, scrollColor)

            drawable = wrappedDrawable

        } else {
            drawable = ContextCompat.getDrawable(context, R.drawable.scroll_thumb)
        }

        FastScrollerBuilder(this)
            .setThumbDrawable(drawable!!)
            .setTrackDrawable(ContextCompat.getDrawable(context, R.drawable.scroll_line)!!.apply {
                setBounds(0, 0, intrinsicWidth, height)
            })
            .setPadding(0, 0, 0, 0)
            .apply {
                disableScrollbarAutoHide()
            }
            .build()


    }
}