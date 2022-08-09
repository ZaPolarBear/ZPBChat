package com.zpb.zchat.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zpb.zchat.CONST;
import com.zpb.zchat.R;

import java.util.List;
import java.util.Objects;

public class DialogsAdapter extends RecyclerView.Adapter<DialogsAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private DatabaseReference firebaseDatabase;

    public DialogsAdapter(List<Chat> chatList){
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseDatabase = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users");
        return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.userNick.setText(chat.getName());
        holder.lastTime.setText(chat.getLastTime());
        holder.lastMessage.setText(chat.getLastMessage());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    protected static class ChatViewHolder extends RecyclerView.ViewHolder
    {

        ImageView userImage;
        TextView lastMessage, lastTime, userNick;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            lastMessage = itemView.findViewById(R.id.last_message);
            lastTime = itemView.findViewById(R.id.time);
            userNick = itemView.findViewById(R.id.user_nick);
        }
    }
}
