package com.example.driverangkot.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.driverangkot.R
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.data.preference.dataStore
import com.example.driverangkot.data.repository.OrderRepository
import com.example.driverangkot.di.Injection
import com.example.driverangkot.domain.entity.OrderData
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.DecimalFormat

class PusherService : Service() {

    private lateinit var pusher: Pusher
    private lateinit var userPreference: UserPreference
    private lateinit var orderRepository: OrderRepository
    private val subscribedChannels = mutableSetOf<Channel>()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        userPreference = UserPreference.getInstance(applicationContext.dataStore)
        orderRepository = Injection.orderRepository(applicationContext)
        startForegroundService()
        val isOnline = userPreference.getStatusOnline() ?: false
        if (isOnline) {
            initPusher()
        } else {
            Log.d(TAG, "Driver offline, Pusher not initialized")
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "pusher_service"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(channelId, "Pusher Service", NotificationManager.IMPORTANCE_LOW)
            )
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Layanan Driver Aktif")
            .setContentText("Menerima pesanan baru di background")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    private fun initPusher() {
        val driverId = userPreference.getDriverId()
        if (driverId == null) {
            Log.d(TAG, "Driver ID tidak ditemukan")
            return
        }

        val options = PusherOptions().setCluster("ap1")
        pusher = Pusher("d1373b327727bf1ce9cf", options)
        pusher.connection.bind(ConnectionState.ALL, object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.d(TAG, "Pusher state changed from ${change.previousState} to ${change.currentState}")
                if (change.currentState == ConnectionState.DISCONNECTED) {
                    pusher.connect()
                }
            }

            override fun onError(message: String, code: String?, e: Exception?) {
                Log.d(TAG, "Pusher connection error: $message, code: $code, exception: ${e?.message}")
            }
        })

        val driverChannel = pusher.subscribe("driver.$driverId")

        driverChannel.bind("App\\Events\\OrderCreated") { event ->
            try {
                val data = JSONObject(event.data)
                val order = OrderData(
                    orderId = data.getInt("order_id"),
                    startLat = data.getDouble("starting_point_lat"),
                    startLong = data.getDouble("starting_point_long"),
                    destLat = data.getDouble("destination_point_lat"),
                    destLong = data.getDouble("destination_point_long"),
                    passengers = data.getInt("number_of_passengers"),
                    price = data.getInt("price"),
                    trayekName = data.getString("trayek_name")
                )
                coroutineScope.launch { orderRepository.saveOrder(order) }
                showNotification(
                    order.orderId,
                    "Terdapat Pesanan Baru",
                    "Penumpang: ${order.passengers}, Harga: Rp ${DecimalFormat("#,###").format(order.price)}"
                )
            } catch (e: Exception) {
                Log.d(TAG, "Error parsing OrderCreated message: ${e.message}")
            }
        }

        driverChannel.bind("App\\Events\\OrderCancelled") { event ->
            try {
                val data = JSONObject(event.data)
                val orderId = data.getInt("order_id")
                if (data.getString("status") == "dibatalkan") {
                    coroutineScope.launch { orderRepository.removeOrder(orderId) }
                    showNotification(orderId + 1000, "Pesanan Dibatalkan (#$orderId)", data.getString("message"))
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error parsing OrderCancelled message: ${e.message}")
            }
        }

        subscribedChannels.add(driverChannel)
        pusher.connect()
    }

    private fun showNotification(notificationId: Int, title: String, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Notification skipped: Permission not granted")
            return
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "order_notifications"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Order Notifications", NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribedChannels.forEach { pusher.unsubscribe(it.name) }
        subscribedChannels.clear()
        pusher.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "PusherService"
    }
}
