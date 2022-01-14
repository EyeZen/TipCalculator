package com.example.tipcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class HistoryActivity : AppCompatActivity() {
    companion object {
        const val TAG = "HistoryActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

            val historyJson = Gson().toJson(tipHistory)
            Log.i(TAG, historyJson)

        val rvHistory = findViewById<RecyclerView>(R.id.rvHistory)
        // layout manager
        rvHistory.layoutManager = LinearLayoutManager(this)
        // ViewAdapter
        rvHistory.adapter = HistoryViewAdapter(tipHistory)
    }

}