package com.example.weatherapp.view

import android.text.Editable
import android.text.TextWatcher
import com.example.weatherapp.R
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

const val DAY_FULL_MONTH_NAME = "dd MMMM"
const val DAY_WEEK_NAME_LONG = "dd EEEE"
const val HOUR_DOUBLE_DOT_MINUTE = "HH:mm"


fun Long.toDateFormatOf(format : String) : String {
    val cal = Calendar.getInstance()
    val timeZone = cal.timeZone
    val sdf = SimpleDateFormat(format,Locale.getDefault())
    sdf.timeZone = timeZone
    return sdf.format(Date(this * 1000))
}

fun Double.toDegree() = SettingsHolder.temp.getValue(this)

fun Double.toPercentString(extraPart : String = "") = (this * 100).roundToInt().toString() + extraPart

fun String.provideIcon() = when(this){
    "01d" -> R.drawable.ic_sunny
    "01n" -> R.drawable.ic_night_clear
    "02n", "02d" -> R.drawable.ic_partly_cloudy
    "03n", "03d" -> R.drawable.ic_partly_cloudy
    "04n", "04d" -> R.drawable.ic_mostly_cloudy
    "09n", "09d" -> R.drawable.ic_showers
    "10n", "10d" -> R.drawable.ic_showers
    "11n", "11d" -> R.drawable.ic_thunderstroms
    "13n", "13d" -> R.drawable.ic_snow
    "50n", "50d" -> R.drawable.ic_cloudy
    else -> R.drawable.ic_error
}

fun String.provideWeatherImage() = when(this){
    "01d" -> R.mipmap.sunnyx3
    "01n" -> R.mipmap.night_clear3x
    "02n", "02d" -> R.mipmap.partly_cloudy3x
    "03d" -> R.mipmap.partly_cloudy3x
    "03n" -> R.mipmap.night_perly_cloud3x
    "04n", "04d" -> R.mipmap.clowd3x
    "09n", "09d" -> R.mipmap.showers3x
    "10n", "10d" -> R.mipmap.showers3x
    "11n", "11d" -> R.mipmap.thunderstorm3x
    "13n", "13d" -> R.mipmap.snow3x
    "50n", "50d" -> R.mipmap.clowd3x
    else -> R.drawable.ic_error
}

fun TextInputEditText.createObservable() : Flowable<String> {
    return Flowable.create({
        addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                it.onNext(s.toString())
            }
        })

    }, BackpressureStrategy.BUFFER)
}

private abstract class SimpleTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
    }
}