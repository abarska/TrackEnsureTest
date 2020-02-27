package com.abarska.trackensuretest.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

fun String.showToast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_SHORT).show()

fun String.showInfoLog() = Log.i("MY_TAG", this)