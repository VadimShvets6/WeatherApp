package com.example.weatherapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.weatherapp.R
import com.example.weatherapp.bussiness.model.DailyWeatherModel
import com.example.weatherapp.view.*
import com.google.android.material.textview.MaterialTextView
import java.lang.StringBuilder

class MainDailyListAdapter : BaseAdapter<DailyWeatherModel>(){

    interface DayItemClick {
        fun showDetails(data: DailyWeatherModel)
    }

    lateinit var clickListener : DayItemClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_daily, parent, false)
        return DailyViewHolder(view)
    }

    inner class DailyViewHolder(view : View) : BaseViewHolder(view){

        lateinit var container : CardView
        lateinit var date : MaterialTextView
        lateinit var popRate : MaterialTextView
        lateinit var maxTemp : MaterialTextView
        lateinit var minTemp : MaterialTextView
        lateinit var icon : ImageView

        init {
            container = itemView.findViewById(R.id.day_container)
            date = itemView.findViewById(R.id.item_daily_dates_tv)
            popRate = itemView.findViewById(R.id.item_daily_pop_tv)
            maxTemp = itemView.findViewById(R.id.item_daily_max_temp_tv)
            minTemp = itemView.findViewById(R.id.item_daily_min_temp_tv)
            icon = itemView.findViewById(R.id.item_daily_weather_condition_icon)
        }

        override fun bindView(position: Int) {

            val itemData = mData[position]
            val defaultTextColor = date.textColors

            if(position == 0){
                date.setTextColor(ContextCompat.getColor(date.context, R.color.purple_500))
            } else {
                date.setTextColor(defaultTextColor)
            }

            container.setOnClickListener {
                clickListener.showDetails(itemData)
            }

            if(mData.isNotEmpty()){
                itemData.apply {
                    val dateOfDay = dt.toDateFormatOf(DAY_WEEK_NAME_LONG)
                    date.text = if(dateOfDay.startsWith("0", true)) dateOfDay.removePrefix("0") else dateOfDay

                    if(pop < 0.01){
                        popRate.visibility = View.INVISIBLE
                    } else {
                        popRate.visibility = View.VISIBLE
                        popRate.text = pop.toPercentString("%")
                    }
                    icon.setImageResource(weather[0].icon.provideIcon())
                    minTemp.text = itemView.context.getString(R.string.degree_symbol, temp.min.toDegree())
                    maxTemp.text = itemView.context.getString(R.string.degree_symbol, temp.max.toDegree())
                }
            }

        }
    }
}