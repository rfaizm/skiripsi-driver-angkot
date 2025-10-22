package com.example.driverangkot.utils

import android.content.Context
import android.net.Uri
import com.example.driverangkot.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object Utils {
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun showConfirmationDialog(context : Context, navigateToNextFragment: () -> Unit,
                               textTitle : String, textMessage : String, textPositive : String,
                               textNegative : String, ) {
        MaterialAlertDialogBuilder(context, R.style.CustomAlertDialog).apply {
            setTitle(textTitle)
            setMessage(textMessage)
            setPositiveButton(textPositive) { _, _ ->
                navigateToNextFragment()
            }
            setNegativeButton(textNegative) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    fun formatNumber(number: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return "Rp. ${formatter.format(number)}"
    }

    fun getTrayekName(context: Context, trayekId: String?): String {
        if (trayekId == null) return "Trayek Tidak Ditemukan"

        // Ambil array trayeks dari string.xml
        val trayekArray = context.resources.getStringArray(R.array.trayeks)
        // Cari trayek yang ID-nya cocok dengan trayekId
        trayekArray.forEach { trayek ->
            val parts = trayek.split(":", limit = 2)
            if (parts.isNotEmpty() && parts[0].trim() == trayekId) {
                return parts.getOrNull(1)?.trim() ?: "Nama Trayek Tidak Ditemukan"
            }
        }
        return "Trayek Tidak Ditemukan"
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Radius bumi dalam kilometer
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c // Jarak dalam kilometer
    }

}