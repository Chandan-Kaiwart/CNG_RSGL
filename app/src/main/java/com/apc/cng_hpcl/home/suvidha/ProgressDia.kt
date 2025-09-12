package com.apc.cng_hpcl.home.suvidha

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.apc.cng_hpcl.R

class ProgressDia : AppCompatDialogFragment() {
    private lateinit var dialog: AlertDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val rootView: View = inflater.inflate(R.layout.progress_dia, null, false)
        dialog = AlertDialog.Builder(requireActivity()).setView(rootView).show()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}