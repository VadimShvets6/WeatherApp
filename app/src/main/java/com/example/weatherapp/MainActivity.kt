package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.bussiness.model.DailyWeatherModel
import com.example.weatherapp.bussiness.model.HourlyWeatherModel
import com.example.weatherapp.bussiness.model.WeatherDataModel
import com.example.weatherapp.presenters.MainPresenter
import com.example.weatherapp.view.*
import com.example.weatherapp.view.adapters.MainDailyListAdapter
import com.example.weatherapp.view.adapters.MainHourlyListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.lang.Exception
import kotlin.math.roundToInt

const val COORDINATES = "coordinates"
class MainActivity : MvpAppCompatActivity(), MainView {

    private fun checkPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Нам нужны гео данные")
                .setMessage("Пожалуйста разрешите доступ к вашим гео данным для продолжения работы приложения.")
                .setPositiveButton("ОК") { _,_ ->
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        GEO_LOCATION_REQUEST_COD_SUCCESS
                    )
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        GEO_LOCATION_REQUEST_COD_SUCCESS
                    )
                }
                .setNegativeButton("Закрыть") { dialog,_ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private val mainPresenter by moxyPresenter { MainPresenter() }

    private val tokenSource: CancellationTokenSource = CancellationTokenSource()
    private val geoService by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val locationRequest by lazy {
        com.google.android.gms.location.LocationRequest.create().apply {
            interval = 600000
            fastestInterval = 5000
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
    private lateinit var  mLocation : Location

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        mainPresenter.enable()

        initBottomSheets()
        initSwipeRefresh()

        refresh.isRefreshing = true

        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, DailyListFragment(), DailyListFragment::class.simpleName).commit()


        if(!intent.hasExtra(COORDINATES)){
            checkGeoAvailability()
            getGeo()
        } else {
            val coord = intent.extras!!.getBundle(COORDINATES)!!
            val loc = Location("")
            loc.latitude = coord.getString("lat")!!.toDouble()
            loc.longitude = coord.getString("lon")!!.toDouble()
            mLocation = loc
            mainPresenter.refresh(lat = mLocation.latitude.toString(), lon = mLocation.longitude.toString())
        }

        main_setting_btn.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out)
        }

        main_menu_btn.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_in)
        }

        main_hourly_list.apply {
            adapter = MainHourlyListAdapter()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }


    }


    // -------------------------- location code -----------------


    private val geoCallback = object : LocationCallback() {
        override fun onLocationResult(geo: LocationResult) {
            mLocation = geo.locations[0]
            mainPresenter.refresh(mLocation.latitude.toString(), mLocation.longitude.toString())
        }
    }

    // -------------------------- location code -----------------


    //--------------------- MOXY code ------------------------

    override fun displayLocation(data: String) {
       main_city_name_tv.text = data
    }


    override fun displayCurrentData(data: WeatherDataModel) {
        data.apply {
            main_date_tv.text = current.dt.toDateFormatOf(DAY_FULL_MONTH_NAME)
            main_weather_condition_icon.setImageResource(current.weather[0].icon.provideIcon())
            main_temp.text = StringBuilder().append(current.temp.toDegree()).append("\u00B0").toString()
            daily[0].temp.apply {
                main_temp_min_tv.text = StringBuilder().append(min.toDegree()).append("\u00B0").toString()
                main_temp_medium_tv.text = StringBuilder().append(((min.roundToInt() + max.roundToInt() - 546.3) / 2).roundToInt().toString()).append("\u00B0").toString()
                main_temp_max_tv.text = StringBuilder().append(max.toDegree()).append("\u00B0").toString()
            }

            val pressureSet = SettingsHolder.pressure
            main_pressure_mu_tv.text = getString(
                pressureSet.mesureUnitStringRes.toInt(),
                pressureSet.getValue(current.pressure.toDouble())
            )

            val windSpeedSet = SettingsHolder.windSpeed
            main_wind_speed_tv.text = getString(
                windSpeedSet.mesureUnitStringRes.toInt(),
                windSpeedSet.getValue(current.wind_speed)
            )

            main_weather_condition_description.text = current.weather[0].description
            main_weather_image.setImageResource(current.weather[0].icon.provideWeatherImage())
           // main_pressure_mu_tv.text = StringBuilder().append(current.pressure.toString()).append(" пГа").toString()
            main_humidity_tv.text = StringBuilder().append(current.humidity.toString()).append(" %").toString()
            //main_wind_speed_tv.text = StringBuilder().append(current.wind_speed.toString()).append(" м/с").toString()
            main_sunrise_tv.text = current.sunrise.toDateFormatOf(HOUR_DOUBLE_DOT_MINUTE)
            main_sunset_tv.text = current.sunset.toDateFormatOf(HOUR_DOUBLE_DOT_MINUTE)
        }

    }

    override fun displayHourlyData(data: List<HourlyWeatherModel>) {
        (main_hourly_list.adapter as MainHourlyListAdapter).updateData(data)
    }

    override fun displayDailyData(data: List<DailyWeatherModel>) {
        (supportFragmentManager.findFragmentByTag(DailyListFragment::class.simpleName) as DailyListFragment).setData(data)
    }

    override fun displayError(error: Throwable) {
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
    }

    override fun setLoading(flag: Boolean) {
        refresh.isRefreshing = flag
    }

    private fun initBottomSheets(){
        main_bottom_sheets.isNestedScrollingEnabled = true
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        main_bottom_sheets_container.layoutParams =
            CoordinatorLayout.LayoutParams(size.x, (size.y * 0.5).toInt())
    }

    private fun initSwipeRefresh() {
        refresh.apply {
            setColorSchemeResources(R.color.purple_700)
            setProgressViewEndTarget(false, 280)
            setOnRefreshListener {
                mainPresenter.refresh(mLocation.latitude.toString(), mLocation.longitude.toString())
            }
        }
    }


    //-------------- geo -------------------
    @SuppressLint("MissingPermission")
    private fun getGeo(){
        geoService
            .getCurrentLocation(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY, tokenSource.token)
            .addOnSuccessListener {
                if(it!=null){
                    mLocation = it
                    mainPresenter.refresh(mLocation.latitude.toString(), mLocation.longitude.toString())
                }else{
                    displayError(Exception("Geodata is not available"))
                }
            }
    }

    private fun checkGeoAvailability() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, 100)
                } catch (sendEx: IntentSender.SendIntentException) {

                }
            }
        }
    }
    //-------------- geo -------------------


    //--------------------- MOXY code ------------------------


}