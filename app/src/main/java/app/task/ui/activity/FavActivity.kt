package app.task.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.task.R
import app.task.adapter.FavAdapter
import app.task.global.Constant
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class FavActivity : AppCompatActivity() {

    // Bind Views
    var fav_recycler_view: RecyclerView? = null
    var loading_progress: ProgressBar? = null
    var tv_msg: TextView? = null

    // vars
    private var adapter: FavAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    var list = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav)
        fav_recycler_view = findViewById(R.id.fav_recycler_view) as? RecyclerView
        loading_progress = findViewById(R.id.loading_progress) as? ProgressBar
        tv_msg = findViewById(R.id.tv_msg) as? TextView

        savedList
        initializationRecyclerView()
    } // onCreate

    //============================================================================================
    private fun initializationRecyclerView() {
        layoutManager = GridLayoutManager(this, 2)
        adapter = FavAdapter(this, list)
        fav_recycler_view!!.setHasFixedSize(true)
        fav_recycler_view!!.layoutManager = layoutManager
        fav_recycler_view!!.adapter = adapter
    } // initializationRecyclerView

    //============================================================================================
    // getSavedList
    private val savedList: Unit
        private get() {
            val sharedPreferences = Objects.requireNonNull(getSharedPreferences(Constant.MY_PREFS_NAME
                    , Context.MODE_PRIVATE))
            list = Gson().fromJson(sharedPreferences.getString("SAVED_ARRAY", null),
                    object : TypeToken<List<String?>?>() {}.type)
            Log.i(Constant.TAG, "getSAVED_ARRAY -->  $list")
        }
    //============================================================================================
}