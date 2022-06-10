package com.example.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuPresenter
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.bussiness.model.GeoCodeModel
import com.example.weatherapp.view.adapters.CityListAdapter
import com.example.weatherapp.view.createObservable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_menu.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.util.concurrent.TimeUnit

class MenuActivity : MvpAppCompatActivity(), com.example.weatherapp.view.MenuView {

    private val presenter by moxyPresenter { com.example.weatherapp.presenters.MenuPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        inner_toolbarMenu.setNavigationOnClickListener {
            onBackPressed()
        }

        presenter.enable()
        presenter.getFavouriteList()

        initCitiList(predictions)
        initCitiList(favorites)

        search_field.createObservable()
            .doOnNext { setLoading(true) }
            .debounce (2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (!it.isNullOrEmpty())  presenter.searchFor(it)
            }, {
                Log.d("123", "error")
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_left)
    }

    //--------------------------MOXY------------------------------
    override fun setLoading(flag: Boolean) {
        loading.isActivated = flag
        loading.visibility = if(flag) View.VISIBLE else View.GONE
    }

    override fun fillPredictionList(data: List<GeoCodeModel>) {
        (predictions.adapter as CityListAdapter).updateData(data)
    }

    override fun fillFavoriteList(data: List<GeoCodeModel>) {
        (favorites.adapter as CityListAdapter).updateData(data)
    }

    //--------------------------MOXY------------------------------

    private fun initCitiList(rv: RecyclerView){
        val cityAdapter = CityListAdapter()
        cityAdapter.clickListener = searchItemClickListener
        rv.apply {
            adapter = cityAdapter
            layoutManager = object : LinearLayoutManager(this@MenuActivity, VERTICAL,false){
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            setHasFixedSize(true)
        }

    }

    private val searchItemClickListener = object : CityListAdapter.SearchItemClickListener{
        override fun addToFavourites(item: GeoCodeModel) {
            presenter.saveLocation(item)
            Toast.makeText(applicationContext, "Добавленно в избарнное", Toast.LENGTH_SHORT).show()
        }

        override fun removeFromFavourites(item: GeoCodeModel) {
            presenter.removeLocation(item)
            Toast.makeText(applicationContext, "Удаленно из избарнного", Toast.LENGTH_SHORT).show()
        }

        override fun showWeatherIn(item: GeoCodeModel) {
            val intent = Intent(this@MenuActivity, MainActivity::class.java)
            val bundle = Bundle()
            bundle.putString("lat", item.lat.toString())
            bundle.putString("lon", item.lon.toString())
            intent.putExtra(COORDINATES, bundle)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_left)
        }
    }




}