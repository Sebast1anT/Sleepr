package com.mistershorr.loginandregistration

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.mistershorr.loginandregistration.databinding.ActivitySleepListBinding
import java.util.Date


class SleepListActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySleepListBinding
    private lateinit var sleepAdapter : SleepAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadBackendlessData()



    }

    override fun onResume() {
        super.onResume()
        loadBackendlessData()
    }
    fun loadBackendlessData(){
        val userId = Backendless.UserService.CurrentUser().userId
        // need the ownerId to match the objectId of the user
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        // include the query builder in the find function
        Backendless.Data.of(Sleep::class.java).find(queryBuilder,
            object : AsyncCallback<MutableList<Sleep?>?> {
                override fun handleResponse(foundSleepers: MutableList<Sleep?>?) {
                        sleepAdapter = SleepAdapter(foundSleepers!!)
                    binding.recyclerViewSleepListDisplay.adapter  = sleepAdapter
                    binding.recyclerViewSleepListDisplay.layoutManager = LinearLayoutManager(this@SleepListActivity)
                    // the "foundSleepers" collection now contains instances of the Sleep class.
                    // each instance represents an object stored on the server.
                }

                override fun handleFault(fault: BackendlessFault) {
                    // an error has occurred, the error code can be retrieved with fault.getCode()
                }
            })
    }
    // trying to turn LocalDateTime back into milliseconds, failing with ComponentInfo unable to start error
    fun saveToBackendless() {
        // the real use case will be to read from all the editText
        // fields in the detail activity and then use that info
        // to make the object

        // here, we'll just make up an object
        val sleep = Sleep(
            Date().time, Date().time, Date().time,
            10, "finally a restful night"
        )
        sleep.ownerId = Backendless.UserService.CurrentUser().userId
        // if i do not set the objectId, it will make a new object
        // if I do set the objectId to an existing object Id from data table
        // on backendless, it will update the object.

        // include the async callback to save the object here
    }

}