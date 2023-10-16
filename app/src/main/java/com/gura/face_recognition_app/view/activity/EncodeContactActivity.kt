package com.gura.face_recognition_app.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.EncodeContactActivityViewModel
import com.hluhovskyi.camerabutton.CameraButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EncodeContactActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var CAMERA_SIDE = "DEFAULT_BACK_CAMERA"
    private var CAMERA_STATE = false
    private lateinit var loadingBottomSheetDialog: BottomSheetDialog
    private var contactId: Int = 0

    private lateinit var viewModel: EncodeContactActivityViewModel
    private lateinit var factory: AppViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encode_contact)

        contactId = intent.getIntExtra("contactId",0)

        // Change color of status bar and navigation bar to black
        supportActionBar?.hide()
        // Initialize helper for customizing display component
        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.black
        window.navigationBarColor = R.color.black
        window.allowNightMode = false
        window.keepScreenOn = true
        window.publish()

        // View Model instance
        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[EncodeContactActivityViewModel::class.java]


        // Check camera permissions if all permission granted
        // start camera else ask for the permission
        if (allPermissionsGranted()) {
            startBackCamera()
        } else {
            ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        val captureCameraButton: CameraButton = findViewById(R.id.captureCameraButton)
        val rotateCameraImageButton: ImageButton = findViewById(R.id.rotateCameraImageButton)
        val closeImageButton: ImageButton = findViewById(R.id.closeImageButton)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        captureCameraButton.setOnClickListener {
            loadingBottomSheetDialog.show()
            takePhoto()
        }

        rotateCameraImageButton.setOnClickListener {
            CAMERA_SIDE = if (CAMERA_SIDE == "DEFAULT_BACK_CAMERA") {
                startFrontCamera()
                "DEFAULT_FRONT_CAMERA"
            } else {
                startBackCamera()
                "DEFAULT_BACK_CAMERA"
            }
        }

        closeImageButton.setOnClickListener {
            finish()
        }

        val loadingBottomSheetView: View =
            layoutInflater.inflate(R.layout.bottomsheet_loading, null)
        loadingBottomSheetDialog = BottomSheetDialog(this@EncodeContactActivity)
        loadingBottomSheetDialog.setContentView(loadingBottomSheetView)
        loadingBottomSheetDialog.setCancelable(false)
        loadingBottomSheetDialog.setCanceledOnTouchOutside(false)
        val loadingBottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(loadingBottomSheetView.parent as View)
        loadingBottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)

        viewModel.cmd.observe(this) {
            if(it.cmd == "FACE_NOT_FOUND") {
                Toast.makeText(
                    this@EncodeContactActivity,
                    "Face not found please try again.",
                    Toast.LENGTH_SHORT).show()
            } else if (it.cmd == "ENCODE_SUCCESS"){
                Toast.makeText(
                    this@EncodeContactActivity,
                    "Encoded successfully",
                    Toast.LENGTH_SHORT).show()
            }
            loadingBottomSheetDialog.cancel()
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the
        // modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        val userId = App.instance.userId

        // Set up image capture listener,
        // which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)

                    val bitmap: Bitmap = MediaStore
                        .Images.Media
                        .getBitmap(this@EncodeContactActivity.contentResolver, savedUri)

                    lifecycleScope.launch(Dispatchers.Main) {
                        if(contactId == 0){
                            Toast.makeText(this@EncodeContactActivity,"ERROR",Toast.LENGTH_SHORT).show()
                            loadingBottomSheetDialog.cancel()
                            finish()
                        }

                        viewModel.send(bitmap, contactId)
                    }

                    val fileDelete = File(savedUri.path!!)

                    if (fileDelete.exists()) {
                        if (fileDelete.delete()) {
                            Log.d("CameraActivity", "file Deleted :")
                        } else {
                            Log.d("CameraActivity", "file not Deleted :")
                        }
                    }

                }
            })
    }

    private fun startFrontCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {

            val viewFinder = findViewById<PreviewView>(R.id.viewFinder)

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun startBackCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {

            val viewFinder = findViewById<PreviewView>(R.id.viewFinder)

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // creates a folder inside internal storage
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    // checks the camera permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // If all permissions granted , then start Camera
            if (allPermissionsGranted()) {
                startBackCamera()
            } else {
                // If permissions are not granted,
                // present a toast to notify the user that
                // the permissions were not granted.
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXGFG"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                loadingBottomSheetDialog.cancel()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }
}