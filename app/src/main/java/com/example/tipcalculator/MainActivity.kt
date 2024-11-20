package com.example.tipcalculator

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextAmount: EditText = findViewById(R.id.editTextAmount)
        val seekBar: SeekBar = findViewById(R.id.seekBar)
        val tipFeedback: TextView = findViewById(R.id.tip_feedback)
        val tipAmount: TextView = findViewById(R.id.tip_amount)
        val grandTotalAmount: TextView = findViewById(R.id.grand_total_amount)
        val tipPercentageLabel: TextView = findViewById(R.id.seek_bar_label)
        val imageView: ImageView = findViewById(R.id.imageView)

        sharedPreferences = getSharedPreferences("TipCalculatorPrefs", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        seekBar.max = 35
        seekBar.progress = 0

        val savedTipPercentage = sharedPreferences.getInt("tip_percentage", 0)
        val savedTipDescription = sharedPreferences.getString("tip_description", "Poor")

        seekBar.progress = savedTipPercentage
        tipPercentageLabel.text = "$savedTipPercentage%"
        updateTipFeedback(savedTipPercentage, tipFeedback, imageView)
        tipFeedback.text = savedTipDescription


        editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculate(s.toString(), seekBar.progress, tipAmount, grandTotalAmount)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //https://developer.android.com/reference/android/widget/SeekBar.OnSeekBarChangeListener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateTipFeedback(progress, tipFeedback, imageView)
                tipPercentageLabel.text = "$progress%"
                calculate(editTextAmount.text.toString(), progress, tipAmount, grandTotalAmount)
                editor.putInt("tip_percentage", progress)
                editor.putString("tip_description", tipFeedback.text.toString())
                editor.apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


    }

    private fun calculate(billInput: String, tipPercentage: Int, tipAmount: TextView, grandTotalAmount: TextView) {
        val billAmount = billInput.toDoubleOrNull() ?: 0.0
        val tip = billAmount * tipPercentage / 100
        val grandTotal = billAmount + tip

        tipAmount.text = String.format("$%.2f", tip)
        grandTotalAmount.text = String.format("$%.2f", grandTotal)
    }

    private fun updateTipFeedback(progress: Int, tipFeedback: TextView, imageView: ImageView) {
        if (progress >= 0 && progress <= 9) {
            tipFeedback.text = "Poor"
            tipFeedback.setTextColor(resources.getColor(R.color.poor))
            imageView.setImageResource(R.drawable.poorcinna)
        } else if (progress >= 10 && progress <= 15) {
            tipFeedback.text = "OK"
            tipFeedback.setTextColor(resources.getColor(R.color.ok))
            imageView.setImageResource(R.drawable.itsokcinna)
        } else if (progress >= 16 && progress <= 20) {
            tipFeedback.text = "Good"
            tipFeedback.setTextColor(resources.getColor(R.color.good))
            imageView.setImageResource(R.drawable.thankfulcinna)
        } else if (progress >= 21 && progress <= 25) {
            tipFeedback.text = "Great"
            tipFeedback.setTextColor(resources.getColor(R.color.great))
            imageView.setImageResource(R.drawable.happycinna)
        } else if (progress >= 26 && progress <= 35) {
            tipFeedback.text = "Awesome"
            tipFeedback.setTextColor(resources.getColor(R.color.awesome))
            imageView.setImageResource(R.drawable.cinnaveryhappy)
        }
    }

    override fun onStop() {
        super.onStop()
        editor.putInt("tip percentage", 0)
        editor.putString("tip description", "Poor")
        editor.apply()
    }


}