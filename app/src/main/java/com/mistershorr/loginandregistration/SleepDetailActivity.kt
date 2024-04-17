package com.mistershorr.loginandregistration

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.mistershorr.loginandregistration.databinding.ActivitySleepDetailBinding
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField


class SleepDetailActivity : AppCompatActivity() {

    companion object {
        val TAG = "SleepDetailActivity"
        val EXTRA_SLEEP = "sleepytime"
    }

    private lateinit var binding: ActivitySleepDetailBinding
    lateinit var bedTime: LocalDateTime
    lateinit var wakeTime: LocalDateTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sleep : Sleep? = intent.getParcelableExtra(SleepAdapter.EXTRA_SLEEP)

        if (sleep !=  null){
            setExistingValues(sleep)
        }
        else {
            setDefaultValues()
        }


        // these are default values that should be set when creating a new entry
        // however, if editing an existing entry, those values should be used instead


        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE MMM dd, yyyy")
        val trueTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val sleepMillis = binding.buttonSleepDetailBedTime.text.toString().format(trueTimeFormatter)
        val test = LocalDateTime.now()
        val dateOf = LocalDate.parse(binding.buttonSleepDetailDate.text, dateFormatter)


        Log.d("TAG", "onCreate: ${LocalDateTime.of(dateOf, timeOf)}")

        //Log.d("TAG", "bloop bloop ${LocalDateTime.parse(sleepMillis, trueTimeFormatter)}")


        binding.buttonSleepDetailBedTime.setOnClickListener {
            setTime(bedTime, timeFormatter, binding.buttonSleepDetailBedTime)
        }

        binding.buttonSleepDetailWakeTime.setOnClickListener {
            setTime(wakeTime, timeFormatter, binding.buttonSleepDetailWakeTime)
        }

        binding.buttonSleepDetailDate.setOnClickListener {
            val selection = bedTime.toEpochSecond(ZoneOffset.UTC)
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(selection*1000) // requires milliseconds
                .setTitleText("Select a Date")
                .build()

            Log.d(TAG, "onCreate: after build: ${LocalDateTime.ofEpochSecond(datePicker.selection?: 0L, 0, ZoneOffset.UTC)}")
            datePicker.addOnPositiveButtonClickListener { millis ->
                val selectedLocalDate = Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDateTime()
                Toast.makeText(this, "Date is: ${dateFormatter.format(selectedLocalDate)}", Toast.LENGTH_SHORT).show()

                // make sure that waking up the next day if waketime < bedtime is preserved
                var wakeDate = selectedLocalDate

                if(wakeTime.dayOfMonth != bedTime.dayOfMonth) {
                    wakeDate = wakeDate.plusDays(1)
                }

                bedTime = LocalDateTime.of(
                    selectedLocalDate.year,
                    selectedLocalDate.month,
                    selectedLocalDate.dayOfMonth,
                    bedTime.hour,
                    bedTime.minute
                )

                wakeTime = LocalDateTime.of(
                    wakeDate.year,
                    wakeDate.month,
                    wakeDate.dayOfMonth,
                    wakeTime.hour,
                    wakeTime.minute
                )
                binding.buttonSleepDetailDate.text = dateFormatter.format(bedTime)
            }
            datePicker.show(supportFragmentManager, "datepicker")
        }
        binding.buttonSleepDetailCancel.setOnClickListener {
            finish()
        }

