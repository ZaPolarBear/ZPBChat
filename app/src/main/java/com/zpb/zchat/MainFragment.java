package com.zpb.zchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zpb.zchat.authorization.RegistrationFragment;

public class MainFragment extends Fragment {

    private RecyclerView chatListView;
    private TextView text;
    private Bundle bundle = new Bundle();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.main_page, container, false);

        chatListView =  view.findViewById(R.id.chat_list);
        chatListView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatListView.setOnClickListener(this::OnClick);

        text = view.findViewById(R.id.chat);
        text.setOnClickListener(this::OnClick);

        bundle.putString("key","start");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void OnClick(View view){
        FragmentTransaction ft;
        switch (view.getId()){
            case R.id.chat:

                break;
        }
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
