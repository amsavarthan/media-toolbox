package com.amsavarthan.apps.media_toolbox.whatsapp.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.amsavarthan.apps.media_toolbox.R
import com.amsavarthan.apps.media_toolbox.whatsapp.utils.Utils
import com.amsavarthan.apps.media_toolbox.whatsapp.utils.Utils.Companion.WHATSAPP_STATUSES_LOCATION
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity(),OnClickDownloadListener {

    private companion object {
        const val EXTERNAL_STORAGE_PERMISSION_CODE: Int = 343

        private const val TYPE_VIDEO = 12
        private const val TYPE_IMAGE = 13
        private const val TYPE_SAVED = 15

        class FetchFilesTask(activity: HomeActivity) : AsyncTask<Int, Int, ArrayList<File>>() {

            private val mRef: WeakReference<HomeActivity> = WeakReference(activity)

            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg p0: Int?): ArrayList<File>? {
                return mRef.get()?.fetchFiles(p0[0])
            }

            override fun onPostExecute(result: ArrayList<File>) {
                super.onPostExecute(result)
                mRef.get()?.statusAdapter?.addAll(result)
                if (result.size == 0){
                    mRef.get()?.showHelp()
                }

            }
        }

    }


    private lateinit var statusAdapter: StatusAdapter

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        statusAdapter = StatusAdapter(this)
        statusAdapter.setOnClickDownloadListener(this)
        statusRcV.layoutManager = GridLayoutManager(this,2)
        statusRcV.adapter = statusAdapter

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission()
        }

        FetchFilesTask(this).execute(TYPE_IMAGE)

    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun askForPermission() {

        if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions( arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), EXTERNAL_STORAGE_PERMISSION_CODE)
        }
    }

    fun fetchFiles(type: Int?): ArrayList<File> {

        var parentDir = File(Environment.getExternalStorageDirectory().toString()+WHATSAPP_STATUSES_LOCATION)

        if (type == TYPE_SAVED){

            val directory = getSharedPreferences("directory", Context.MODE_PRIVATE).getString("path", Environment.getExternalStorageDirectory().toString() + "/Media Toolbox")
            parentDir = File("$directory/WhatsApp Status/")
        }


        val files: Array<File>?
        files = parentDir.listFiles()

        val fetchedFiles: ArrayList<File> = ArrayList()

        if (files != null) {

            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE)

            for (file in files) {

                if (Utils.isImageFile(this,file.path) && type == TYPE_IMAGE){

                    fetchedFiles.add(file)
                }

                if (Utils.isVideoFile(this,file.path) && type == TYPE_VIDEO){

                    fetchedFiles.add(file)
                }
                if (type == TYPE_SAVED){

                    fetchedFiles.add(file)
                }


            }
        }

        return fetchedFiles
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission

            val alertDialog=AlertDialog.Builder(this)
            alertDialog
                    .setTitle(getString(R.string.no_permission))
                    .setMessage(getString(R.string.require_read_write_external_storage_permissin))
                    .setPositiveButton("Allow"){dialog, which ->
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            // User checked "Never ask again"
                            val intent =  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }else{
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                askForPermission()
                            }
                        }
                    }
                    .setNegativeButton("Close"){dialog, which -> finish() }
                    .setCancelable(false)
                    .show()

            return

        }


        FetchFilesTask(this).execute(TYPE_IMAGE)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission()
        }
        when (item.itemId) {

            R.id.navigation_images -> {
                FetchFilesTask(this).execute(TYPE_IMAGE)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_video -> {
                FetchFilesTask(this).execute(TYPE_VIDEO)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_saved -> {
                FetchFilesTask(this).execute(TYPE_SAVED)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun showHelp(){

        val builder=AlertDialog.Builder(this)
        builder.setTitle("Help")
        builder.setMessage(getString(R.string.help_message))
        builder.setPositiveButton("Close",null)
        builder.show()

    }

    override fun onClickDownload() {
        //To show ads
    }

}
