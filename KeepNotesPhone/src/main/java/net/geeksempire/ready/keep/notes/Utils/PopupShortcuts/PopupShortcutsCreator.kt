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

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.SearchProcess

object PopupShortcutsItems {
    const val ShortcutId = "ShortcutId"
    const val ShortcutLink = "ShortcutLink"
    const val ShortcutLabel = "ShortcutLabel"
    const val ShortcutDescription = "ShortcutDescription"
}

object PopupShortcutsActions {
    const val TakeNote = "POPUP_SHORTCUTS_TAKE_NOTE"
    const val QuickTakeNote = "POPUP_SHORTCUTS_QUICK_TAKE_NOTE"
    const val SearchProcess = "POPUP_SHORTCUTS_SEARCH_PROCESS"
    const val Browser = "POPUP_SHORTCUTS_BROWSER"
}

/**
 * POPUP_SHORTCUTS_TAKE_NOTE & POPUP_SHORTCUTS_QUICK_TAKE_NOTE & POPUP_SHORTCUTS_SEARCH_PROCESS & POPUP_SHORTCUTS_BROWSER
 **/
class PopupShortcutsCreator (initialContext: Context, workerParameters: WorkerParameters) : CoroutineWorker(initialContext, workerParameters) {

    private val context = initialContext

    val shortcutManager: ShortcutManager = initialContext.getSystemService(ShortcutManager::class.java) as ShortcutManager

    private var popupShortcutsCreatorResult: Result = Result.failure()

    override suspend fun doWork(): Result {

        configure()

        popupShortcutsCreatorResult = if (shortcutManager.dynamicShortcuts.isNotEmpty()) {
            Result.success()
        } else {
            Result.failure()
        }

        return popupShortcutsCreatorResult
    }


