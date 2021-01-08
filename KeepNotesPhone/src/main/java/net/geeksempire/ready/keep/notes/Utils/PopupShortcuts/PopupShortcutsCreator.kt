/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/7/21 10:43 AM
 * Last modified 1/7/21 10:42 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.PopupShortcuts

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.R

object PopupShortcutsItems {
    const val ShortcutId = "ShortcutId"
    const val ShortcutLink = "ShortcutLink"
    const val ShortcutLabel = "ShortcutLabel"
    const val ShortcutDescription = "ShortcutDescription"
}

object PopupShortcutsActions {
    const val TakeNote = "POPUP_SHORTCUTS_TAKE_NOTE"
    const val QuickTakeNote = "POPUP_SHORTCUTS_QUICK_TAKE_NOTE"
    const val Browser = "POPUP_SHORTCUTS_BROWSER"
}

/**
 * POPUP_SHORTCUTS_TAKE_NOTE & POPUP_SHORTCUTS_QUICK_TAKE_NOTE & POPUP_SHORTCUTS_BROWSER
 **/
class PopupShortcutsCreator (private val context: AppCompatActivity) {

    val shortcutManager: ShortcutManager = context.getSystemService(ShortcutManager::class.java) as ShortcutManager

    fun configure() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {

        shortcutManager.removeAllDynamicShortcuts()

        addShortcutKeyboardTyping()

        addShortcutRateShare()
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun addShortcutKeyboardTyping() {

        val shortcutsHomeLauncherCategories: HashSet<String> = HashSet<String>()
        shortcutsHomeLauncherCategories.addAll(arrayOf("Note", "Keyboard", "Type", "Handwriting", "Productivity", "Voice", "Record"))

        val intent = Intent(context, TakeNote::class.java).apply {
            action = PopupShortcutsActions.TakeNote
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.putExtra(PopupShortcutsItems.ShortcutId, TakeNote::class.java.simpleName)
        intent.putExtra(PopupShortcutsItems.ShortcutLink, context.getString(R.string.playStoreLink))
        intent.putExtra(PopupShortcutsItems.ShortcutLabel, context.getString(R.string.handwritingText))
        intent.putExtra(PopupShortcutsItems.ShortcutDescription, context.getString(R.string.handwritingText))
        intent.putExtra(TakeNote.NoteConfigurations.KeyboardTyping, TakeNote.NoteConfigurations.KeyboardTyping)

        val iconDrawable = context.getDrawable(R.drawable.icon_keyboard)?.mutate()
        iconDrawable?.setTint(context.getColor(R.color.default_color_game_light))

        Glide.with(context)
            .asBitmap()
            .load(iconDrawable)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {

                    return true
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    println(">>>>>>>> 0")

                    resource?.let {
                        println(">>>>>>>> 1")

                        shortcutManager.addDynamicShortcuts(arrayListOf(
                            ShortcutInfo.Builder(context, "${shortcutManager.dynamicShortcuts.size}")
                                .setShortLabel(context.getString(R.string.handwritingText))
                                .setLongLabel(context.getString(R.string.handwritingText))
                                .setIcon(Icon.createWithBitmap(resource))
                                .setIntent(intent)
                                .setCategories(shortcutsHomeLauncherCategories)
                                .setRank(shortcutManager.dynamicShortcuts.size)
                                .build()))

                    }

                    return true
                }

            })
            .submit()

    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun addShortcutRateShare() {

        val shortcutsHomeLauncherCategories: HashSet<String> = HashSet<String>()
        shortcutsHomeLauncherCategories.addAll(arrayOf("Note", "Keyboard", "Type", "Handwriting", "Productivity", "Voice", "Record"))

        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(context.getString(R.string.playStoreLink))
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.putExtra(PopupShortcutsItems.ShortcutId, TakeNote::class.java.simpleName)
        intent.putExtra(PopupShortcutsItems.ShortcutLink, context.getString(R.string.playStoreLink))
        intent.putExtra(PopupShortcutsItems.ShortcutLabel, context.getString(R.string.handwritingText))
        intent.putExtra(PopupShortcutsItems.ShortcutDescription, context.getString(R.string.handwritingText))

        val iconDrawable = context.getDrawable(R.drawable.rate_icon)?.mutate()

        Glide.with(context)
            .asBitmap()
            .load(iconDrawable)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {

                    return true
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                    resource?.let {

                        shortcutManager.addDynamicShortcuts(arrayListOf(
                            ShortcutInfo.Builder(context, "${shortcutManager.dynamicShortcuts.size}")
                                .setShortLabel(context.getString(R.string.rateShare))
                                .setLongLabel(context.getString(R.string.rateShare))
                                .setIcon(Icon.createWithBitmap(resource))
                                .setIntent(intent)
                                .setCategories(shortcutsHomeLauncherCategories)
                                .setRank(shortcutManager.dynamicShortcuts.size)
                                .build()))

                    }

                    return true
                }

            })
            .submit()

    }

}