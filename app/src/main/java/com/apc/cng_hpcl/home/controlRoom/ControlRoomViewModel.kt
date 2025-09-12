package com.apc.cng_hpcl.home.controlRoom

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ControlRoomViewModel @Inject constructor(

)  : ViewModel() {


     var issues = MutableStateFlow<MutableList<IssueDataModel>>(mutableListOf())
     var issueNames = MutableStateFlow<MutableList<String>>(mutableListOf("Select"))
     var issueId=MutableLiveData<String>("")
     var issueName=MutableLiveData<String>("")
     var stationId=MutableLiveData<String>("")
     var raisedBy=MutableLiveData<String>("")
     var remarks=MutableLiveData<String>("")
     var lcvPressureReading=MutableLiveData<String>("")
     var lcvPressureImgUrl=MutableLiveData<String>("")
     var lcvPressureImgPath=MutableLiveData<String>("")
     var stationPressureReading=MutableLiveData<String>("")
     var stationPressureImgUrl=MutableLiveData<String>("")
     var stationPressureImgPath=MutableLiveData<String>("")
     var ticketStatus=MutableLiveData<String>("Pending")
     var lcvNum=MutableLiveData<String>("")
     var latlng=MutableLiveData<String>("")






 /*   val bps: MutableLiveData<MutableList<ImageData>>
        get() = _bps

    fun addValueToList(listValue: ImageData){
        // In the following line: _sectionList.value is still null, so this will
        // never call the add
        _bps.value?.add(listValue)
    }
    init {
        _bps.value = mutableListOf<ImageData>()
    }*/

}
