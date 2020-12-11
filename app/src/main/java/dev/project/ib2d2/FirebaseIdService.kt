package dev.project.ib2d2


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/*
 * push notification service handler
 * @ref: https://lobothijau.medium.com/create-android-push-notification-easily-with-kotlin-and-firebase-cloud-messaging-part-1-9062f2a57555
 */

class FirebaseIdService : FirebaseMessagingService() {
    private val TAG = javaClass.name
    lateinit var name: String

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "TOKEN REFRESHED FOR PUSH NOTIFICATIONS")

    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            if (token != null) {
                Log.d(TAG, token)
            }
        })

        // if there is a notification, lets display it....
        if(msg.notification != null){
            // spawn intents
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)

            // notification channels
            // @ref: https://medium.com/exploring-android/exploring-android-o-notification-channels-94cd274f604c
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel
                val name = "my_channel_01"
                val descriptionText = "my_channel_01"
                val mChannel = NotificationChannel(name, descriptionText, NotificationManager.IMPORTANCE_DEFAULT)
                mChannel.description = descriptionText

                // Register the channel with the system
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
            }

            // send the notification
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, "my_channel_01")
                .setSmallIcon(R.drawable.ib2d2_logo)
                .setContentTitle(msg.notification?.title)
                .setContentText(msg.notification?.body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, notificationBuilder.build())
        }
    }
}