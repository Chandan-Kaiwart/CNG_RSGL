package com.apc.cng_hpcl.home.newDisp

import android.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.BuildConfig.BASE_URL
import com.apc.cng_hpcl.databinding.FragDispReadBinding
import com.apc.cng_hpcl.util.ProgressDia
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject


@AndroidEntryPoint
class DispReadFrag: Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private lateinit var binding:FragDispReadBinding
    val disps: MutableList<DispModel> = ArrayList()
    val dispNames: MutableList<String> = ArrayList()
    private lateinit var station:String
    private lateinit var username:String
    private var disp:Int=-1
    private var bay:Int=-1


    private lateinit var imageUrl:String


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragDispReadBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)
        val args: DispReadFragArgs = DispReadFragArgs.fromBundle(requireArguments())
        station=args.station
        username=args.username
        imageUrl= args.imageUrl.toString()
        disp=args.disp




        if(imageUrl.isNotEmpty() || imageUrl !="def")
        {
            val options: RequestOptions = RequestOptions()
               // .centerCrop()
                .placeholder(com.apc.cng_hpcl.R.mipmap.dispenser)
                .error(com.apc.cng_hpcl.R.mipmap.dispenser)

            Glide.with(mContext).load(imageUrl).apply(options).into(binding.dispImage)

        }




        readDisps(station)
        binding.dispImage.setOnClickListener {
        val action=   DispReadFragDirections.actionDispReadFragToDispPhotoFrag(station,username)
            action.disp=binding.selectDispenser.selectedItemPosition
            navController.navigate(action)
        }
        binding.dispTitle.setOnClickListener {
            val action=   DispReadFragDirections.actionDispReadFragToDispPhotoFrag(station,username)
            action.disp=binding.selectDispenser.selectedItemPosition
            navController.navigate(action)
        }

        binding.proceed.setOnClickListener {
                if(binding.manualDisp.text.toString().trim().isEmpty()){
                    Toast.makeText(mContext,"Enter reading !",Toast.LENGTH_LONG).show()
                }
                else{
                putDispReading(station,disps[binding.selectDispenser.selectedItemPosition].disp_id,binding.selectBay.selectedItem.toString(),binding.manualDisp.text.toString().trim(),username,imageUrl)
            }

        }

    }
    private fun readDisps(dbs: String) {
        val request: StringRequest = object : StringRequest(
            Method.POST, BASE_URL+"v2/cng_dispenser_v2.php?apicall=getDisps",
            Response.Listener { response ->
                Log.d("RESP>>>", response!!)
                try {
                    val obj = JSONObject(response)
                    val arr=obj.getJSONArray("data")

                    for (i in 0 until arr.length()) {
                        val item = arr.getJSONObject(i)
                        val arrBays=item.getJSONObject("active_bays").getJSONArray("bays")
                        val bays: MutableList<String> = ArrayList()

                        for (i1 in 0 until arrBays.length()) {
                            bays.add(arrBays.getString(i1))

                        }

                        val disp1=DispModel(ActiveBays(bays),item.getString("bays_count"),
                            item.getString("disp_id"),item.getString("name"),
                            item.getString("station_id"),item.getString("status"))
                        disps.add(disp1)
                        dispNames.add(disp1.name)
                        val spinnerArrayAdapterDisps = ArrayAdapter(
                            mContext,
                            R.layout.simple_spinner_item,
                            dispNames
                        )
                        spinnerArrayAdapterDisps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
                        binding.selectDispenser.adapter = spinnerArrayAdapterDisps
                        if(disp!=-1){
                            binding.selectDispenser.setSelection(disp)
                        }

                        binding.selectDispenser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                val spinnerArrayAdapterBays = ArrayAdapter(
                                    mContext,
                                    R.layout.simple_spinner_item,
                                    disps[position].active_bays.bays
                                )
                                spinnerArrayAdapterBays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
                                binding.selectBay.adapter = spinnerArrayAdapterBays
                                if(bay!=-1){
                                    binding.selectBay.setSelection(bay)
                                }
                            //  readReorderPoint();
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }

                        // Your code here
                    }
                        //                                removeSimpleProgressDialog();

                } catch (e: JSONException) {
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
                params["station_id"] = dbs
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }
    private fun putDispReading(station_id: String,disp_id:String,bay:String,reading:String,op_id:String,imgUrl:String) {
        val pd=ProgressDia()
        pd.show(childFragmentManager,"LOAD>>")
        val request: StringRequest = object : StringRequest(
            Method.POST, BASE_URL+"v2/cng_dispenser_v2.php?apicall=addReading",
            Response.Listener { response ->
                Log.d("RESP>>>", response!!)
                pd.dismiss()
                Toast.makeText(mContext,"Reading Submitted",Toast.LENGTH_LONG).show()
                requireActivity().finish()
            },
            Response.ErrorListener {

                //                progressDialog.dismiss();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>{
                val params: MutableMap<String, String> = java.util.HashMap()
                params["station_id"] = station_id
                params["bay"] = bay
                params["op_id"] = op_id
                params["reading"] = reading
                params["disp_id"] = disp_id
                params["image_url"] = imgUrl

                Log.d("RESP>>>", params.toString())

                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(request)
    }


}