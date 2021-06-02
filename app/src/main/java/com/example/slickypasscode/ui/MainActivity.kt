package com.example.slickypasscode.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.slickypasscode.R
import com.example.slickypasscode.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.passcode_entry_layout.*
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val digitsViewsList by lazy {
        LinkedList<Button>().apply {
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

    private val passwordViewList by lazy {
        LinkedList<ImageView>().apply {
            add(pin_first)
            add(pin_second)
            add(pin_third)
            add(pin_fourth)
        }
    }

    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.passcode_entry_layout)
        viewModel.fetchPasswordData()

        initDigits()

        button_delete.setOnClickListener {
            viewModel.deleteInput()
        }
        button_cancel.setOnClickListener {
            viewModel.cleanInput()
        }

        viewModel.currentPasswordLD.observe(this, Observer {
            drawFilledPasswordViews(it)
        })

        viewModel.digits.observe(this, Observer {
            it.forEachIndexed { index, i ->
                digitsViewsList[index].text = i.toString()
            }
        })

        viewModel.validationResult.observe(this, Observer {
            handleValidationResult(it)
        })
    }

    private fun drawFilledPasswordViews(filledCount: Int) {
        passwordViewList.forEach { passwordView ->
            passwordView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_progressempty
                )
            )
        }

        passwordViewList.subList(0, filledCount).forEach { imageView ->
            imageView.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_progressfull)
            )
        }
    }

    private fun initDigits() {
        digitsViewsList.forEach { button ->
            button.setOnClickListener {
                it as Button
                viewModel.addInput(it.text.toString().toInt())
            }
        }
    }

    private fun handleValidationResult(validationState: MainViewModel.ValidationState) {
        progress_circular.visibility = View.INVISIBLE
        passwordViewList.onEach { it.visibility = View.VISIBLE }
        when(validationState) {
            MainViewModel.ValidationState.STARTED -> {
                passwordViewList.onEach { it.visibility = View.INVISIBLE }
                progress_circular.visibility = View.VISIBLE
            }
            MainViewModel.ValidationState.VALID -> {
                Log.i(MainActivity::class.simpleName, "validation succeed")
            }
            MainViewModel.ValidationState.INVALID -> {
                Log.i(MainActivity::class.simpleName, "validation failed")
            }
        }
    }
}