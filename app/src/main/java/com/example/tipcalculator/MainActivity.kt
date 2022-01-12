package com.example.tipcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity-C"
    }

    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etBaseAmount  = findViewById(R.id.etBaseAmount)
        seekBarTip    = findViewById(R.id.seekBarTip)
        tvTipPercent  = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount   = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)

        computeTipAndTotal(0)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                computeTipAndTotal(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Toast.makeText(this@MainActivity    , "Total$ = Base$ + Tip$", Toast.LENGTH_SHORT).show()
            }

        })

        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                computeTipAndTotal(seekBarTip.progress)
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
    }

    private fun computeTipAndTotal(tipPercent: Int) {
        if(etBaseAmount.text.isEmpty()) {
            Toast.makeText(this, "Enter Base Amount", Toast.LENGTH_SHORT).show()
            seekBarTip.progress = 15
            tvTipPercent.text = "0%"
            tvTipAmount.text = ""
            tvTotalAmount.text = ""

            return
        }
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipAmt = baseAmount * tipPercent / 100.0
        val totalAmt = baseAmount + tipAmt
        tvTipPercent .text = "$tipPercent%"
        tvTipAmount  .text = "$%.2f".format(tipAmt)
        tvTotalAmount.text = "$%.2f".format(totalAmt)

    }
}