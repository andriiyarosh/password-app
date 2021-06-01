package com.example.slickypasscode.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.slickypasscode.R
import com.example.slickypasscode.viewModel.MainViewModel
import kotlinx.android.synthetic.main.passcode_entry_layout.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val digitsViewsList = LinkedList<Button>()

    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.passcode_entry_layout)
        viewModel.fetchPasswordData()

        initDigitsList()

        viewModel.digits.observe(this, Observer {
            it.forEachIndexed { index, i ->
                digitsViewsList[index].text = i
            }
        })
    }

    private fun initDigitsList() {
        with(digitsViewsList) {
            add(top_left)
            add(top_center)
            add(top_right)
            add(center_left)
            add(center_center)
            add(center_right)
            add(bottom_left)
            add(bottom_center)
            add(bottom_right)
            add(bottom)
        }
    }
}