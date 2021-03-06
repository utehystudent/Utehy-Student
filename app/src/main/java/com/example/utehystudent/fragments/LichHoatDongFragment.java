package com.example.utehystudent.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.utehystudent.Pushy.PushyAPI;
import com.example.utehystudent.R;
import com.example.utehystudent.activity.MainActivity;
import com.example.utehystudent.calendar_setup.DrawableUtils;
import com.example.utehystudent.model.Activity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Activity activity_chose = new Activity();
    String username, maLop, name;

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
        username = pref.getString("username", "");
        maLop = pref.getString("class_ID", "");
        name = pref.getString("name", "");

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
                    activity_chose = sk;
                    isHasEvent = true;
                    linearNgay.setVisibility(View.VISIBLE);
                    tvDay.setText("N???i dung: " + sk.getDate());
                    tvContent.setText(sk.getContent());
                    tvContent.setVisibility(View.VISIBLE);
                    btnThemTbao.setVisibility(View.GONE);
                    if (regency.equals("lt")) {
                        imgEdit.setVisibility(View.VISIBLE);
                        imgUnpin.setVisibility(View.VISIBLE);
                    } else {
                        imgEdit.setVisibility(View.GONE);
                        imgUnpin.setVisibility(View.GONE);
                    }
                    return;
                }
            }
            if (!isHasEvent) {
                linearNgay.setVisibility(View.VISIBLE);
                tvDay.setText("N???i dung: " + ngayClick);
                imgEdit.setVisibility(View.GONE);
                imgUnpin.setVisibility(View.GONE);

                if (regency.equals("lt")) {
                    btnThemTbao.setVisibility(View.VISIBLE);
                    tvContent.setVisibility(View.GONE);
                } else {
                    tvContent.setText("Kh??ng c?? ho???t ?????ng n??o !");
                }
            }
        });

        btnThemTbao.setOnClickListener(view -> {
            DialogThemHoatDong();
        });

        imgEdit.setOnClickListener(view -> {
            DialogSuaThongBao();
        });

        imgUnpin.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage("B???n c?? ch???c mu???n g??? th??ng b??o ng??y "+activity_chose.getDate()+" kh??ng ?");
            alert.setNegativeButton("H???Y", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                return;
            });
            alert.setPositiveButton("C??", (dialogInterface, i) -> {
                unpinActivity(dialogInterface);
            });
            alert.create();
            alert.show();
        });


    }

    private void unpinActivity(DialogInterface dialogInterface) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Activity")
                .whereEqualTo("class_ID", activity_chose.getClass_ID())
                .whereEqualTo("date", activity_chose.getDate())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        value.forEach(it -> {
                            String docID = it.getId();
                            unpin(docID, activity_chose.getDate(), dialogInterface);
                        });
                    }
                });
    }

    private void unpin(String docID, String date, DialogInterface dialogInterface) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Activity")
                .document(docID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "G??? ho???t ?????ng th??nh c??ng !", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        deleteActivityFromList();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "G??? ho???t ?????ng th???t b???i", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
    }

    private void deleteActivityFromList() {
        for (int i = 0; i< MainActivity.listActivitySchedule.size(); ++i) {
            if (MainActivity.listActivitySchedule.get(i).getDate().equals(activity_chose.getDate())) {
                MainActivity.listActivitySchedule.remove(i);
                break;
            }
        }
        activity_chose = new Activity();

        setUpCalendar();

        linearNgay.setVisibility(View.GONE);
        tvContent.setVisibility(View.GONE);
        btnThemTbao.setVisibility(View.GONE);
    }

    private void DialogSuaThongBao() {
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.edit_activity_schedule);
        TextView tvDate = dialog.findViewById(R.id.dialogSuaHoatDong_tvNgay);
        EditText edtContent = dialog.findViewById(R.id.dialogSuaHoatDong_edtND);
        Button btnXong = dialog.findViewById(R.id.dialogSuaHoatDong_btnXong);

        tvDate.setText("Ng??y: " + activity_chose.getDate());
        edtContent.setText(activity_chose.getContent());

        btnXong.setOnClickListener(view -> {
            if (edtContent.getText().toString().equals("")) {
                edtContent.setError("N???i dung c??n tr???ng");
                edtContent.requestFocus();
                return;
            }

            activity_chose.setContent(edtContent.getText().toString());
            //update activity to firestore
            getDocumentID(activity_chose, dialog);
            //send notification
            String message = activity_chose.getContent();
            String title = name+" ???? thay ?????i ho???t ?????ng ng??y "+activity_chose.getDate();
            sendNotificationToClass(message, title, classID);
        });

        dialog.create();
        dialog.show();
    }

    private void getDocumentID(Activity activity, Dialog dialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Activity")
                .whereEqualTo("class_ID", activity.getClass_ID())
                .whereEqualTo("date", activity.getDate())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        value.forEach(it -> {
                            String docID = it.getId();
                            updateActivity(activity, dialog, docID);
                        });
                    }
                });
    }

    private void updateActivity(Activity activity, Dialog dialog, String docID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Activity")
                .document(docID)
                .update("content", activity.getContent())
                .addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    Toast.makeText(context, "C???p nh???t ?????ng th??nh c??ng !", Toast.LENGTH_SHORT).show();
                    linearNgay.setVisibility(View.GONE);
                    tvContent.setVisibility(View.GONE);
                    btnThemTbao.setVisibility(View.GONE);

                    setUpCalendar();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(context, "C???p nh???t th???t b???i", Toast.LENGTH_SHORT).show();
                    Log.e("UPDATE ACTIVITY", "updateActivity: "+e.getMessage() );
                    return;
                });
    }

    public int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000.0 * 60.0 * 60.0 * 24.0));
    }

    public void setUpCalendar() {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("??ang t???i d??? li???u");
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
                                    if (t < 0) {        //old t <= 0
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
        Log.d("qqq", "reloadData: "+MainActivity.listActivitySchedule.size());
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
            Toast.makeText(context, "Vui l??ng ch???n ng??y", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.add_activity_schedule);
        TextView tvDate = dialog.findViewById(R.id.dialogThemHoatDong_tvNgay);
        EditText edtContent = dialog.findViewById(R.id.dialogThemHoatDong_edtND);
        Button btnXong = dialog.findViewById(R.id.dialogThemHoatDong_btnXong);

        tvDate.setText("Ng??y: " + dateChose);

        btnXong.setOnClickListener(view -> {
            SharedPreferences pref = context.getSharedPreferences("User", Context.MODE_PRIVATE);
            String cID = pref.getString("class_ID", "");
            if (edtContent.getText().toString().equals("")) {
                edtContent.setError("N???i dung c??n tr???ng");
                edtContent.requestFocus();
                return;
            }
            Activity activity = new Activity();
            activity.setClass_ID(cID);
            activity.setDate(dateChose);

            activity.setContent(edtContent.getText().toString());
            //add activity to firestore
            addActivity(activity, dialog);
            //send notification
            String message = edtContent.getText().toString();
            String title = name+" ???? th??m m???t ho???t ?????ng v??o ng??y "+activity.getDate();
            sendNotificationToClass(message, title, classID);
        });

        dialog.create();
        dialog.show();
    }

    private void addActivity(Activity activity, Dialog dialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Activity")
                .add(activity)
                .addOnSuccessListener(documentReference -> {
                    MainActivity.listActivitySchedule.add(activity);
                    //reloadData();

                    setUpCalendar();

                    dialog.dismiss();
                    Toast.makeText(context, "Th??m ho???t ?????ng th??nh c??ng", Toast.LENGTH_SHORT).show();
                    linearNgay.setVisibility(View.GONE);
                    tvContent.setVisibility(View.GONE);
                    btnThemTbao.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Th??m ho???t ?????ng th???t b???i", Toast.LENGTH_SHORT).show();
                    return;
                });
    }

    public void sendNotificationToClass(String message, String title, String classID) {

        String to = "/topics/"+classID;

        // Set payload (any object, it will be serialized to JSON)
        Map<String, String> payload = new HashMap<>();

        // Add "message" parameter to payload
        payload.put("message", message);
        payload.put("idNguoiGui", username);
        payload.put("title", title);

        // iOS notification fields
        Map<String, Object> notification = new HashMap<>();

        notification.put("badge", 1);
        notification.put("sound", "ping.aiff");
        notification.put("title", title);
        notification.put("body", message);

        // Prepare the push request
        PushyAPI.PushyPushRequest push = new PushyAPI.PushyPushRequest(payload, to, notification);

        try {
            // Try sending the push notification
            PushyAPI.sendPush(push);
        }
        catch (Exception exc) {
            // Error, print to console
            System.out.println(exc.toString());
        }
    }
}