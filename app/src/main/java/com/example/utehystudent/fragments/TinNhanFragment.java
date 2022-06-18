package com.example.utehystudent.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.Pushy.PushyAPI;
import com.example.utehystudent.R;
import com.example.utehystudent.adapters.ChatAdapter;
import com.example.utehystudent.model.Chat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gun0912.tedbottompicker.TedBottomPicker;

public class TinNhanFragment extends Fragment {

    Context activity, context;

    private RecyclerView rcv;
    ChatAdapter chatAdapter;
    ArrayList<Chat> listChat;
    FirebaseFirestore db;
    SharedPreferences pref;
    StorageReference storageReference;
    ProgressBar prgBar;
    String classID = "";

    EditText edtChat;
    ImageView imgSendChat, imgPickPhoto;

    Timestamp timestamp;

    FrameLayout frameSend, framePickPhoto;

    Uri uriTN;
    Boolean hasImage = false;


    public TinNhanFragment() {
        // Required empty public constructor
    }

    public TinNhanFragment(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tin_nhan, container, false);

        pref = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);

        rcv = view.findViewById(R.id.fragmentTN_rcv);
        chatAdapter = new ChatAdapter(view.getContext());

        storageReference = FirebaseStorage.getInstance().getReference("/"+pref.getString("class_ID", ""));

        edtChat = view.findViewById(R.id.fragmentTN_edtInput);
        imgSendChat = view.findViewById(R.id.fragmentTN_imgSend);
        imgPickPhoto = view.findViewById(R.id.fragmentTN_imgPickPhoto);

        frameSend = view.findViewById(R.id.fragmentTN_layoutSend);
        framePickPhoto = view.findViewById(R.id.fragmentTN_layoutPickPhoto);
        frameSend.setVisibility(View.GONE);

        prgBar = view.findViewById(R.id.fragmentTN_prgBar);

        db = FirebaseFirestore.getInstance();

        listChat = new ArrayList<>();

        classID = pref.getString("class_ID", "");

        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        rcv.setLayoutManager(manager);

        chatAdapter.setData(listChat);
        rcv.setAdapter(chatAdapter);

        prgBar.setVisibility(View.VISIBLE);
        rcv.setVisibility(View.GONE);

        listenForChatChange();

        prgBar.setVisibility(View.GONE);
        rcv.setVisibility(View.VISIBLE);

        Events();

        return view;
    }

    private void Events() {
        imgSendChat.setOnClickListener(view -> {
            if (edtChat.getText().toString().equals("")) {
                edtChat.setError("");
                edtChat.requestFocus();
                return;
            }
            sendMessage(edtChat.getText().toString().trim(), "");
        });

        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtChat.getText().toString().equals("")) {
                    frameSend.setVisibility(View.GONE);
                }else {
                    frameSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        framePickPhoto.setOnClickListener(view -> {
            requestPermission();
        });

    }

    private void sendMessage(String message, String imgLink) {
        String username = pref.getString("username", "");
        String name = pref.getString("name", "");
        timestamp = new Timestamp(System.currentTimeMillis());
        Chat chat = new Chat("chat"+username+""+timestamp.getTime(), username, imgLink, classID, message);
        db.collection("Chat")
                .add(chat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        edtChat.setText("");
                        sendNotificationToClass(chat.getNoiDung(), name);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    private void listenForChatChange() {
        db.collection("Chat")
                .whereEqualTo("classID", classID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("get chat", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    listChat.add(dc.getDocument().toObject(Chat.class));
                                    Collections.sort(listChat);
                                    chatAdapter.notifyDataSetChanged();
                                    Log.d("get chat", "New chat: " + dc.getDocument().getData());
                                    break;
                            }
                        }
                        rcv.scrollToPosition(listChat.size()-1);
                    }
                });
    }

    public void sendNotificationToClass(String message, String title) {
        String username = pref.getString("username", "");
        String classID = pref.getString("class_ID", "");

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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = activity.getContentResolver();
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(cR.getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        return extension;
    }

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openBottomPicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(requireActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(requireActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void openBottomPicker() {
        TedBottomPicker.OnImageSelectedListener listener = new TedBottomPicker.OnImageSelectedListener() {
            @Override
            public void onImageSelected(Uri uri) {
                uploadImage(uri);
            }
        };

        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(requireActivity())
                .setOnImageSelectedListener(listener)
                .setCompleteButtonText("DONE")
                .setEmptySelectionText("NO IMAGE")
                .create();
        tedBottomPicker.show(requireActivity().getSupportFragmentManager());
    }

    private void uploadImage(Uri uri) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        if (uri != null) {
            String fileName = "chatimage"+pref.getString("username","")+""+ts.getTime()+"." + getFileExtension(uri);
            StorageReference fileRef = storageReference.child(fileName);
            UploadTask uploadTask = fileRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(requireActivity(),"Gửi ảnh thất bại",Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("post","thanhcong");
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String link_img = String.valueOf(uri);
                            sendMessage(edtChat.getText().toString().trim(), link_img);
                        }
                    });
                }
            });
        }
    }

}