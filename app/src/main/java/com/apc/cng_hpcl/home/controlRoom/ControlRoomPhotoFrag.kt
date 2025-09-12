package com.apc.cng_hpcl.home.controlRoom

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.BuildConfig.BASE_URL
import com.apc.cng_hpcl.ImageUpload.VolleyMultipartRequest
import com.apc.cng_hpcl.databinding.ControlRoomPhotoFragBinding
import com.apc.cng_hpcl.util.ManagePermissions
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ControlRoomPhotoFrag : Fragment() {
    var mCurrentPhotoPath: String? = null
    var req = -1
    private lateinit var imageCapture: ImageCapture
    private lateinit var verTv: TextView
    private lateinit var prevTv: TextView
    private var url = ""
    private var unit = ""

    private var isTorchOn = false
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var torchFab: FloatingActionButton
    private var latitude: String? = ""
    private var longitude: String? = ""
    private lateinit var  transId:String
    private lateinit var drawLL: LinearLayout
    private lateinit var ll2: LinearLayout
    private lateinit var username:String
    private lateinit var caEt: EditText
    private lateinit var args:ControlRoomPhotoFragArgs
    private var type = -1
    private lateinit var seekBar: SeekBar
    private lateinit var cons: ConstraintLayout
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    // Initializing other items
    // from layout file
    // from layout file
    var PERMISSION_ID = 44
    private val permissionsRequestCode = 123
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var managePermissions: ManagePermissions
    var someActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private lateinit var photoURI: Uri
    private lateinit var recIv: ImageView
    private lateinit var recTv: TextView
    private lateinit var previewView: PreviewView
    var rcpt: Bitmap? = null
    private lateinit var sub: Button
    private lateinit var cap: Button
    private val vm: ControlRoomViewModel by activityViewModels()


    private lateinit var pb: ProgressBar
    private lateinit var tempSpin: Spinner
    private lateinit var presSpin: Spinner
    private lateinit var lcv:String
    private lateinit var mgs:String
    private lateinit var dbs:String
    private lateinit var mContext:Context
    private lateinit var navController:NavController
    private lateinit var binding:ControlRoomPhotoFragBinding


    @RequiresApi(Build.VERSION_CODES.O)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= ControlRoomPhotoFragBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
            seekBar = binding.seekBar
            sub = binding.subBt
            cons = binding.cons
            pb = binding.pb
            cap = binding.capBt
            verTv = binding.verTv
            prevTv = binding.prevTv
            presSpin = binding.presSpin
            tempSpin = binding.tempSpin
           binding.vm = vm
           binding.lifecycleOwner = this


            ll2 = binding.linearLayout2
       //     verTv.text = "v" + BuildConfig.VERSION_NAME
            previewView = binding.previewView
            args= ControlRoomPhotoFragArgs.fromBundle(requireArguments())

            type = args.type


                //   getAllPermissions();
            pb.visibility = View.GONE
            drawLL = binding.drawLL
            recIv = binding.recieptIv
            recTv = binding.recieptTv
            caEt = binding.caNumber
            caEt.visibility=View.GONE
            binding.presSpin.visibility=View.GONE
             binding.tempSpin.visibility=View.GONE


        torchFab = binding.torchFab

        when (type) {




            1 -> {

            }
            2 -> {

            }

        }



            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            cameraProviderFuture = ProcessCameraProvider.getInstance(mContext)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()

       //     lastLocation
            cameraProviderFuture.addListener(Runnable {
                cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            }, ContextCompat.getMainExecutor(mContext))
            //   bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(mContext))




            previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE

       /*     sub.setOnClickListener {
                //  rcpt=previewView.bitmap
                rcpt = cropImage(previewView.bitmap!!, requireActivity().window.decorView.rootView, drawLL)
                val ca = caEt.text.toString()
                if (managePermissions.checkPermissions()) {
                    lastLocation
                }
                if (rcpt == null) {
                    Toast.makeText(mContext, "Select image !", Toast.LENGTH_LONG).show()
                } else if (ca.isEmpty()) {
                    caEt.error = "Enter"
                    Toast.makeText(mContext, "Enter  !", Toast.LENGTH_LONG).show()
                } else if (latitude.isNullOrEmpty()) {
                    Toast.makeText(
                        mContext, "Couldn't get location,Kindly Retry !", Toast.LENGTH_LONG
                    ).show()

                } else if (longitude.isNullOrEmpty()) {
                    Toast.makeText(
                        mContext, "Couldn't get location,Kindly Retry !", Toast.LENGTH_LONG
                    ).show()
                } else {
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                    val formatted = current.format(formatter)
                    try {
                        val geocoder = Geocoder(mContext, Locale.getDefault())
                        val addresses: List<Address> = geocoder.getFromLocation(
                            latitude!!.toDouble(), longitude!!.toDouble(), 1
                        ) as List<Address> // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                        val address: String =
                            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                        val city: String = addresses[0].getLocality()
                        val state: String = addresses[0].getAdminArea()
                        val country: String = addresses[0].getCountryName()
                        val postalCode: String = addresses[0].getPostalCode()
                        Log.d("LOC>>>>addSize", addresses.size.toString())
                        Log.d("LOC>>>>add", address)
                        Log.d("LOC>>>>city", city)
                        Log.d("LOC>>>>state", state)
                        Log.d("LOC>>>>coun", country)
                        Log.d("LOC>>>>postal", postalCode)
                        rcpt = saveImage(rcpt!!, formatted + "\n" + address)
                        ReportMultiPartRequest(
                            ca, "$latitude,$longitude", address, rcpt!!
                        )
                    } catch (err: Exception) {
                        Log.d("LOC>>>>", "1")
                        val s =
                            "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&sensor=true&key=AIzaSyCxugGqDXfKCV_iKWMla5D4vaA5BR9dCYQ"
                        val jsonArrayRequest: JsonObjectRequest = object :
                            JsonObjectRequest(Request.Method.GET,
                                s,
                                null,
                                object : Response.Listener<JSONObject?> {
                                    override fun onResponse(obj: JSONObject?) {
                                        Log.d("LOC>>>>", "2")

                                        val results = obj?.getJSONArray("results")
                                        val result1 = results?.getJSONObject(0)
                                        val address = result1?.getString("formatted_address")
                                        //
                                        if (address != null) {
                                            rcpt = saveImage(rcpt!!, formatted + "\n" + address)
                                            ReportMultiPartRequest(
                                                ca, "$latitude,$longitude", address, rcpt!!
                                            )
                                        }
                                    }

                                },
                                object : Response.ErrorListener {
                                    override fun onErrorResponse(error: VolleyError?) {
                                        Log.d("RESP>>>", error.toString())

                                        //  Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                                        if (error is NoConnectionError) {
                                        }
                                    }
                                }) {}
                        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
                            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                            2,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                        jsonArrayRequest.setShouldCache(false)
                        Volley.newRequestQueue(mContext).add(jsonArrayRequest)
                    }
                    //    val out = ByteArrayOutputStream()
                    //  rcpt!!.compress(Bitmap.CompressFormat.JPEG, 10, out);


                }


            }*/


    }

    private fun showImageChooser() {
        req = 200
        val galIntent = Intent()
        galIntent.type = "image/*"
        galIntent.action = Intent.ACTION_OPEN_DOCUMENT
        //  Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(mContext.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile(binding.previewView.bitmap!!)
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(
                    mContext, "com.apc.atgltest.provider", photoFile
                )
                Log.d("PHOTOURI>>>", photoURI.toString())
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
        }
        val chooser = Intent.createChooser(galIntent, "Choose....")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intent))
        someActivityResultLauncher!!.launch(chooser)

        //   startActivityForResult(chooser, CHOOSE_IMAGE);
    }









    @Throws(IOException::class)
    private fun createImageFile(bp:Bitmap): File {
        val sd: File = mContext.cacheDir
        val folder = File(sd, "/apc_kyc/")
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.e("ERROR", "Cannot create a directory!")
            } else {
                folder.mkdirs()
            }
        }
        var name="def"
        if(type==2){
            name="pressure.jpg";

        }
        else if(type==3){
            name="temperature.jpg"

        }
        val fileName= File(folder,name)
        try {
            val outputStream = FileOutputStream(java.lang.String.valueOf(fileName))
     //       var rcpt = cropImage(bp, binding.cons, binding.drawLL)
       //     rcpt=saveImage(rcpt,"CNG LUAG")
            bp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            mCurrentPhotoPath=fileName.absolutePath
        //    Toast.makeText(mContext,"Created,"+fileName.absolutePath,Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fileName


        /*
          val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
          val imageFileName = "PNG_" + timeStamp + "_"
          val storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
          val image = File.createTempFile(
              imageFileName,
              ".png",
              storageDir
          )

          mCurrentPhotoPath = image.absolutePath
          return image*/
    }


    companion object {
        const val REQUEST_TAKE_PHOTO = 1
        private const val CHOOSE_IMAGE = 101
        fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
            if (context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context, permission!!
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        //
        val preview1: Preview = Preview.Builder().build()
        imageCapture =
            ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setFlashMode(ImageCapture.FLASH_MODE_OFF).build()

        sub.setOnClickListener {


                capturePhoto()



        }

        val cameraSelector1: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        // val cameraControl = getCameraControl(lensFacing)
        preview1.setSurfaceProvider(previewView.surfaceProvider)

        val camera = cameraProvider.bindToLifecycle(
            this, cameraSelector1, preview1, imageCapture
        )


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                camera.cameraControl.setLinearZoom(progress / 100.toFloat())

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        previewView.afterMeasured {
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


        torchFab.setOnClickListener(View.OnClickListener {
            if (camera.cameraInfo.hasFlashUnit()) {
                if (!isTorchOn) {
                    camera.cameraControl.enableTorch(true)
                    isTorchOn = true
                    torchFab.setImageResource(com.apc.cng_hpcl.R.drawable.ic_baseline_flashlight_off_24)
                } else {
                    camera.cameraControl.enableTorch(false)
                    isTorchOn = false
                    torchFab.setImageResource(com.apc.cng_hpcl.R.drawable.ic_baseline_flashlight_on_24)

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
                mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
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
        mFusedLocationClient!!.requestLocationUpdates(
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
        } catch (e: Exception) {
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

    override fun onPause() {
        super.onPause()
        //    cameraProvider.unbindAll()

    }

    override fun onResume() {
        super.onResume()
//        bindPreview(cameraProvider)

    }

    private fun capturePhoto() {
        pb.visibility = View.VISIBLE
        sub.isEnabled = false
        cap.isEnabled = false
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
                    bitmap = cropImage(bitmap, requireActivity().window.decorView.rootView, drawLL)
                    createImageFile(bitmap)
                    if(type==4){
                        UploadImageRequest("MFM"+System.currentTimeMillis()+".jpg","$latitude,$longitude", "Address", bitmap
                            ,args)

                    }
                    else{
                            UploadImageRequest("ticket"+ caEt.text.toString()+".jpg", "$latitude,$longitude", "Address", bitmap,args)



                    }

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
    private fun UploadImageRequest(
        name: String, latlng: String, address: String, bp: Bitmap,args:ControlRoomPhotoFragArgs
    ) {
        Log.d("name>>", "NAME$name")

        pb.visibility = View.VISIBLE
        sub.isEnabled = false
     //   val action= DispPhotoFragDirections.actionDispPhotoFragToDispReadFrag(sta,username)
        cap.isEnabled = false
        cameraProvider.unbindAll()
        val action=ControlRoomPhotoFragDirections.actionControlRoomPhotoFragToControlRoomLcvFrag(type,vm.stationId.value,vm.lcvNum.value,vm.latlng.value)
        action.type=args.type
        val volleyMultipartRequest: VolleyMultipartRequest =
            object : VolleyMultipartRequest(
                Request.Method.POST,
                BASE_URL+"v2/cng_transaction_api_v2.php?apicall=uploadImage",
                Response.Listener<NetworkResponse> { response ->

                    pb.visibility = View.GONE
                    val res = String(response.data)
                    Log.d("MissionSlip>>>", res)
                    try {
                        val json=JSONObject(res)
                        val arr=json.getJSONArray("image_resposne")
                        val  json2=arr.getJSONObject(0).getJSONObject("data")
                        action.type=type

                        when (type) {
                            1 ->
                            {
                                vm.lcvPressureImgPath.value=mCurrentPhotoPath
                                vm.lcvPressureImgUrl.value=json2.getString("filelocation")
                            }
                            2 ->
                            {
                                vm.stationPressureImgPath.value=mCurrentPhotoPath
                                vm.stationPressureImgUrl.value=json2.getString("filelocation")
                            }




                        }
                        navController.navigate(action)


                    } catch (exp: Exception) {
                        exp.printStackTrace()
                        Toast.makeText(mContext,"Invalid gauge image !",Toast.LENGTH_LONG).show()
                    //    navController.navigate(action)

                    }


                },
                Response.ErrorListener { error -> // Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                    bindPreview(cameraProvider)
                    Toast.makeText(mContext,"Invalid gauge image !",Toast.LENGTH_LONG).show()
                //    navController.navigate(action)
                    if (error is NoConnectionError) {
                        Toast.makeText(
                            mContext,
                            "No Internet Available !",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    //     Log.d("RESP>>>err", error.toString())
                    //    Log.d("RESP>>>err", error.networkResponse.statusCode.toString() + "")
                    try {
                        val resBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                        Log.d("RESP>>>err", resBody)
                        val data = JSONObject(resBody)
                        Log.d("RESP>>>err", data.toString())
                        val message = data.getString("message")
                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("RESP>>>err>>err", e.toString())
                    }
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val inputs: MutableMap<String, String> = HashMap()
                    /* params.put("case_id", caseId);inputs["api_token"] = token */


                    //     inputs.put("payment_slip",imageToBase64(bp));
                    return inputs
                }
                //    String name=new String();
                //  params.put("payment_slip", new DataPart("img.png","/png",getFileDataFromDrawable(bp)));

                override fun getByteData(): MutableMap<String, DataPart> {
                    val params: MutableMap<String, DataPart> = HashMap<String, DataPart>()
                    //    String name=new String();
                    params["image"] = DataPart(name, "image/jpg", bp)
                    //       params["img"] = DataPart("test1232.jpg", inputData)

                    //  params.put("payment_slip", new DataPart("img.png","/png",getFileDataFromDrawable(bp)));
                    return params
                }

            }
        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        //adding the request to volley
        Volley.newRequestQueue(mContext).add(volleyMultipartRequest)
    }
}