package com.example.utehystudent.fragments;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.AlarmReceiver;
import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.ScheduleViewModel;
import com.example.utehystudent.ViewModel.UserViewModel;
import com.example.utehystudent.activity.AttendanceDetailActivity;
import com.example.utehystudent.activity.MainActivity;
import com.example.utehystudent.adapters.SubjectAbsentAdapter;
import com.example.utehystudent.adapters.ThongBaoAdmin_Adapter;
import com.example.utehystudent.model.Attendance;
import com.example.utehystudent.model.BaiViet;
import com.example.utehystudent.model.Schedule_detail;
import com.example.utehystudent.model.Subject;
import com.example.utehystudent.model.SubjectAbsent;
import com.example.utehystudent.model.SubjectsOfSemester_Detail;
import com.example.utehystudent.model.User;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

public class HomeFragment extends Fragment implements SubjectAbsentAdapter.EventListener{
    final String TAG = "Home";
    ImageView imgAvt;
    TextView tvName, tvClass, tvXinChao, tvMorning, tvAfternoon, tvEvening;
    UserViewModel userViewModel;
    ScheduleViewModel scheduleViewModel;
    Dialog dialog;
    LinearLayout linearEvening;
    ArrayList<Attendance> listAttendance = new ArrayList<>();
    FirebaseFirestore db;
    ArrayList<Subject> listSubjectInTerm;
    ArrayList<SubjectAbsent> listSubjectAbsent;
    ArrayList<SubjectsOfSemester_Detail> listDetail;
    String classID;
    SharedPreferences pref;
    SubjectAbsentAdapter subjectAbsentAdapter;
    RecyclerView rcvSubjectAbsent, rcvThongBao;
    LinearLayoutManager linearLayoutManager;
    RecyclerView.ItemDecoration itemDecoration;
    ImageView imgGood;
    TextView tvChuaNghi;
    ShimmerFrameLayout shimmerSubjectAbsent;
    ArrayList<BaiViet> listPost;
    ThongBaoAdmin_Adapter adapterTbao;
    TextView tvCVHN;
    Button btnAlarm;
    AlarmManager alarmManager;


    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imgAvt = view.findViewById(R.id.Home_imgAvt);
        tvName = view.findViewById(R.id.Home_tvTenSV);
        tvClass = view.findViewById(R.id.Home_tvLop);
        tvXinChao = view.findViewById(R.id.Home_tvXinChao);
        tvMorning = view.findViewById(R.id.Home_tvMorning);
        tvAfternoon = view.findViewById(R.id.Home_tvAfternoon);
        tvEvening = view.findViewById(R.id.Home_tvEvening);
        linearEvening = view.findViewById(R.id.Home_linearEvening);
        imgGood = view.findViewById(R.id.Home_imgGood);
        tvChuaNghi = view.findViewById(R.id.Home_tvChuaNghi);
        shimmerSubjectAbsent = view.findViewById(R.id.shimmerSubjectAbsent);
        shimmerSubjectAbsent.startShimmer();

        listSubjectInTerm = new ArrayList<>();
        listDetail = new ArrayList<>();

        tvCVHN = view.findViewById(R.id.Home_tvCVHomNay);
        btnAlarm = view.findViewById(R.id.Home_btnAlarm);

