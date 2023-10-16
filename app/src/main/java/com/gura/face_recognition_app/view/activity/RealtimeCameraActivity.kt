package com.gura.face_recognition_app.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.RealtimeCameraActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RealtimeCameraActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var delayHandler: Handler

    // Texture view camera
    private var cameraState = false
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraManager: CameraManager
    private lateinit var textureView: TextureView

    // Detector time delay and switch for opening and closing
    private val mInterval: Long = 2000
    private var isDetectorOpen: Boolean = false
    private var isOnProgress: Boolean = false

    // View Model
    private lateinit var viewModel: RealtimeCameraActivityViewModel
    private lateinit var factory: AppViewModelFactory

    // Variable of customer information bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var unknownBottomSheetDialog: BottomSheetDialog
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

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime_camera)

        // Change color of status bar and navigation bar to black
        supportActionBar?.hide()
        // Initialize helper for customizing display component
        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.black
        window.navigationBarColor = R.color.black
        window.allowNightMode = false
        window.keepScreenOn = true
        window.publish()

        // Check camera permissions if all permission granted
        // start camera else ask for the permission
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // View Model instance
        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[RealtimeCameraActivityViewModel::class.java]

        // Initialize handler for delaying, video camera threading,
        delayHandler = Handler(Looper.getMainLooper())
        startRepeatingTask()
        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        // Initialize bottom sheet layout and others
        initializeBottomSheet()
        textureView = findViewById(R.id.cameraView)

        // Initialize ML-Kit detector for detecting face on the image
        val detector = viewModel.initializeDetector()
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                if (allPermissionsGranted()) {
                    openCamera()
                } else {
                    ActivityCompat.requestPermissions(this@RealtimeCameraActivity,
                        REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                    )
                }
            }
            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {}
            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean { return false }
            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                Log.d("Detector","-----------------------------")
                if(isDetectorOpen && !isOnProgress){
                    isDetectorOpen = false
                    // Get image bitmap on text view
                    val bitmap = textureView.bitmap!!
                    val image = viewModel.resizeImage(bitmap)

                    detector.process(image)
                        .addOnSuccessListener { faces ->
                            if(faces != null) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    isOnProgress = true
                                    viewModel.send(bitmap)
                                }
                            }
                        }
                }
            }
        }

        viewModel.recognitionCommand.observe(this){
            isOnProgress = false
            when (it.statusCode) {
                -1 -> {
                    cameraState = false
                }
                0 -> {
                    unknownBottomSheetDialog.show()
                    stopRepeatingTask()
                    cameraState = false
                }
                1 -> {
                    cameraState = true
                    stopRepeatingTask()
                    nameTextView.text =
                        "${it.response.contact.firstname} ${it.response.contact.lastname}"
                    organizationNameTextView.text = it.response.contact.contactCompany
                    emailTextView.text = it.response.contact.email1
                    alternateEmailTextView.text = it.response.contact.email2
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
                        this@RealtimeCameraActivity,
                        "ERROR: Invalid status code",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        adjustAspectRatio(width,height)

        cameraManager.openCamera(cameraManager.cameraIdList[0],
            object: CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0

                val surfaceTexture = textureView.surfaceTexture
                val surface = Surface(surfaceTexture)
                val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface),
                    object : CameraCaptureSession.StateCallback(){
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(), null, null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                        TODO("Not yet implemented")
                    }
                },handler)
            }
            override fun onDisconnected(p0: CameraDevice) {}
            override fun onError(p0: CameraDevice, p1: Int) {}
        },handler)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
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
                // do something if permission is allowed
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    @SuppressLint("InflateParams")
    private fun initializeBottomSheet(){
        // Unknown customer bottom sheet
        val unknownBottomSheetView: View = layoutInflater.inflate(R.layout.bottomsheet_unknown, null)
        unknownBottomSheetDialog = BottomSheetDialog(this@RealtimeCameraActivity)
        unknownBottomSheetDialog.setContentView(unknownBottomSheetView)
        val unknownBottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(unknownBottomSheetView.parent as View)
        unknownBottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)

        // Customer information bottom sheet
        bottomSheetDialog = BottomSheetDialog(this@RealtimeCameraActivity)
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
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)
    }

    private fun adjustAspectRatio(videoWidth: Int, videoHeight: Int) {
        val viewWidth: Int = textureView.width
        val viewHeight: Int = textureView.height
        val aspectRatio = videoHeight.toDouble() / videoWidth
        val newWidth: Int
        val newHeight: Int
        if (viewHeight > (viewWidth * aspectRatio).toInt()) {
            // limited by narrow width; restrict height
            newWidth = viewWidth
            newHeight = (viewWidth * aspectRatio).toInt()
        } else {
            // limited by short height; restrict width
            newWidth = (viewHeight / aspectRatio).toInt()
            newHeight = viewHeight
        }
        val xOff = (viewWidth - newWidth) / 2
        val yOff = (viewHeight - newHeight) / 2

        val matrix = Matrix()
        matrix.setScale((newWidth.toFloat() / viewWidth)+0.8f, (newHeight.toFloat() / viewHeight))
        matrix.postTranslate(xOff.toFloat(), yOff.toFloat())
        textureView.setTransform(matrix)
    }

    private var bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetDialog.cancel()
                unknownBottomSheetDialog.cancel()
                startRepeatingTask()
                isOnProgress = false
                isDetectorOpen = true
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private var statusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                isDetectorOpen = true
            }finally {
                delayHandler.postDelayed(this, mInterval)
            }
        }
    }

    private fun startRepeatingTask(){
        statusChecker.run()
    }

    private fun stopRepeatingTask(){
        delayHandler.removeCallbacks(statusChecker)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
    }
}