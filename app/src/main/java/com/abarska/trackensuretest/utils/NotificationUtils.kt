package com.abarska.trackensuretest.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.abarska.trackensuretest.R
import java.text.SimpleDateFormat
import java.util.*

private const val FIREBASE_NOTIFICATION_CHANNEL = "firebase_notification_channel"

fun sendNotification(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val firebaseNotificationChannel =
            NotificationChannel(
                FIREBASE_NOTIFICATION_CHANNEL,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        firebaseNotificationChannel.description =
            context.getString(R.string.notification_channel_description)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(firebaseNotificationChannel)
    }

    val manager = NotificationManagerCompat.from(context)
    val formatPattern = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss", Locale.getDefault())
    val dateTime = formatPattern.format(Date(System.currentTimeMillis()))

    val notification = NotificationCompat.Builder(context, FIREBASE_NOTIFICATION_CHANNEL)
        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(context.getString(R.string.notification_text, dateTime))
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .build()
    manager.notify(1, notification)
}