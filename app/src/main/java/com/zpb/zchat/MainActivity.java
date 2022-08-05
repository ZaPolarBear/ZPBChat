package com.zpb.zchat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends Activity {

    private RecyclerView chatListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatListView =  findViewById(R.id.chat_list);
        chatListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private static class ChatViewHolder extends RecyclerView.ViewHolder
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