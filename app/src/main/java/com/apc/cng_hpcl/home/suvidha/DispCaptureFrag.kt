package com.apc.cng_hpcl.home.suvidha

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.R
import com.apc.cng_hpcl.databinding.FragDispCaptureBinding
import com.apc.cng_hpcl.home.suvidha.ImageUpload.VolleyMultipartRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
@androidx.camera.core.ExperimentalGetImage
class DispCaptureFrag : Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private lateinit var binding: FragDispCaptureBinding
    private var type:Int=-1
    private var shift:Int=-1

    private  var qrRaw:String="-1"
    private lateinit var imageCapture: ImageCapture

    private lateinit var pd: ProgressDia

    private var isTorchOn = false
    private lateinit var cameraProvider: ProcessCameraProvider
    private var latitude: String? = ""
    private var longitude: String? = ""
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    var rcpt: Bitmap? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragDispCaptureBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        val args= DispCaptureFragArgs.fromBundle(requireArguments())
        type=args.type
        shift=args.dispType
        if(type==1){
            requireActivity().title="Dispenser Reading"
            binding.rg.visibility=View.GONE
            binding.drawLL.visibility=View.VISIBLE
            binding.drawLL2.visibility=View.GONE
        }
        else if(type==2){
            requireActivity().title="Before Price Update"
            binding.rg.visibility=View.GONE
            binding.drawLL.visibility=View.GONE
            binding.drawLL2.visibility=View.VISIBLE

          /*  binding.rg.setOnCheckedChangeListener { radioGroup, i ->

                if(type==2){
                    if(radioGroup.checkedRadioButtonId==R.id.befRb){
                        type=21
                    }
                    else if(radioGroup.checkedRadioButtonId==R.id.afterRb){
                        type=22
                    }
                }



            }*/
        }
        if(type==3){
            binding.rg.visibility=View.GONE
            binding.drawLL.visibility=View.GONE
            binding.drawLL2.visibility=View.VISIBLE
            requireActivity().title="After Price Update"
        }
        pd= ProgressDia()
        binding.capBt.isEnabled=false



        //   type = intent.getIntExtra("type", -1)
        //   getAllPermissions();
        binding.pb.visibility = View.GONE
     //   binding.caNumber.setText(vm.conData.value?.CONSUMER_NO.toString())


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        cameraProviderFuture = ProcessCameraProvider.getInstance(mContext)
        //   lastLocation
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()

            //lastLocation
            cameraProviderFuture.addListener(Runnable {
                cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            }, ContextCompat.getMainExecutor(mContext))
            //   bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(mContext))


        binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE


    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        //
        val preview1: Preview = Preview.Builder().build()
        imageCapture =
            ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setFlashMode(ImageCapture.FLASH_MODE_OFF).build()


        val imageAnalyzer = ImageAnalysis.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
            .also {
                it.setAnalyzer(getExecutor(), QRCodeAnalyzer { qrCodes ->
                    // Process detected QR codes here
                    //    for (qrCode in qrCodes) {
                    if(qrCodes.isNotEmpty()){
                        val qrCode=qrCodes[0]
                        val value = qrCode.rawValue!!
                        if(qrRaw=="-1"){
                            qrRaw=value
                            try {
                                Log.d("QR>>", "bindPreview: $value")
                                val json=JSONObject(value)
                                var stat=json.getString("stat")
                                try {
                                     stat=json.getString("name")

                                }
                                catch (e:Exception){
                                    e.printStackTrace()
                                }

                                val disp= json.get("disp")
                                val nozzle=  json.get("nozzle")
                                val dispType=json.getString("disp_make")
                                binding.capBt.isEnabled=true
                                blinkButton()
                                binding.capBt.setOnClickListener {


                                    val ca="$stat.$disp.$nozzle"
                                    capturePhoto(ca,dispType)
                                }
                            } catch (e: Exception) {
                                Toast.makeText(mContext,e.toString(),Toast.LENGTH_LONG).show()

                            }
                        }
                        else{
                            if(qrRaw!=value){
                                binding.capBt.isEnabled=false
                                Toast.makeText(mContext,"Invalid Location !",Toast.LENGTH_LONG).show()

                            }
                            else{
                                try {
                                    Log.d("QR>>", "bindPreview: $value")
                                    val json=JSONObject(value)
                                    val stat=   json.getString("stat")
                                    val disp= json.get("disp")
                                    val nozzle=  json.get("nozzle")
                                    val dispType=json.getString("disp_make")
                                    binding.capBt.isEnabled=true
                                    blinkButton()
                                    binding.capBt.setOnClickListener {
                                        val ca="$stat.$disp.$nozzle"
                                        capturePhoto(ca, dispType)
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(mContext,e.toString(),Toast.LENGTH_LONG).show()

                                }
                            }
                        }

                    }

                    // Handle the QR code data (e.g., display or use it)
                    // }
                })
            }
        val cameraSelector1: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        // val cameraControl = getCameraControl(lensFacing)
        preview1.setSurfaceProvider(binding.previewView.surfaceProvider)

        val camera =
            cameraProvider.bindToLifecycle(
                this, cameraSelector1, preview1, imageCapture,imageAnalyzer
            )







        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                camera.cameraControl.setLinearZoom(progress / 100.toFloat())

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.seekBar.afterMeasured {
            val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1f, 1f).createPoint(.5f, .5f)
            try {

                val autoFocusAction = FocusMeteringAction.Builder(
                    autoFocusPoint, FocusMeteringAction.FLAG_AF
                ).apply {
                    //start auto-focusing after 2 seconds
                    Log.d("FOCUS>>>>", "bindPreview: ")
                    setAutoCancelDuration(5, TimeUnit.SECONDS)
                }.build()
                camera.cameraControl.startFocusAndMetering(autoFocusAction)
            } catch (e: CameraInfoUnavailableException) {
                Log.d("ERROR", "cannot access camera", e)
            }
        }


        binding.torchFab.setOnClickListener(View.OnClickListener {
            if (camera.cameraInfo.hasFlashUnit()) {
                if (!isTorchOn) {
                    camera.cameraControl.enableTorch(true)
                    isTorchOn = true
                    binding. torchFab.setImageResource(R.drawable.ic_baseline_flashlight_off_24)
                } else {
                    camera.cameraControl.enableTorch(false)
                    isTorchOn = false
                    binding.torchFab.setImageResource(R.drawable.ic_baseline_flashlight_on_24)

                }

            }
        })


        //


    }


    //Location

    @get:SuppressLint("MissingPermission")
    private val lastLocation: Unit
        private get() {
            // check if permissions are given

            // check if location is enabled
            if (isLocationEnabled) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        Log.d("LAT>>>", location.latitude.toString())
                        Log.d("LONG>>>", location.longitude.toString())

                        //  latitudeTextView.setText(location.getLatitude() + "");
                        //longitTextView.setText(location.getLongitude() + "");
                    }
                }
            } else {
                Toast.makeText(mContext, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            //  latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            //longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    }


    // method to request for permissions

    // method to check
    // if location is enabled
    private val isLocationEnabled: Boolean
        private get() {
            val locationManager = mContext.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

    private fun cropImage(bitmap: Bitmap, rootView: View, smallFrame: View): Bitmap {
        val heightOriginal = rootView.height
        val widthOriginal = rootView.width
        val heightFrame = smallFrame.height
        val widthFrame = smallFrame.width
        val leftFrame = smallFrame.left
        val topFrame = smallFrame.top
        val heightReal = bitmap.height
        val widthReal = bitmap.width
        val widthFinal = widthFrame * widthReal / widthOriginal
        val heightFinal = heightFrame * heightReal / heightOriginal
        val leftFinal = leftFrame * widthReal / widthOriginal
        val topFinal = topFrame * heightReal / heightOriginal
        Log.d("Crop>>>heightOriginal", heightOriginal.toString())
        Log.d("Crop>>>widthOriginal", widthOriginal.toString())
        Log.d("Crop>>>heightFrame", heightFrame.toString())
        Log.d("Crop>>>widthFrame", widthFrame.toString())
        Log.d("Crop>>>leftFrame", leftFrame.toString())
        Log.d("Crop>>>topFrame", topFrame.toString())
        Log.d("Crop>>>heightReal", heightReal.toString())
        Log.d("Crop>>>widthReal", widthReal.toString())
        Log.d("Crop>>>leftFinal", leftFinal.toString())
        Log.d("Crop>>>topFinal", topFinal.toString())
        Log.d("Crop>>>widthFinal", widthFinal.toString())
        Log.d("Crop>>>heightFinal", heightFinal.toString())

        /*   val stream = ByteArrayOutputStream()
        bitmapFinal.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            stream
        )*/ //100 is the best quality possibe
        return Bitmap.createBitmap(
            bitmap, leftFinal, topFinal - 100, widthFinal, heightFinal + 100
        )
    }

    fun saveImage(bp: Bitmap, txt: String): Bitmap {

        try {

            // NEWLY ADDED CODE STARTS HERE [
            val canvas = Canvas(bp)
            val paint = Paint()
            paint.color = Color.WHITE // Text Color
            paint.textSize = 12F // Text Size
            paint.xfermode =
                PorterDuffXfermode(PorterDuff.Mode.SRC_OVER) // Text Overlapping Pattern
            // some more settings...
            canvas.drawBitmap(bp, 0F, 0F, paint)
            canvas.drawText(txt, 10F, 10F, paint)
            val stream = ByteArrayOutputStream()
            bp.compress(
                Bitmap.CompressFormat.JPEG, 100, stream
            )
            // NEWLY ADDED CODE ENDS HERE ]
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return bp

    }

    private inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    f()
                }
            }
        })

    }



    private fun capturePhoto(ca:String,dispType:String) {
        binding.pb.visibility = View.VISIBLE
  //      binding.sub.isEnabled = false
        binding.capBt.isEnabled = false
        val timestamp = System.currentTimeMillis()
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        imageCapture.takePicture(ImageCapture.OutputFileOptions.Builder(
            mContext.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build(), getExecutor(), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                if (Build.VERSION.SDK_INT >= 28) {

                    val source =
                        ImageDecoder.createSource(mContext.contentResolver, outputFileResults.savedUri!!)
                    var bitmap = ImageDecoder.decodeBitmap(source)
                    bitmap = cropImage(bitmap, activity!!.window.decorView.rootView, if(type==1){
                        binding.drawLL
                    } else{
                        binding.drawLL2

                    })



                    ReportMultiPartRequest(
                        ca, "$latitude,$longitude", "Address", bitmap,dispType
                    )
                    Toast.makeText(
                        mContext,
                        "Photo has been saved successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(
                    mContext,
                    "Error saving photo: " + exception.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun getExecutor(): Executor {
        return ContextCompat.getMainExecutor(mContext)
    }
    private fun ReportMultiPartRequest(
        ca: String, latlng: String, address: String, bp: Bitmap, dispType: String
    ) {
        binding.pb.visibility = View.VISIBLE
        pd.show(childFragmentManager,"LOAD>>")
        binding.subBt.isEnabled = false
        binding.capBt.isEnabled = false
        val s: String
    if(type==1){
            if(dispType.equals("compaq",true)){
                s="https://www.cng-suvidha.in/dispenser/dispenser_api_v2.php"

            }
            else if(dispType.equals("compac",true)){
                s="https://www.cng-suvidha.in/dispenser/dispenser_api_v2.php"

            }

            else if(dispType.equals("tgt",true)){
                s="https://www.cng-suvidha.in/dispenser/dispenser_api_tgt.php"

            }
            else{
                s="https://www.cng-suvidha.in/dispenser/dispenser_api_parker.php"
            }

        }
        else{
            s="https://www.cng-suvidha.in/dispenser/dispenser_api_v3.php"
        }
        Log.d("TAG>>", "ReportMultiPartRequest: $s")



        //    cameraProvider.unbindAll()
        val volleyMultipartRequest: VolleyMultipartRequest =
            object : VolleyMultipartRequest(
                Request.Method.POST,
                s,
                Response.Listener<NetworkResponse> { response ->
                    pd.dismiss()
                    val res = String(response.data)
                    try{
                        Log.d("RESP>>", "ReportMultiPartRequestUrl: $s")

                        Log.d("RESP>>", "ReportMultiPartRequest: $res")
                        val json=JSONObject(res)
                        if (!json.getBoolean("error")){
                            val img=json.getString("url")
                            val reading=json.getString("meter_reading")
                            val action=
                       DispCaptureFragDirections.actionDispCaptureFragToDispCapOutputFrag(
                                    ca,
                                    img,
                                    reading,
                                    shift
                                )
                            action.lat=latlng
                            action.longi=latlng
                            action.address=address
                            action.type=type
                            action.dispType=shift
                            if(type!=2)
                            action.pid=
            DispCaptureFragArgs.fromBundle(requireArguments()).pid
                            navController.navigate(action)
                        }
                        else{
                            binding.pb.visibility=View.GONE
                            binding.subBt.isEnabled = true
                            binding.capBt.isEnabled = true

                            Toast.makeText(mContext,json.getString("meter_reading"),Toast.LENGTH_LONG).show()
                        }
                    }
                    catch (e:Exception){

                        val img=""
                        val reading="NA"
                        val action=
                            DispCaptureFragDirections.actionDispCaptureFragToDispCapOutputFrag(
                                ca,
                                img,
                                reading,
                                shift
                            )
                        action.lat=latlng
                        action.longi=latlng
                        action.address=address
                        action.type=type
                        action.dispType=shift
                        if(type!=2)
                            action.pid=
                                DispCaptureFragArgs.fromBundle(requireArguments()).pid
                        navController.navigate(action)

                        Toast.makeText(mContext,e.toString(),Toast.LENGTH_LONG).show()
                    }/*
                    {
                    "meter_reading":"01205852",
                    "meter_number":"0131005574",
                    "status":"success",
                    "error":false,
                    "status_code":"200"
                    }
*/
                    /*       if(type==4){
                               binding.pb.visibility = View.GONE
                               val res = String(response.data)
                               Log.d("MissionSlip>>>", res)
                               try {
                                   Log.d("LOC>>>>", "4")

                                   val json = JSONObject(res)
                                   val reading = json.getString("meter_reading")
                                   val img=json.getString("image_path")
                                   if (reading.isNotEmpty()) {

                                       val mDia = BomDia(
                                           bp,
                                           "Sequence Number : $ca",
                                           "Lat-Long : $latlng",
                                           reading,
                                           "Address(approx.) : $address",
                                           img
                                       )

                                       mDia.setStyle(
                                           DialogFragment.STYLE_NORMAL,
                                           R.style.DialogFragmentTheme
                                       )
                                       mDia.show(supportFragmentManager, "Dialog")
                                       mDia.dialog?.setOnDismissListener(DialogInterface.OnDismissListener {
                                           bindPreview(cameraProvider)
                                           cap.isEnabled=true


                                           *//*   finish()
                                       startActivity(
                                           Intent(
                                               this@MainActivity,
                                               MainActivity::class.java
                                           )
                                       )*//*

                                })
                            } else {

                                val btmFrag = AiErrorFrag(reading)
                                btmFrag.show(supportFragmentManager, "SHOW")

                            }


                        } catch (exp: Exception) {
                            val btmFrag = AiErrorFrag("Kindly retake the image !")
                            btmFrag.show(supportFragmentManager, "SHOW")
                        }
                    }
                    else {
                        binding. pb.visibility = View.GONE
                        val res = String(response.data)
                        Log.d("MissionSlip>>>", res)
                        try {
                            Log.d("LOC>>>>", "4")

                            val idx = res.indexOf("{")
                            val json = JSONObject(res.substring(idx, res.length))
                            val reading = json.getString("meter_reading")
                            val number = json.getString("meter_number")

                            val isReadingValid = reading.isDigitsOnly()
                            if (!isReadingValid) {
                                val btmFrag = AiErrorFrag(reading)
                                btmFrag.show(supportFragmentManager, "SHOW")
                            } else if (reading.isNotEmpty()) {

                                val mDia = RespDia(
                                    bp,
                                    "CA Number : $ca",
                                    "Lat-Long : $latlng",
                                    "Meter Reading($unit) : $reading",
                                    "Meter Number : $number",
                                    "Address(approx.) : $address"
                                )

                                mDia.setStyle(
                                    DialogFragment.STYLE_NORMAL,
                                    R.style.DialogFragmentTheme
                                )
                                mDia.show(supportFragmentManager, "Dialog")
                                mDia.dialog?.setOnDismissListener(DialogInterface.OnDismissListener {
                                    bindPreview(cameraProvider)
                                    cap.isEnabled=true
                                    *//*  finish()
                                        startActivity(
                                            Intent(
                                                this@MainActivity,
                                                MainActivity::class.java
                                            )
                                        )*//*
                                })
                            } else {

                                val btmFrag = AiErrorFrag(reading)
                                btmFrag.show(supportFragmentManager, "SHOW")

                            }


                        } catch (exp: Exception) {
                            val btmFrag = AiErrorFrag("Kindly retake the image !")
                            btmFrag.show(supportFragmentManager, "SHOW")
                        }
                    }*/

                },
                Response.ErrorListener { error -> // Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                    //     bindPreview(cameraProvider)
                    pd.dismiss()

                        Toast.makeText(
                            mContext,
                            error.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()




                        val img=""
                        val reading="NA"
                        val action=
                            DispCaptureFragDirections.actionDispCaptureFragToDispCapOutputFrag(
                                ca,
                                img,
                                reading,
                                shift
                            )
                        action.lat=latlng
                        action.longi=latlng
                        action.address=address
                        action.type=type
                        action.dispType=shift
                        if(type!=2)
                            action.pid=
                                DispCaptureFragArgs.fromBundle(requireArguments()).pid
                        navController.navigate(action)

                //    Log.d("RESP>>>err", error.toString())
               //     Log.d("RESP>>>err", error.networkResponse.statusCode.toString() + "")

                }) {
                override fun getParams(): MutableMap<String, String> {
                    val inputs: MutableMap<String, String> = HashMap()
                    /* params.put("case_id", caseId);inputs["api_token"] = token */
                    inputs["customer_ca_no"] = ca
                    inputs["customer_bp_no"] = ca
                    inputs["customer_lat_long"] = latlng
                    inputs["customer_meter_id"] = ca
                    inputs["meter_reading_manual"] = "date"
                    inputs["time"] = "ref_id"
                    inputs["date"] = "remark"
                    inputs["type"] = "amt"


                    //     inputs.put("payment_slip",imageToBase64(bp));
                    return inputs
                }
                //    String name=new String();
                //  params.put("payment_slip", new DataPart("img.png","/png",getFileDataFromDrawable(bp)));

                override fun getByteData(): MutableMap<String, DataPart> {
                    val params: MutableMap<String, DataPart> = HashMap<String, DataPart>()
                    //    String name=new String();
                    params["img"] = DataPart("img.jpg", "/jpg", bp)
                //    params["img1"] = DataPart("img.jpg", "/jpg", bp)

                    //       params["img"] = DataPart("test1232.jpg", inputData)

                    //  params.put("payment_slip", new DataPart("img.png","/png",getFileDataFromDrawable(bp)));
                    return params
                }

            }

        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        //adding the request to volley
        Volley.newRequestQueue(mContext).add(volleyMultipartRequest)
    }
private class QRCodeAnalyzer(private val onQRCodesDetected: (List<Barcode>) -> Unit) : ImageAnalysis.Analyzer {
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    private val scanner = BarcodeScanning.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    onQRCodesDetected(barcodes)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}
    private fun blinkButton(){
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 350 //You can manage the blinking time with this parameter
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
       binding.capBt.startAnimation(anim)
    }


}