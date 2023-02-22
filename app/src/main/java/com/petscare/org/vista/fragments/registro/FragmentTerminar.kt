package com.petscare.org.vista.fragments.registro

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.petscare.org.R
import com.petscare.org.databinding.FragmentNombreBinding
import com.petscare.org.databinding.FragmentTerminarBinding
import com.petscare.org.vista.activitys.ActivityMenu

class FragmentTerminar : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Toast.makeText(context,"Registro de datos exitoso", Toast.LENGTH_SHORT).show()
        return inflater.inflate(R.layout.fragment_terminar, container, false)

    }
}