package com.example.utehystudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.model.Contact;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    Context context;
    ArrayList<Contact> listContact;

    public ContactAdapter(Context context) {
        this.context = context;
        listContact = new ArrayList<>();
    }

    public void setData(ArrayList<Contact> listContact) {
        this.listContact = listContact;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.row_contact, parent, false);

        ContactViewHolder viewHolder = new ContactViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = listContact.get(position);
        if (contact != null) {
            try{
                Picasso.get().load(contact.getAvt_link()).noFade().into(holder.imgAvt);
            }catch (Exception e) {
                Picasso.get().load("https://mybnec.org/storage/images/bom/no-picture.jpg").noFade().into(holder.imgAvt);
            }
            holder.tvName.setText(contact.getName());
            holder.tvPosition.setText(contact.getPosition());
            holder.tvEmail.setText(contact.getEmail());
            holder.tvPhone.setText(contact.getPhone());
        }
    }

    @Override
    public int getItemCount() {
        if (listContact.size() > 0) {
            return listContact.size();
        }
        return 0;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imgAvt;
        TextView tvName, tvPosition, tvEmail, tvPhone;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvt = itemView.findViewById(R.id.rowContact_imgAvt);

            tvName = itemView.findViewById(R.id.rowContact_tvName);
            tvPosition = itemView.findViewById(R.id.rowContact_tvPosition);
            tvEmail = itemView.findViewById(R.id.rowContact_tvEmail);
            tvPhone = itemView.findViewById(R.id.rowContact_tvPhone);
        }
    }
}
