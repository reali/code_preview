package com.lefffo.oxyengynar1.utils

import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DialogManager(private val activity: AppCompatActivity) {

    private var dialog: AlertDialog? = null

    fun showDialog(
        @StringRes title: Int,
        @StringRes body: Int? = null,
        positiveButton: Button,
        neutralButton: Button,
        negativeButton: Button? = null,
    ) {
        dialog?.cancel()
        dialog = AlertDialog.Builder(activity).apply {
            title.let(::setTitle)
            body?.let(::setMessage)

            positiveButton.let {
                setPositiveButton(
                    it.title
                ) { _, _ -> it.clickListener.invoke() }
            }
            neutralButton.let {
                setNeutralButton(
                    it.title
                ) { _, _ -> it.clickListener.invoke() }
            }
            negativeButton?.let {
                setNegativeButton(
                    it.title
                ) { _, _ -> it.clickListener.invoke() }
            }
            setCancelable(false)
        }.create().also { it.show() }
    }

    fun onDispose() {
        dialog?.cancel()
    }

    data class Button(
        @StringRes val title: Int,
        val clickListener: () -> Unit
    )
}