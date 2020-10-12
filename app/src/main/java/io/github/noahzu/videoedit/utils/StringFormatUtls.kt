package com.haoqi.teacher.videoedit.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object StringFormatUtls {
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.CHINA)

    fun formatMillSecondsToString(time: Long): String {
        val date = Date(time)
        return dateFormat.format(date)
    }

    fun highLightPartString(string: String, patternStr: String, @ColorInt color: Int): SpannableString {
        val spannableString = SpannableString(string)
        val pattern = Pattern.compile(patternStr)
        val matcher = pattern.matcher(string)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.regionEnd()
            spannableString.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannableString
    }
}