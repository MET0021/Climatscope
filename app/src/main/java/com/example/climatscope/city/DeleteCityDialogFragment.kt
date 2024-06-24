package com.example.climatscope.city

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.climatscope.R

class DeleteCityDialogFragment : DialogFragment() {
    interface DeleteCityDialogListener {
        fun onDeletePositiveClick()
        fun onDeleteNegativeClick()
    }

    companion object {
        const val EXTRA_CITY_NAME = "EXTRA_CITY_NAME"

        fun newInstance(cityName : String): DeleteCityDialogFragment {
            val fragment = DeleteCityDialogFragment()
            fragment.arguments = Bundle().apply {
                putString(EXTRA_CITY_NAME, cityName)
            }

            return fragment
        }
    }
    private lateinit var cityName : String
    var listener : DeleteCityDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cityName = arguments?.getString(EXTRA_CITY_NAME).toString()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.delete_city_dialog_title, cityName))
            .setPositiveButton(getString(R.string.delete_city_positive)) { _, _ ->
                listener?.onDeletePositiveClick()
            }
            .setNegativeButton(getString(R.string.delete_city_nagative)) { _, _ ->
                listener?.onDeleteNegativeClick()
            }

        return builder.create()
    }
}