package com.gura.face_recognition_app.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.DisplayComponentHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.CameraActivityViewModel
import com.hluhovskyi.camerabutton.CameraButton
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


open class CameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var cameraSide = "DEFAULT_BACK_CAMERA"
    private var cameraState = false

    private lateinit var viewModel: CameraActivityViewModel
    private lateinit var factory: AppViewModelFactory

    private lateinit var nameTextView: TextView
    private lateinit var organizationNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var alternateEmailTextView: TextView
    private lateinit var officePhoneTextView: TextView
    private lateinit var telephoneTextView: TextView
    private lateinit var facebookTextView: TextView
    private lateinit var lineTextView: TextView
    private lateinit var linkedinTextView: TextView
    private lateinit var contactOwnerTextView: TextView
    private lateinit var createAtTextView: TextView
    private lateinit var accuracyTextView: TextView

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var faceNotFoundBottomSheetDialog: BottomSheetDialog
    private lateinit var unknownBottomSheetDialog: BottomSheetDialog
    private lateinit var loadingBottomSheetDialog: BottomSheetDialog

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Change color of status bar and navigation bar to black
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val displayComponentHelper = DisplayComponentHelper(this,window)
        displayComponentHelper.apply {
            changeStatusBarColor(R.color.black)
            changeNavigationBarColor(R.color.black)
        }
        // Check camera permissions if all permission granted
        // start camera else ask for the permission
        if (allPermissionsGranted()) {
            openCamera("DEFAULT_BACK_CAMERA")
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        // View Model instance
        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[CameraActivityViewModel::class.java]

        // Initialize the layout variable with view id
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
            cameraSide = if (cameraSide == "DEFAULT_BACK_CAMERA") {
                openCamera("DEFAULT_FRONT_CAMERA")
                "DEFAULT_FRONT_CAMERA"
            } else {
                openCamera("DEFAULT_BACK_CAMERA")
                "DEFAULT_BACK_CAMERA"
            }
        }

        closeImageButton.setOnClickListener {
            finish()
        }

        val loadingBottomSheetView: View =
            layoutInflater.inflate(R.layout.bottomsheet_loading, null)
        loadingBottomSheetDialog = BottomSheetDialog(this@CameraActivity)
        loadingBottomSheetDialog.setContentView(loadingBottomSheetView)
        loadingBottomSheetDialog.setCancelable(false)
        loadingBottomSheetDialog.setCanceledOnTouchOutside(false)
        val loadingBottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(loadingBottomSheetView.parent as View)
        loadingBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        val unknownBottomSheetView: View =
            layoutInflater.inflate(R.layout.bottomsheet_unknown, null)
        unknownBottomSheetDialog = BottomSheetDialog(this@CameraActivity)
        unknownBottomSheetDialog.setContentView(unknownBottomSheetView)
        val unknownBottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(unknownBottomSheetView.parent as View)
        unknownBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        val faceNotFoundBottomSheetView: View =
            layoutInflater.inflate(R.layout.bottomsheet_no_face, null)
        faceNotFoundBottomSheetDialog = BottomSheetDialog(this@CameraActivity)
        faceNotFoundBottomSheetDialog.setContentView(faceNotFoundBottomSheetView)
        val faceNotFoundBottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(unknownBottomSheetView.parent as View)
        faceNotFoundBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        bottomSheetDialog = BottomSheetDialog(this@CameraActivity)
        val bottomSheetView: View = layoutInflater.inflate(R.layout.bottomsheet_result, null)
        nameTextView = bottomSheetView.findViewById(R.id.nameTextView)
        organizationNameTextView = bottomSheetView.findViewById(R.id.organizationNameTextView)
        emailTextView = bottomSheetView.findViewById(R.id.emailTextView)
        alternateEmailTextView = bottomSheetView.findViewById(R.id.alternateEmailTextView)
        officePhoneTextView = bottomSheetView.findViewById(R.id.officePhoneTextView)
        telephoneTextView = bottomSheetView.findViewById(R.id.telTextView)
        facebookTextView = bottomSheetView.findViewById(R.id.facebookTextView)
        lineTextView = bottomSheetView.findViewById(R.id.lineTextView)
        linkedinTextView = bottomSheetView.findViewById(R.id.linkedinTextView)
        contactOwnerTextView = bottomSheetView.findViewById(R.id.contactOwnerTextView)
        createAtTextView = bottomSheetView.findViewById(R.id.createAtTextView)
        accuracyTextView = bottomSheetView.findViewById(R.id.accuracyTextView)
        bottomSheetDialog.setContentView(bottomSheetView)
        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(bottomSheetView.parent as View)
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        viewModel.recognitionCommand.observe(this){
            loadingBottomSheetDialog.cancel()
            when (it.statusCode) {
                -1 -> {
                    faceNotFoundBottomSheetDialog.show()
                    cameraState = false
                }

                0 -> {
                    unknownBottomSheetDialog.show()
                    cameraState = false
                }

                1 -> {
                    cameraState = true
                    nameTextView.text =
                        "${it.response.contact.firstname} ${it.response.contact.lastname}"
                    organizationNameTextView.text = it.response.contact.organizationName
                    emailTextView.text = it.response.contact.email
                    alternateEmailTextView.text = it.response.contact.alternateEmail
                    officePhoneTextView.text = it.response.contact.officePhone
                    telephoneTextView.text = it.response.contact.mobile
                    facebookTextView.text = it.response.contact.facebook
                    lineTextView.text = it.response.contact.lineId
                    linkedinTextView.text = it.response.contact.linkedin
                    contactOwnerTextView.text = it.response.contact.contactOwner
                    createAtTextView.text = it.response.contact.createdTime
                    accuracyTextView.text = "${it.response.accuracy.toString()}%"
                    bottomSheetDialog.show()
                }
                else -> {
                    Toast.makeText(
                        this@CameraActivity,
                        "ERROR: Invalid status code",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)

                    lifecycleScope.launch {
                        viewModel.sendImageForRecognizing(savedUri)
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

    private fun openCamera(cameraTag: String) {
        var cameraSelector: CameraSelector?
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val previewView = findViewById<PreviewView>(R.id.viewFinder)

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            cameraSelector = if(cameraTag == "DEFAULT_BACK_CAMERA"){
                CameraSelector.DEFAULT_BACK_CAMERA
            }else{
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector!!, preview, imageCapture
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
                openCamera("DEFAULT_BACK_CAMERA")
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

    private var bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetDialog.cancel()
                unknownBottomSheetDialog.cancel()
                loadingBottomSheetDialog.cancel()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }
}