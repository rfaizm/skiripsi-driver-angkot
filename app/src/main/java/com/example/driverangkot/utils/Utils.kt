package com.example.driverangkot.utils

import android.content.Context
import android.location.Location
import android.net.Uri
import com.example.driverangkot.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = File.createTempFile(timeStamp, ".jpg", context.externalCacheDir)
        context.contentResolver.openInputStream(imageUri)!!.use { input ->
            FileOutputStream(myFile).use { input.copyTo(it) }
        }
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

    // Meters.
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double =
        FloatArray(1).also { Location.distanceBetween(lat1, lon1, lat2, lon2, it) }[0].toDouble()

}