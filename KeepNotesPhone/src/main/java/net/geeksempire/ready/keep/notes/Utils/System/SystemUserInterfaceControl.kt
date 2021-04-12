/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.abanabsalan.aban.magazine.Utils.System

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager

fun showKeyboard(context: Context, toggleView: View) {

    val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(toggleView, InputMethodManager.SHOW_IMPLICIT)

}

fun hideKeyboard(context: Context, toggleView: View) {

    val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(toggleView.windowToken, 0)

}

fun doVibrate(context: Context, millisecondVibrate: Long) {

    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        vibrator?.vibrate(
            VibrationEffect.createOneShot(
                millisecondVibrate,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )

    } else {
        vibrator?.vibrate(millisecondVibrate)
    }
}