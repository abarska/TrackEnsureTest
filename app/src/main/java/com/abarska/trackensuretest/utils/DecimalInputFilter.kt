package com.abarska.trackensuretest.utils

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class DecimalInputFilter(digitsBefore: Int, digitsAfter: Int) : InputFilter {

    private val moneyPattern: Pattern =
        Pattern.compile("[0-9]{0," + (digitsBefore - 1) + "}+((\\.[0-9]{0," + (digitsAfter - 1) + "})?)||(\\.)?")

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        result: Spanned?,
        resultStart: Int,
        resultEnd: Int
    ): CharSequence? {
        val matcher = moneyPattern.matcher(result)
        if (!matcher.matches()) return ""
        return null
    }
}