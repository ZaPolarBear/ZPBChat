package com.zpb.zchat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zpb.zchat.chat.Chat;
import com.zpb.zchat.chat.DialogsAdapter;
import com.zpb.zchat.profile.AvatarFragment;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private RecyclerView chatListView;
    private TextView text;
    private EditText findUser;
    private List<Chat> chatList = new ArrayList<>();
    private ImageButton openAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.main_page, container, false);

        text = view.findViewById(R.id.chat);
        findUser = view.findViewById(R.id.find_user);
        chatListView = view.findViewById(R.id.chat_list);
        openAvatar = view.findViewById(R.id.avatar_open);

        openAvatar.setOnClickListener(this::OnClick);
        chatListView.setOnClickListener(this::OnClick);
        text.setOnClickListener(this::OnClick);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadChats();

        findUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (findUser.getText().length() > 0){

                    chatList.clear();
                    DialogsAdapter dialogsAdapter = new DialogsAdapter(chatList);
                    chatListView.setAdapter(dialogsAdapter);

                    String searchUser = findUser.getText().toString().toLowerCase();

                    Query query = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()){
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    String userNick = dataSnapshot.child("nickname").getValue().toString().toLowerCase();
                                    if (userNick.contains(searchUser)) {
                                        Chat chat = new Chat(dataSnapshot.child("nickname").getValue().toString(), "Today", "Say hello!", "private", dataSnapshot.child("id").getValue().toString());
                                        chatList.add(chat);
                                        chatListView.setAdapter(dialogsAdapter);
                                        chatListView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void OnClick(View view) {
        FragmentTransaction ft;
        switch (view.getId()) {
            case R.id.chat:
                Toast.makeText(getContext(), "Макс лох", Toast.LENGTH_SHORT).show();
                break;
            case R.id.avatar_open:
                Fragment mFragment = null;
                mFragment = new AvatarFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame, mFragment).commit();

                break;
        }
    }

    private void loadChats() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        chatList.clear();

        DialogsAdapter dialogsAdapter = new DialogsAdapter(chatList);

        String senderUid = preferences.getString("uid", null);

        Query query = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").child(senderUid).child("Chats").orderByChild("TimeMill");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("uid").exists()) {
                            Chat chat = new Chat(dataSnapshot.child("nickName").getValue().toString(), dataSnapshot.child("LastTime").getValue().toString(), dataSnapshot.child("LastMessage").getValue().toString(), "private", dataSnapshot.child("uid").getValue().toString());
                            chatList.add(chat);
                            chatListView.setAdapter(dialogsAdapter);
                            chatListView.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
