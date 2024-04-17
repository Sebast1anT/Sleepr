package com.mistershorr.loginandregistration

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class SleepAdapter (var sleepList: MutableList<Sleep?>) : RecyclerView.Adapter<SleepAdapter.ViewHolder>() {

    companion object {
        val TAG = "SleepAdapter"
        val EXTRA_SLEEP = "sleeptime"
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewDate : TextView
        val textViewHours: TextView
        val textViewDuration: TextView
        val layout : ConstraintLayout
        val ratingBarQuality : RatingBar
        init {
            textViewDate = view.findViewById(R.id.textView_itemSleep_date)
            textViewDuration = view.findViewById(R.id.textView_itemSleep_duration)
            textViewHours = view.findViewById(R.id.textView_itemSleep_hours)
            layout = view.findViewById(R.id.layout_itemSleep)
            ratingBarQuality = view.findViewById(R.id.ratingBar_itemSleep_sleepQuality)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sleep, parent, false)
        val holder = ViewHolder(view)
        return holder
    }



    override fun onBindViewHolder(holder: SleepAdapter.ViewHolder, position: Int) {
        val sleep = sleepList[position]
        val context = holder.layout.context


        val formatter = DateTimeFormatter.ofPattern("yyy-MM-dd")
        val sleepDate = sleep?.sleepDateMillis?.div(1000)?.let {
            LocalDateTime.ofEpochSecond(
                it, 0,
                ZoneId.systemDefault().rules.getOffset(Instant.now()))
        }
        holder.textViewDate.text = formatter.format(sleepDate)

        var bedtimeDiff = (sleep?.wakeTimeMillis?.minus(sleep?.bedTimeMillis!!))?.div(1000)
        var hoursInBed = bedtimeDiff?.div(3600)
        var minutesInBed = bedtimeDiff?.rem(3600)
        var strHours = String.format("%02d", hoursInBed)
        var strMin = String.format("%02d", minutesInBed)
        var displayTime = "$strHours:$strMin"
        // calculate the difference in time from bed to wake and convert to hours & minutes
        // use String.format() to display it in HH:mm format in the duration textview
        // hint: you need leading zeroes and a width of 2




        // sets the actual hours slept textview
        val bedTime = sleep?.bedTimeMillis?.let {
            LocalDateTime.ofEpochSecond(
                it.div(1000), 0,
                ZoneId.systemDefault().rules.getOffset(Instant.now()))
        }
        val wakeTime = sleep?.wakeTimeMillis?.let {
            LocalDateTime.ofEpochSecond(
                it.div(1000), 0,
                ZoneId.systemDefault().rules.getOffset(Instant.now()))
        }
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        holder.textViewHours.text = "${timeFormatter.format(bedTime)} - ${timeFormatter.format(wakeTime)}"

        holder.ratingBarQuality.rating = sleep?.quality?.toFloat()!!


        holder.layout.setOnClickListener {
            val intent = Intent(context, SleepDetailActivity::class.java).apply {
                putExtra(SleepDetailActivity.EXTRA_SLEEP, sleep)
            }
            context.startActivity(intent)
        }
        holder.layout.isLongClickable = true
        holder.layout.setOnLongClickListener {
            val popMenu = PopupMenu(context, holder.textViewHours)
            popMenu.inflate(R.menu.menu_sleep_list_context)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_sleeplist_delete -> {
                        deleteFromBackendless(position)
                        true
                    }
                    else -> true
                }
            }
            popMenu.show()
            true
        }
    }
    private fun deleteFromBackendless(position: Int) {
        Log.d("SleepAdapter", "deleteFromBackendless: Trying to delete ${sleepList[position]}")
        val sleep = sleepList[position]
        Backendless.Data.of(Sleep::class.java).save(sleep, object : AsyncCallback<Sleep?> {
            override fun handleResponse(savedContact: Sleep?) {
                Backendless.Data.of(Sleep::class.java).remove(sleep,
                    object : AsyncCallback<Long?> {
                        override fun handleResponse(response: Long?) {
                            val sleepAdapter = SleepAdapter(sleepList)
                            sleepList.removeAt(position)
                            sleepAdapter.notifyDataSetChanged()
                            // Contact has been deleted. The response is the
                            // time in milliseconds when the object was deleted
                        }

                        override fun handleFault(fault: BackendlessFault) {
                            // an error has occurred, the error code can be
                            // retrieved with fault.getCode()
                        }
                    })
            }

            override fun handleFault(fault: BackendlessFault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        })
        // put in the code to delete the item using the callback from Backendless
        // in the handleResponse, we'll need to also delete the item from the sleepList
        // and make sure that the recyclerview is updated
    }




    override fun getItemCount(): Int {
        return sleepList.size
    }
}
