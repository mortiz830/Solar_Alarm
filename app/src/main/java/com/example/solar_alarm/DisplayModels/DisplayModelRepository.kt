package com.example.solar_alarm.DisplayModels

import androidx.annotation.RequiresApi
import android.os.Build
import android.app.Application
import com.example.solar_alarm.Data.Repositories.SolarAlarmRepository
import androidx.lifecycle.*
import java.util.ArrayList
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

@RequiresApi(api = Build.VERSION_CODES.O)
class DisplayModelRepository(private val _Application: Application) {
    private var _SolarAlarmDisplayModels: MutableList<SolarAlarmDisplayModel>? = null
    private var _LiveData: LiveData<List<SolarAlarmDisplayModel>>? = null
    fun GetSolarAlarmDisplayModels(): LiveData<List<SolarAlarmDisplayModel>>? {
        if (_SolarAlarmDisplayModels == null) {
            val lock: Lock = ReentrantLock()
            lock.lock()
            val solarAlarms = SolarAlarmRepository().all
            _SolarAlarmDisplayModels = ArrayList()
            var values = solarAlarms!!.value
            if (values == null) {
                values = ArrayList()
            }
            for (solarAlarm in values) {
                _SolarAlarmDisplayModels.add(SolarAlarmDisplayModel(_Application, solarAlarm))
            }
            _LiveData = object : LiveData<List<SolarAlarmDisplayModel?>?>() {
                override fun observe(owner: LifecycleOwner, _SolarAlarmDisplayModels: Observer<in List<SolarAlarmDisplayModel?>?>) {
                    super.observe(owner, _SolarAlarmDisplayModels)
                }
            }
            lock.unlock()
        }
        return _LiveData
    }

    fun Refresh() {
        val lock: Lock = ReentrantLock()
        lock.lock()
        _SolarAlarmDisplayModels = null
        lock.unlock()
    }
}