/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/6/20 8:22 AM
 * Last modified 11/6/20 8:07 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Invitations.Utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import net.geeksempire.ready.keep.notes.R

class ShareIt (val context: Context) {

    fun invokeTextSharing(shareText: String) {

        val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.inviteTitle)))

    }

    fun invokeCompleteSharing(shareText: String?, shareImage: String?) {

        val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_TEXT, shareText)
            shareImage?.let {
                putExtra(Intent.EXTRA_STREAM, Uri.parse(it))
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.shareText)))

    }

}