        listPost = new ArrayList<>();

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        scheduleViewModel = new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);

        db = FirebaseFirestore.getInstance();

        rcvSubjectAbsent = view.findViewById(R.id.Home_rcvSubjectAbsent);
        rcvThongBao = view.findViewById(R.id.Home_rcvThongBao);

        linearLayoutManager = new LinearLayoutManager(requireActivity());
        rcvSubjectAbsent.setLayoutManager(linearLayoutManager);
        itemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        rcvSubjectAbsent.addItemDecoration(itemDecoration);
        //
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(requireActivity());
        rcvThongBao.setLayoutManager(linearLayoutManager2);
        RecyclerView.ItemDecoration itemDecoration2 = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        rcvThongBao.addItemDecoration(itemDecoration2);

        pref = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);

        if ((MainActivity.listSubjectAbsent.size() == 0 && MainActivity.listAttendance.size() == 0) || MainActivity.listSubjectAbsent == null || MainActivity.listAttendance == null) {

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                return;
            }, 1000);

            GetListAttendance();
            GetListSubjectDetailInTerm();
            GetListSubjectAbsent();
        }else {
            subjectAbsentAdapter = new SubjectAbsentAdapter(MainActivity.listSubjectAbsent, HomeFragment.this);
            rcvSubjectAbsent.setAdapter(subjectAbsentAdapter);
            rcvSubjectAbsent.setVisibility(View.VISIBLE);
            shimmerSubjectAbsent.setVisibility(View.GONE);
            if (MainActivity.listSubjectAbsent.size() == 0) {
                rcvSubjectAbsent.setVisibility(View.GONE);
                imgGood.setVisibility(View.VISIBLE);
                tvChuaNghi.setVisibility(View.VISIBLE);
                MainActivity.listSubjectAbsent.clear();
            }
            listAttendance = MainActivity.listAttendance;
        }

        BindData();

        btnAlarm.setOnClickListener(v -> {
            showDialogAlarmSetup();
        });

        createNotificationChannel();

        return view;
    }

    private void GetListSubjectAbsent() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String maSV = pref.getString("username", "");
               /* MainActivity.listAttendance.clear();
                MainActivity.listAttendance.addAll(listAttendance);*/
                if (listSubjectInTerm.size() > 0) {
                    ArrayList<SubjectAbsent> dsMonHocVang = new ArrayList<>();
                    for (Subject mh : listSubjectInTerm) {
                        String maMH = mh.getSubject_ID();
                        String tenMH = mh.getSubject_name();
                        int soTC = mh.getNum_cred();
                        int soBuoiVang = 0;
                        for (Attendance dd : listAttendance) {
                            if (maMH.equals(dd.getSubject_ID())) {
                                if (dd.getList_Absent().contains(maSV)) {
                                    soBuoiVang++;
                                }
                            }
                        }
                        SubjectAbsent subjectAbsent = new SubjectAbsent(maMH, tenMH, soBuoiVang, soTC);
                        dsMonHocVang.add(subjectAbsent);
                    }

                    ArrayList<SubjectAbsent> tmp = new ArrayList<>();
                    for (SubjectAbsent mhv : dsMonHocVang) {
                        if (mhv.getNum_Absent() > 0) {
                            tmp.add(mhv);
                        }
                    }

                    Collections.sort(tmp);
                    subjectAbsentAdapter = new SubjectAbsentAdapter(tmp, HomeFragment.this);
                    MainActivity.listSubjectAbsent.clear();
                    MainActivity.listSubjectAbsent.addAll(tmp);
                    rcvSubjectAbsent.setAdapter(subjectAbsentAdapter);
                    rcvSubjectAbsent.setVisibility(View.VISIBLE);
                    shimmerSubjectAbsent.hideShimmer();
                    shimmerSubjectAbsent.setVisibility(View.GONE);
                    if (tmp.size() == 0) {
                        rcvSubjectAbsent.setVisibility(View.GONE);
                        imgGood.setVisibility(View.VISIBLE);
                        tvChuaNghi.setVisibility(View.VISIBLE);
                        MainActivity.listSubjectAbsent.clear();
                    }
                    handler.removeCallbacks(this);
                } else {
                    handler.postDelayed(this, 500);
                }
            }
        }, 300);

    }

    private void BindData() {
        ShowLoadingDialog();

        userViewModel.getUserLiveData().observe(requireActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                try {
                    Picasso.get().load(user.getAvt_link()).resize(300, 300).centerCrop().into(imgAvt);
                } catch (Exception e) {
                    imgAvt.setImageResource(R.drawable.utehy_logo);
                }
                tvName.setText(user.getName() + "");
                tvClass.setText("Mã SV: " + user.getUsername() + "  |  " + "Lớp: " + user.getClass_ID());
                tvXinChao.setText("XIN CHÀO");
            }
        });

        scheduleViewModel.GetData();
        scheduleViewModel.getScheduleLiveData().observe(requireActivity(), new Observer<Schedule_detail>() {
            @Override
            public void onChanged(Schedule_detail schedule_detail) {
                if (schedule_detail != null) {
                    tvMorning.setText(schedule_detail.getMorning());
                    tvAfternoon.setText(schedule_detail.getAfternoon());
                    if (schedule_detail.getEvening().equals("N/A") || schedule_detail.getEvening().equals("Nghỉ")) {
                        linearEvening.setVisibility(View.GONE);
                    } else {
                        linearEvening.setVisibility(View.VISIBLE);
                        tvEvening.setText(schedule_detail.getEvening());
                    }
                }
            }
        });

        getListPost();
        getCongViecHomNay();

        //dismiss loading dialog
        dialog.dismiss();
    }

    private void getCongViecHomNay() {
        tvCVHN.setVisibility(View.GONE);
        //get today time
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String currentDate = formatter.format(date);

        db.collection("Activity")
                .whereEqualTo("date", currentDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String taskToday = "";
                            for (DocumentSnapshot doc : task.getResult()) {
                                taskToday += doc.getString("content");
                            }
                            if (taskToday.equals("")) {
                                tvCVHN.setText("Không có công việc nào hôm nay");
                            }else {
                                tvCVHN.setText(taskToday);
                            }
                            tvCVHN.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void ShowLoadingDialog() {
        dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.loading_layout1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.create();
        dialog.show();
    }

    private void GetListAttendance() {
        String classID = pref.getString("class_ID", "");
        db.collection("Attendance")
                .whereEqualTo("class_ID", classID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            listAttendance.add(document.toObject(Attendance.class));
                            Log.d("attendance", "GetListAttendance: " + document.toObject(Attendance.class));
                        }
                        MainActivity.listAttendance = listAttendance;
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void GetSubjectInTerm() {
        for (SubjectsOfSemester_Detail detail : listDetail) {
            db.collection("Subject")
                    .whereEqualTo("subject_ID", detail.getSubject_ID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    listSubjectInTerm.add(document.toObject(Subject.class));
                                }
                                Log.d("subject", "onComplete: " + listSubjectInTerm.size());
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void GetListSubjectDetailInTerm() {
        classID = pref.getString("class_ID", "");

        db.collection("SubjectsOfSemester_Detail")
                .whereEqualTo("class_ID", classID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listDetail.add(document.toObject(SubjectsOfSemester_Detail.class));
                                Log.d("detail", "onComplete: " + document.toObject(SubjectsOfSemester_Detail.class));
                            }
                            GetSubjectInTerm();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getListPost() {
        rcvThongBao.setVisibility(View.GONE);
        listPost.clear();
        db.collection("Post")
                .whereEqualTo("idNguoiDang", "admin")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                listPost.add(doc.toObject(BaiViet.class));
                            }
                            Collections.sort(listPost);
                            adapterTbao = new ThongBaoAdmin_Adapter(getContext(), listPost);
                            rcvThongBao.setAdapter(adapterTbao);
                            rcvThongBao.setVisibility(View.VISIBLE);
                        }else {
                            return;
                        }
                    }
                });
    }

    private void showDialogAlarmSetup() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.alarm_setup);
        dialog.show();

        final int[] hour = {0};
        final int[] min = {0};
        
        TimePicker timePicker = dialog.findViewById(R.id.AlarmSetup_Picker);
        timePicker.setIs24HourView(true);

        TextInputEditText edtContent = dialog.findViewById(R.id.AlarmSetup_edtContent);
        Button btnXong = dialog.findViewById(R.id.AlarmSetup_btnXong);

        edtContent.setText(tvCVHN.getText().toString().trim());

        Calendar calendar = Calendar.getInstance();


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour[0] = hourOfDay;
                min[0] = minute;
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
        });

        btnXong.setOnClickListener(v -> {
            cancelAlarm(requireActivity());
            alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(requireActivity(), AlarmReceiver.class);
            intent.putExtra("message", edtContent.getText().toString().trim());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, intent, 0);

            Log.d("alarmm", "showDialogAlarmSetup: "+calendar.getTimeInMillis());

            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(requireActivity(), "Nhắc nhở vào lúc: "+hour[0]+":"+min[0], Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

    }

    private void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Log.d("alarmm", "cancelAlarm: HỦY");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "android";
            String description = "Channel alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("android", name, importance);
            channel.setDescription(description);

            NotificationManager manager = requireActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onEvent(String subjectID) {
        Intent intent = new Intent(requireActivity(), AttendanceDetailActivity.class);
        intent.putExtra("attendances", listAttendance);
        intent.putExtra("subject_ID", subjectID);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}