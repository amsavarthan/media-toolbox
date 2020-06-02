package com.amsavarthan.apps.media_toolbox.whatsapp.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.FileProvider
import  com.amsavarthan.apps.media_toolbox.BuildConfig
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File

class Utils {

    companion object {


         const val WHATSAPP_STATUSES_LOCATION = "/WhatsApp/Media/.Statuses"


         fun isImageFile(context: Context,path: String): Boolean {
             val uri:Uri = Uri.parse(path)

             val mimeType: String?
             mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                 val cr = context.contentResolver
                 cr.getType(uri)
             } else {
                 val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                         .toString())
                 MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                         fileExtension.toLowerCase())
             }


            return mimeType != null && mimeType.startsWith("image")
        }

         fun isVideoFile(context: Context,path: String): Boolean {

             val uri:Uri = Uri.parse(path)

             val mimeType: String?
             mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                 val cr = context.contentResolver
                 cr.getType(uri)
             } else {
                 val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                         .toString())
                 MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                         fileExtension.toLowerCase())
             }

             return mimeType != null && mimeType.startsWith("video")
        }


        fun addToGallery(context: Context,f:File) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

                val contentUri = Uri.fromFile(f)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)
            } else {
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())))
            }
        }


         fun shareFile(context: Context,f: File) {

            val intentShareFile = Intent(Intent.ACTION_SEND)

            if (f.exists()) {
                intentShareFile.type = "image/*"

                val uri=FileProvider.getUriForFile(context,context.packageName+".provider", f)

                intentShareFile.putExtra(Intent.EXTRA_STREAM,uri)
                intentShareFile.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(Intent.createChooser(intentShareFile, f.name))
            }
        }


    }
}