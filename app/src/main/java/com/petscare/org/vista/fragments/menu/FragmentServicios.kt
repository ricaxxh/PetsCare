package com.petscare.org.vista.fragments.menu
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.petscare.org.R
import com.petscare.org.databinding.FragmentServiciosBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.petscare.org.domain.providers.PetServices
import com.petscare.org.modelo.objetos.PetService

class FragmentServicios : Fragment(), OnMapReadyCallback {

    private var binding: FragmentServiciosBinding? = null

    private lateinit var gmap : GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var location_request : LocationRequest
    private lateinit var fused_location : FusedLocationProviderClient

    private val location_callback = object : LocationCallback(){
        override fun onLocationResult(location_result: LocationResult) {
            super.onLocationResult(location_result)
            for (location in location_result.locations){
                if (requireContext().applicationContext != null){
                    //Obtener la ubicacion del ususario en tiempo real
                    gmap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                        .target(LatLng(location.latitude,location.longitude))
                            .zoom(12f)
                            .build()))
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentServiciosBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.fused_location = LocationServices.getFusedLocationProviderClient(requireContext())
        location_request = LocationRequest.create()
        mapFragment = childFragmentManager.findFragmentById(R.id.contenedor_gmaps) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap) {

        gmap = map
        gmap.mapType = GoogleMap.MAP_TYPE_NORMAL
        gmap.uiSettings.isZoomControlsEnabled = true


        location_request = LocationRequest.create()
        location_request.fastestInterval = 1000
        location_request.fastestInterval = 1000
        location_request.priority  =LocationRequest.PRIORITY_HIGH_ACCURACY
        location_request.smallestDisplacement = 15f

        verificarPermisos()

        binding!!.chipGroup.setOnCheckedChangeListener{ group, checkedId ->
            gmap.clear()
            when(checkedId){
                R.id.chip_salud -> mostrarMarcadoresSalud(PetServices.getSaludServices())
                R.id.chip_alimento -> mostrarMarcadoresSalud(PetServices.getAlimentoServices())
                R.id.chip_estancia -> Toast.makeText(requireContext(),"Estancia",Toast.LENGTH_SHORT).show()
                R.id.chip_higiene -> Toast.makeText(requireContext(),"Higiene",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verificarPermisos() {
        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            val dialogo = LocationSettingsRequest.Builder()
                .addLocationRequest(location_request)
                .setAlwaysShow(true)

            val result_gps = LocationServices.getSettingsClient(requireContext()).checkLocationSettings(dialogo.build())

            fused_location.requestLocationUpdates(location_request,location_callback, Looper.myLooper())
            gmap.isMyLocationEnabled = true
        } else{
            solicitarPermisoUbicacion()
        }
    }

    private fun solicitarPermisoUbicacion() {
        resultado_permisos.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun mostrarMarcadoresSalud(marcadores: ArrayList<PetService>) {
        for (i in marcadores){
            gmap.addMarker(MarkerOptions()
                .position(i.ubicacion!!)
                .title(i.nombre)
            )
        }
    }

    private fun verificarGPSActivado(): Boolean{
        var isGPSActived = false
        val location_manager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isGPSActived = true
        }
        return isGPSActived
    }

    private fun mostrarDialogoActivarGps(){
        val dialogo = MaterialAlertDialogBuilder(requireContext())
        dialogo.setTitle("Activar Ubicación")
        dialogo.setMessage("Por favor activa la ubicacion del dispositivo")
        dialogo.setPositiveButton("Aceptar") { dialogI, i ->

        }
    }

    @SuppressLint("MissingPermission")
    private val resultado_permisos = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permiso_concedido ->
        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            val dialogo = LocationSettingsRequest.Builder()
                .addLocationRequest(location_request)
                .setAlwaysShow(true)

            val result_gps = LocationServices.getSettingsClient(requireContext()).checkLocationSettings(dialogo.build())

            result_gps.addOnCompleteListener{ result ->
                try {
                    val result = result.result
                } catch (e : ApiException){

                }
            }

            fused_location.requestLocationUpdates(location_request,location_callback, Looper.myLooper())
            gmap.isMyLocationEnabled = true

        } else{
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                val dialogo = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Permisos denegados")
                    .setMessage(
                        "Debido a que rechazo los permisos de ubicación en mas de una ocasión, el sistema Android, no nos permite volver a " +
                                "solicitarle los permisos, por lo que deberá permitirlos manualmente en la siguiente pantalla de Información de la aplicación > Permisos " +
                                "> Ubicación > Permitir"
                    )
                    .setPositiveButton("Aceptar") { dialogo, boton ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", requireContext().packageName, null)
                        intent.setData(uri)
                        resultado_ajustes_ubicacion.launch(intent)
                    }
                    .setNegativeButton("Cancelar") { dialogo, boton ->
                        dialogo.dismiss()
                    }
                dialogo.show()
            } else{
                val dialogo = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Solicitud de permisos")
                    .setMessage(
                        "Es necesario aceptar los permisos de ubicacion, si desea conocer los servicios para mascotas disponible" +
                                " en su zona, mientras no acepte el permiso, no podra utilizar esta funcionalidad"
                    )
                    .setPositiveButton("Aceptar") { dialogo, boton ->
                        solicitarPermisoUbicacion()
                    }
                    .setNegativeButton("Cancelar") { dialogo, boton ->
                        dialogo.dismiss()
                    }
                dialogo.show()
            }
        }
    }

    private val resultado_ajustes_ubicacion = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        verificarPermisos()
    }
}