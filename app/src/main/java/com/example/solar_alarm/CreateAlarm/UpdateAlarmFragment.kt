package com.example.solar_alarm.CreateAlarm

import android.os.Bundle
import com.example.solar_alarm.R
import butterknife.BindView
import butterknife.ButterKnife
import com.example.solar_alarm.AlarmList.AlarmListViewModel
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.solar_alarm.DisplayModels.SolarAlarmDisplayModel
import androidx.lifecycle.ViewModelProvider
import com.example.solar_alarm.AlarmList.AlarmListFragment
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class UpdateAlarmFragment : Fragment() {
    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_timePicker)
    var timePicker: TimePicker? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_title)
    var title: EditText? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_scheduleAlarm)
    var scheduleAlarm: Button? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_recurring)
    var recurring: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkMon)
    var mon: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkTue)
    var tue: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkWed)
    var wed: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkThu)
    var thu: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkFri)
    var fri: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkSat)
    var sat: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_checkSun)
    var sun: CheckBox? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.fragment_updatealarm_recurring_options)
    var recurringOptions: LinearLayout? = null
    private var updateAlarmViewModel: AlarmListViewModel? = null
    private val updatedAlarm: SolarAlarmDisplayModel? = null
    var location = 0
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        location = bundle!!.getInt("position")
        updateAlarmViewModel = ViewModelProvider(requireParentFragment()).get(AlarmListViewModel::class.java)
        //        updateAlarmViewModel.getAlarmDisplayLiveData().observe(this, new Observer<List<AlarmDisplayData>>() {
//            @Override
//            public void onChanged(List<SolarAlarmDisplayModel> alarms) {
//                if(alarms != null)
//                {
//                   updatedAlarm = new SolarAlarmDisplayModel(alarms.get(location));
//                   timePicker.setHour(updatedAlarm.getHour());
//                   timePicker.setMinute(updatedAlarm.getMinute());
//                   title.setText(updatedAlarm.getTitle());
//                   recurring.setChecked(updatedAlarm.isRecurring());
//                   mon.setChecked(updatedAlarm.isMonday());
//                   tue.setChecked(updatedAlarm.isTuesday());
//                   wed.setChecked(updatedAlarm.isWednesday());
//                   thu.setChecked(updatedAlarm.isThursday());
//                   fri.setChecked(updatedAlarm.isFriday());
//                   sat.setChecked(updatedAlarm.isSaturday());
//                   sun.setChecked(updatedAlarm.isSunday());
//                }
//            }
//        });
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_updatealarm, container, false)
        ButterKnife.bind(this, view)
        recurring!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                recurringOptions!!.visibility = View.VISIBLE
            } else {
                recurringOptions!!.visibility = View.GONE
            }
        }
        scheduleAlarm!!.setOnClickListener {
            updateAlarm()
            val alarmListFragment = AlarmListFragment()
            val manager = fragmentManager
            manager!!.beginTransaction().replace(R.id.activity_main_nav_host_fragment, alarmListFragment).commit()
        }
        return view
    }

    private fun updateAlarm() {
//        updatedAlarm.setHour(TimePickerUtil.getTimePickerHour(timePicker));
//        updatedAlarm.setMinute(TimePickerUtil.getTimePickerMinute(timePicker));
//        updatedAlarm.setTitle(title.getText().toString());
//        updatedAlarm.setCreated(System.currentTimeMillis());
//        updatedAlarm.setRecurring(recurring.isChecked());
//        updatedAlarm.setMonday(mon.isChecked());
//        updatedAlarm.setTuesday(tue.isChecked());
//        updatedAlarm.setWednesday(wed.isChecked());
//        updatedAlarm.setThursday(thu.isChecked());
//        updatedAlarm.setFriday(fri.isChecked());
//        updatedAlarm.setSaturday(sat.isChecked());
//        updatedAlarm.setSunday(sun.isChecked());
//
//        updateAlarmViewModel.update(updatedAlarm);
//
//        updatedAlarm.schedule(getContext());
    }
}