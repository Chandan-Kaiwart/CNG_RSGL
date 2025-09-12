package com.apc.cng_hpcl.home.newTrans
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.BuildConfig.BASE_URL
import com.apc.cng_hpcl.ImageUpload.VolleyMultipartRequest
import com.apc.cng_hpcl.databinding.TransReadingFragBinding
import com.apc.cng_hpcl.home.newDisp.DispPhotoFragDirections
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling
import com.apc.cng_hpcl.ui.GailActivity
import com.apc.cng_hpcl.util.Constant
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*


@AndroidEntryPoint
class ReadingFrag : Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private lateinit var binding: TransReadingFragBinding
    private lateinit var heading: TextView
    private val vm: TransViewModel by activityViewModels()
    private var isTempTaken = false
    private var isPressTaken = false
    private var isHpTaken = false
    private var isMpTaken = false
    private var isLpTaken = false
    private var isMfmTaken = false
    private var tempGaugeMake: String = ""
    private var presGaugeMake: String = ""
    private var volume: Int = 0
    private var volumeLp: Int = 0
    private var volumeMp: Int = 0
    private var volumeHp: Int = 0
    private val baseUrl = BASE_URL+"v2/cng_transaction_api_v2.php?apicall="
    private val urlCreateTransMgs: String = baseUrl + "insertMGS"
    private val urlTransBeforeFilling: String = baseUrl + "updateMGS1"
    private val urlTransAfterFilling: String = baseUrl + "updateMGS2"
    private val urlCreateTransDbs: String = baseUrl + "insertDBS"
    private val urlTransBeforeEmptying: String = baseUrl + "updateDBS1"
    private val urlTransAfterEmptying: String = baseUrl + "updateDBS2"
    private val notifyMgr: String = baseUrl + "notify1"
    private lateinit var transId: String
    private lateinit var lcv_num: String
    private lateinit var mgs_id: String
    private lateinit var dbs_id: String
    private lateinit var presUrl: String
    private lateinit var opId: String
    private lateinit var tempUrl: String
    private var lpUrl: String = ""
    private var hpUrl: String = ""
    private var mpUrl: String = ""
    private var mfmUrl: String = ""
    private var tempValue = "0"
    private var pressValue = "0"
    private var lpValue = "0"
    private var mpValue = "0"
    private var hpValue = "0"
    private var prevMass = "0"


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TransReadingFragBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        heading = binding.stageTv
        val args = ReadingFragArgs.fromBundle(requireArguments())
        opId = args.opId
        val lcv = args.lcv
        transId = args.transId
        mgs_id = args.mgsId
        lcv_num = args.lcv
        dbs_id = args.dbsId
        binding.mfmCard.visibility=View.GONE
        binding.lcvTv.text = lcv
        tempUrl = ""
        presUrl = ""
        binding.vm = vm
        binding.lifecycleOwner = this
        binding.stateCard.visibility = View.GONE
     //   binding.pressCard.visibility = View.GONE
        binding.optCard.visibility = View.GONE



        binding.pressCard.setOnClickListener {
            val action = ReadingFragDirections.actionReadingFragToPhotoFrag(
                opId,
                lcv,
                transId,
                mgs_id,
                dbs_id,
                presGaugeMake
            )
            action.type = 2
            action.stage = args.stage
            action.gaugeMake=presGaugeMake
            navController.navigate(action)
        }
        binding.tempCard.setOnClickListener {
            val action = ReadingFragDirections.actionReadingFragToPhotoFrag(
                opId,
                lcv,
                transId,
                mgs_id,
                dbs_id,
                tempGaugeMake
            )
            action.type = 3
            action.stage = args.stage

            navController.navigate(action)
        }
        if(args.stage==2){
            binding.mfmCard.visibility=View.VISIBLE
        }
        binding.lpCard.setOnClickListener {
            val action = ReadingFragDirections.actionReadingFragToPhotoFrag(
                opId,
                lcv,
                transId,
                mgs_id,
                dbs_id,
                presGaugeMake
            )
            action.type = 21
            action.stage = args.stage
            navController.navigate(action)
        }
        binding.mpCard.setOnClickListener {
            val action = ReadingFragDirections.actionReadingFragToPhotoFrag(
                opId,
                lcv,
                transId,
                mgs_id,
                dbs_id,
                presGaugeMake
            )
            action.type = 22
            action.stage = args.stage
            navController.navigate(action)
        }
        binding.hpCard.setOnClickListener {
            val action = ReadingFragDirections.actionReadingFragToPhotoFrag(
                opId,
                lcv,
                transId,
                mgs_id,
                dbs_id,
                presGaugeMake
            )
            action.type = 23
            action.stage = args.stage
            navController.navigate(action)
        }
        binding.mfm.setOnClickListener {
            val action = ReadingFragDirections.actionReadingFragToPhotoFrag(
                opId,
                lcv,
                transId,
                mgs_id,
                dbs_id,
                presGaugeMake
            )
            action.type = 4
            action.stage = args.stage
            navController.navigate(action)
        }
        readVolume(lcv)
        // Access SharedPreferences

        readTrans(transId, args.stage)
        vm.tempValue.observe(viewLifecycleOwner, Observer { it1 ->
            //your code here


            calcMassMultiple()

        })
        vm.presValue.observe(viewLifecycleOwner, Observer { it1 ->
            //your code here


            calcMassMultiple()

        })
        vm.lpValue.observe(viewLifecycleOwner, Observer { it2 ->
            //your code here

            calcMassMultiple()

        })
        vm.mpValue.observe(viewLifecycleOwner, Observer { it3 ->
            //your code here


            calcMassMultiple()
        })
        vm.hpValue.observe(viewLifecycleOwner, Observer { it4 ->
            //your code here


            calcMassMultiple()
        })
        vm.volLp.observe(viewLifecycleOwner, Observer {
            volumeLp = it.toFloat().toInt()
        })
        vm.volMp.observe(viewLifecycleOwner, Observer {
            volumeMp = it.toFloat().toInt()

        })
        vm.volHp.observe(viewLifecycleOwner, Observer {
            volumeHp = it.toFloat().toInt()
            calcMassMultiple()


        })
        binding.manCard.visibility = View.GONE
        binding.manualTemp.setText("")
        binding.manualPressure.setText("")
        binding.manualLp.setText("")
        binding.manualMp.setText("")
        binding.manualHp.setText("")
        binding.manualMfm.setText("")


