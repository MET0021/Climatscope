package com.myapp.climatscope.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.myapp.climatscope.domain.entities.City

private const val DATABASE_NAME = "climatscope.db"
private const val DATABASE_VERSION = 1

private const val CITY_TABLE_NAME = "city"
private const val CITY_KEY_ID = "id"
private const val CITY_KEY_NAME = "name"

private const val CITY_TABLE_CREATE = """
    CREATE TABLE $CITY_TABLE_NAME (
        $CITY_KEY_ID INTEGER PRIMARY KEY,
        $CITY_KEY_NAME TEXT
)    
"""
private const val CITY_QUERY_SELECT_ALL = "SELECT * FROM $CITY_TABLE_NAME"

class CityLocalDataSource(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val TAG = CityLocalDataSource::class.java.simpleName

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CITY_TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if needed
    }

    suspend fun createCity(city: City): Boolean {
        return try {
            val values = ContentValues().apply {
                put(CITY_KEY_NAME, city.name)
            }

            Log.d(TAG, "creating City: $values")
            val id = writableDatabase.insert(CITY_TABLE_NAME, null, values)
            id > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error creating city", e)
            false
        }
    }

    suspend fun getAllCities(): List<City> {
        val cities = mutableListOf<City>()
        try {
            readableDatabase.rawQuery(CITY_QUERY_SELECT_ALL, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val city = City(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(CITY_KEY_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(CITY_KEY_NAME))
                    )
                    cities.add(city)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cities", e)
        }
        return cities
    }

    suspend fun deleteCity(city: City): Boolean {
        return try {
            Log.d(TAG, "delete city: $city")
            val deleteCount = writableDatabase.delete(
                CITY_TABLE_NAME,
                "$CITY_KEY_ID = ?",
                arrayOf(city.id.toString())
            )
            deleteCount == 1
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting city", e)
            false
        }
    }
}
