package com.hari.rideit.Controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hari.rideit.R
import com.hari.rideit.model.InternalStoragePhoto
import com.hari.rideit.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.*
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AddDriverActivity : AppCompatActivity() {
    lateinit var lat: String
    lateinit var long: String
    private var imageData: ByteArray? = null
    lateinit var name: EditText
    lateinit var licenseno: EditText
    lateinit var propic: MultipartBody.Part
    lateinit var both: CheckBox
    lateinit var only: CheckBox
    lateinit var value: String
    private var REQUEST_CODE = 1000
    private val IMAGE_PICK_CODE = 500
    private val sharedPrefFile = "pathfile"
    private lateinit var viewModel: MainViewModel
    lateinit var sharedPreferences: SharedPreferences
    lateinit var file:File


    private lateinit var imageView: ImageView

    companion object {
        private const val IMAGE_PICK_CODE = 999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_driver)
        imageView = findViewById(R.id.proImg)
        name = findViewById(R.id.Dname)
        licenseno = findViewById(R.id.Dlicenseno)
        both = findViewById(R.id.both)
        only = findViewById(R.id.onlycar)

        both.setOnClickListener {
            only.setChecked(false)
            both.setChecked(true)
            value = "Both Car And Bike"
        }
        only.setOnClickListener {
            both.setChecked(false)
            value = "Only Car"
            only.setChecked(true)
        }
        sharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val upload = findViewById<Button>(R.id.uploadBtnDriver)
        upload.setOnClickListener {
            openFile(null)
        }

    }

    private fun openFile(pickerInitialUri: Uri?) {
        val uriString = sharedPreferences.getString("URI", "")
        Log.d("URIDOC",uriString)
        when {
            uriString == "" -> {

                askPermission()
                Log.d("INSIDEWHEN1","WHEN1")
            }
            arePermissionsGranted(uriString) -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setData(Uri.parse(uriString))
                intent.type = "image/*"

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                if (pickerInitialUri != null) {
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                }
                Log.d("INSIDEWHEN2","WHEN2")
                startActivityForResult(intent, IMAGE_PICK_CODE)
            }
            else -> {
                Log.d("INSIDEWHEN3","WHEN3")
                askPermission()

            }
        }

    }


    fun RegisterDriverBtnClicked(view: View) {
        val uriString = sharedPreferences.getString("URI", "")



        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
        val jsonobj: JSONObject = JSONObject()
        val name = name.text.toString()
        val license = licenseno.text.toString()
        val licensetype = value
        val lat = 9.172
        val long = 76.501
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        Log.d("NameD",name)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        val uid: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), name)
        val lno: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), license)
        val ltype: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), licensetype)

        file= File("${getFilesDir().getAbsolutePath()}/photo.jpg")



        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
        propic = MultipartBody.Part.createFormData("riderimg", file.name, requestFile)

        val lati: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), lat.toString())
        val longi: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), long.toString())
        val auth = intent.getStringExtra("JWT")
        viewModel.putPost(uid, lno, ltype, propic, lati, longi, auth)
        try {
            viewModel.myResponse.observe(this, Observer { response ->
                if (response.isSuccessful) {
                    Toast.makeText(this, "Sucessfull ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show()
                    Log.d("EERR",response.toString())
                }
            })
        } catch (e: Exception) {
            Log.d("ERR", e.toString())
        }

    }



//    fun launchGallery(uri: Uri){
//        val Intent=Intent(Intent.ACTION_PICK)
//
//        Intent.type="image/*"
//        val dir=DocumentFile.fromTreeUri(this, Uri.parse(uri))
//        Intent.setData(dir!!.uri)
//        startActivityForResult(Intent,IMAGE_PICK_CODE)
//    }

//    @SuppressLint("WrongConstant")
//    private fun releasePermissions(uri: Uri) {
//        val flags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
//                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
//        contentResolver.releasePersistableUriPermission(uri,flags)
//        //we should remove this uri from our shared prefs, so we can start over again next time
//        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
//        editor.putString("URI","")
//
//        editor.apply()
//        editor.commit()
//    }

    private fun askPermission() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        )
        intent.setType("image/*")
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun arePermissionsGranted(uriString: String?): Boolean {
        // list of all persisted permissions for our app
        val list = contentResolver.persistedUriPermissions
        for (i in list.indices) {
            val persistedUriString = list[i].uri.toString()
            //Log.d(LOGTAG, "comparing $persistedUriString and $uriString")
            if (persistedUriString == uriString && list[i].isReadPermission) {
                Log.d("LOGTAG", "permission ok")
                return true
            }
            Log.d("LOGTAG", "permission Not ok")
        }
        Log.d("LOGTAG", "permission Not  NEW ok")
        return false
    }


    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            val dataStr = data!!.dataString
            val returnUri = data!!.data
            val returnCursor = contentResolver.query(returnUri!!, null, null, null, null)
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = returnCursor!!.getColumnIndex(OpenableColumns.SIZE)

            returnCursor!!.moveToFirst()





            Handler().postDelayed({
                runOnUiThread {
                    imageView.setImageURI(returnUri)
                    if(CheckPermission()){

                    }
                    else {
                        askpathpermission()
                    }
                    if(returnUri!=null){
                        returnUri.also { uri->


                            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, returnUri!!))
                            } else {
                                contentResolver.openInputStream(returnUri)?.use { inputStream ->
                                    BitmapFactory.decodeStream(inputStream)
                                }
                            }




                            savePhotoToInternalStorage("photo",bitmap!!)


                        }




                        Toast.makeText(this,"OKEY",Toast.LENGTH_LONG).show()
                    }
                }
            }, 1000)


        } else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                //this is the uri user has provided us
                val treeUri: Uri? = data.data
                if (treeUri != null) {

                    // here we should do some checks on the uri, we do not want root uri
                    // because it will not work on Android 11, or perhaps we have some specific
                    // folder name that we want, etc
                    if (Uri.decode(treeUri.toString()).endsWith(":")) {
                        Toast.makeText(this, "Cannot use root folder!", Toast.LENGTH_SHORT).show()
                        // consider asking user to select another folder
                        return
                    }
                    // here we ask the content resolver to persist the permission for us
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(
                        treeUri,
                        takeFlags
                    )

                    // we should store the string fo further use

                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("URI", treeUri.toString())

                    editor.apply()
                    editor.commit()
                    //Finally, we can do our file operations
                    //Please note, that all file IO MUST be done on a background thread. It is not so in this
                    //sample - for the sake of brevity.

                }
            }
        }

    }
    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch(e: IOException) {
            e.printStackTrace()
            false
        }
    }


    private suspend fun loadPhotosFromInternalStorage(): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }

//    fun getPathFromURI(contentUri: Uri?): String? {
//        var res: String? = null
//        val proj = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(contentUri!!, proj, null, null, null)
//        if (cursor!!.moveToFirst()) {
//            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            res = cursor.getString(column_index)
//        }
//        cursor.close()
//        return res
//    }
    private fun bitmapToFile(bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
    fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }
    private fun CheckPermission():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_MEDIA_LOCATION)==PackageManager.PERMISSION_GRANTED

        ){
            return true

        }

        return false
    }
    fun askpathpermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION),100
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== 100){
            if(grantResults.isNotEmpty()&&grantResults[0]==  PackageManager.PERMISSION_GRANTED){

            }
            else{
                Toast.makeText(this,"Pls Allow to upload",Toast.LENGTH_SHORT).show()
            }
        }
    }


}

