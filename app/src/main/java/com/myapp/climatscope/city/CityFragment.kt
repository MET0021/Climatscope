package com.myapp.climatscope.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapp.climatscope.App
import com.myapp.climatscope.DataBase
import com.myapp.climatscope.R
import com.myapp.climatscope.utils.toast

class CityFragment : Fragment(), CityAdapter.CityItemListener {

    interface CityFragmentListener {
        fun onCitySelected(city: City)
        fun onEmptyCities()
    }

    lateinit var listener: CityFragmentListener
    private lateinit var database: DataBase
    private lateinit var cities: MutableList<City>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = App.dataBase
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_city, container, false)
        recyclerView = view.findViewById(R.id.cities_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cities = database.getAllCities()

        adapter = CityAdapter(cities, this)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_city, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_create_city -> {
                showCreateCityDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCitySelected(city: City) {
        listener.onCitySelected(city)
    }

    fun selectFirstCity() {
        when (cities.isEmpty()) {
            true -> listener.onEmptyCities()
            false -> listener.onCitySelected(cities.first())
        }
    }

    override fun onCityDeleted(city: City) {
        showDeleteCityDialog(city)
    }


    private fun showCreateCityDialog() {
        val createCityFragment = CreateCityDialogFragment()
        createCityFragment.listener = object : CreateCityDialogFragment.CreateCityDialogListener {
            override fun onDialogPositiveClick(cityName: String) {
                saveCity(City(cityName))
            }

            override fun onDialogNegativeClick() {
            }

        }
        createCityFragment.show(parentFragmentManager, "CreateCityDialogFragment")
    }

    private fun showDeleteCityDialog(city: City) {
        val deleteCityFragment = DeleteCityDialogFragment.newInstance(city.name)
        deleteCityFragment.listener = object : DeleteCityDialogFragment.DeleteCityDialogListener {
            override fun onDeletePositiveClick() {
                deleteCity(city)
            }

            override fun onDeleteNegativeClick() {
            }

        }

        deleteCityFragment.show(parentFragmentManager, "DeleteCityDialogFragment")
    }

    private fun saveCity(city: City) {
        if (database.createCity(city)) {
            cities.add(city)
            adapter.notifyDataSetChanged()
        } else {
            context?.toast(getString(R.string.could_not_create_city))
        }
    }

    private fun deleteCity(city: City) {
        if (database.deleteCity(city)) {
            cities.remove(city)
            adapter.notifyDataSetChanged()
            selectFirstCity()
            context?.toast(getString(R.string.city_message_delete_info, city.name))
        } else {
            context?.toast(getString(R.string.could_not_delete_city, city.name))
        }
    }
}