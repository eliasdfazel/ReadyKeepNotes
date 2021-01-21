package net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import net.geeksempire.ready.keep.notes.R

class NotificationBuilder (val context: Context) {

    private val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

    fun create(notificationChannelId: String = this@NotificationBuilder.javaClass.simpleName, notificationId: Int = 666,
               notificationTitle: String?, notificationContent: String?, notificationContentDone: String? = null,
               notificationColor: Int = context.getColor(R.color.default_color),
               notificationDone: Boolean = false) : Notification{

        val notificationBuilder = NotificationCompat.Builder(context, notificationChannelId)
        notificationBuilder.setContentTitle(notificationTitle?:context.getString(R.string.applicationName))
        notificationBuilder.setContentText(notificationContent?:context.getString(R.string.settingUpText))
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        notificationBuilder.color = notificationColor
        notificationBuilder.setNotificationSilent()

        if (notificationDone) {

            notificationBuilder.setContentText(notificationContentDone?:context.getString(R.string.doneText))
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setOngoing(false)

            notificationManager.notify(notificationId, notificationBuilder.build())

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationBuilder.setChannelId(notificationChannelId)

            val notificationChannel = NotificationChannel(
                notificationChannelId,
                context.getString(R.string.applicationName),
                NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(notificationChannel)

        }

        return notificationBuilder.build()

    }

}