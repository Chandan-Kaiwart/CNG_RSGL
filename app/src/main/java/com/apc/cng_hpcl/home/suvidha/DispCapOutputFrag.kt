package com.apc.cng_hpcl.home.suvidha

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.R
import com.apc.cng_hpcl.databinding.FragDispCapOutputBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class DispCapOutputFrag: Fragment() {
    private lateinit var mContext: Context
    private lateinit var binding: FragDispCapOutputBinding
    private lateinit var navController: NavController
    private lateinit var url:String
    private lateinit var manualReading:String
    private lateinit var shift:String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragDispCapOutputBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val formatted = current.format(formatter)
        val args=DispCapOutputFragArgs.fromBundle(requireArguments())
        manualReading="0";
        binding.readingTv.text="Totalizer Reading : \n${args.read}"
        Log.d("TYPE>>>", "onViewCreated: ${args.type}")

        val stat=args.ca.split(".")[0]
        val disp=args.ca.split(".")[1]
        val bay=args.ca.split(".")[2]
        val type=args.type
        shift = when (args.shift) {
            0 -> "A"
            1 -> {
                "B"
            }
            else -> {
                "C"
            }
        }

        if(args.url.isNotEmpty()){
            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(android.R.drawable.stat_notify_error)
                .error(android.R.drawable.stat_notify_error)

            Glide.with(this).load(args.url).apply(options).into(binding.imgIv)
        }
        else{
            binding.imgIv.setImageResource(android.R.drawable.stat_notify_error)
        }

        if(type==1){
            url="https://www.cng-suvidha.in/dispenser/API/api.php?apicall=insert_dispenser_readings"
        }
        else if(type==2){
            binding.conBt.text= "After Update"
            url="https://www.cng-suvidha.in/dispenser/API/price_api.php?apicall=insert_price_readings"
        }
        else if(type==3){
            url="https://www.cng-suvidha.in/dispenser/API/price_api.php?apicall=update_price_readings"
        }

        binding.statTv.text = stat
        binding.dispTv.text=disp
        binding.dateTimeTv.text=formatted
        binding.nozzleTv.text=bay



      //  imgIv.setImageBitmap(bp)

        binding.rejBt.setOnClickListener(View.OnClickListener {
            val action=DispCapOutputFragDirections.actionDispCapOutputFragToDispCaptureFrag()
            action.pid=args.pid
            action.type=args.type
            action.dispType=args.dispType
            navController.navigate(action)
            //  dismiss()
        })
        binding.conBt.setOnClickListener {
                putDispReading(type,stat,disp,bay,args.read,"admin",args.url,args.pid!!)
            }



    }

    private fun putDispReading(type:Int,station_id: String,disp_id:String,bay:String,reading:String,op_id:String,imgUrl:String,pid:String) {
        val pd= ProgressDia()
        pd.show(childFragmentManager,"LOAD>>")
        if(binding.manMetNo.text.toString().trim().isNotEmpty()){
            manualReading=binding.manMetNo.text.toString().trim()
        }


        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { res ->
                Log.d("RESP>>>", res!!)
                Log.d("RESP>>>", url)

                pd.dismiss()
                try {
                    val json=JSONObject(res)
                    Toast.makeText(mContext,"Reading Submitted", Toast.LENGTH_LONG).show()
                    if(type==2){
                        val action=DispCapOutputFragDirections.actionDispCapOutputFragToDispCaptureFrag()
                        val id=json.getString("id")
                        action.pid=id
                        action.type=3
                        navController.navigate(action)
                    }
                    else{
                        activity?.finish()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            },
            Response.ErrorListener {

                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>{
                val params: MutableMap<String, String> = java.util.HashMap()


                if(type==1){
                    params["station_id"] = station_id
                    params["bay"] = bay
                    params["op_id"] = op_id
                    params["reading"] = reading
                    params["disp_id"] = disp_id
                    params["image_url"] = imgUrl
                }
                else if(type==2){
                    params["station_id"] = station_id
                    params["userid"] = op_id
                    params["price"] = reading
                    params["image_url"] = imgUrl
                }
                else if(type==3){
                    params["id"] = pid
                    params["station_id"] = station_id
                    params["userid"] = op_id
                    params["updated_price"] = reading
                    params["image_url"] = imgUrl
                }
                params["manual_reading"]=manualReading
                params["shift"]=shift
                Log.d("RESP>>>", params.toString())

                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }

}