package com.example.utehystudent.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.CalendarWeekDay;
import com.applandeo.materialcalendarview.EventDay;
import com.example.utehystudent.R;
import com.example.utehystudent.activity.MainActivity;
import com.example.utehystudent.calendar_setup.DrawableUtils;
import com.example.utehystudent.model.Activity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
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
    LinearLayout linearNgay;
    String regency = "";
    ImageView imgEdit, imgUnpin;
    Button btnThemTbao;
    String dateChose = "";
    String classID = "";
    FirebaseFirestore db;
    View rootView;

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
        //snackbar
        View mView = view.findViewById(R.id.fragment_lichHoatDong);
        View v = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = mView.getRootView();
        //
        tvDay = view.findViewById(R.id.ActivitySchedule_tvNgay);
        tvContent = view.findViewById(R.id.ActivitySchedule_tvContent);
        linearNgay = view.findViewById(R.id.ActivitySchedule_linearNgay);
        imgEdit = view.findViewById(R.id.ActivitySchedule_imgEdit);
        imgUnpin = view.findViewById(R.id.ActivitySchedule_imgUnpin);
        btnThemTbao = view.findViewById(R.id.ActivitySchedule_btnAdd);
        btnThemTbao.setVisibility(View.GONE);

        calendarView = view.findViewById(R.id.ActivitySchedule_calendar);
        calendarView.setFirstDayOfWeek(CalendarWeekDay.SUNDAY);
        calendarView.setSwipeEnabled(true);
        calendarView.setCalendarDayLayout(R.layout.custom_calendar_day_row);
        listActivity = new ArrayList<>();

        SharedPreferences pref = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        regency = pref.getString("regency", "");

        if (!regency.equals("lt")) {
            imgUnpin.setVisibility(View.GONE);
            imgEdit.setVisibility(View.GONE);
        }

        if (MainActivity.listActivitySchedule.size() == 0) {
            setUpCalendar();
        } else {
            reloadData();
        }

        Events();


        // Inflate the layout for this fragment
        return view;
    }

    private void Events() {
        calendarView.setOnDayClickListener(eventDay -> {
            Boolean isHasEvent = false;
            Calendar calendar = eventDay.getCalendar();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String ngayClick = simpleDateFormat.format(calendar.getTime());
            dateChose = ngayClick;
            for (Activity sk : listActivity) {
                if (sk.getDate().equals(ngayClick)) {
                    isHasEvent = true;
                    linearNgay.setVisibility(View.VISIBLE);
                    tvDay.setText("Nội dung: " + sk.getDate());
                    tvContent.setText(sk.getContent());
                    tvContent.setVisibility(View.VISIBLE);
                    btnThemTbao.setVisibility(View.GONE);
                    if (regency.equals("lt")) {
                        imgEdit.setVisibility(View.VISIBLE);
                        imgUnpin.setVisibility(View.VISIBLE);
                    }else {
                        imgEdit.setVisibility(View.GONE);
                        imgUnpin.setVisibility(View.GONE);
                    }
                    return;
                }
            }
            if (!isHasEvent) {
                linearNgay.setVisibility(View.VISIBLE);
                tvDay.setText("Nội dung: " + ngayClick);
                imgEdit.setVisibility(View.GONE);
                imgUnpin.setVisibility(View.GONE);

                if (regency.equals("lt")) {
                    btnThemTbao.setVisibility(View.VISIBLE);
                    tvContent.setVisibility(View.GONE);
                }else {
                    tvContent.setText("Không có hoạt động nào !");
                }
            }
        });

        btnThemTbao.setOnClickListener(view -> {
            DialogThemHoatDong();
        });
    }

    public int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000.0 * 60.0 * 60.0 * 24.0));
    }

    public void setUpCalendar() {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Đang tải dữ liệu");
        progressDialog.show();
        List<EventDay> events = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences("User", Context.MODE_PRIVATE);

        classID = pref.getString("class_ID", "");

        db = FirebaseFirestore.getInstance();
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


    private void DialogThemHoatDong() {
        if (dateChose.equals("")) {
            Toast.makeText(context, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.add_activity_schedule);
        TextView tvDate = dialog.findViewById(R.id.dialogThemHoatDong_tvNgay);
        EditText edtContent = dialog.findViewById(R.id.dialogThemHoatDong_edtND);
        Button btnXong = dialog.findViewById(R.id.dialogThemHoatDong_btnXong);

        tvDate.setText("Ngày: "+dateChose);

        btnXong.setOnClickListener(view -> {
            if (edtContent.getText().toString().equals("")) {
                edtContent.setError("Nội dung còn trống");
                edtContent.requestFocus();
                return;
            }
            Activity activity = new Activity();
            activity.setClass_ID(classID);
            activity.setDate(dateChose);
            activity.setContent(edtContent.getText().toString().trim());
            //add activity to firestore
            addActivity(activity, dialog);
        });

        dialog.create();
        dialog.show();
    }

    private void addActivity(Activity activity, Dialog dialog) {
        db.collection("Activity")
                .add(activity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dialog.dismiss();
                        Toast.makeText(context, "Thêm hoạt động thành công", Toast.LENGTH_SHORT).show();
                        MainActivity.listActivitySchedule.add(activity);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Thêm hoạt động thất bại", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
        reloadData();
    }

}