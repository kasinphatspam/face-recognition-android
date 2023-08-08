package com.gura.face_recognition_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.gura.face_recognition_app.model.FaceRecognitionResponse
import com.gura.face_recognition_app.service.RecognitionService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket


class RealtimeCameraActivity : AppCompatActivity() {

    private lateinit var cameraDevice: CameraDevice
    private lateinit var handler: Handler
    private lateinit var textureView: TextureView
    private lateinit var cameraManager: CameraManager
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var webSocket: WebSocket
    private lateinit var faceDetector: FaceDetector
    private var recognitionStatus: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime_camera)

        // hide the action bar
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Check camera permissions if all permission granted
        // start camera else ask for the permission
        if (allPermissionsGranted()) {
            connect()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .enableTracking()
            .build()

        faceDetector = FaceDetection.getClient(highAccuracyOpts)
        val recognitionService = RecognitionService(this@RealtimeCameraActivity)

        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        textureView = findViewById(R.id.cameraView)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {

            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                if(!recognitionStatus){
                    val bitmap = textureView.bitmap
                    val inputImage = InputImage.fromBitmap(bitmap!!,0)
                    faceDetector.process(inputImage).addOnSuccessListener {
                        if(it.isNotEmpty()){
                            recognitionStatus = true
                            val base64 = recognitionService.convertImageToBase64(bitmap)
                            recognitionService.startFaceRecognition(base64,object: RecognitionService.RecognitionInterface{
                                override fun onCompleted(faceRecognitionResponse: FaceRecognitionResponse) {
                                    Log.e("FaceRecognition",faceRecognitionResponse.name.toString())
                                    recognitionStatus = false
                                }
                            })
                        }
                    }
                }
            }

        }

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        // Initialize BottomSheet component
        val bottomSheetView: View = layoutInflater.inflate(R.layout.bottomsheet_result, null)
        bottomSheetDialog = BottomSheetDialog(this@RealtimeCameraActivity)
        bottomSheetDialog.setContentView(bottomSheetView)
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheetView.parent as View)
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        updateTextureViewSize(width, width)

        cameraManager.openCamera(cameraManager.cameraIdList[0], object: CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0

                val surfaceTexture = textureView.surfaceTexture
                val surface = Surface(surfaceTexture)
                val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface),object : CameraCaptureSession.StateCallback(){
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(), null, null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                        TODO("Not yet implemented")
                    }

                },handler)

            }

            override fun onDisconnected(p0: CameraDevice) {

            }

            override fun onError(p0: CameraDevice, p1: Int) {

            }

        },handler)
    }

    private fun updateTextureViewSize(viewWidth: Int, viewHeight: Int) {
        textureView.layoutParams = FrameLayout.LayoutParams(viewWidth, viewHeight)
    }

    private fun connect(){
        val client = OkHttpClient()
        val apiKey = "AJfpHEXVd3pxKjKAOwnSHm9L7j9Ysk1pc0XmDfkA"
        val request: Request = Request.Builder().url("https://a1a0-171-99-162-222.ngrok-free.app/").build()

        val listener = WebsocketListener()
        webSocket = client.newWebSocket(request,listener)
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
                // If permissions are not granted,
                // present a toast to notify the user that
                // the permissions were not granted.
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private var bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if(newState == BottomSheetBehavior.STATE_HIDDEN){
                bottomSheetDialog.cancel()
            }
        }
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }
}