package com.apc.cng_hpcl.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.apc.cng_hpcl.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class GasReconciliation : Fragment() {

    private lateinit var tvInputGas: TextView
    private lateinit var tvOutputGas: TextView
    private lateinit var tvLoss: TextView
    private lateinit var tvBalance: TextView
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.gas_reconciliation, container, false)

        // Bind Views
        tvInputGas = view.findViewById(R.id.tvInputGas)
        tvOutputGas = view.findViewById(R.id.tvOutputGas)
        tvLoss = view.findViewById(R.id.tvLoss)
        tvBalance = view.findViewById(R.id.tvBalance)
        pieChart = view.findViewById(R.id.pieChart)

        // Example total gas
        val totalGas = 10000f

        // Example category data (replace with real values)
        val autoGas = 300f   // Auto (0–4kg)
        val carGas = 500f    // Car (5–8kg)
        val hmvGas = 185f    // HMV (10+kg)
        val lcvGas = 1500f   // LCV movement

        // Used gas
        val usedGas = autoGas + carGas + hmvGas + lcvGas

        // Loss calculation
        val loss = totalGas - usedGas

        // Set values in UI
        tvInputGas.text = "Total Gas: $totalGas SCM"
        tvOutputGas.text = "Used Gas: $usedGas SCM"
        tvLoss.text = "Loss: $loss SCM"
        tvLoss.setTextColor(Color.RED) // 🔴 Loss text red
        tvBalance.text = "Balance: ${(totalGas - (usedGas + loss))} SCM" // should be 0

        // Setup chart (without loss slice)
        setupPieChart(autoGas, carGas, hmvGas, lcvGas)

        return view
    }

    private fun setupPieChart(autoGas: Float, carGas: Float, hmvGas: Float, lcvGas: Float) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(autoGas, "Auto (0–4kg)"))
        entries.add(PieEntry(carGas, "Car (5–8kg)"))
        entries.add(PieEntry(hmvGas, "HMV (10+kg)"))
        entries.add(PieEntry(lcvGas, "LCV Movement"))

        val dataSet = PieDataSet(entries, "Gas Distribution")
        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"), // Auto - Green
            Color.parseColor("#2196F3"), // Car - Blue
            Color.parseColor("#FF9800"), // HMV - Orange
            Color.parseColor("#9C27B0")  // LCV - Purple
        )
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)

        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Gas Split"
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(1000)
    }
}
