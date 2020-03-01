package app.task.global

import com.google.android.gms.maps.model.LatLng
import net.sharewire.googlemapsclustering.ClusterItem

class SampleClusterItem(val country: String, private val location: LatLng) : ClusterItem {

    override fun getLatitude(): Double {
        return location.latitude
    }

    override fun getLongitude(): Double {
        return location.longitude
    }

    override fun getTitle(): String? {
        return null
    }

    override fun getSnippet(): String? {
        return null
    }

}