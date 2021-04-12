/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Invitations.Utils

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.ready.keep.notes.R
import org.json.JSONArray

class ShareIt (val context: AppCompatActivity) {

    fun invokeTextSharing(shareText: String) {

        val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareText)
            addCategory(Intent.CATEGORY_DEFAULT)
            type = "text/plain"
            flags = (Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.inviteTitle)))

    }

    fun invokeCompleteSharing(shareText: String?, shareImage: String?, shareAudio: String?) {

        val shareExtraStream = ArrayList<Uri>()

        if (!shareImage.isNullOrBlank()) {
            shareExtraStream.add(Uri.parse(shareImage))
        }

        if (!shareAudio.isNullOrBlank()) {

            val jsonArrayAudio = JSONArray(shareAudio)

            for (index in 0 until jsonArrayAudio.length()) {

                shareExtraStream.add(Uri.parse(jsonArrayAudio.getJSONObject(index).toString()))

            }

        }

        val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareText)
            if (shareImage != null || shareAudio != null) {
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareExtraStream)
            }
            addCategory(Intent.CATEGORY_DEFAULT)
            type = "*/*"
            flags = (Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.shareText)))

    }

}