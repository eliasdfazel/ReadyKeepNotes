/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/8/20 9:53 AM
 * Last modified 11/8/20 9:53 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Invitations.Send

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.Invitations.Utils.InvitationConstant
import net.geeksempire.ready.keep.notes.Invitations.Utils.ShareIt
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarBuilder

class SendInvitation (val context: Context, val rootView: ViewGroup) {

    fun invite(firebaseUser: FirebaseUser) {

        val dynamicLink = Firebase.dynamicLinks.dynamicLink {

            link = Uri.parse("https://www.geeksempire.net/ReadyKeepNotesInvitation.html")
                .buildUpon()
                .appendQueryParameter(InvitationConstant.UniqueUserId, firebaseUser.uid)
                .appendQueryParameter(InvitationConstant.UserEmailAddress, firebaseUser.email)
                .appendQueryParameter(InvitationConstant.UserDisplayName, firebaseUser.displayName)
                .appendQueryParameter(InvitationConstant.UserProfileImage, firebaseUser.photoUrl.toString())
                .build()

            domainUriPrefix = "https://keepnotes.page.link"

            socialMetaTagParameters {

            }

            androidParameters(context.packageName) {

            }

            iosParameters(context.packageName) {

            }

        }

        val dynamicLinkUri = dynamicLink.uri

        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newHtmlText(
            firebaseUser.displayName,
            InvitationConstant.generateInvitationText(dynamicLinkUri, firebaseUser.displayName.toString()),
            InvitationConstant.generateInvitationText(dynamicLinkUri, firebaseUser.displayName.toString())
        )
        clipboardManager.setPrimaryClip(clipData).also {

            SnackbarBuilder(context).show (
                rootView = rootView,
                messageText= context.getString(R.string.invitationDataReady),
                messageDuration = Snackbar.LENGTH_INDEFINITE,
                actionButtonText = R.string.inviteAction,
                snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                    override fun onActionButtonClicked(snackbar: Snackbar) {
                        super.onActionButtonClicked(snackbar)

                        ShareIt(context)
                            .invokeTextSharing(InvitationConstant.generateInvitationText(dynamicLinkUri, firebaseUser.displayName.toString()))

                    }

                }
            )

        }

    }

}