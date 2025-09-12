package com.apc.cng_hpcl.util

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.apc.cng_hpcl.R

class ProgressDia : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val rootView: View = inflater.inflate(R.layout.progress_dia, null, false)
        var dialog = AlertDialog.Builder(requireActivity()).setView(rootView)
            .show()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}