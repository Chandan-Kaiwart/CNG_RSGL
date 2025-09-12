package com.apc.cng_hpcl.home.newTrans

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransViewModel @Inject constructor(
)  : ViewModel() {
   // private var _bps= MutableLiveData<MutableList<ImageData>>()
    val mfmPath=MutableLiveData<String>()
    val mfmUrl=MutableLiveData<String>()
    val presPath=MutableLiveData<String>()
    val presUrl=MutableLiveData<String>()
    val lpPath=MutableLiveData<String>()
    val lpUrl=MutableLiveData<String>()
    val mpPath=MutableLiveData<String>()
    val mpUrl=MutableLiveData<String>()
    val hpPath=MutableLiveData<String>()
    val hpUrl=MutableLiveData<String>()
    val tempPath=MutableLiveData<String>()
    val tempUrl=MutableLiveData<String>()
    var lat=MutableLiveData<String>("")
    var long=MutableLiveData<String>("")
    var stationId=MutableLiveData<String>("")
    var presValue=MutableLiveData<String>("")
    var lpValue=MutableLiveData<String>("")
    var mpValue=MutableLiveData<String>("")
    var hpValue=MutableLiveData<String>("")
    var lmValue=MutableLiveData<String>("0")
    var mmValue=MutableLiveData<String>("0")
    var mfmValue=MutableLiveData<String>("0")
    var hmValue=MutableLiveData<String>("0")
    var tempValue=MutableLiveData<String>("")
    var massValue=MutableLiveData<String>("0")
    var latlng=MutableLiveData<String>("")

    var vol=MutableLiveData<String>("0")
 var volLp=MutableLiveData<String>("0")
    var volMp=MutableLiveData<String>("0")
    var volHp=MutableLiveData<String>("0")
    var tempMake=MutableLiveData<Int>(-1)
    var lpMake=MutableLiveData<Int>(-1)
    var mpMake=MutableLiveData<Int>(-1)
    var hpMake=MutableLiveData<Int>(-1)






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
