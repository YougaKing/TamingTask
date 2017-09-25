package youga.tamingtask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import rxbind.view.RxView;
import youga.tamingtask.taming.IntentWrapper;
import youga.tamingtask.taming.NoticeTask;
import youga.tamingtask.taming.TamingUtil;


public class ExerciseTamingActivity extends Activity {


    private static final int REQUEST_IGNORE_BATTERY_CODE = 121;
    private TextView mTvTimeCycle;
    private TextView mCbMonday;
    private TextView mCbTuesday;
    private TextView mCbWednesday;
    private TextView mCbThursday;
    private TextView mCbFriday;
    private TextView mCbSaturday;
    private TextView mCbSunday;
    private ImageView mBack;
    private TextView mTitle;
    private TextView mNext;
    private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private List<Integer> mIntegers = new ArrayList<>();

    private void assignViews() {
        mTvTimeCycle = (TextView) findViewById(R.id.tv_time_cycle);
        mCbMonday = (TextView) findViewById(R.id.cb_monday);
        mCbTuesday = (TextView) findViewById(R.id.cb_tuesday);
        mCbWednesday = (TextView) findViewById(R.id.cb_wednesday);
        mCbThursday = (TextView) findViewById(R.id.cb_thursday);
        mCbFriday = (TextView) findViewById(R.id.cb_friday);
        mCbSaturday = (TextView) findViewById(R.id.cb_saturday);
        mCbSunday = (TextView) findViewById(R.id.cb_sunday);

        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);
        mNext = (TextView) findViewById(R.id.next);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_taming);
        assignViews();


        RxView.clicks(mBack)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        finish();
                    }
                });
        mTitle.setText("修改时间");
        mNext.setText("保存");
        RxView.clicks(mNext)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        String[] split = mTvTimeCycle.getText().toString().split(":");
                        NoticeTask task = new NoticeTask();
                        task.hour = Integer.valueOf(split[0]);
                        task.minute = Integer.valueOf(split[1]);
                        int[] ints = new int[mIntegers.size()];
                        for (Integer i : mIntegers) {
                            ints[mIntegers.indexOf(i)] = i;
                        }
                        task.loopDays = ints;
                        TamingUtil.saveNoticeTask(getBaseContext(), task);


                        TamingUtil.setTamingAlarmTask(getBaseContext());
                        IntentWrapper.whiteListMatters(ExerciseTamingActivity.this, "轨迹跟踪服务的持续运行");

