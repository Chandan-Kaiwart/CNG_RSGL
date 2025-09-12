package com.apc.cng_hpcl.home.newDisp

data class DispModel(
    val active_bays: ActiveBays,
    val bays_count: String,
    val disp_id: String,
    val name: String,
    val station_id: String,
    val status: String
)