    fun configure() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {

        if (shortcutManager.dynamicShortcuts.size != 5) {

            shortcutManager.removeAllDynamicShortcuts()

            addShortcutKeyboardTyping()

        }
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
        intent.putExtra(PopupShortcutsItems.ShortcutId, "addShortcutKeyboardTyping")
        intent.putExtra(PopupShortcutsItems.ShortcutLink, context.getString(R.string.playStoreLink))
        intent.putExtra(PopupShortcutsItems.ShortcutLabel, context.getString(R.string.keyboardTypingText))
        intent.putExtra(PopupShortcutsItems.ShortcutDescription, context.getString(R.string.keyboardTypingText))
        intent.putExtra(TakeNote.NoteConfigurations.ExtraConfigurations, TakeNote.NoteConfigurations.KeyboardTyping)

        Glide.with(context)
            .asBitmap()
            .load(R.drawable.layer_icon_keyboard)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    e?.printStackTrace()

                    return true
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                    resource?.let {

                        shortcutManager.addDynamicShortcuts(arrayListOf(
                            ShortcutInfo.Builder(context, "addShortcutKeyboardTyping")
                                .setShortLabel(context.getString(R.string.keyboardTypingText))
                                .setLongLabel(context.getString(R.string.keyboardTypingText))
                                .setIcon(Icon.createWithBitmap(resource))
                                .setIntent(intent)
                                .setCategories(shortcutsHomeLauncherCategories)
                                .setRank(1)
                                .build()))

                    }

                    addShortcutHandwriting()

                    return true
                }

            })
            .submit()

    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun addShortcutHandwriting() {

        val shortcutsHomeLauncherCategories: HashSet<String> = HashSet<String>()
        shortcutsHomeLauncherCategories.addAll(arrayOf("Note", "Keyboard", "Type", "Handwriting", "Productivity", "Voice", "Record"))

        val intent = Intent(context, TakeNote::class.java).apply {
            action = PopupShortcutsActions.TakeNote
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.putExtra(PopupShortcutsItems.ShortcutId, "addShortcutHandwriting")
        intent.putExtra(PopupShortcutsItems.ShortcutLink, context.getString(R.string.playStoreLink))
        intent.putExtra(PopupShortcutsItems.ShortcutLabel, context.getString(R.string.handwritingText))
        intent.putExtra(PopupShortcutsItems.ShortcutDescription, context.getString(R.string.handwritingText))
        intent.putExtra(TakeNote.NoteConfigurations.ExtraConfigurations, TakeNote.NoteConfigurations.Handwriting)

        Glide.with(context)
            .asBitmap()
            .load(R.drawable.layer_icon_handwriting)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    e?.printStackTrace()

                    return true
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                    resource?.let {

                        shortcutManager.addDynamicShortcuts(arrayListOf(
                            ShortcutInfo.Builder(context, "addShortcutHandwriting")
                                .setShortLabel(context.getString(R.string.handwritingText))
                                .setLongLabel(context.getString(R.string.handwritingText))
                                .setIcon(Icon.createWithBitmap(resource))
                                .setIntent(intent)
                                .setCategories(shortcutsHomeLauncherCategories)
                                .setRank(2)
                                .build()))

                    }

                    addShortcutVoiceRecording()

                    return true
                }

            })
            .submit()

    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun addShortcutVoiceRecording() {

        val shortcutsHomeLauncherCategories: HashSet<String> = HashSet<String>()
        shortcutsHomeLauncherCategories.addAll(arrayOf("Note", "Keyboard", "Type", "Handwriting", "Productivity", "Voice", "Record"))

        val intent = Intent(context, TakeNote::class.java).apply {
            action = PopupShortcutsActions.TakeNote
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.putExtra(PopupShortcutsItems.ShortcutId, "addShortcutVoiceRecording")
        intent.putExtra(PopupShortcutsItems.ShortcutLink, context.getString(R.string.playStoreLink))
        intent.putExtra(PopupShortcutsItems.ShortcutLabel, context.getString(R.string.voiceRecordingText))
        intent.putExtra(PopupShortcutsItems.ShortcutDescription, context.getString(R.string.voiceRecordingText))
        intent.putExtra(TakeNote.NoteConfigurations.ExtraConfigurations, TakeNote.NoteConfigurations.VoiceRecording)

        Glide.with(context)
            .asBitmap()
            .load(R.drawable.layer_icon_voice_recording)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    e?.printStackTrace()

                    return true
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                    resource?.let {

                        shortcutManager.addDynamicShortcuts(arrayListOf(
                            ShortcutInfo.Builder(context, "addShortcutVoiceRecording")
                                .setShortLabel(context.getString(R.string.voiceRecordingText))
                                .setLongLabel(context.getString(R.string.voiceRecordingText))
                                .setIcon(Icon.createWithBitmap(resource))
                                .setIntent(intent)
                                .setCategories(shortcutsHomeLauncherCategories)
                                .setRank(3)
                                .build()))

                    }

                    addShortcutSearching()

                    return true
                }

            })
            .submit()

    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun addShortcutSearching() {

        val shortcutsHomeLauncherCategories: HashSet<String> = HashSet<String>()
        shortcutsHomeLauncherCategories.addAll(arrayOf("Note", "Keyboard", "Type", "Handwriting", "Productivity", "Voice", "Record", "Search"))

        val intent = Intent(context, SearchProcess::class.java).apply {
            action = PopupShortcutsActions.SearchProcess
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.putExtra(PopupShortcutsItems.ShortcutId, "addShortcutSearching")
        intent.putExtra(PopupShortcutsItems.ShortcutLink, context.getString(R.string.playStoreLink))
        intent.putExtra(PopupShortcutsItems.ShortcutLabel, context.getString(R.string.voiceRecordingText))
        intent.putExtra(PopupShortcutsItems.ShortcutDescription, context.getString(R.string.voiceRecordingText))

        Glide.with(context)
            .asBitmap()
            .load(R.drawable.layer_icon_search)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    e?.printStackTrace()

                    return true
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                    resource?.let {

                        shortcutManager.addDynamicShortcuts(arrayListOf(
                            ShortcutInfo.Builder(context, "addShortcutSearching")
                                .setShortLabel(context.getString(R.string.searchText))
                                .setLongLabel(context.getString(R.string.searchText))
                                .setIcon(Icon.createWithBitmap(resource))
                                .setIntent(intent)
                                .setCategories(shortcutsHomeLauncherCategories)
                                .setRank(4)
                                .build()))

                    }

                    addShortcutRateShare()

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

        Glide.with(context)
            .asBitmap()
            .load(R.drawable.rate_icon)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    e?.printStackTrace()

                    return true
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                    resource?.let {

                        shortcutManager.addDynamicShortcuts(arrayListOf(
                            ShortcutInfo.Builder(context, "addShortcutRateShare")
                                .setShortLabel(context.getString(R.string.rateShare))
                                .setLongLabel(context.getString(R.string.rateShare))
                                .setIcon(Icon.createWithBitmap(resource))
                                .setIntent(intent)
                                .setCategories(shortcutsHomeLauncherCategories)
                                .setRank(5)
                                .build()))

                    }

                    return true
                }

            })
            .submit()

    }

}