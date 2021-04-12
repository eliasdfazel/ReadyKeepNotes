/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.RemoteTasks.Notifications

import com.google.firebase.inappmessaging.model.Action
import com.google.firebase.inappmessaging.model.InAppMessage

class RemoteMessageHandler {

    fun extractData(inAppMessage: InAppMessage, inAppMessageAction: Action) {

        val inAppMessageData: HashMap<String, String> = inAppMessage.data as HashMap<String, String>

    }
}