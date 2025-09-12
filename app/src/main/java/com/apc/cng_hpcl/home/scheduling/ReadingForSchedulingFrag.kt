package com.apc.cng_hpcl.home.scheduling

import android.R.layout
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.databinding.TransReadingFragBinding
import com.apc.cng_hpcl.home.newTrans.ReadingFragDirections
import com.apc.cng_hpcl.home.newTrans.TransViewModel
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling
import com.apc.cng_hpcl.util.Constant
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject


@AndroidEntryPoint
class ReadingForSchedulingFrag:Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private lateinit var binding:TransReadingFragBinding
    var dbs = ArrayList<String>()
    private lateinit var dbsId:String
    private lateinit var presUrl:String
    private val vm: TransViewModel by activityViewModels()

    private var isTempTaken=false
    private var isPressTaken=false
    private lateinit var opId: String
    private lateinit var tempUrl:String
    private var volume:Int=0
    private var volumeLp:Int=0
    private var volumeMp:Int=0
    private var volumeHp:Int=0
    private var isMfmTaken = false
    private var mfmUrl: String = ""
    private var tempGaugeMake: String = ""
    private var presGaugeMake: String = ""


    private var tempValue="0"
    private var pressValue="0"
    private var lpValue="0"
    private var mpValue="0"
    private var hpValue="0"
    private var lpUrl:String=""
    private var hpUrl:String=""
    private var mpUrl:String=""
    private var isHpTaken=false
    private var isMpTaken=false
    private var isLpTaken=false





    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= TransReadingFragBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)
        val args:ReadingForSchedulingFragArgs= ReadingForSchedulingFragArgs.fromBundle(requireArguments())
        opId=args.opId
        dbsId=args.dbsId
        tempUrl=""
        presUrl=""
        binding.vm=vm
        binding.lifecycleOwner=this
        binding.pressCard.visibility=View.VISIBLE

      //  binding.lpCard.visibility=View.VISIBLE
     //   binding.mpCard.visibility=View.VISIBLE
     //   binding.hpCard.visibility=View.VISIBLE

        binding.mfmCard.visibility=View.GONE
        binding.stageCard.visibility=View.GONE
        binding.manCard.visibility=View.GONE

        binding.pressCard.setOnClickListener {
            val action=ReadingForSchedulingFragDirections.actionReadingForSchedulingFragToSchPhotoFrag(opId,dbsId,binding.selectCascade.selectedItem.toString(),presGaugeMake)
            action.type=2
            action.gaugeMake=presGaugeMake
         //   action.stage=args.stage
            navController.navigate(action)
        }
        binding.tempCard.setOnClickListener {
            val action=ReadingForSchedulingFragDirections.actionReadingForSchedulingFragToSchPhotoFrag(opId,dbsId,binding.selectCascade.selectedItem.toString(),tempGaugeMake)
            action.type=3
            action.gaugeMake=tempGaugeMake
          //  action.stage=args.stage
            navController.navigate(action)
        }
        binding.lpCard.setOnClickListener {
            val action=ReadingForSchedulingFragDirections.actionReadingForSchedulingFragToSchPhotoFrag(opId,dbsId,binding.selectCascade.selectedItem.toString(),presGaugeMake)
            action.type=21
        //    action.stage=args.stage
            navController.navigate(action)
        }
        binding.mpCard.setOnClickListener {
            val action=ReadingForSchedulingFragDirections.actionReadingForSchedulingFragToSchPhotoFrag(opId,dbsId,binding.selectCascade.selectedItem.toString(),presGaugeMake)
            action.type=22
           // action.stage=args.stage
            navController.navigate(action)
        }
        binding.hpCard.setOnClickListener {
            val action=ReadingForSchedulingFragDirections.actionReadingForSchedulingFragToSchPhotoFrag(opId,dbsId,binding.selectCascade.selectedItem.toString(),presGaugeMake)
            action.type=23
         //   action.stage=args.stage
            navController.navigate(action)
        }

        vm.tempValue.observe(viewLifecycleOwner, Observer {
            //your code here
            tempValue= if(it.toString().trim().isNotEmpty()){
                it.toString()
            } else{
                "0"

            }
            calcMassMultiple()
        })
        vm.lpValue.observe(viewLifecycleOwner, Observer {
            //your code here
            lpValue= if(it.toString().trim().isNotEmpty()){
                it.toString()
            } else{
                "0"

            }
            calcMassMultiple()

        })
        vm.mpValue.observe(viewLifecycleOwner, Observer {
            //your code here
            mpValue= if(it.toString().trim().isNotEmpty()){
                it.toString()
            } else{
                "0"

            }
            calcMassMultiple()
        })
        vm.presValue.observe(viewLifecycleOwner, Observer {

            calcMassMultiple()
        })
        vm.hpValue.observe(viewLifecycleOwner, Observer {
            //your code here
            hpValue= if(it.toString().trim().isNotEmpty()){
                it.toString()
            } else{
                "0"
            }
            calcMassMultiple()
        })
        vm.vol.observe(viewLifecycleOwner, Observer {
            volume=it.toFloat().toInt()

        })
        vm.volLp.observe(viewLifecycleOwner, Observer {
            volumeLp=it.toFloat().toInt()
        })
        vm.volMp.observe(viewLifecycleOwner, Observer {
            volumeMp=it.toFloat().toInt()

        })
        vm.volHp.observe(viewLifecycleOwner, Observer {
            volumeHp=it.toFloat().toInt()
            calcMassMultiple()


        })
        binding.manualTemp.setText("")
        binding.manualPressure.setText("")
        binding.manualLp.setText("")
        binding.manualMp.setText("")
        binding.manualHp.setText("")
        binding.manualMfm.setText("")
        binding.updateBt.setOnClickListener {
            if(tempValue== "0"){
                Toast.makeText(mContext,"Please input temperature !",Toast.LENGTH_LONG).show()
            }
            /*else if(lpValue == "0"){
                Toast.makeText(mContext,"Please input lp !",Toast.LENGTH_LONG).show()
            }
            else if(mpValue == "0"){
                Toast.makeText(mContext,"Please input mp !",Toast.LENGTH_LONG).show()
            }
              else if(hpValue == "0"){
                Toast.makeText(mContext,"Please input hp !",Toast.LENGTH_LONG).show()
            }
            */
            else if(pressValue == "0"){
                Toast.makeText(mContext,"Please input pressure !",Toast.LENGTH_LONG).show()
            }

            else{
                insertSchedulingData()
            }
        }

        //readMgsDbs();
        dbs = ArrayList<String>()
        dbs.add(dbsId)
        val spinnerArrayAdapterDbs: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, layout.simple_spinner_item, dbs)
        spinnerArrayAdapterDbs.setDropDownViewResource(layout.simple_spinner_dropdown_item) // The drop down view

        binding.selectStation.setAdapter(spinnerArrayAdapterDbs)
        readCascade(dbsId)
