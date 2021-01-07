/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/6/20 9:08 AM
 * Last modified 11/6/20 9:08 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Invitations.Receive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.AccountManager.Utils.UserInformation
import net.geeksempire.ready.keep.notes.Invitations.Utils.InvitationConstant
import net.geeksempire.ready.keep.notes.KeepNoteApplication



class ReceiveInvitation : AppCompatActivity() {

    val firebaseUser: FirebaseUser? = Firebase.auth.currentUser



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this@ReceiveInvitation) { pendingDynamicLinkData ->

                pendingDynamicLinkData?.let {

                    pendingDynamicLinkData.link?.also { dynamicLinkUri ->

                        if (firebaseUser != null) {

                            //Data Of User Who Invited This User
                            val uniqueUserId = dynamicLinkUri.getQueryParameter(InvitationConstant.UniqueUserId)!!
                            val userDisplayName = dynamicLinkUri.getQueryParameter(InvitationConstant.UserDisplayName)!!
                            val userProfileImage = dynamicLinkUri.getQueryParameter(InvitationConstant.UserProfileImage)!!

                            val invitedFriend = LinkedHashMap<String, String>()
                            invitedFriend[InvitationConstant.UniqueUserId] = firebaseUser.uid
                            invitedFriend[InvitationConstant.UserEmailAddress] = firebaseUser.email.toString()
                            invitedFriend[InvitationConstant.UserDisplayName] = firebaseUser.displayName.toString()
                            invitedFriend[InvitationConstant.UserProfileImage] = firebaseUser.photoUrl.toString()

                            if (firebaseUser.uid != uniqueUserId) {

                                (application as KeepNoteApplication).firestoreDatabase
                                    .document(UserInformation.invitedSuccessDatabasePath(invitingFriendUniqueIdentifier = firebaseUser.uid, userUniqueIdentifier = uniqueUserId))
                                    .set(invitedFriend)
                                    .addOnSuccessListener {



                                    }.addOnFailureListener {



                                    }

                            }

                        } else {

                            this@ReceiveInvitation.finish()

                        }

                    }

                }

            }.addOnFailureListener(this) { exception ->
                exception.printStackTrace()

            }

    }

}