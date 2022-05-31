package com.example.utehystudent.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.CalendarWeekDay;
import com.applandeo.materialcalendarview.EventDay;
import com.example.utehystudent.R;
import com.example.utehystudent.activity.MainActivity;
import com.example.utehystudent.calendar_setup.DrawableUtils;
import com.example.utehystudent.model.Activity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LichHoatDongFragment extends Fragment {
    CalendarView calendarView;
    ArrayList<Activity> listActivity;
    TextView tvDay, tvContent;
    Context activity;
    Context context;

    public LichHoatDongFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lich_hoat_dong, container, false);

        tvDay = view.findViewById(R.id.ActivitySchedule_tvNgay);
        tvContent = view.findViewById(R.id.ActivitySchedule_tvContent);

        calendarView = view.findViewById(R.id.ActivitySchedule_calendar);
        calendarView.setFirstDayOfWeek(CalendarWeekDay.SUNDAY);
        calendarView.setSwipeEnabled(true);
        calendarView.setCalendarDayLayout(R.layout.custom_calendar_day_row);
        listActivity = new ArrayList<>();

        if (MainActivity.listActivitySchedule.size() == 0) {
            setUpCalendar();
        }else {
            reloadData();
        }

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar calendar = eventDay.getCalendar();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String ngayClick = simpleDateFormat.format(calendar.getTime());
            for (Activity sk : listActivity) {
                if (sk.getDate().equals(ngayClick)) {
                    tvDay.setVisibility(View.VISIBLE);
                    tvDay.setText("Nội dung: " + sk.getDate());
                    tvContent.setText(sk.getContent());
                    return;
                }
            }
            tvDay.setVisibility(View.VISIBLE);
            tvDay.setText("Nội dung: " + ngayClick);
            tvContent.setText("Không có hoạt động nào !");
        });

        // Inflate the layout for this fragment
        return view;
    }

    public int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000.0 * 60.0 * 60.0 * 24.0));
    }


    public void setUpCalendar() {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Đang tải dữ liệu");
        progressDialog.show();
        List<EventDay> events = new ArrayList<>();
        SharedPreferences pref = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);

        String classID = pref.getString("class_ID", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MainActivity.listActivitySchedule.clear();
        db.collection("Activity")
                .whereEqualTo("class_ID", classID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Activity activity = document.toObject(Activity.class);
                            listActivity.add(activity);
                            MainActivity.listActivitySchedule.add(activity);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                Date dateSK = simpleDateFormat.parse(activity.getDate());
                                Date today = new Date();

                                String da = simpleDateFormat.format(dateSK);
                                String to = simpleDateFormat.format(today);

                                if (da.equals(to)) {
                                    Calendar calendar1 = Calendar.getInstance();
                                    calendar1.add(Calendar.DAY_OF_WEEK, 0);
                                    events.add(new EventDay(calendar1, DrawableUtils.getThreeDots(context)));
                                } else {
                                    int t = daysBetween(today, dateSK);
                                    if (t <= 0) {
                                        Calendar calendar2 = Calendar.getInstance();
                                        calendar2.add(Calendar.DAY_OF_WEEK, t);
                                        events.add(new EventDay(calendar2, DrawableUtils.getThreeDots(context)));
                                    } else {
                                        Calendar calendar3 = Calendar.getInstance();
                                        calendar3.add(Calendar.DAY_OF_WEEK, t + 1);
                                        events.add(new EventDay(calendar3, DrawableUtils.getThreeDots(context)));
                                    }
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            calendarView.setEvents(events);
                            progressDialog.dismiss();
                        }

                    } else {
                        Log.d("get activity: ", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void reloadData() {
        listActivity.clear();
        listActivity = MainActivity.listActivitySchedule;
        List<EventDay> events = new ArrayList<>();
        for (Activity activity : listActivity) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date dateSK = simpleDateFormat.parse(activity.getDate());
                Date today = new Date();

                String da = simpleDateFormat.format(dateSK);
                String to = simpleDateFormat.format(today);

                if (da.equals(to)) {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.add(Calendar.DAY_OF_WEEK, 0);
                    events.add(new EventDay(calendar1, DrawableUtils.getThreeDots(context)));
                } else {
                    int t = daysBetween(today, dateSK);
                    if (t <= 0) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.add(Calendar.DAY_OF_WEEK, t);
                        events.add(new EventDay(calendar2, DrawableUtils.getThreeDots(context)));
                    } else {
                        Calendar calendar3 = Calendar.getInstance();
                        calendar3.add(Calendar.DAY_OF_WEEK, t + 1);
                        events.add(new EventDay(calendar3, DrawableUtils.getThreeDots(context)));
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendarView.setEvents(events);
        }
    }
}