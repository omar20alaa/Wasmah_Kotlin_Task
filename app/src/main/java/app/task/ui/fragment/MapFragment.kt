package app.task.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import app.task.R
import app.task.global.Constant
import app.task.global.SampleClusterItem
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_map.*
import net.sharewire.googlemapsclustering.Cluster
import net.sharewire.googlemapsclustering.ClusterManager
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class MapFragment : Fragment() {

    @JvmField
//    @BindView(R.id.mapView)
    var mapView: MapView? = null
    // Vars
    var m_li: HashMap<String, String>? = null
    private var googleMap: GoogleMap? = null
    var clusterManager: ClusterManager<SampleClusterItem>? = null
    var clusterItems: MutableList<SampleClusterItem> = ArrayList()
    var countryList: LatLngBounds? = null
    var country = ""
    var list = ArrayList<String>()
    var lat = 0.0
    var lng = 0.0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
       // ButterKnife.bind(this, view)
        loadJSONFromAsset()
        mapView = view.findViewById(R.id.mapView) as? MapView

        mapView?.onCreate(savedInstanceState)
        initMapView()
        return view
    } // onCreateView

    //============================================================================================
    private fun initMapView() {
        mapView!!.onResume()
        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //========================= On Map Ready ===================================================================
        mapView!!.getMapAsync { mMap ->
            googleMap = mMap
            clusterManager = ClusterManager(activity!!, googleMap!!)
            googleMap!!.setOnCameraIdleListener(clusterManager)
            clusterItems = ArrayList()
            fetchCountries()
            //============================= Event For Cluster ===============================================================
            clusterManager!!.setCallbacks(object : ClusterManager.Callbacks<SampleClusterItem> {
                override fun onClusterClick(cluster: Cluster<SampleClusterItem>): Boolean {
                    Log.i(Constant.TAG, "onClusterClick")
                    return true
                }

                override fun onClusterItemClick(clusterItem: SampleClusterItem): Boolean {
                    Log.i(Constant.TAG, "onClusterItemClick --> " + clusterItem.country)
                    val sharedPreferences = activity!!.getSharedPreferences(Constant.MY_PREFS_NAME
                            , Context.MODE_PRIVATE)
                    val editorr = sharedPreferences.edit()
                    list.add(clusterItem.country)
                    sharedPreferences.edit().putString("SAVED_ARRAY", Gson().toJson(list)).apply()
                    editorr.apply()
                    Log.i(Constant.TAG, "SAVED_ARRAY --> " + list.size)
                    return true
                }
            })
            //=========================== Marker Listner =================================================================
            googleMap!!.setOnMarkerClickListener { marker ->
                // change background when click
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                Log.i(Constant.TAG, "onMarkerClick getPosition --> " + marker.position)
                val lat = marker.position.latitude
                val lng = marker.position.longitude
                // =================================== compare lat and lng if json file contain get country name from it ====================
                for (i in clusterItems.indices) {
                    Log.i(Constant.TAG + "countryList --> ", clusterItems[i].latitude.toString() + "")
                    if (clusterItems[i].latitude == lat && clusterItems[i].longitude == lng) {
                        Snackbar.make(view!!, clusterItems[i].country
                                + " "
                                + getString(R.string.added), Snackbar.LENGTH_LONG).show()
                        val sharedPreferences = activity!!.getSharedPreferences(Constant.MY_PREFS_NAME
                                , Context.MODE_PRIVATE)
                        val editorr = sharedPreferences.edit()
                        list.add(clusterItems[i].country)
                        sharedPreferences.edit().putString("SAVED_ARRAY", Gson().toJson(list)).apply()
                        editorr.apply()
                        Log.i(Constant.TAG, "SAVED_ARRAY --> " + list.size)
                    }
                }
                true
            }
            //============================================================================================
// For showing a move to my location button
            if (ContextCompat.checkSelfPermission(
                            activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity!!,
                            Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                googleMap!!.isMyLocationEnabled = true
                googleMap!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                Log.i(Constant.TAG, "Location is Off  --> ")
            }
            // For dropping a marker at a point on the Map
            val sydney = LatLng((-34).toDouble(), 151.0)
            googleMap!!.addMarker(MarkerOptions().position(sydney).title(country)
                    .snippet(country))
            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(sydney).zoom(12f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(NETHERLANDS, 0))
        }
    } // init Map view

    //============================================================================================
    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    //============================================================================================
    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    //============================================================================================
    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    //============================================================================================
    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    //============================================================================================
    fun loadJSONFromAsset(): String? {
        var json: String? = null
        json = try {
            val `is` = activity!!.assets.open("countries.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    } //loadJSONFromAsset

    //============================================================================================
    private fun fetchCountries() {
        try {
            val obj = JSONObject(loadJSONFromAsset())
            val m_jArry = obj.getJSONArray("ref_country_codes")
            val formList = ArrayList<HashMap<String, String>>()
            for (i in 0 until m_jArry.length()) {
                val jo_inside = m_jArry.getJSONObject(i)
                Log.i(Constant.TAG + "Details --> ", jo_inside.getString("country"))
                country = jo_inside.getString("country")
                lat = jo_inside.getDouble("latitude")
                lng = jo_inside.getDouble("longitude")
                Log.i(Constant.TAG + " country --> ", country)
                Log.i(Constant.TAG + " latitude --> ", lat.toString() + "")
                Log.i(Constant.TAG + " longitude --> ", lng.toString() + "")
                m_li = HashMap()
                m_li!!["country"] = country
                formList.add(m_li!!)
                countryList = LatLngBounds(LatLng(lat, lng), LatLng(lat, lng))
                clusterItems.add(SampleClusterItem(country, LatLng(lat, lng)))
            }
            Log.i(Constant.TAG + "clusterItems --> ", clusterItems.size.toString() + "")
            clusterManager!!.setItems(clusterItems)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    } // fetchCountries

    //============================================================================================
    companion object {
        private val NETHERLANDS = LatLngBounds(
                LatLng(50.77083, 3.57361), LatLng(53.35917, 7.10833))
    }
}