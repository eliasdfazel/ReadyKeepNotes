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
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async

object PopupShortcutsItems {
    const val ShortcutId = "ShortcutId"
    const val ShortcutLink = "ShortcutLink"
    const val ShortcutLabel = "ShortcutLabel"
    const val ShortcutDescription = "ShortcutDescription"
}

/**
 * @param shortcutAction = POPUP_SHORTCUTS_QUICK_TAKE_NOTE & POPUP_SHORTCUTS_TAKE_NOTE
 **/
data class PopupShortcutsItemsData(var shortcutClass: Class<AppCompatActivity>,
                                   var shortcutAction: String,
                                   var shortcutIndex: Int,
                                   var shortcutId: String,
                                   var shortcutLink: String?,
                                   var shortcutLabel: String,
                                   var shortcutDescription: String?,
                                   var shortcutIcon: Icon)

class PopupShortcutsCreator (private val context: AppCompatActivity) {

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun addShortcut(popupShortcutsItemsData: PopupShortcutsItemsData) = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {

        val shortcutManager: ShortcutManager = context.getSystemService(ShortcutManager::class.java) as ShortcutManager

        if (popupShortcutsItemsData.shortcutIndex == 0) {
            shortcutManager.removeAllDynamicShortcuts()
        }

        val shortcutsInformation: ArrayList<ShortcutInfo> = ArrayList<ShortcutInfo>()
        shortcutsInformation.clear()

        val shortcutsHomeLauncherCategories: HashSet<String> = HashSet<String>()
        shortcutsHomeLauncherCategories.addAll(arrayOf("Note", "Keyboard", "Type", "Handwriting", "Productivity", "Voice", "Record"))

        val intent = Intent(context, popupShortcutsItemsData.shortcutClass)
        intent.action = popupShortcutsItemsData.shortcutAction
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(PopupShortcutsItems.ShortcutId, popupShortcutsItemsData.shortcutId)
        intent.putExtra(PopupShortcutsItems.ShortcutLink, popupShortcutsItemsData.shortcutLink)
        intent.putExtra(PopupShortcutsItems.ShortcutLabel, popupShortcutsItemsData.shortcutLabel)
        intent.putExtra(PopupShortcutsItems.ShortcutDescription, popupShortcutsItemsData.shortcutDescription)

        val shortcutInfo = ShortcutInfo.Builder(context, popupShortcutsItemsData.shortcutId)
            .setShortLabel(popupShortcutsItemsData.shortcutLabel)
            .setLongLabel(popupShortcutsItemsData.shortcutLabel)
            .setIcon(popupShortcutsItemsData.shortcutIcon)
            .setIntent(intent)
            .setCategories(shortcutsHomeLauncherCategories)
            .setRank(popupShortcutsItemsData.shortcutIndex)
            .build()

        shortcutsInformation.add(shortcutInfo)

        shortcutManager.addDynamicShortcuts(shortcutsInformation)

    }

}