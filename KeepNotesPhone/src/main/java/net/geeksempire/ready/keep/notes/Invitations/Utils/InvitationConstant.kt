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

import android.net.Uri

class InvitationConstant {

    companion object {
        const val UniqueUserId: String = "UniqueUserId"
        const val UserEmailAddress: String = "UserEmailAddress"
        const val UserDisplayName: String = "UserDisplayName"
        const val UserProfileImage: String = "UserProfileImage"

        fun generateInvitationText(dynamicLinkUri: Uri, displayName: String) : String {

            return "Always Ready To Keep Your Notes | Invited By Your Friend ${displayName}" +
                    "\n\n" +
                    "${dynamicLinkUri}"
        }

    }

}