//        Calculate Mass of Cascade before filling

        when (args.stage) {
            -1 -> {
                heading.text = "Stage 0"


            }
            1 -> {
                val text = "$mgs_id पर भरने से पहले "
                //     heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_mother_gas_station_before_filling)
                heading.text = text
            }
            2 -> {
                val text = "$mgs_id पर भरने से  बाद "

                //   heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_mother_gas_station_after_filling)
                heading.text = text

            }
            4 -> {
                val text = "$dbs_id पर खाली होने से पहले "
                // heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_before_emptying)
                heading.text = text

            }
            5 -> {
                val text = "$dbs_id पर खाली होने से बाद "
                //  heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_after_emptying)
                heading.text = text

            }
            7 -> {
                val text = "$dbs_id पर खाली होने से पहले "
                // heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_before_emptying)
                heading.text = text

            }
            8 -> {
                val text = "$dbs_id पर खाली होने से बाद "
                //  heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_after_emptying)
                heading.text = text

            }

        }
        binding.updateBt.setOnClickListener {
            if (tempValue == "0") {
                Toast.makeText(mContext, "Please input temperature !", Toast.LENGTH_LONG).show()
            } else if (pressValue == "0") {
                Toast.makeText(mContext, "Please input pressure !", Toast.LENGTH_LONG).show()
            }  else {
                when (args.stage) {
                    -1 -> {

                    }
                    1 -> {
                        notify1msg((args.stage + 1).toString())
                        update1Data(pressValue,tempValue, opId)

                     //   insertTransMgs(pressValue, tempValue, opId)
                    }
                    2 -> {
                        notify2msg((args.stage + 1).toString())
                        update2Data(pressValue, tempValue, opId)
                    }
                    3 -> {

                        //  heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_before_emptying)

                    }
                    4 -> {
                        notify3msg((args.stage + 1).toString())
                        insert3Data(
                            binding.manualPressure.text.toString().trim(),
                            binding.manualTemp.text.toString().trim(),
                            opId
                        )
                        //     heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_after_emptying)

                    }
                    5 -> {
                        notify4msg((args.stage + 1).toString())
                        insert4Data(
                            binding.manualPressure.text.toString().trim(),
                            binding.manualTemp.text.toString().trim(),
                            opId
                        )
                        //     heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_after_emptying)

                    }
                    7 -> {
                        notify3msg((args.stage + 1).toString())
                        insert5Data(
                            binding.manualPressure.text.toString().trim(),
                            binding.manualTemp.text.toString().trim(),
                            opId
                        )
                        //     heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_after_emptying)

                    }
                    8 -> {
                        notify4msg((args.stage + 1).toString())
                        insert6Data(
                            binding.manualPressure.text.toString().trim(),
                            binding.manualTemp.text.toString().trim(),
                            opId
                        )
                        //     heading.text=resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_after_emptying)

                    }

                }

            }


        }


        if (!vm.presPath.value.isNullOrEmpty()) {
            Log.d("type>>21", "onViewCreated: ")
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.presPath.value, bmOptions)

            binding.pressureGuage.setImageBitmap(bp)
            // vm.lpValue.value= it.value
            //binding.manualPressure.setText(it.value)
            isPressTaken = true
            presUrl = vm.presUrl.value.toString()

        }
        if (!vm.mpPath.value.isNullOrEmpty()) {
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.mpPath.value, bmOptions)
            binding.mpGuage.setImageBitmap(bp)
            //     binding.manualPressure.setText(it.value)
            isMpTaken = true
            mpUrl = vm.mpUrl.value.toString()

        }
        if (!vm.hpPath.value.isNullOrEmpty()) {
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.mpPath.value, bmOptions)
            binding.hpGuage.setImageBitmap(bp)
            //     binding.manualPressure.setText(it.value)
            isHpTaken = true
            hpUrl = vm.hpUrl.value.toString()

        }
        if (!vm.tempPath.value.isNullOrEmpty()) {
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.tempPath.value, bmOptions)
            binding.tempGuage.setImageBitmap(bp)
            //   binding.manualTemp.setText(it.value)
            isTempTaken = true
            tempUrl = vm.tempUrl.value.toString()

        }
        if (!vm.mfmPath.value.isNullOrEmpty()) {
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.mfmPath.value, bmOptions)
            binding.mfm.setImageBitmap(bp)
            //     binding.manualPressure.setText(it.value)
            isMfmTaken = true
            mfmUrl = vm.mfmUrl.value.toString()

        }
        if (!vm.lpPath.value.isNullOrEmpty()) {
            Log.d("type>>21", "onViewCreated: ")
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.lpPath.value, bmOptions)

            binding.lpGuage.setImageBitmap(bp)
            // vm.lpValue.value= it.value
            //binding.manualPressure.setText(it.value)
            isLpTaken = true
            lpUrl = vm.lpUrl.value.toString()

        }
        if (!vm.mpPath.value.isNullOrEmpty()) {
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.mpPath.value, bmOptions)
            binding.mpGuage.setImageBitmap(bp)
            //     binding.manualPressure.setText(it.value)
            isMpTaken = true
            mpUrl = vm.mpUrl.value.toString()

        }
        if (!vm.hpPath.value.isNullOrEmpty()) {
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.mpPath.value, bmOptions)
            binding.hpGuage.setImageBitmap(bp)
            //     binding.manualPressure.setText(it.value)
            isHpTaken = true
            hpUrl = vm.hpUrl.value.toString()

        }
        if (!vm.tempPath.value.isNullOrEmpty()) {
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.tempPath.value, bmOptions)
            binding.tempGuage.setImageBitmap(bp)
            //   binding.manualTemp.setText(it.value)
            isTempTaken = true
            tempUrl = vm.tempUrl.value.toString()

        }
        if (!vm.mfmPath.value.isNullOrEmpty()) {
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.mfmPath.value, bmOptions)
            binding.mfm.setImageBitmap(bp)
            //     binding.manualPressure.setText(it.value)
            isMfmTaken = true
            mfmUrl = vm.mfmUrl.value.toString()

        }



    }
    private fun readTemp(latLng:String) {
       val  lat = latLng.split(",")[0]
        val  long = latLng.split(",")[1]
        val queue = Volley.newRequestQueue(mContext)
        val request: StringRequest = object : StringRequest(
            Method.GET, "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$long&current=temperature_2m&timezone=IST",
            Response.Listener { response ->
                try {
                    Log.e(ContentValues.TAG, "Response = $response")
                    val jsonObject = JSONObject(response)
                    val jsonObject2=jsonObject.getJSONObject("current")
                    var temp=jsonObject2.getString("temperature_2m")
                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
                    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                    if(hourOfDay in 9..17){
                       temp=(temp.toFloat()+6).toString()
                    }
                    vm.tempValue.value=temp

                } catch (e: Exception) {
                    e.printStackTrace()
                    //   volume=0;
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    mContext,
                    "Failed to get data$error",
                    Toast.LENGTH_SHORT
                ).show()
            }) {


            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()

                return params
            }
        }
        queue.add(request)
    }

    private fun readVolume(lcv_num:String) {
        val queue = Volley.newRequestQueue(mContext)
        val request: StringRequest = object : StringRequest(
            Method.POST, MGS_Before_Filling.URL_READ_VOLUME,
            Response.Listener { response ->
                try {
                    Log.e(ContentValues.TAG, "Response = $response")
                    val jsonObject = JSONObject(response)
                    try {
                        if(jsonObject.getBoolean("error")){
                            Toast.makeText(mContext,jsonObject.getString("message"),Toast.LENGTH_LONG).show()
                            requireActivity().finish()
                        }
                    } catch (e: Exception) {
                       e.printStackTrace()
                    }
//                    jsonObject.getString("Lcv_Num")
                    val vol = jsonObject.getString("Cascade_Capacity")
                    val volLp = jsonObject.getString("casc_cap_lp")
                    val volMp = jsonObject.getString("casc_cap_mp")
                    val volHp = jsonObject.getString("casc_cap_hp")
                    try {
                        val isHCV=jsonObject.getString("vehicle_type").equals("HCV",ignoreCase = true)
                        if(isHCV){
                            val sharedPreferences = requireContext().getSharedPreferences("login", Context.MODE_PRIVATE)

                            // Retrieve data from SharedPreferences
                            val latlng = sharedPreferences.getString("latlng", "25.317644,82.973915")
                            readTemp(latlng.toString())
                          //  binding.manualTemp.setText("30")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    //        vm.tempMake.value=jsonObject.getString("temp_make").substring(1,2).toInt()
              //      vm.lpMake.value=jsonObject.getString("lp_make").substring(1,2).toInt()
               //     vm.mpMake.value=jsonObject.getString("mp_make").substring(1,2).toInt()
               //     vm.hpMake.value=jsonObject.getString("hp_make").substring(1,2).toInt()

                    tempGaugeMake=  jsonObject.getString("temperature_gauge_make")
                    presGaugeMake = jsonObject.getString("high_pressure_gauge_make")
                    volume = vol.toFloat().toInt()
                    volumeLp = volLp.toFloat().toInt()
                    volumeMp = volMp.toFloat().toInt()
                    volumeHp = volHp.toFloat().toInt()
                    vm.vol.value=vol
                    vm.volLp.value = volLp
                    vm.volMp.value = volMp
                    vm.volHp.value = volHp
                } catch (e: Exception) {
                    e.printStackTrace()
                    //   volume=0;
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    mContext,
                    "Failed to get data$error",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["lcv_id"] = lcv_num
                //                params.put("station_id",mgsId);
//                params.put("dbs_station_id", dbsId);
//                params.put("lcv_from_mgs_to_dbs", s);
                Log.d("LCV>>", params.toString())
                return params
            }
        }
        queue.add(request)
    }
    private fun readTrans(trans_id:String,stage:Int) {
        val queue = Volley.newRequestQueue(mContext)
        val request: StringRequest = object : StringRequest(
            Method.POST, BASE_URL+"v2/cng_transaction_api_v2.php?apicall=readTransactionDetails",
            Response.Listener { response ->
                try {
                    Log.e("Trans>>>", response)
                    val jsonObject = JSONObject(response)
                    if(!jsonObject.getBoolean("error")){
                        val gson= Gson()
                        val json=jsonObject.getJSONObject("data")
                        val data=gson.fromJson(json.toString(),TransDataModel::class.java)
                        if(stage==2){
                            prevMass=data.before_filing_at_mgs_mass_cng
                            binding.prevLL.visibility=View.VISIBLE
                            binding.prevMass.text=prevMass
                            binding.diffLL.visibility=View.VISIBLE
                            calcMassMultiple()

                        }
                        else if(stage==5){
                            prevMass=data.before_empty_at_db_mass_cng
                            binding.prevLL.visibility=View.VISIBLE
                            binding.diffLL.visibility=View.VISIBLE
                            binding.prevMass.text=prevMass
                            calcMassMultiple()
                        }
                        else if(stage==8){
                            prevMass=data.before_empty_at_secondary_dbs_mass_cng
                            binding.prevLL.visibility=View.VISIBLE
                            binding.diffLL.visibility=View.VISIBLE
                            binding.prevMass.text=prevMass
                            calcMassMultiple()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    //   volume=0;
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    mContext,
                    "Failed to get data$error",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                if(trans_id[0]=='n'){
                    params["transaction_id"] = trans_id.substring(2,trans_id.length)

                }
                else{
                    params["transaction_id"] = trans_id

                }
                //                params.put("station_id",mgsId);
//                params.put("dbs_station_id", dbsId);
//                params.put("lcv_from_mgs_to_dbs", s);
                return params
            }
        }
        queue.add(request)
    }

    fun insertTransMgs(press1:String,temp1:String,opId:String) {

        val lcv_id: String = lcv_num
        val station_id: String = mgs_id
        val dbs_station_id: String = dbs_id

        val request: StringRequest = object : StringRequest(
            Method.POST, urlCreateTransMgs,
            Response.Listener { response ->
                Log.d("RESP>>>1", response!!)
                try {
                    val jsonObject = JSONObject(response)
                    if(!jsonObject.getBoolean("error")){
                        Log.d("RESP>>>1", jsonObject.toString())
                        val message = jsonObject.getString("message")
                     //   update1Data(press1,temp1, opId)
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    Log.d("RESP>>>1", e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    mContext,
                    "Insertion failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                val lcv_from_mgs_to_dbs = "1"
                val lcv_status = "$lcv_id waiting for Filling at $station_id"

              /*  if(transId[0]=='n'){
                    params["transaction_id"] = transId.substring(2,transId.length)

                }
                else {
                    params["transaction_id"] = transId
                }*/
                params["transaction_id"] = transId

                params["lcv_status"] = lcv_status
                params["lcv_id"] = lcv_id
                params["station_id"] = station_id
                params["dbs_station_id"] = dbs_station_id
                params["lcv_from_mgs_to_dbs"] = lcv_from_mgs_to_dbs
                params["operator_id"]=opId
                //       params["before_filing_at_mgs_mfm_img"] = ""
                //       params["before_filing_at_mgs_value_mfm_read"] = before_filing_at_mgs_value_mfm_read
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)

    }
    fun update1Data(press1:String,temp1:String,opId:String) {

            val lcv_id: String = lcv_num
            val station_id: String = mgs_id
            val dbs_station_id: String = dbs_id
            val before_filing_at_mgs_value_pressure_gauge_read: String = press1
            val before_filing_at_mgs_value_temperature_gauge_read: String = temp1
            val before_filing_at_mgs_value_mfm_read: String = vm.mfmValue.value.toString()
            val before_filing_at_mgs_mass_cng: String = vm.massValue.value.toString()
            val request: StringRequest = object : StringRequest(
                Method.POST, urlTransBeforeFilling,
                Response.Listener { response ->
                    Log.d("RESP>>>1", response!!)
                    try {
                        val jsonObject = JSONObject(response)
                        if(!jsonObject.getBoolean("error")){
                            Log.d("RESP>>>1", jsonObject.toString())
                            val message = jsonObject.getString("message")
                            val i=Intent(mContext,NewTransActivity::class.java)
                            i.putExtra("username", opId)
                            requireActivity().finish()
                            mContext.startActivity(i)
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                        }
                        else{
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                        }

                    } catch (e: JSONException) {
                        Log.d("RESP>>>1", e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(
                        mContext,
                        "Insertion failed please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    //                progressDialog.dismiss();
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = java.util.HashMap()
                    val lcv_from_mgs_to_dbs = "2"
                    val lcv_status = "$lcv_id filling at$station_id"
               /*     if(transId[0]=='n'){
                        params["transaction_id"] = transId.substring(2,transId.length)

                    }
                    else {
                        params["transaction_id"] = transId
                    }*/
                    params["transaction_id"] = transId

                    params["lcv_status"] = lcv_status
                    params["lcv_id"] = lcv_id
                    params["station_id"] = station_id
                    params["dbs_station_id"] = dbs_station_id
                    params["operator_id_bfr_filling"] = opId


//                params.put("parameter_name", new DataPart("image_image.jpg", getFileDataFromDrawable( imageView.getDrawable()), "image/jpeg"));
                    params["before_filing_at_mgs_temperature_gauge_img"] = tempUrl
                    params["before_filing_at_mgs_pressure_gauge_img"] = presUrl
                    params["before_filing_at_mgs_lp_gauge_img"] = lpUrl
                    params["before_filing_at_mgs_mp_gauge_img"] = mpUrl
                    params["before_filing_at_mgs_hp_gauge_img"] = hpUrl

                    //                params.put("after_filling_at_mgs_temperature_gauge_img", encodedTemp2image);
//                params.put("after_filling_at_mgs_pressure_gauge_img", encodedPressure2image);
                    params["before_filing_at_mgs_value_pressure_gauge_read"] =
                        before_filing_at_mgs_value_pressure_gauge_read
                    params["before_filing_at_mgs_value_lp_gauge_read"] =
                        lpValue
                    params["before_filing_at_mgs_value_mp_gauge_read"] =
                        mpValue
                    params["before_filing_at_mgs_value_hp_gauge_read"] =
                       hpValue
                    params["before_filing_at_mgs_value_temperature_gauge_read"] =
                        before_filing_at_mgs_value_temperature_gauge_read
                    params["before_filing_at_mgs_mass_cng"] = before_filing_at_mgs_mass_cng

//                params.put("after_filling_at_mgs_value_pressure_gauge_read", after_filling_at_mgs_value_pressure_gauge_read);
//                params.put("after_filling_at_mgs_value_temperature_gauge_read", after_filling_at_mgs_value_temperature_gauge_read);
//                params.put("after_filling_at_mgs_mass_cng", after_filling_at_mgs_mass_cng);
//                params.put("time_taken_to_fill_lcv", time_taken_to_fill_lcv);
                    params["lcv_from_mgs_to_dbs"] = lcv_from_mgs_to_dbs
                    //       params["before_filing_at_mgs_mfm_img"] = ""
                          params["before_filing_at_mgs_value_mfm_read"] = binding.manualMfm.text.toString()
                    Log.d("RESP>>>2", params.toString())
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(mContext)
            requestQueue.add(request)

    }
    fun notify1msg(flag:String) {
        val MGS: String = mgs_id
        val LCV: String = lcv_num
        val DBS: String = dbs_id
        val Message = "Readings captured before filling $LCV at $MGS"
        val request: StringRequest = object : StringRequest(
            Method.POST, notifyMgr,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getString("message") == null) {
                        Toast.makeText(
                            mContext,
                            "Invalid Operation",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val message = jsonObject.getString("message")
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    mContext,
                    "Insertion failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
               /* if(transId[0]=='n'){
                    params["transaction_id"] = transId.substring(2,transId.length)

                }
                else {
                    params["transaction_id"] = transId
                }*/
                params["transaction_id"] = transId

                params["Notification_LCV"] = LCV
                params["Notification_MGS"] = MGS
                params["Notification_DBS"] = DBS
                params["Notification_Message"] = Message
                params["status"] = "Pending"
                params["flag"] = flag
                params["operator_id"] = opId
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }

    fun update2Data(press1:String,temp1:String,opId:String) {
        val lcv_id: String = lcv_num
        val station_id: String = mgs_id
        val dbs_station_id: String = dbs_id
        val after_filling_at_mgs_mfm_value_read: String = ""
        val after_filling_at_mgs_value_pressure_gauge_read: String = press1
        val after_filling_at_mgs_value_temperature_gauge_read: String = temp1
        val after_filling_at_mgs_mass_cng: String = vm.massValue.value.toString()
        //        final String time_taken_to_fill_lcv = timeTakenToFillLCV;
        val request: StringRequest = object : StringRequest(
            Method.POST, urlTransAfterFilling,
            Response.Listener { response ->
                try {
                    Log.d("RESP>>>1",response)

                    val jsonObject = JSONObject(response)
                    if(!jsonObject.getBoolean("error")){
                        Log.d("RESP>>>1", jsonObject.toString())
                        val message = jsonObject.getString("message")
                       /* val i=Intent(mContext,NewTransActivity::class.java)
                        i.putExtra("username", opId)
                        requireActivity().finish()
                        mContext.startActivity(i)*/
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                        val sharedPreferences: SharedPreferences =
                           mContext. getSharedPreferences("login", Context.MODE_PRIVATE)
                        val myEdit = sharedPreferences.edit()
                        myEdit.putString("username", "")
                        myEdit.putString("station", "")
                        myEdit.putBoolean("isDbs", false)
                        myEdit.putBoolean("isLoggedIn", false)
                        myEdit.apply()
                        //updateStatus()
                        val intent = Intent(
                           mContext,
                           GailActivity::class.java
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    //    finish()
                    }
                    else{
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    Log.d("RESP>>>1", e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    mContext,
                    "Insertion failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                val lcv_from_mgs_to_dbs = "3"
                val lcv_status = lcv_id + " in Transit between" + station_id + "to" + dbs_station_id
                params["lcv_status"] = lcv_status
                params["lcv_id"] = lcv_id
                params["station_id"] = station_id
                params["dbs_station_id"] = dbs_station_id
                params["operator_id_aftr_filling"] = opId
                params["after_filling_at_mgs_value_mfm_read"] = binding.manualMfm.text.toString()

                /* if(transId[0]=='n'){
                     params["transaction_id"] = transId.substring(2,transId.length)

                 }
                 else {
                     params["transaction_id"] = transId
                 }*/
                params["transaction_id"] = transId

//                params.put("before_filing_at_mgs_mass_cng", before_filing_at_mgs_mass_cng);
                params["after_filling_at_mgs_mfm_img"] = ""
                params["after_filling_at_mgs_temperature_gauge_img"] = tempUrl
                params["after_filling_at_mgs_pressure_gauge_img"] = presUrl
                params["after_filling_at_mgs_lp_gauge_img"] = lpUrl
                params["after_filling_at_mgs_mp_gauge_img"] = mpUrl
                params["after_filling_at_mgs_hp_gauge_img"] = hpUrl

                params["after_filling_at_mgs_value_pressure_gauge_read"] =
                    after_filling_at_mgs_value_pressure_gauge_read
                params["after_filling_at_mgs_value_lp_gauge_read"] =
                    lpValue
                params["after_filling_at_mgs_value_mp_gauge_read"] =
                    mpValue
                params["after_filling_at_mgs_value_hp_gauge_read"] =
                    hpValue
                params["after_filling_at_mgs_value_temperature_gauge_read"] =
                    after_filling_at_mgs_value_temperature_gauge_read
                params["after_filling_at_mgs_mass_cng"] = after_filling_at_mgs_mass_cng
                //                params.put("time_taken_to_fill_lcv", time_taken_to_fill_lcv);
                params["lcv_from_mgs_to_dbs"] = lcv_from_mgs_to_dbs
                Log.d("PARAMS>>>",params.toString())
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }
    fun notify2msg(flag:String) {
        val MGS: String = mgs_id
        val LCV: String = lcv_num
        val DBS: String = dbs_id
        val Message =
            "Readings captured after filling $LCV at $MGS and started its journey to $DBS"
        val request: StringRequest = object : StringRequest(
            Method.POST, notifyMgr,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if(!jsonObject.getBoolean("error")){
                        Log.d("RESP>>>1", jsonObject.toString())
                        val message = jsonObject.getString("message")
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    Log.d("RESP>>>1", e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    mContext,
                    "Insertion failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>{
                val params: MutableMap<String, String> = java.util.HashMap()
               /* if(transId[0]=='n'){
                    params["transaction_id"] = transId.substring(2,transId.length)

                }
                else {
                    params["transaction_id"] = transId
                }*/
                params["transaction_id"] = transId

                params["Notification_LCV"] = LCV
                params["Notification_MGS"] = MGS
                params["Notification_DBS"] = DBS
                params["Notification_Message"] = Message
                params["status"] = "Pending"
                params["flag"] = flag
                params["operator_id"] = opId
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }

    fun insert3Data(press1:String,temp1:String,opId:String) {
        val lcv_id: String = lcv_num
        val station_id: String = dbs_id
        val before_empty_at_db_value_pressure_gauge_read: String = press1
        val before_empty_at_db_value_temperature_gauge_read: String = temp1
        val before_empty_at_db_mass_cng =  vm.massValue.value.toString()
        val lcv_from_mgs_to_dbs = "5"
        val request: StringRequest = object : StringRequest(
            Method.POST, urlTransBeforeEmptying,
            Response.Listener { response ->
                Log.d("RESP>>>1", response)

                try {
                    val jsonObject = JSONObject(response)
                    if(!jsonObject.getBoolean("error")){
                        val message = jsonObject.getString("message")
                        val i=Intent(mContext,NewTransActivity::class.java)
                        i.putExtra("username", opId)
                        requireActivity().finish()
                        mContext.startActivity(i)
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    Log.d("RESP>>>1", e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    mContext,
                    "Insertion failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>{
                val params: MutableMap<String, String> = java.util.HashMap()
                val lcv_status = "$lcv_id emptying at$station_id"
                /*if(transId[0]=='n'){
                    params["transaction_id"] = transId.substring(2,transId.length)

                }
                else {
                    params["transaction_id"] = transId
                }*/
                params["transaction_id"] = transId

                params["lcv_status"] = lcv_status
                params["lcv_id"] = lcv_id
                params["dbs_station_id"] = station_id
                params["operator_id_bfr_emptying"] = opId
                params["before_empty_at_db_temperature_gauge_img"] = tempUrl
                params["before_empty_at_dbs_pressure_gauge_img"] = presUrl
                params["before_empty_at_dbs_lp_gauge_img"] = lpUrl
                params["before_empty_at_dbs_mp_gauge_img"] = mpUrl
                params["before_empty_at_dbs_hp_gauge_img"] = hpUrl
                params["before_empty_at_dbs_value_mfm_read"] = binding.manualMfm.text.toString()


                params["before_empty_at_db_value_pressure_gauge_read"] =
                    before_empty_at_db_value_pressure_gauge_read
                params["before_empty_at_db_value_lp_gauge_read"] =
                    lpValue
                params["before_empty_at_db_value_mp_gauge_read"] =
                    mpValue
                params["before_empty_at_db_value_hp_gauge_read"] =
                    hpValue
                params["before_empty_at_db_value_temperature_gauge_read"] =
                    before_empty_at_db_value_temperature_gauge_read
                params["before_empty_at_db_mass_cng"] = before_empty_at_db_mass_cng
                params["lcv_from_mgs_to_dbs"] = lcv_from_mgs_to_dbs
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }

    fun notify3msg(flag:String) {
        val MGS = mgs_id
        val LCV: String = lcv_num
        val DBS: String = dbs_id
        val operator_id: String = opId
        Log.e(ContentValues.TAG, "operator_id in insert Notify =$operator_id")
        val Message = "Readings captured before emptying$LCV at$DBS"
        val request: StringRequest = object : StringRequest(
            Method.POST, notifyMgr,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getString("message") == null) {
                        Toast.makeText(
                            mContext,
                            "Invalid Operation",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val message = jsonObject.getString("message")
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    mContext,
                    "Insertion failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>{
                val params: MutableMap<String, String> = java.util.HashMap()
             /*   if(transId[0]=='n'){
                    params["transaction_id"] = transId.substring(2,transId.length)

                }
                else {
                    params["transaction_id"] = transId
                }*/
                params["transaction_id"] = transId

                params["Notification_LCV"] = LCV
                params["Notification_MGS"] = MGS
                params["Notification_DBS"] = DBS
                params["Notification_Message"] = Message
                params["status"] = "Pending"
                params["flag"] = flag
                params["operator_id"] = operator_id
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }


    fun insert4Data(press1:String,temp1:String,opId:String) {
        val lcv_id: String = lcv_num
        val station_id: String = dbs_id
        val before_empty_at_db_mass_cng = vm.massValue.value.toString()
        val after_empty_at_dbs_value_pressure_gauge_read: String = press1
        val after_empty_at_dbs_value_temperature_gauge_read: String = temp1
        val after_empty_at_dbs_mass_cng: String = vm.massValue.value.toString()
        val lcv_from_mgs_to_dbs = "6"
        val request: StringRequest = object : StringRequest(
            Method.POST, urlTransAfterEmptying,
            Response.Listener { response ->
                Log.d("RESP>>>1", response)

                try {
                    val jsonObject = JSONObject(response)
                    if(!jsonObject.getBoolean("error")){
                        Log.d("RESP>>>1", jsonObject.toString())
                        val message = jsonObject.getString("message")
                      /*  val i=Intent(mContext,NewTransActivity::class.java)
                        i.putExtra("username", opId)
                        requireActivity().finish()
                        mContext.startActivity(i)*/
                        val sharedPreferences: SharedPreferences =
                            mContext. getSharedPreferences("login", Context.MODE_PRIVATE)
                        val myEdit = sharedPreferences.edit()
                        myEdit.putString("username", "")
                        myEdit.putString("station", "")
                        myEdit.putBoolean("isDbs", false)
                        myEdit.putBoolean("isLoggedIn", false)
                        myEdit.apply()
                        //updateStatus()
                        val intent = Intent(
                            mContext,
                            GailActivity::class.java
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    Log.d("RESP>>>1", e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    mContext,
                    "Insertion failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                val lcv_status = "$lcv_id emptied at$station_id"
                params["transaction_id"]=transId

                params["lcv_status"] = lcv_status
                params["lcv_id"] = lcv_id
                params["dbs_station_id"] = station_id
                params["operator_id_aftr_emptying"] = opId
                params["after_empty_at_dbs_temperature_gauge_img"] = tempUrl
                params["after_empty_at_dbs_pressure_gauge_img"] = presUrl
                params["after_empty_at_dbs_lp_gauge_img"] = lpUrl
                params["after_empty_at_dbs_mp_gauge_img"] = mpUrl
                params["after_empty_at_dbs_hp_gauge_img"] = hpUrl
                params["after_empty_at_dbs_value_mfm_read"] = binding.manualMfm.text.toString()
                params["before_empty_at_db_mass_cng"] = before_empty_at_db_mass_cng
                params["after_empty_at_dbs_value_pressure_gauge_read"] =
                    after_empty_at_dbs_value_pressure_gauge_read
                params["after_empty_at_dbs_value_lp_gauge_read"] =
                    lpValue
                params["after_empty_at_dbs_value_mp_gauge_read"] =
                    mpValue
                params["after_empty_at_dbs_value_hp_gauge_read"] =
                    hpValue
                params["isHoppingEnabled"] = "true"
                params["after_empty_at_dbs_value_temperature_gauge_read"] =
                    after_empty_at_dbs_value_temperature_gauge_read
                params["after_empty_at_dbs_mass_cng"] = after_empty_at_dbs_mass_cng
                params["lcv_from_mgs_to_dbs"] = lcv_from_mgs_to_dbs
                Log.d("PARAMS>>", params.toString())
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }


    fun notify4msg(flag:String) {
        val MGS = mgs_id
        val LCV: String = lcv_num
        val DBS: String = dbs_id
        val operator_id: String = opId
        Log.e(ContentValues.TAG, "operator_id in insert Notify =$operator_id")
        val Message = "Readings captured After emptying$LCV at $DBS"
        val request: StringRequest = object : StringRequest(
            Method.POST, notifyMgr,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if(!jsonObject.getBoolean("error")){
                        Log.d("RESP>>>1", jsonObject.toString())
                        val message = jsonObject.getString("message")
                     /*   val i=Intent(mContext,NewTransActivity::class.java)
                        i.putExtra("username", opId)
                        requireActivity().finish()
                        mContext.startActivity(i)*/
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    Log.d("RESP>>>1", e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    mContext,
                    "Insertion failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
               /* if(transId[0]=='n'){
                    params["transaction_id"] = transId.substring(2,transId.length)

                }
                else {
                    params["transaction_id"] = transId
                }*/
                params["transaction_id"] = transId

                params["Notification_LCV"] = LCV
                params["Notification_MGS"] = MGS
                params["Notification_DBS"] = DBS
                params["Notification_Message"] = Message
                params["status"] = "Pending"
                params["flag"] = flag
                params["isHoppingEnabled"] = "true"

                params["operator_id"] = operator_id
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }


    fun insert5Data(press1:String,temp1:String,opId:String) {
        val lcv_id: String = lcv_num
        val station_id: String = dbs_id
        val before_empty_at_db_value_pressure_gauge_read: String = press1
        val before_empty_at_db_value_temperature_gauge_read: String = temp1
        val before_empty_at_db_mass_cng =  vm.massValue.value.toString()
        val lcv_from_mgs_to_dbs = "8"
        val request: StringRequest = object : StringRequest(
            Method.POST, BASE_URL+"v2/cng_transaction_api_v2.php?apicall=updateSecDBS1",
            Response.Listener { response ->
                Log.d("RESP>>>1", response)

                try {
                    val jsonObject = JSONObject(response)
                    if(!jsonObject.getBoolean("error")){
                        val message = jsonObject.getString("message")
                        val i=Intent(mContext,NewTransActivity::class.java)
                        i.putExtra("username", opId)
                        requireActivity().finish()
                        mContext.startActivity(i)
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    Log.d("RESP>>>1", e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    mContext,
                    "Insertion failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>{
                val params: MutableMap<String, String> = java.util.HashMap()
                val lcv_status = "$lcv_id emptying at$station_id"
                /*if(transId[0]=='n'){
                    params["transaction_id"] = transId.substring(2,transId.length)

                }
                else {
                    params["transaction_id"] = transId
                }*/

                params["transaction_id"] = transId

                params["lcv_status"] = lcv_status
                params["lcv_id"] = lcv_id
                params["secondary_dbs_id"] = station_id
                params["operator_id_bfr_emptying"] = opId
                params["before_empty_at_secondary_dbs_temperature_gauge_img"] = tempUrl
                params["before_empty_at_secondary_dbs_pressure_gauge_img"] = presUrl
                params["before_empty_at_secondary_dbs_lp_gauge_img"] = lpUrl
                params["before_empty_at_secondary_dbs_mp_gauge_img"] = mpUrl
                params["before_empty_at_secondary_dbs_hp_gauge_img"] = hpUrl
                params["before_empty_at_secondary_dbs_value_mfm_read"] = binding.manualMfm.text.toString()


                params["before_empty_at_secondary_dbs_value_pressure_gauge_read"] =
                    before_empty_at_db_value_pressure_gauge_read
                params["before_empty_at_secondary_dbs_value_lp_gauge_read"] =
                    lpValue
                params["before_empty_at_secondary_dbs_value_mp_gauge_read"] =
                    mpValue
                params["before_empty_at_secondary_dbs_value_hp_gauge_read"] =
                    hpValue
                params["before_empty_at_secondary_dbs_value_temperature_gauge_read"] =
                    before_empty_at_db_value_temperature_gauge_read
                params["before_empty_at_secondary_dbs_mass_cng"] = before_empty_at_db_mass_cng
                params["lcv_from_mgs_to_dbs"] = lcv_from_mgs_to_dbs
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }

    fun insert6Data(press1:String,temp1:String,opId:String) {
        val lcv_id: String = lcv_num
        val station_id: String = dbs_id
        val before_empty_at_db_mass_cng = prevMass
        val after_empty_at_dbs_value_pressure_gauge_read: String = press1
        val after_empty_at_dbs_value_temperature_gauge_read: String = temp1
        val after_empty_at_dbs_mass_cng: String = vm.massValue.value.toString()
        val lcv_from_mgs_to_dbs = "9"
        val request: StringRequest = object : StringRequest(
            Method.POST, BASE_URL+"v2/cng_transaction_api_v2.php?apicall=updateSecDBS2",
            Response.Listener { response ->
                Log.d("RESP>>>1", response)

                try {
                    val jsonObject = JSONObject(response)
                    if(!jsonObject.getBoolean("error")){
                        Log.d("RESP>>>1", jsonObject.toString())
                        val message = jsonObject.getString("message")
                        /*  val i=Intent(mContext,NewTransActivity::class.java)
                          i.putExtra("username", opId)
                          requireActivity().finish()
                          mContext.startActivity(i)*/
                        val sharedPreferences: SharedPreferences =
                            mContext. getSharedPreferences("login", Context.MODE_PRIVATE)
                        val myEdit = sharedPreferences.edit()
                        myEdit.putString("username", "")
                        myEdit.putString("station", "")
                        myEdit.putBoolean("isDbs", false)
                        myEdit.putBoolean("isLoggedIn", false)
                        myEdit.apply()
                        //updateStatus()
                        val intent = Intent(
                            mContext,
                            GailActivity::class.java
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    Log.d("RESP>>>1", e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    mContext,
                    "Insertion failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                val lcv_status = "$lcv_id emptied at$station_id"

                params["transaction_id"]=transId

                params["lcv_status"] = lcv_status
                params["lcv_id"] = lcv_id
                params["secondary_dbs_id"] = station_id
                params["operator_id_aftr_emptying"] = opId
                params["after_empty_at_secondary_dbs_temperature_gauge_img"] = tempUrl
                params["after_empty_at_secondary_dbs_pressure_gauge_img"] = presUrl
                params["after_empty_at_secondary_dbs_lp_gauge_img"] = lpUrl
                params["after_empty_at_secondary_dbs_mp_gauge_img"] = mpUrl
                params["after_empty_at_secondary_dbs_hp_gauge_img"] = hpUrl
                params["after_empty_at_secondary_dbs_value_mfm_read"] = binding.manualMfm.text.toString()
                params["before_empty_at_secondary_dbs_mass_cng"] = before_empty_at_db_mass_cng
                params["after_empty_at_secondary_dbs_value_pressure_gauge_read"] =
                    after_empty_at_dbs_value_pressure_gauge_read
                params["after_empty_at_secondary_dbs_value_lp_gauge_read"] =
                    lpValue
                params["after_empty_at_secondary_dbs_value_mp_gauge_read"] =
                    mpValue
                params["after_empty_at_secondary_dbs_value_hp_gauge_read"] =
                    hpValue
                params["after_empty_at_secondary_dbs_value_temperature_gauge_read"] =
                    after_empty_at_dbs_value_temperature_gauge_read
                params["after_empty_at_secondary_dbs_mass_cng"] = after_empty_at_dbs_mass_cng
                params["lcv_from_mgs_to_dbs"] = lcv_from_mgs_to_dbs
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }

    fun calcMassMultiple(){
        pressValue= if(vm.presValue.value.isNullOrEmpty()) 0f.toString() else vm.presValue.value.toString()
        tempValue= if(vm.tempValue.value.isNullOrEmpty()) 0f.toString() else vm.tempValue.value.toString()

        //    if (isPressTaken && isTempTaken) {

     /*       val  massLp =
                Constant.molarmass * lpValue.toFloat() *
                        volumeLp / (83.14 * (tempValue.toFloat() +
                        273.15))
            val  massMp =
                Constant.molarmass * mpValue.toFloat() *
                        volumeMp / (83.14 * (tempValue.toFloat() +
                        273.15))
            val  massHp =
                Constant.molarmass * hpValue.toFloat() *
                        volumeHp / (83.14 * (tempValue.toFloat() +
                        273.15))
            val mass1=massLp+massMp+massHp
        Log.d("LP>>>", "LP>>>$massLp")
        vm.lmValue.value=String.format("%.2f", massLp)
        vm.mmValue.value=String.format("%.2f", massMp)
        vm.hmValue.value=String.format("%.2f", massHp)
        vm.massValue.value=String.format("%.2f", mass1)
        if (prevMass.isNullOrEmpty())
            prevMass="0"
        val diff=mass1-prevMass.toFloat()
        binding.prevMass.text=String.format("%.2f", prevMass.toFloat())
        binding.diffMass.text=String.format("%.2f", diff)*/

        val  mass1 =
            Constant.molarmass * pressValue.toFloat() *
                    volume / (83.14 * (tempValue.toFloat() +
                    273.15))
        Log.d("CALC>>", "calcMassMultiple: $pressValue")
        Log.d("CALC>>", "calcMassMultiple: $volume")
        Log.d("CALC>>", "calcMassMultiple: $tempValue")

        vm.massValue.value=String.format("%.2f", mass1)
        if (prevMass.isNullOrEmpty())
            prevMass="0"
        val diff=mass1-prevMass.toFloat()
        binding.prevMass.text=String.format("%.2f", prevMass.toFloat())
        binding.diffMass.text=String.format("%.2f", diff)

        /* } else {
        Toast.makeText(
           mContext,
            "Please Capture Temperature and Pressure Value and Image",
            Toast.LENGTH_SHORT
        ).show()
    }*/
    }




}