//                        if (!TamingUtil.isIgnoringBatteryOptimizations(getBaseContext())) {
//                            TamingUtil.isIgnoreBatteryOption(ExerciseTamingActivity.this, REQUEST_IGNORE_BATTERY_CODE);
//                        }
//                        finish();
                    }
                });

        NoticeTask task = TamingUtil.obtainNoticeTask(this);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, task.hour);
        calendar.set(Calendar.MINUTE, task.minute);
        mTvTimeCycle.setText(task.hour + ":" + task.minute);
        RxView.clicks(mTvTimeCycle)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        new TimePickerView.Builder(ExerciseTamingActivity.this, new TimePickerView.OnTimeSelectListener() {
                            @Override
                            public void onTimeSelect(Date date, View v) {
                                calendar.setTime(date);
                                mTvTimeCycle.setText(mFormat.format(date));
                            }
                        }).isCyclic(false)
                                .setOutSideCancelable(true)
                                .setType(new boolean[]{false, false, false, true, true, false})
                                .setTitleText("垃圾")
                                .setDate(calendar)
                                .setLabel("", "", "", "", "", "")
                                .build().show();
                    }
                });

        RxView.clicks(mCbMonday)
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mCbMonday.setSelected(!mCbMonday.isSelected());
                        mCbMonday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbMonday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
                        if (mCbMonday.isSelected()) {
                            mIntegers.add(NoticeTask.MONDAY);
                        } else {
                            mIntegers.remove(Integer.valueOf(NoticeTask.MONDAY));
                        }
                        mNext.setEnabled(!mIntegers.isEmpty());
                        mNext.setTextColor(mNext.isEnabled() ? Color.parseColor("#999999") : Color.parseColor("#66999999"));
                    }
                });

        RxView.clicks(mCbTuesday)
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mCbTuesday.setSelected(!mCbTuesday.isSelected());
                        mCbTuesday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbTuesday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
                        if (mCbTuesday.isSelected()) {
                            mIntegers.add(NoticeTask.TUESDAY);
                        } else {
                            mIntegers.remove(Integer.valueOf(NoticeTask.TUESDAY));
                        }
                        mNext.setEnabled(!mIntegers.isEmpty());
                        mNext.setTextColor(mNext.isEnabled() ? Color.parseColor("#999999") : Color.parseColor("#66999999"));
                    }
                });

        RxView.clicks(mCbWednesday)
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mCbWednesday.setSelected(!mCbWednesday.isSelected());
                        mCbWednesday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbWednesday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
                        if (mCbWednesday.isSelected()) {
                            mIntegers.add(NoticeTask.WEDNESDAY);
                        } else {
                            mIntegers.remove(Integer.valueOf(NoticeTask.WEDNESDAY));
                        }
                        mNext.setEnabled(!mIntegers.isEmpty());
                        mNext.setTextColor(mNext.isEnabled() ? Color.parseColor("#999999") : Color.parseColor("#66999999"));
                    }
                });

        RxView.clicks(mCbThursday)
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mCbThursday.setSelected(!mCbThursday.isSelected());
                        mCbThursday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbThursday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
                        if (mCbThursday.isSelected()) {
                            mIntegers.add(NoticeTask.THURSDAY);
                        } else {
                            mIntegers.remove(Integer.valueOf(NoticeTask.THURSDAY));
                        }
                        mNext.setEnabled(!mIntegers.isEmpty());
                        mNext.setTextColor(mNext.isEnabled() ? Color.parseColor("#999999") : Color.parseColor("#66999999"));
                    }
                });

        RxView.clicks(mCbFriday)
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mCbFriday.setSelected(!mCbFriday.isSelected());
                        mCbFriday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbFriday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
                        if (mCbFriday.isSelected()) {
                            mIntegers.add(NoticeTask.FRIDAY);
                        } else {
                            mIntegers.remove(Integer.valueOf(NoticeTask.FRIDAY));
                        }
                        mNext.setEnabled(!mIntegers.isEmpty());
                        mNext.setTextColor(mNext.isEnabled() ? Color.parseColor("#999999") : Color.parseColor("#66999999"));
                    }
                });

        RxView.clicks(mCbSaturday)
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mCbSaturday.setSelected(!mCbSaturday.isSelected());
                        mCbSaturday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbSaturday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
                        if (mCbSaturday.isSelected()) {
                            mIntegers.add(NoticeTask.SATURDAY);
                        } else {
                            mIntegers.remove(Integer.valueOf(NoticeTask.SATURDAY));
                        }
                        mNext.setEnabled(!mIntegers.isEmpty());
                        mNext.setTextColor(mNext.isEnabled() ? Color.parseColor("#999999") : Color.parseColor("#66999999"));
                    }
                });

        RxView.clicks(mCbSunday)
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mCbSunday.setSelected(!mCbSunday.isSelected());
                        mCbSunday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbSunday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
                        if (mCbSunday.isSelected()) {
                            mIntegers.add(NoticeTask.SUNDAY);
                        } else {
                            mIntegers.remove(Integer.valueOf(NoticeTask.SUNDAY));
                        }
                        mNext.setEnabled(!mIntegers.isEmpty());
                        mNext.setTextColor(mNext.isEnabled() ? Color.parseColor("#999999") : Color.parseColor("#66999999"));
                    }
                });

        mCbMonday.setSelected(task.containsWeek(NoticeTask.MONDAY));
        mCbMonday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbMonday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
        if (mCbMonday.isSelected()) mIntegers.add(NoticeTask.MONDAY);
        mCbTuesday.setSelected(task.containsWeek(NoticeTask.TUESDAY));
        mCbTuesday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbTuesday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
        if (mCbTuesday.isSelected()) mIntegers.add(NoticeTask.TUESDAY);
        mCbWednesday.setSelected(task.containsWeek(NoticeTask.WEDNESDAY));
        mCbWednesday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbWednesday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
        if (mCbWednesday.isSelected()) mIntegers.add(NoticeTask.WEDNESDAY);
        mCbThursday.setSelected(task.containsWeek(NoticeTask.THURSDAY));
        mCbThursday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbThursday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
        if (mCbThursday.isSelected()) mIntegers.add(NoticeTask.THURSDAY);
        mCbFriday.setSelected(task.containsWeek(NoticeTask.FRIDAY));
        mCbFriday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbFriday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
        if (mCbFriday.isSelected()) mIntegers.add(NoticeTask.FRIDAY);
        mCbSaturday.setSelected(task.containsWeek(NoticeTask.SATURDAY));
        mCbSaturday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbSaturday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
        if (mCbSaturday.isSelected()) mIntegers.add(NoticeTask.SATURDAY);
        mCbSunday.setSelected(task.containsWeek(NoticeTask.SUNDAY));
        mCbSunday.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCbSunday.isSelected() ? R.mipmap.ic_launcher : 0, 0);
        if (mCbSunday.isSelected()) mIntegers.add(NoticeTask.SUNDAY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IGNORE_BATTERY_CODE:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "请开启忽略电池优化", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
