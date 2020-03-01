package app.task.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.task.R
import app.task.adapter.ReposAdapter
import app.task.global.Constant
import app.task.model.RepositoryModel
import app.task.network.retrofitClient.Companion.instance
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.maps.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RepositoriesFragment : Fragment() {
    // bind views
    var repos_recycler_view: RecyclerView? = null
    var loading_progress: ProgressBar? = null
    var tv_msg: TextView? = null
    // vars
    private var pastVisiblesItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var page_index = 1
    var endOfREsults = false
    private var loading_flag = true
    var arrayList: ArrayList<RepositoryModel>? = arrayListOf()
    private var adapter: ReposAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_repositories, container, false)

        repos_recycler_view = view.findViewById(R.id.repos_recycler_view) as? RecyclerView
        loading_progress = view.findViewById(R.id.loading_progress) as? ProgressBar
        tv_msg = view.findViewById(R.id.tv_msg) as? TextView

        fetchRepositories()
        return view
    } // onCreateView

    fun fetchRepositories() {
        loading_progress!!.visibility = View.VISIBLE
        instance!!
                .fetchRepositories(page_index.toString() + "")
                ?.enqueue(
                        object : Callback<ArrayList<RepositoryModel>> {
                            override fun onResponse(call: Call<ArrayList<RepositoryModel>>,
                                                    response: Response<ArrayList<RepositoryModel>>) {
                                try {
                                    arrayList!!.clear()
                                    response.body()?.let { arrayList?.addAll(it) }
                                } catch (e: Exception) {
                                }
                                if (arrayList!!.size == 0) {
                                    tv_msg!!.text = getString(R.string.empty)
                                    tv_msg!!.visibility = View.VISIBLE
                                    repos_recycler_view!!.visibility = View.GONE
                                }
                                if (arrayList!!.isEmpty()) {
                                    page_index = page_index - 1
                                    endOfREsults = true
                                }
                                initializationRecyclerView()
                                setPaging()
                                loading_flag = true
                                loading_progress!!.visibility = View.GONE
                            }

                            override fun onFailure(call: Call<ArrayList<RepositoryModel>>, t: Throwable) {
                                tv_msg!!.text = getString(R.string.Fail)
                                tv_msg!!.visibility = View.VISIBLE
                                Log.i(Constant.TAG, "error onFailure --> " + t.localizedMessage)
                                loading_progress!!.visibility = View.GONE
                            }
                        })
    } // fetchRepositories

    private fun initializationRecyclerView() {
        layoutManager = GridLayoutManager(context, 2)
        adapter = ReposAdapter(activity!!, arrayList!!)
        repos_recycler_view!!.setHasFixedSize(true)
        repos_recycler_view!!.layoutManager = layoutManager
        repos_recycler_view!!.adapter = adapter
    } // initializationRecyclerView

    private fun setPaging() {
        Log.i(Constant.TAG, "setPaging called : ")
        repos_recycler_view!!.post {
            repos_recycler_view!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!endOfREsults) {
                        visibleItemCount = layoutManager!!.childCount
                        totalItemCount = layoutManager!!.itemCount
                        pastVisiblesItems = layoutManager!!.findFirstVisibleItemPosition()
                        if (loading_flag) {
                            if (visibleItemCount + pastVisiblesItems + 2 >= totalItemCount) {
                                loading_flag = false
                                if (arrayList!!.size != 0) {
                                    loading_progress!!.visibility = View.GONE
                                }
                                page_index = page_index + 1
                                fetchMoreRepos()
                            }
                        }
                    }
                }
            })
        }
    } // set pagination

    private fun fetchMoreRepos() {
        loading_progress!!.visibility = View.VISIBLE
        instance!!.fetchRepositories(page_index.toString() + "")!!.enqueue(object :
                Callback<ArrayList<RepositoryModel>> {
            override fun onResponse(call: Call<ArrayList<RepositoryModel>>, response: Response<ArrayList<RepositoryModel>>) {
                loading_progress!!.visibility = View.GONE
                if (response.body() != null) {
                    if (response.body()!!.isEmpty()) {
                        page_index = page_index - 1
                        endOfREsults = true
                    }
                    arrayList!!.addAll(response.body()!!)
                    adapter!!.notifyDataSetChanged()
                    loading_flag = true
                }
                loading_progress!!.visibility = View.GONE
            }

            override fun onFailure(call: Call<ArrayList<RepositoryModel>>, t: Throwable) {
                Log.i(Constant.TAG, "error in ViewModel = " + t.localizedMessage)
                loading_progress!!.visibility = View.GONE
            }
        })
    } // fetchMoreRepos
}