/*
        binding.selectStation.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {

                Log.d("Here>>1", "onItemSelected:  1")
                readCascade(dbsId)
                Log.d("Here>>2", "onItemSelected:  2")

                //  readReorderPoint();
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.e(ContentValues.TAG, "Select Station ID ")
            }
        })
*/
        if (!vm.presPath.value.isNullOrEmpty()) {
            val bmOptions = BitmapFactory.Options()
            val bp = BitmapFactory.decodeFile(vm.presPath.value, bmOptions)
            binding.pressureGuage.setImageBitmap(bp)
            //   binding.manualTemp.setText(it.value)
            isPressTaken = true
            presUrl = vm.presUrl.value.toString()

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

    private fun readVolume(casc_id:String) {
        val queue = Volley.newRequestQueue(mContext)
        val request: StringRequest = object : StringRequest(
            Method.POST, "msg_dbs_transaction.php?apicall=readStationEquipInfo",
            Response.Listener { response ->
                try {
                    Log.e(ContentValues.TAG, "Response = $response")
                    val jsonObject1 = JSONObject(response)
                    if(jsonObject1.getBoolean("error")){
                        Toast.makeText(mContext,jsonObject1.getString("message"),Toast.LENGTH_LONG).show()
                        requireActivity().finish()
                    }
                    val jsonObject=jsonObject1.getJSONObject("data")
//                    jsonObject.getString("casc_id")
                    val vol = jsonObject.getString("stationary_cascade_capacity")
                    val volLp = jsonObject.getString("stationary_cascade_capacity_lp")
                    val volMp = jsonObject.getString("stationary_cascade_capacity_mp")
                    val volHp = jsonObject.getString("stationary_cascade_capacity_hp")
                  //  vm.tempMake.value=jsonObject.getString("temperature_gauge_make").substring(1,2).toInt()
                  //  vm.lpMake.value=jsonObject.getString("low_pressure_gauge_make").substring(1,2).toInt()
                  //  vm.mpMake.value=jsonObject.getString("medium_pressure_gauge_make").substring(1,2).toInt()
                 //   vm.hpMake.value=jsonObject.getString("high_pressure_gauge_make").substring(1,2).toInt()
                tempGaugeMake=  jsonObject.getString("temperature_gauge_make")
                presGaugeMake = jsonObject.getString("high_pressure_gauge_make")
                    volume = vol.toInt().toFloat().toInt()
                    volumeLp = volLp.toFloat().toInt()
                    volumeMp = volMp.toFloat().toInt()
                    volumeHp = volHp.toFloat().toInt()
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

            override fun getParams(): Map<String, String>{
                val params: MutableMap<String, String> = HashMap()
                params["stationary_cascade_id"] = casc_id
                params["station_id"] = dbsId;
//                params.put("dbs_station_id", dbsId);
//                params.put("lcv_from_mgs_to_dbs", s);
                Log.d("RESP>>", params.toString())
                return params
            }
        }
        queue.add(request)
    }
    private fun insertSchedulingData() {
        binding.updateBt.isEnabled=false
        val station_id: String = binding.selectStation.getSelectedItem().toString()
        //        final String dispenser_id = select_dispenser.getSelectedItem().toString();
//        final String dispenser_read = disp;
        val operator_id: String = opId
        //        final String stationary_cascade_id = select_cascade.getSelectedItem().toString();
        val mp_stationary_cascade_pressure_gauge_value: String = binding.manualPressure.text.toString().trim()
        val hp_stationary_cascade_pressure_gauge_value = "0"
        val lp_stationary_cascade_pressure_gauge_value = "0"
        val stationary_cascade_value_temperature_gauge: String = binding.manualTemp.text.toString().trim()
        val mass_of_gas: String = binding.massCng.getText().toString()
        val request: StringRequest = object : StringRequest(
            Method.POST, SchedularStationaryCascade.URL_Dispneser,
            Response.Listener { response ->

                Log.d("RESP>>>", response!!)
                try {
                    val jsonObject = JSONObject(response)
                    val message1 = jsonObject.getString("message")
                  //  val message2 = jsonObject.getString("req_message")
                    Toast.makeText(
                        mContext,
                        message1,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.updateBt.isEnabled=true
                    requireActivity().finish()
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
                params["station_id"] = station_id
                params["operator_id"] = operator_id
                params["stationary_cascade_id"] = binding.selectCascade.selectedItem.toString()

//                params.put("dispenser_id", dispenser_id);
//                params.put("dispenser_read", dispenser_read);

//                params.put("dispenser_img", encodedDispimage);
//                params.put("stationary_cascade_id", stationary_cascade_id);
                params["mp_stationary_cascade_pressure_gauge_value"] =
                    mpValue
                params["hp_stationary_cascade_pressure_gauge_value"] =
                  hpValue
                params["stationary_cascade_pressure_gauge_value"] =
                    vm.presValue.value.toString()
                params["lp_stationary_cascade_pressure_gauge_value"] =
                    lpValue
                params["stationary_cascade_value_temperature_gauge"] =
                    tempValue
                params["mass_of_gas"] = vm.massValue.value.toString()
                params["stationary_cascade_temperature_gauge_img"] = tempUrl
                params["mp_stationary_cascade_pressure_gauge_img"] = mpUrl
                params["stationary_cascade_pressure_gauge_img"] = presUrl
                params["lp_stationary_cascade_pressure_gauge_img"] = lpUrl
                params["hp_stationary_cascade_pressure_gauge_img"] = hpUrl
                Log.d("RESP>>", params.toString())

                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }
/*    private fun calcMassMultiple(){
        //    if (isPressTaken && isTempTaken) {

        val  massLp =
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

        *//* } else {
        Toast.makeText(
           mContext,
            "Please Capture Temperature and Pressure Value and Image",
            Toast.LENGTH_SHORT
        ).show()
    }*//*
    }*/
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
Corrected           vm.mmValue.value=String.format("%.2f", massMp)
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


        /* } else {
        Toast.makeText(
           mContext,
            "Please Capture Temperature and Pressure Value and Image",
            Toast.LENGTH_SHORT
        ).show()
    }*/
    }

    private fun readCascade(dbs: String) {
        Log.d("RESP>>>", SchedularStationaryCascade.URL_Cascade)
        val request: StringRequest = object : StringRequest(
            Method.POST, "BASE_URLmsg_dbs_transaction.php?apicall=readAllStationaryCascade",
            Response.Listener
            { response ->
                Log.d("RESP>>>", response!!)
                try {
                    val obj = JSONObject(response)
                    if (obj.optString("error") == "false") {
                        val cas: MutableList<String> = ArrayList()
                        val dataArray = obj.getJSONArray("cascades")
                        for (i in 0 until dataArray.length()) {
                            cas.add(dataArray.getString(i))
                        }
                        val spinnerArrayAdapterDbs = ArrayAdapter(
                            mContext,
                           android. R.layout.simple_spinner_item,
                            cas
                        )
                        spinnerArrayAdapterDbs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
                        binding.selectCascade.adapter = spinnerArrayAdapterDbs
                        binding.selectCascade.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                Log.d("Here>>3", "onItemSelected:  3")
                                readVolume(cas[position])
                                Log.d("Here>>4", "onItemSelected:  4")

                                //  readReorderPoint();
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                Log.e(ContentValues.TAG, "Select Casc ID ")
                            }
                        })

                        //                                removeSimpleProgressDialog();
                    }
                    else{
                        Toast.makeText(mContext,obj.getString("message"),Toast.LENGTH_LONG).show()
                        requireActivity().finish()
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
                params["station_id"] = dbs
                Log.d("Res>>>", "getParams: $dbs")
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }


}