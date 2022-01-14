package com.example.tipcalculator

import android.animation.ArgbEvaluator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type

const val TIP_DATA = "tip_history"
data class TipData(val baseAmount: Double, val tip: Int, val totalAmount: Double, val splitNum: Int = 1) : Serializable
val tipHistory = mutableListOf<TipData>()

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity-C"
        const val PREF = "TipHistory"
    }

    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipCaption: TextView
    private lateinit var tvPerPersonAmount: TextView
    private lateinit var seekBarSplit: SeekBar

//    private val gson = Gson()
//    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etBaseAmount  = findViewById(R.id.etBaseAmount)
        seekBarSplit    = findViewById(R.id.seekBarSplit)
        tvTipPercent  = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount   = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipCaption  = findViewById(R.id.tvTipCaption)
        tvPerPersonAmount = findViewById(R.id.tvPerPersonAmount)
        seekBarTip    = findViewById(R.id.seekBarTip)

        // load tipping history on create
//        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
//        sharedPrefs = getSharedPreferences(PREF, Context.MODE_PRIVATE)
//        val listType: Type = object: TypeToken<MutableList<TipData>>(){}.type
//        val history = gson.fromJson<MutableList<TipData>>(sharedPrefs.getString(TIP_DATA, "[]"), listType)
//        Log.i(TAG, "history loaded")
//
//        for(item in history) {
//            tipHistory.add(0,item)
//        }

        loadHistory()

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

        seekBarSplit.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                computeTipAndTotal(seekBarTip.progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Toast.makeText(this@MainActivity    , "Total$ = Base$ + Tip$", Toast.LENGTH_SHORT).show()
            }

        })

        val btnPay = findViewById<Button>(R.id.btnPay)
        btnPay.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                Toast.makeText(this@MainActivity, "Payment Initiated", Toast.LENGTH_LONG).show()

                if(computeTipAndTotal(seekBarTip.progress)) {
                    val baseAmount      = etBaseAmount.text.toString().toDouble()
                    val tip             = seekBarTip.progress
                    val totalAmount     = tvTotalAmount.text.toString().substring(1).toDouble()
                    val splitNum        = seekBarSplit.progress + 1

                    val tipData = TipData(baseAmount, tip, totalAmount, splitNum)

                    tipHistory.add(tipData)

                    clearInput()
                }

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        saveHistory()
        // save tipping history on destroy
//        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
//        val historyJson = gson.toJson(tipHistory)
//        sharedPrefs.edit().putString(TIP_DATA, historyJson).apply()
//        Log.i(TAG, "history saved")
    }

    private fun updateCaption(progress: Int) {
        tvTipCaption.text = when(progress) {
            in 0..5 -> "Poor"
            in 5..15 -> "Acceptable"
            in 15..20 -> "Good"
            in 20..25 -> "Great"
            else -> "Awesome"
        }
        val progressFraction = progress.toFloat() / 30
        val color = ArgbEvaluator().evaluate(progressFraction,
                                             ContextCompat.getColor(this, R.color.red),
                                             ContextCompat.getColor(this, R.color.green)) as Int
        tvTipCaption.setTextColor(color)
    }

    private fun computeTipAndTotal(tipPercent: Int): Boolean {
        if(etBaseAmount.text.isEmpty()) {
            Toast.makeText(this, "Enter Base Amount", Toast.LENGTH_SHORT).show()
            seekBarTip.progress = 15
            tvTipPercent.text = "15%"
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            tvPerPersonAmount.text = ""
            seekBarSplit.progress = 0

            return false
        }
        try {
            val baseAmount = etBaseAmount.text.toString().toDouble()
            val tipAmt = baseAmount * tipPercent / 100.0
            val totalAmt = Math.ceil(baseAmount + tipAmt)
            val splitVal = seekBarSplit.progress
            val numPerson = splitVal + 1
            val perPersonAmt = totalAmt / numPerson

            tvTipPercent .text = "$tipPercent%"
            tvTipAmount  .text = "$%.2f".format(tipAmt)
            tvTotalAmount.text = "$%.2f".format(totalAmt)
            tvPerPersonAmount.text = "$%.2f".format(perPersonAmt)

            updateCaption(tipPercent)
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Enter Valid Amount", Toast.LENGTH_LONG).show()
            etBaseAmount.text.clear()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)

        return true
    }

    private fun saveHistory() {
        val sharedPrefs = getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val historyJson = Gson().toJson(tipHistory)
        sharedPrefs.edit().putString(TIP_DATA, historyJson).apply()

        Log.i(TAG, "History Saved")
    }
    private fun loadHistory() {
        val sharedPrefs = getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val listType = object: TypeToken<MutableList<TipData>>(){}.type
        val historyJson = sharedPrefs.getString(TIP_DATA, "[]")
        val history = Gson().fromJson<MutableList<TipData>>(historyJson, listType)
        tipHistory.clear()
        tipHistory.addAll(0, history)
        Log.i(TAG, "History Loaded")
    }
    private fun clearInput() {
        etBaseAmount.text.clear()
        seekBarTip.progress = 15
        tvTipPercent.text = "15%"
        tvTipAmount.text = ""
        tvTotalAmount.text = ""
        tvPerPersonAmount.text = ""
        seekBarSplit.progress = 0
    }
}