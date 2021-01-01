package net.geeksempire.ready.keep.notes.Utils.RemoteTasks.Notifications

import com.google.firebase.messaging.FirebaseMessaging

class RemoteSubscriptions {

    fun subscribe(topicToSubscribe: String) {

        FirebaseMessaging.getInstance().subscribeToTopic(topicToSubscribe)

    }

}