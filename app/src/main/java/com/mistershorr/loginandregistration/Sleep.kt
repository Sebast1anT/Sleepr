package com.mistershorr.loginandregistration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
data class Sleep(var wakeTimeMillis : Long = Date().time,
                 var bedTimeMillis: Long = Date().time,
                 var sleepDateMillis: Long = Date().time,
                 var quality: Int = 5,
                 var notes : String = "Notes",
                 var ownerId : String = "5"
) : Parcelable
