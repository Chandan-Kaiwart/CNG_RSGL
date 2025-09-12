package com.apc.cng_hpcl.home.newTrans

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.BuildConfig.BASE_URL
import com.apc.cng_hpcl.databinding.NewTransListFragBinding
import com.apc.cng_hpcl.databinding.ReqListItemBinding
import com.apc.cng_hpcl.home.notification.DataModel
import com.apc.cng_hpcl.home.transaction.ReqAdapter
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_After_Emptying
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_After_Filling
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation
import org.json.JSONException
import org.json.JSONObject

class TransListFrag: Fragment() {
    private lateinit var mContext: Context
    private lateinit var dataModels: ArrayList<DataModel>

    private lateinit var navController: NavController
    private lateinit var binding:NewTransListFragBinding
    private lateinit var uid:String
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=NewTransListFragBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        dataModels= ArrayList()
         uid= arguments?.getString("op_id")!!
        binding.reqRv.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
       binding.reqRv.setHasFixedSize(true)

        getReqs()
     //   navController.navigate(action)
    }
    private fun getReqs() {
        dataModels.clear()
        val s = BASE_URL+"v2/cng_transaction_api_v2.php?apicall=readOperatorNotifications"
        Log.d("reqs>>>", s)
        val jsonObjectRequest: StringRequest = object : StringRequest(
            Method.POST, s,
            Response.Listener { resp ->
                Log.d("Res>>>", resp!!)
                try {
                    val response = JSONObject(resp)
                    Log.d("Req>>>", response.toString())
                    if(response.getBoolean("error")){
                        Toast.makeText(mContext,response.getString("message"),Toast.LENGTH_LONG).show()
                        requireActivity().finish()
                    }
                    val arr = response.getJSONArray("notification_data")
                    for (i in 0 until arr.length()) {
                        /* {
                                  "date": "2023-02-20 17:20:18",
                                      "dbs": "DBS1",
                                      "MGS": "MGS123",
                                      "LCV_Num": "DL1TEMP2021",
                                      "status": "3",
                                      "transaction_id": "200",
                                      "stage": "1"
                              }*/
                        val obj = arr.getJSONObject(i)
                        val dm = DataModel()
                        dm.lcvnum = obj.getString("LCV_Num")
                        dm.status = obj.getString("status")
                        dm.stage = obj.getString("stage")

                        dm.dbs = obj.getString("dbs")
                        dm.mgs = obj.getString("MGS")
                        dm.transId = obj.getString("transaction_id")
                        dm.date = obj.getString("date")
                        dm.msg=obj.getString("msg")
                        try {
                            val sh: SharedPreferences =
                                mContext.getSharedPreferences("login", Context.MODE_PRIVATE)


                            val a = sh.getString("lcv","")
                            if(dm.stage!="3" && dm.stage!="6") {
                                if (dm.dbs.trim().isEmpty()) {
                                    dataModels.add(dm)
                                } else if (dm.lcvnum.equals(a)) {
                                    dataModels.add(dm)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dataModels.add(dm)

                        }

                    }
                    val adapter = DataAdapter(dataModels,navController,uid,mContext)
                    binding.reqRv.adapter = adapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["operator_id"] = "ope$uid"
                Log.d("params>>>", params.toString())
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
            2,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.setShouldCache(false)
        Volley.newRequestQueue(mContext).add(jsonObjectRequest)
    }
    class DataAdapter(
        private var largeNewsList: ArrayList<DataModel>,
        private val navController: NavController,
        private val op_id:String,
        private val mContext: Context

        ) : RecyclerView.Adapter<DataAdapter.DataViewHolder>()
    {


        private lateinit var binding: ReqListItemBinding

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
            binding = ReqListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DataViewHolder(binding,navController,op_id,mContext)
        }


        override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
            val largeNews = largeNewsList[position]
            holder.bind(largeNews,position+1,mContext)

        }


        override fun getItemCount(): Int = largeNewsList.size
        class DataViewHolder(
            private val binding: ReqListItemBinding,
            private val navController: NavController,
            private val op_id:String,
            private val mContext: Context
            ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(largeNews: DataModel,position: Int,context: Context) {
                binding.date.text = largeNews.date
                binding.time.text = largeNews.lcvnum
                binding.status.text = largeNews.status
                binding.task.text=largeNews.msg
             /*   if(largeNews.status=="Approved"){
                    binding.cardView.setCardBackgroundColor(mContext.resources.getColor(com.apc.cng_hpcl.R.color.bgl_background,mContext.theme))
                }
                else{
                    binding.cardView.setCardBackgroundColor(mContext.resources.getColor(com.apc.cng_hpcl.R.color.Accent,mContext.theme))
                }*/

                  if(largeNews.stage=="1"||largeNews.stage=="4"){
                      binding.cardView.setCardBackgroundColor(mContext.resources.getColor(com.apc.cng_hpcl.R.color.dot_light_screen1,mContext.theme))

                   }
                   else{
                      binding.cardView.setCardBackgroundColor(mContext.resources.getColor(com.apc.cng_hpcl.R.color.bgl_background,mContext.theme))

                  }

             /*   when (largeNews.stage.toInt()) {
                    0 -> {
                        binding.task.text = "Stage 0"
                    }
                    1 -> {
                        binding.task.text =
                            mContext.resources.getString(com.apc.cng_hpcl.R.string.at_mother_gas_station_before_filling)

                    }
                    2 -> {
                        binding.task.text =
                            mContext.resources.getString(com.apc.cng_hpcl.R.string.at_mother_gas_station_after_filling)

                    }
                    3 -> {
                        binding.task.text =
                            mContext.resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_before_emptying)

                    }
                    4 -> {
                        binding.task.text =
                            mContext.resources.getString(com.apc.cng_hpcl.R.string.at_daughter_booster_station_after_emptying)

                    }

                }*/
                //binding.task.text=largeNews.lcvnum+largeNews.mgs+largeNews.dbs+largeNews.status
                binding.cardView.setOnClickListener {


                    //    Toast.makeText(mContext,dataModels.get(position).getLcvnum(),Toast.LENGTH_LONG).show();
                    readNotification(largeNews, navController)


             /*       val action = TransListFragDirections.actionTransListFragToReadingFrag(
                        op_id, largeNews.lcvnum
                    )
                    action.stage = largeNews.stage.toInt()


                    navController.navigate(action)*/
                }
            }
            fun insertData(dm: DataModel) {
                val station_id = dm.mgs
                val lcv_id = dm.lcvnum
                val dbs_station_id = dm.dbs
                if (!(station_id == "NA" && lcv_id == "NA" && dbs_station_id == "NA")) {
                    val request: StringRequest = object : StringRequest(
                        Method.POST, TransMotherGasStation.URL_MGS,
                        Response.Listener { response ->
                            Log.e(ContentValues.TAG, "insertData Response = $response")
                            try {
                                val jsonObject = JSONObject(response)
                                if (jsonObject.getString("message") == null) {
                                    Toast.makeText(mContext, "Invalid Operation", Toast.LENGTH_SHORT).show()
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
                        override fun getParams(): Map<String, String>? {
                            val params: MutableMap<String, String> = java.util.HashMap()
                            val lcv_from_mgs_to_dbs = "1"
                            val lcv_status = "$lcv_id waiting for Filling at $station_id"
                            params["lcv_status"] = lcv_status
                            params["lcv_id"] = lcv_id
                            params["station_id"] = station_id
                            params["dbs_station_id"] = dbs_station_id
                            params["lcv_from_mgs_to_dbs"] = lcv_from_mgs_to_dbs
                            params["transaction_id"]=dm.transId

                            params["operator_id"] = op_id
                            return params
                        }
                    }
                    val requestQueue = Volley.newRequestQueue(mContext)
                    requestQueue.add(request)
                } else {
                    Toast.makeText(
                        mContext,
                        "Please select valid LCV,MGS and DBS station ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            fun notifymsg(dm: DataModel) {
                val MGS = dm.mgs
                val LCV = dm.lcvnum
                val DBS = dm.dbs
                val operator_id: String = op_id
                val url = BASE_URL + "msg_dbs_transaction.php?apicall=notify1"
                Log.e(ContentValues.TAG, "operator_id in insert Notify =$operator_id")
                val Message = "$LCV Reached at Mother Gas Station READY FOR RE-FILLING:$MGS"
                val request: StringRequest = object : StringRequest(
                    Method.POST, TransMotherGasStation.URL_NOTIFY1,
                    Response.Listener { response ->
                        Log.d("MyTag","URL: "+ url)
                        Log.e(ContentValues.TAG, "notifymsg Response = $response")
                        try {
                            val jsonObject = JSONObject(response)
                            if (jsonObject.getString("message") == null) {
                                Toast.makeText(mContext, "Invalid Operation", Toast.LENGTH_SHORT).show()
                            } else {
                                val message = jsonObject.getString("message")
                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(mContext, "Insertion failed please try again", Toast.LENGTH_SHORT)
                            .show()
                        //                progressDialog.dismiss();
                    }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String>? {
                        val params: MutableMap<String, String> = java.util.HashMap()
                        params["Notification_LCV"] = LCV
                        params["Notification_MGS"] = MGS
                        params["Notification_DBS"] = DBS
                        params["Notification_Message"] = Message
                        params["status"] = op_id
                        params["flag"] = "1"
                        params["operator_id"] = operator_id
                        params["transaction_id"]=dm.transId

                        return params
                    }
                }
                val requestQueue = Volley.newRequestQueue(mContext)
                requestQueue.add(request)
            }
            fun readNotification(dm: DataModel, navController: NavController) {
                    val queue = Volley.newRequestQueue(mContext)
                    if (!(dm.mgs == "NA" || dm.dbs == "NA" || dm.lcvnum == "NA")) {
                        val request: StringRequest = object : StringRequest(
                            Method.POST, ReqAdapter.URL_READ_NOTE,
                            Response.Listener { response ->
                                try {
                                    Log.e(ContentValues.TAG, "getNotificationResponse = $response")
                                    val jsonObject = JSONObject(response)
                                    val myJsonObject = JSONObject(response)
                                    jsonObject.getString("status")
                                    val flag = jsonObject.getString("flag")
                                    val noteStatus = jsonObject.getString("status")
                                    val create_date = jsonObject.getString("create_date")
                                    Log.e(
                                        ContentValues.TAG,
                                        "Flag,NoteStatus,Create_date=$flag$noteStatus$create_date"
                                    )
                                    if (flag == "1" && noteStatus == "Pending") {
                                        Log.e(ContentValues.TAG, "In if Notification Pending MGS")
                                        Toast.makeText(
                                            mContext,
                                            "Waiting for Manager Approval",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (flag == "2" && noteStatus == "Pending") {
                                        Log.e(
                                            ContentValues.TAG,
                                            "In if Notification Pending MGS Before Filling"
                                        )
                                        Toast.makeText(
                                            mContext,
                                            "Waiting for Manager Approval Before Filling",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (flag == "3" && noteStatus == "Pending")
                                    {
                                        Log.e(
                                            ContentValues.TAG,
                                            "In if Notification Pending MGS Before Filling"
                                        )
                                        Toast.makeText(
                                            mContext,
                                            "Waiting for Manager Approval After Filling",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else if ((flag == "4" || flag == "7" )&& noteStatus == "Pending")
                                    {
                                        Log.e(
                                            ContentValues.TAG,
                                            "In if Notification Pending MGS Before Filling"
                                        )
                                        Toast.makeText(
                                            mContext,
                                            "Waiting for Manager Approval Before Emptying",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else if ((flag == "5" || flag == "8" ) && noteStatus == "Pending")
                                    {
                                        Log.e(
                                            ContentValues.TAG,
                                            "In if Notification Pending MGS Before Filling"
                                        )
                                        Toast.makeText(
                                            mContext,
                                            "Waiting for Manager Approval After Emptying",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    else if (flag == "1" && noteStatus == "Approved")
                                    {
                                        Log.e(ContentValues.TAG, "In if Approved First Level MGS")
                                        Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT)
                                            .show()
                                        val bundle = Bundle()
                                        bundle.putString("username", op_id)
                                        bundle.putString("lcv_num", dm.lcvnum)
                                        bundle.putString("mgs_id", dm.mgs)
                                        bundle.putString("dbs_id", dm.dbs)
                                        val intent = Intent(mContext, MGS_Before_Filling::class.java)
                                        intent.putExtras(bundle)

                                              val action = TransListFragDirections.actionTransListFragToReadingFrag(op_id, dm.lcvnum,dm.transId,dm.mgs,dm.dbs)
                                                action.stage = dm.stage.toInt()
                                                navController.navigate(action)

                                        //    mContext.startmContext(intent);
                                        //   mContext.finish();
                                    }
                                    else if (flag == "2" && noteStatus == "Approved") {
                                        Log.e(ContentValues.TAG, "In if Approved Second Level MGS")
                                        Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT)
                                            .show()
                                        val bundle = Bundle()
                                        bundle.putString("username", op_id)
                                        bundle.putString("lcv_num", dm.lcvnum)
                                        bundle.putString("mgs_id", dm.mgs)
                                        bundle.putString("dbs_id", dm.dbs)
                                        val intent = Intent(mContext, MGS_After_Filling::class.java)

                                        //                           Intent intent = new Intent(TransMotherGasStation.this, MGS_After_Filling_MFM.class);
                                        intent.putExtras(bundle)
                                        val action = TransListFragDirections.actionTransListFragToReadingFrag(op_id, dm.lcvnum,dm.transId,dm.mgs,dm.dbs)
                                        action.stage = dm.stage.toInt()
                                        navController.navigate(action)

                                        //     mContext. startmContext(intent);
                                        //   mContext.finish();
                                    }
                                    else if (flag == "3" && noteStatus == "Approved") {
                                        Log.e(ContentValues.TAG, "In if Approved Third Level MGS")
                                        Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT)
                                            .show()
                                        val bundle = Bundle()
                                        bundle.putString("username", op_id)
                                        bundle.putString("lcv_num", dm.lcvnum)
                                        bundle.putString("mgs_id", dm.mgs)
                                        bundle.putString("dbs_id", dm.dbs)
                                        val intent = Intent(mContext, TransDaughterBoosterStation::class.java)
                                        intent.putExtras(bundle)
                                        //mContext.startmContext(intent);
                                        //   mContext.finish();

                                    }
                                    else if ((flag == "4"|| flag == "7") && noteStatus == "Approved") {
                                        Log.e(ContentValues.TAG, "In if Approved Fourth Level MGS")
                                        Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT)
                                            .show()
                                        val bundle = Bundle()
                                        bundle.putString("username", op_id)
                                        bundle.putString("lcv_num", dm.lcvnum)
                                        bundle.putString("mgs_id", dm.mgs)
                                        bundle.putString("dbs_id", dm.dbs)
                                        val intent = Intent(mContext, DBS_Before_Emptying::class.java)

                                        //                           Intent intent = new Intent(TransMotherGasStation.this, DBS_Before_Emptying_MFM.class);
                                        intent.putExtras(bundle)
                                        //   mContext.startmContext(intent);
                                        // mContext.finish();
                                        val action = TransListFragDirections.actionTransListFragToReadingFrag(op_id, dm.lcvnum,dm.transId,dm.mgs,dm.dbs)
                                        action.stage = dm.stage.toInt()
                                        navController.navigate(action)
                                    }
                                    else if ((flag == "5" || flag == "8")&& noteStatus == "Approved") {
                                        Log.e(ContentValues.TAG, "In if Approved Fourth Level MGS")
                                        Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT)
                                            .show()
                                        val bundle = Bundle()
                                        bundle.putString("username", op_id)
                                        bundle.putString("lcv_num", dm.lcvnum)
                                        bundle.putString("mgs_id", dm.mgs)
                                        bundle.putString("dbs_id", dm.dbs)
                                        val intent = Intent(mContext, DBS_After_Emptying::class.java)
                                        intent.putExtras(bundle)
                                        //     mContext.startmContext(intent);
                                        //   mContext.finish();
                                        val action = TransListFragDirections.actionTransListFragToReadingFrag(op_id, dm.lcvnum,dm.transId,dm.mgs,dm.dbs)
                                        action.stage = dm.stage.toInt()
                                        navController.navigate(action)
                                    }
                                    else if ((flag == "6" || flag == "9" )&& noteStatus == "Approved") {
                                        Toast.makeText(mContext, "Manager Approval Done", Toast.LENGTH_SHORT)
                                            .show()
                                        val bundle = Bundle()
                                        bundle.putString("username", op_id)
                                        bundle.putString("lcv_num", dm.lcvnum)
                                        bundle.putString("mgs_id", dm.mgs)
                                        bundle.putString("dbs_id", dm.dbs)
                                        val intent = Intent(mContext, DBS_After_Emptying::class.java)
                                        intent.putExtras(bundle)
                                        //     mContext.startmContext(intent);
                                        //   mContext.finish();

                                    }

                                    else {
                                        Log.e(ContentValues.TAG, "In if First transaction MGS")
                                        notifymsg(dm)
                                        insertData(dm)
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
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
                                val params: MutableMap<String, String> = java.util.HashMap()
                                params["Notification_LCV"] = dm.lcvnum
                                params["Notification_MGS"] = dm.mgs
                                params["Notification_DBS"] = dm.dbs
                                return params
                            }
                        }
                        queue.add(request)
                    } else {
                        Toast.makeText(
                            mContext,
                            "Please select valid LCV,MGS and DBS station ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }



        }

    }


  


}