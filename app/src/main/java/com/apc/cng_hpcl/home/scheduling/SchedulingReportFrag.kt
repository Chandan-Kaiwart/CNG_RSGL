package com.apc.cng_hpcl.home.scheduling

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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.BuildConfig.BASE_URL
import com.apc.cng_hpcl.databinding.ReqListItemBinding
import com.apc.cng_hpcl.databinding.SchReportFragBinding
import com.apc.cng_hpcl.databinding.SchReportListItemBinding
import com.apc.cng_hpcl.home.newTrans.TransListFrag
import com.apc.cng_hpcl.home.newTrans.TransListFragDirections
import com.apc.cng_hpcl.home.newTrans.TransViewModel
import com.apc.cng_hpcl.home.notification.DataModel
import com.apc.cng_hpcl.home.transaction.ReqAdapter
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_After_Emptying
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.DBS_Before_Emptying
import com.apc.cng_hpcl.home.transaction.subtabs.lcvdbs.TransDaughterBoosterStation
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_After_Filling
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.MGS_Before_Filling
import com.apc.cng_hpcl.home.transaction.subtabs.mgslcv.TransMotherGasStation
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class SchedulingReportFrag: Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private lateinit var binding:SchReportFragBinding
    private val vm: TransViewModel by activityViewModels()
    private lateinit var dataModels: ArrayList<ReportDataModel>


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=SchReportFragBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        dataModels=ArrayList()
        val station=arguments?.getString("station")!!
        binding.statTv.text = "Station : $station"
        val calender = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = sdf.format(calender)
        binding.dateTv.text = date
        binding.reqRv.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.reqRv.setHasFixedSize(true)
        getReqs(station)
    }
    private fun getReqs(station:String) {
        dataModels.clear()
       val s = BASE_URL+"v2/app/get_sch_report.php?mgs=$station"
       // val s = BASE_URL+"v2/app/get_sch_report.php?mgs=cgs_harua"

        Log.d("reqs>>>", s)
        val jsonObjectRequest: StringRequest = object : StringRequest(
            Method.GET, s,
            Response.Listener { resp ->
                Log.d("Res>>>", resp!!)
                try {
                    val response = JSONObject(resp)
                    Log.d("Req>>>", response.toString())
                    if(response.getBoolean("error")){
                        Toast.makeText(mContext,response.getString("message"), Toast.LENGTH_LONG).show()
                        requireActivity().finish()
                    }
                    val arr = response.getJSONArray("data")
                    for (i in 0 until arr.length()) {
                        /*  {
                            "LCV_Num": "UP80HT5299",
                            "DBS": "mirzamurad",
                            "Status": "Scheduled",
                            "time": "09:47:24"
                        }*/
                        val obj = arr.getJSONObject(i)
                        val gson = Gson()
                        val dm = gson.fromJson(obj.toString(), ReportDataModel::class.java)
                        dm.LCV_Num = obj.getString("LCV_Num")
                        dm.time = obj.getString("time")
                        dm.DBS = obj.getString("DBS")
                        dm.Status = obj.getString("Status")
                        dataModels.add(dm)
                    }
                    val adapter = DataAdapter(dataModels)
                    binding.reqRv.adapter = adapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

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
        private var largeNewsList: ArrayList<ReportDataModel>,

    ) : RecyclerView.Adapter<DataAdapter.DataViewHolder>()
    {


        private lateinit var binding: SchReportListItemBinding

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
            binding = SchReportListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DataViewHolder(binding)
        }


        override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
            val largeNews = largeNewsList[position]
            holder.bind(largeNews,position+1,)

        }


        override fun getItemCount(): Int = largeNewsList.size
        class DataViewHolder(
            private val binding: SchReportListItemBinding,

        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(largeNews: ReportDataModel,position: Int) {
                binding.timeTv.text = largeNews.time
                binding.lcvTv.text = largeNews.LCV_Num
                binding.statusTv.text = "${largeNews.Status} - ${largeNews.DBS}"

            }



        }

    }
}