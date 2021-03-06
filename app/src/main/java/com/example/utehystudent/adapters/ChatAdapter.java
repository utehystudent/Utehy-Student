package com.example.utehystudent.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.ImageViewerActivity;
import com.example.utehystudent.model.Chat;
import com.example.utehystudent.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Serializable {
    private static int TYPE_SENT_MESSAGE = 1;
    private static int TYPE_RECEIVED_MESSAGE = 2;

    SharedPreferences pref;
    Context context;
    String username = "";
    FirebaseFirestore db;

    public ChatAdapter(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        username = pref.getString("username", "");
        db = FirebaseFirestore.getInstance();
    }

    private ArrayList<Chat> listChat;

    public void setData(ArrayList<Chat> list) {
        listChat = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (TYPE_SENT_MESSAGE == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message, parent, false);
            return new ChatSentViewHoler(view);
        }else if (TYPE_RECEIVED_MESSAGE == viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message, parent, false);
            return new ChatReceivedViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = listChat.get(position);
        if (chat == null) {
            return;
        }
        if (TYPE_SENT_MESSAGE == holder.getItemViewType()) {
            ChatSentViewHoler chatSentViewHoler = (ChatSentViewHoler) holder;
            chatSentViewHoler.sentTVMessage.setText(chat.getNoiDung());
            if (chat.getNoiDung().equals("")) {
                chatSentViewHoler.sentTVMessage.setText("???? g???i m???t ???nh");
            }
            chatSentViewHoler.sentTVTime.setText(chat.getNgayDang());
            chatSentViewHoler.sentTVTime.setVisibility(View.INVISIBLE);
            chatSentViewHoler.sentTVMessage.setOnTouchListener((view, motionEvent) -> {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        chatSentViewHoler.sentTVTime.setVisibility(View.VISIBLE);
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        chatSentViewHoler.sentTVTime.setVisibility(View.INVISIBLE);
                        return true; // if you want to handle the touch event
                }
                return false;
            });
            if (!chat.getLinkImage().equals("")) {
                chatSentViewHoler.imgPhoto.setVisibility(View.VISIBLE);
                try {
                    Picasso.get().load(chat.getLinkImage()).into(chatSentViewHoler.imgPhoto);
                }catch (Exception e) {
                    e.getMessage();
                }
            }

            chatSentViewHoler.imgPhoto.setOnClickListener(view -> {
                Intent it = new Intent(context, ImageViewerActivity.class);
                ArrayList<String> link = new ArrayList<>();
                link.add(chat.getLinkImage());
                it.putExtra("link", link);
                context.startActivity(it);
            });

        }else if (TYPE_RECEIVED_MESSAGE == holder.getItemViewType()) {
            ChatReceivedViewHolder chatReceivedViewHolder = (ChatReceivedViewHolder) holder;
            String userID = chat.getUsername();
            db.collection("User")
                    .whereEqualTo("username", userID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    chatReceivedViewHolder.receivedTVName.setText(doc.toObject(User.class).getName());
                                    try {
                                        Picasso.get().load(doc.toObject(User.class).getAvt_link()).noFade().into(chatReceivedViewHolder.receivedImgAvt);
                                    }catch (Exception e) {
                                        Picasso.get().load("https://icon-library.com/images/no-user-image-icon/no-user-image-icon-27.jpg").noFade().into(chatReceivedViewHolder.receivedImgAvt);
                                    }
                                }
                            }else {
                                Picasso.get().load("https://icon-library.com/images/no-user-image-icon/no-user-image-icon-27.jpg").noFade().into(chatReceivedViewHolder.receivedImgAvt);
                            }
                        }
                    });
            chatReceivedViewHolder.receivedTVMessage.setText(chat.getNoiDung());
            if (chat.getNoiDung().equals("")) {
                chatReceivedViewHolder.receivedTVMessage.setText("???? g???i m???t ???nh");
            }
            chatReceivedViewHolder.receivedTVTime.setText(chat.getNgayDang());
            chatReceivedViewHolder.receivedTVTime.setVisibility(View.INVISIBLE);
            chatReceivedViewHolder.receivedTVMessage.setOnTouchListener((view, motionEvent) -> {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        chatReceivedViewHolder.receivedTVTime.setVisibility(View.VISIBLE);
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        chatReceivedViewHolder.receivedTVTime.setVisibility(View.INVISIBLE);
                        return true; // if you want to handle the touch event
                }
                return false;
            });

            if (!chat.getLinkImage().equals("")) {
                chatReceivedViewHolder.imgPhoto.setVisibility(View.VISIBLE);
                try {
                    Picasso.get().load(chat.getLinkImage()).into(chatReceivedViewHolder.imgPhoto);
                }catch (Exception e) {
                    e.getMessage();
                }
            }else {
                chatReceivedViewHolder.imgPhoto.setVisibility(View.GONE);
            }

            chatReceivedViewHolder.imgPhoto.setOnClickListener(view -> {
                Intent it = new Intent(context, ImageViewerActivity.class);
                ArrayList<String> link = new ArrayList<>();
                link.add(chat.getLinkImage());
                it.putExtra("link", link);
                context.startActivity(it);
            });

        }
    }

    @Override
    public int getItemCount() {
        if (listChat != null) {
            return listChat.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = listChat.get(position);
        if (chat.getUsername().equals(username)) {
            return TYPE_SENT_MESSAGE;
        }else {
            return TYPE_RECEIVED_MESSAGE;
        }
    }

    public class ChatSentViewHoler extends RecyclerView.ViewHolder {

        private TextView sentTVMessage, sentTVTime;
        private ImageView imgPhoto;

        public ChatSentViewHoler(@NonNull View itemView) {
            super(itemView);
            sentTVMessage = itemView.findViewById(R.id.itemSentMessage_tvMessage);
            sentTVTime = itemView.findViewById(R.id.itemSentMessage_tvTime);
            imgPhoto = itemView.findViewById(R.id.itemSentMessage_imgAnh);
        }
    }

    public class ChatReceivedViewHolder extends RecyclerView.ViewHolder {
        private TextView receivedTVMessage, receivedTVTime, receivedTVName;
        private ImageView receivedImgAvt;
        private ImageView imgPhoto;
        public ChatReceivedViewHolder(@NonNull View itemView) {
            super(itemView);

            receivedTVMessage = itemView.findViewById(R.id.itemReceivedMessage_tvMessage);
            receivedTVTime = itemView.findViewById(R.id.itemReceivedMessage_tvTime);
            receivedTVName = itemView.findViewById(R.id.itemReceivedMessage_tvName);
            receivedImgAvt = itemView.findViewById(R.id.itemReceivedMessage_imgAvt);
            imgPhoto = itemView.findViewById(R.id.itemReceivedMessage_imgAnh);

        }
    }
}
