package com.example.utehystudent.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

public class TinNhanFragment extends Fragment {

    private RecyclerView rcv;
    ChatAdapter chatAdapter;
    ArrayList<Chat> listChat;
    FirebaseFirestore db;
    SharedPreferences pref;
    ProgressBar prgBar;
    String classID = "";

    EditText edtChat;
    ImageView imgSendChat;

    Timestamp timestamp;

    FrameLayout frameSend;

    public TinNhanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tin_nhan, container, false);

        rcv = view.findViewById(R.id.fragmentTN_rcv);
        chatAdapter = new ChatAdapter(view.getContext());

        edtChat = view.findViewById(R.id.fragmentTN_edtInput);
        imgSendChat = view.findViewById(R.id.fragmentTN_imgSend);

        frameSend = view.findViewById(R.id.fragmentTN_layoutSend);
        frameSend.setVisibility(View.GONE);

        prgBar = view.findViewById(R.id.fragmentTN_prgBar);

        db = FirebaseFirestore.getInstance();

        listChat = new ArrayList<>();

        pref = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
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
            sendMessage(edtChat.getText().toString().trim());
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
    }

    private void sendMessage(String message) {
        String username = pref.getString("username", "");
        timestamp = new Timestamp(System.currentTimeMillis());
        Chat chat = new Chat("chat"+username+""+timestamp.getTime(), username, "", classID, message);
        db.collection("Chat")
                .add(chat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        edtChat.setText("");
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

}