        binding.buttonSleepDetailSave.setOnClickListener {
            //Log.d("TAG", "saveClickListener: ${LocalDateTime.parse(binding.buttonSleepDetailWakeTime.text, timeFormatter).toEpochSecond(UTC)}" )
            if (sleep != null){
                Backendless.Data.of(Sleep::class.java)
                    .save(sleep , object : AsyncCallback<Sleep> {
                        override fun handleResponse(savedSleep: Sleep) {
                            // New contact object has been saved, now it can be updated.
                            // The savedContact map now has a valid "objectId" value.
                            savedSleep.bedTimeMillis = sleep.bedTimeMillis
                            savedSleep.wakeTimeMillis = sleep.wakeTimeMillis
                            savedSleep.notes = sleep.notes
                            savedSleep.quality = sleep.quality
                            savedSleep.sleepDateMillis = sleep.sleepDateMillis
                            Backendless.Data.of(Sleep::class.java)
                                .save(savedSleep, object : AsyncCallback<Sleep> {
                                    override fun handleResponse(response: Sleep?) {
                                        // Sleep object has been updated
                                    }

                                    override fun handleFault(fault: BackendlessFault) {
                                        // an error has occurred, the error code can be retrieved with fault.getCode()
                                    }
                                })
                        }

                        override fun handleFault(fault: BackendlessFault) {
                            // an error has occurred, the error code can be retrieved with fault.getCode()
                        }
                    })
//                Backendless.Data.of(Sleep::class.java).save(sleep, object : AsyncCallback<Sleep?>  {
//                    override fun handleResponse(response: Sleep?) {
//                        // new Contact instance has been saved
//                    }
//
//                    override fun handleFault(fault: BackendlessFault) {
//                        // an error has occurred, the error code can be retrieved with fault.getCode()
//                    }
//                })
            }
//            else {
//                val timeOfWake = LocalDateTime.parse(binding.buttonSleepDetailWakeTime.text, timeFormatter)
//                val timeOfBed = LocalDateTime.parse(binding.buttonSleepDetailBedTime.text, timeFormatter)
//                val newSleep = Sleep((timeOfWake).toEpochSecond(
//                    UTC) * 1000 + timeOfWake.get(ChronoField.MILLI_OF_SECOND),)
//                Backendless.Data.of(Sleep::class.java).save(sleep, object : AsyncCallback<Sleep?>  {
//                    override fun handleResponse(response: Sleep?) {
//                         //new Contact instance has been saved
//                    }
//
//                    override fun handleFault(fault: BackendlessFault) {
//                        // an error has occurred, the error code can be retrieved with fault.getCode()
//                    }
//                })
//
//            }
        }


    }

    fun convertWakeTimeToMillis(){

    }
    private fun setDefaultValues(){
        bedTime = LocalDateTime.now()
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        binding.buttonSleepDetailBedTime.text = timeFormatter.format(bedTime)
        wakeTime = bedTime.plusHours(8)
        binding.buttonSleepDetailWakeTime.text = timeFormatter.format(wakeTime)
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE MMM dd, yyyy")
        binding.buttonSleepDetailDate.text = dateFormatter.format(bedTime)
    }
    private fun setExistingValues(sleep : Sleep){
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE MMM dd, yyyy")
        bedTime = LocalDateTime.ofEpochSecond((sleep?.bedTimeMillis!!/1000), 0, ZoneId.systemDefault().rules.getOffset(Instant.now()))
        wakeTime = LocalDateTime.ofEpochSecond((sleep?.wakeTimeMillis!!/1000), 0, ZoneId.systemDefault().rules.getOffset(Instant.now()))

        binding.buttonSleepDetailBedTime.text = timeFormatter.format(bedTime)
        binding.buttonSleepDetailWakeTime.text = timeFormatter.format(wakeTime)
        binding.buttonSleepDetailDate.text = dateFormatter.format(bedTime)
    }

    fun setTime(time: LocalDateTime, timeFormatter: DateTimeFormatter, button: Button) {
        val timePickerDialog = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(time.hour)
            .setMinute(time.minute)
            .build()

        timePickerDialog.show(supportFragmentManager, "bedtime")
        timePickerDialog.addOnPositiveButtonClickListener {
            var selectedTime = LocalDateTime.of(time.year, time.month, time.dayOfMonth, timePickerDialog.hour, timePickerDialog.minute)
            button.text = timeFormatter.format(selectedTime)
            when(button.id) {
                binding.buttonSleepDetailBedTime.id -> {
                    bedTime = selectedTime
                    if(wakeTime.toEpochSecond(UTC) < selectedTime.toEpochSecond(UTC)) {
                        wakeTime = wakeTime.plusDays(1)
                    }
                }
                binding.buttonSleepDetailWakeTime.id -> {
                    if(selectedTime.toEpochSecond(UTC) < bedTime.toEpochSecond(UTC)) {
                        selectedTime = selectedTime.plusDays(1)
                    }
                    wakeTime = selectedTime
                }
            }
        }
    }
}