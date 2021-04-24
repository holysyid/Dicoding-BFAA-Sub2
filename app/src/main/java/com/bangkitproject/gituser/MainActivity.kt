package com.bangkitproject.gituser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar

import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var list: ArrayList<Account> = arrayListOf()
    private lateinit var rvAccount: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var coba: String? = null
    private var status: Boolean = true

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)

        status = false
        getData(coba)


    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        val menuitem = menu!!.findItem(R.id.search)
        if (menuitem != null){
            val searchView = menuitem.actionView as SearchView
            searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query!!.isEmpty()) {

                    } else {
                        list.clear()
                        getData(query)
                        rvAccount.layoutManager = LinearLayoutManager(this@MainActivity)
                        val listHeroAdapter = AccountAdapter(this@MainActivity,list)
                        rvAccount.adapter = listHeroAdapter
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText!!.isEmpty()) {
                        if (status==false){
                            //do nothing
                        }else{
                            list.clear()
                            getData(newText)
                            rvAccount.layoutManager = LinearLayoutManager(this@MainActivity)
                            val listHeroAdapter = AccountAdapter(this@MainActivity,list)
                            rvAccount.adapter = listHeroAdapter
                            status = true
                        }


                    } else {
                        list.clear()
                        getData(newText)
                        rvAccount.layoutManager = LinearLayoutManager(this@MainActivity)
                        val listHeroAdapter = AccountAdapter(this@MainActivity,list)
                        rvAccount.adapter = listHeroAdapter
                        status = true
                    }
                    return true
                }


            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun getData(query: String?) {
        progressBar.visibility = View.VISIBLE

        //api config
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token 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")
        if (query.isNullOrEmpty()){
            val url = "https://api.github.com/users"
            var result: String? = null
            client.get(url, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                    // if ok
                    progressBar.visibility = View.INVISIBLE //

                    result = String(responseBody)
                    //datanya ada ga
                    Log.d(TAG, result!!)


                }

                override fun onFinish() {
                    try {
                        val jsonArray = JSONArray(result)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val key: String = jsonObject.getString("login")
//                        Toast.makeText(this@MainActivity, key, Toast.LENGTH_SHORT).show()
                            getDetailedData(key)
                        }
                    }

                    catch (e: Exception) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
                override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                    // if not ok, like ur heart :(
                    progressBar.visibility = View.INVISIBLE

                    val errorMessage = when (statusCode) {
                        401 -> "$statusCode : Bad Request"
                        403 -> "$statusCode : Forbidden"
                        404 -> "$statusCode : Not Found"
                        else -> "$statusCode : ${error.message}"
                    }
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
        }else{
        val url = "https://api.github.com/search/users?q=$query"

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // if ok
                progressBar.visibility = View.INVISIBLE //

                val result = String(responseBody)
                //datanya ada ga
                Log.d(TAG, result)

                try {
                    val jsonArray = JSONObject(result)
                    val items = jsonArray.getJSONArray("items")
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = items.getJSONObject(i)
                        val key: String = jsonObject.getString("login")
//                        Toast.makeText(this@MainActivity, key, Toast.LENGTH_SHORT).show()
                        getDetailedData(key)
                    }
                }

                catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // if not ok, like ur heart :(
                progressBar.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
    }

    private fun getDetailedData(key: String) {

        progressBar.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token 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")
        val url = "https://api.github.com/users/$key"

        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)

                try {
                    val userDetail = JSONObject(result)
                    val user = Account(
                        userDetail.getString("login").toString(),
                        userDetail.getString("name").toString(),
                        userDetail.getString("avatar_url").toString(),
                        userDetail.getString("company").toString(),
                        userDetail.getString("location").toString(),
                        userDetail.getString("public_repos").toString(),
                        userDetail.getString("followers").toString(),
                        userDetail.getString("following").toString()
                    )
                    list.add(user)

                }

                catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG)
                    .show()
            }

            override fun onFinish() {
                rvAccount = findViewById(R.id.recyclerView)
                rvAccount.setHasFixedSize(true)

                rvAccount.layoutManager = LinearLayoutManager(this@MainActivity)
                val listHeroAdapter = AccountAdapter(this@MainActivity,list)
                rvAccount.adapter = listHeroAdapter
            }

        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }


}