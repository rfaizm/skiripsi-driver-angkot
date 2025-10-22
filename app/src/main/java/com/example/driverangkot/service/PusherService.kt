package com.example.driverangkot.service

import android.app.Application
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.driverangkot.R
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.data.preference.dataStore
import com.example.driverangkot.di.Injection
import com.example.driverangkot.di.ViewModelFactory
import com.example.driverangkot.domain.entity.OrderData
import com.example.driverangkot.domain.repository.OrderRepository
import com.example.driverangkot.presentation.home.HomeViewModel
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
        orderRepository = Injection.provideOrderRepository(applicationContext)
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
        val channelName = "Pusher Service"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Layanan Driver Aktif")
            .setContentText("Menerima pesanan baru di background")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
        Log.d(TAG, "Foreground service started")
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
                    Log.d(TAG, "Attempting to reconnect Pusher")
                }
            }

            override fun onError(message: String, code: String?, e: Exception?) {
                Log.d(TAG, "Pusher connection error: $message, code: $code, exception: ${e?.message}")
            }
        })

        val driverChannelName = "driver.$driverId"
        val driverChannel = pusher.subscribe(driverChannelName)

        driverChannel.bind("App\\Events\\OrderCreated") { event ->
            try {
                Log.d(TAG, "Received OrderCreated event on $driverChannelName: ${event.data}")
                val data = JSONObject(event.data)
                val orderId = data.getInt("order_id")
                val startLat = data.getDouble("starting_point_lat")
                val startLong = data.getDouble("starting_point_long")
                val destLat = data.getDouble("destination_point_lat")
                val destLong = data.getDouble("destination_point_long")
                val passengers = data.getInt("number_of_passengers")
                val price = data.getInt("price")
                val trayekName = data.getString("trayek_name")

                coroutineScope.launch {
                    orderRepository.saveOrder(
                        OrderData(
                            orderId = orderId,
                            startLat = startLat,
                            startLong = startLong,
                            destLat = destLat,
                            destLong = destLong,
                            passengers = passengers,
                            price = price,
                            trayekName = trayekName
                        )
                    )
                }

                showOrderNotification(orderId, trayekName, passengers, price)
                Log.d(TAG, "Order received: orderId=$orderId, trayek=$trayekName")
            } catch (e: Exception) {
                Log.d(TAG, "Error parsing OrderCreated message: ${e.message}")
                try {
                    val data = JSONObject(event.data)
                    val orderId = data.getInt("order_id")
                    val trayekName = data.getString("trayek_name")
                    val passengers = data.getInt("number_of_passengers")
                    val price = data.getInt("price")
                    showOrderNotification(orderId, trayekName, passengers, price)
                } catch (e2: Exception) {
                    Log.d(TAG, "Failed to show notification for OrderCreated: ${e2.message}")
                }
            }
        }

        driverChannel.bind("App\\Events\\OrderCancelled") { event ->
            try {
                Log.d(TAG, "Received OrderCancelled event on $driverChannelName: ${event.data}")
                val data = JSONObject(event.data)
                val orderId = data.getInt("order_id")
                val status = data.getString("status")
                val message = data.getString("message")

                if (status == "dibatalkan") {
                    coroutineScope.launch {
                        orderRepository.removeOrder(orderId)
                    }
                    showCancelNotification(orderId, message)
                    Log.d(TAG, "Order $orderId cancelled, notification shown")
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error parsing OrderCancelled message: ${e.message}")
                try {
                    val data = JSONObject(event.data)
                    val orderId = data.getInt("order_id")
                    val message = data.getString("message")
                    showCancelNotification(orderId, message)
                } catch (e2: Exception) {
                    Log.d(TAG, "Failed to show notification for OrderCancelled: ${e2.message}")
                }
            }
        }

        subscribedChannels.add(driverChannel)
        pusher.connect()
        Log.d(TAG, "Pusher initialized and subscribed to $driverChannelName")
    }

    private fun showOrderNotification(orderId: Int, trayekName: String, passengers: Int, price: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "order_notifications"
        val channelName = "Order Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH // [Berubah] Gunakan IMPORTANCE_HIGH untuk suara dan heads-up
            )
            // [Baru] Tetapkan suara default sistem untuk notifikasi
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Terdapat Pesanan Baru")
            .setContentText("Penumpang: $passengers, Harga: Rp ${DecimalFormat("#,###").format(price)}") // [Berubah] Gunakan DecimalFormat
            .setPriority(NotificationCompat.PRIORITY_HIGH) // [Berubah] Gunakan PRIORITY_HIGH untuk kompatibilitas
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(orderId, notification)
            Log.d(TAG, "Notification shown for orderId=$orderId")
        } else {
            Log.d(TAG, "Notification skipped: Permission not granted")
        }
    }

    private fun showCancelNotification(orderId: Int, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "order_notifications"
        val channelName = "Order Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH // [Berubah] Gunakan IMPORTANCE_HIGH untuk suara dan heads-up
            )
            // [Baru] Tetapkan suara default sistem untuk notifikasi
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Pesanan Dibatalkan (#$orderId)")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // [Berubah] Gunakan PRIORITY_HIGH untuk kompatibilitas
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(orderId + 1000, notification)
            Log.d(TAG, "Cancel notification shown for orderId=$orderId")
        } else {
            Log.d(TAG, "Cancel notification skipped: Permission not granted")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribedChannels.forEach { pusher.unsubscribe(it.name) }
        subscribedChannels.clear()
        pusher.disconnect()
        Log.d(TAG, "Pusher disconnected and service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "PusherService"
    }
}