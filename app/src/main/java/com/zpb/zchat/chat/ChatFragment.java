package com.zpb.zchat.chat;

import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zpb.zchat.CONST;
import com.zpb.zchat.MainFragment;
import com.zpb.zchat.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    private EditText messageTextEnter;
    private ImageButton sendMessage;
    private ImageButton backToAllChats;
    private ImageButton sendVoice;
    private Image userImage;
    private ImageButton sendImage;
    private RecyclerView messagesViewList;
    private TextView nickname;
    private TextView lastSeen;

    private MessageAdapter messageAdapter;
    private List<Message> messageList = new ArrayList<>();

    private int voiceDuration;
    private FirebaseDatabase database;
    private DatabaseReference chatReference;

    private String senderUid;
    private String userSender, userReceiver;
    private Chat chat;


    public ChatFragment() {

    }

    public ChatFragment(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

//        root.findViewById(R.id.voice_button);


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        sendMessage = view.findViewById(R.id.send_message);
        sendVoice = view.findViewById(R.id.send_voice);
        sendImage = view.findViewById(R.id.send_image);
        backToAllChats = view.findViewById(R.id.back);
        nickname = view.findViewById(R.id.nickname);
        messageTextEnter = view.findViewById(R.id.enter_text);
        messagesViewList = view.findViewById(R.id.message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        messagesViewList.setLayoutManager(linearLayoutManager);

        database = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl);
        chatReference = database.getReference("users");

        userReceiver = chat.getName();
        userSender = preferences.getString("userNick", null);
        senderUid = preferences.getString("uid", null);
        nickname.setText(userReceiver);
        messageAdapter = new MessageAdapter(messageList, userSender, userReceiver);
        messagesViewList.setAdapter(messageAdapter);
        messagesViewList.setItemViewCacheSize(20);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

        backToAllChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainFragment mainPage = new MainFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, mainPage)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


    @Override
    public void onStart() {

        super.onStart();

        chatReference.child(senderUid).child("Chats").child(userReceiver).child("Messages")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Message messages = dataSnapshot.getValue(Message.class);
                        messageList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        messagesViewList.smoothScrollToPosition(messagesViewList.getAdapter().getItemCount());
                        Log.d("chatInfo", String.valueOf(messagesViewList.getAdapter().getItemCount()));

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void SendMessage() {

        String messageText = messageTextEnter.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(getContext(), "first write your message...", Toast.LENGTH_SHORT).show();
        } else {
            Send(messageText, "text");
        }

    }

    private void addLastMessage(String type, String Message) {
        switch (type) {
            case "text":
                addType("text");
                chatReference.child(senderUid).child("Chats").child(userReceiver).child("LastMessage").setValue(Message);
                chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("LastMessage").setValue(Message);
                break;
            case "voice":
                addType("voice");
                chatReference.child(senderUid).child("Chats").child(userReceiver).child("LastMessage").setValue("Голосовое сообщение");
                chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("LastMessage").setValue("Голосовое сообщение");
                break;
            case "image":
                chatReference.child(senderUid).child("Chats").child(userReceiver).child("LastMessage").setValue("Фотография");
                chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("LastMessage").setValue("Фотография");
                addType("image");
                break;
        }
        Calendar calendar = Calendar.getInstance();
        chatReference.child(senderUid).child("Chats").child(userReceiver).child("LastTime").setValue(getCurrentTime());
        chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("LastTime").setValue(getCurrentTime());
        chatReference.child(senderUid).child("Chats").child(userReceiver).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
        chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
    }

    public void addUnread() {
        final long[] value = new long[1];
        DatabaseReference ref = chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("Unread");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    value[0] = (long) dataSnapshot.getValue();
                    value[0] = value[0] + 1;
                    dataSnapshot.getRef().setValue(value[0]);
                } else dataSnapshot.getRef().setValue(0);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Send(String message, String type) {
        String messageSenderRef = chat.getReceiverUid() + "/Chats/" + userSender + "/Messages";
        String messageReceiverRef = senderUid + "/Chats/" + userReceiver + "/Messages";

        DatabaseReference userMessageKeyRef = chatReference.getRoot().child(senderUid).child("Chats").child(userReceiver).child("Messages").push();
        String messagePushID = userMessageKeyRef.getKey();
        
        Map<String, String> messageTextBody = new HashMap<String, String>();
        messageTextBody.put("message", message);
        messageTextBody.put("type", type);
        messageTextBody.put("from", userSender);
        messageTextBody.put("to", userReceiver);
        messageTextBody.put("time", getCurrentTime());
        messageTextBody.put("messageID", messagePushID);
        
        addLastMessage(type, message);

        Map<String, Object> messageBodyDetails = new HashMap<>();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
        messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);
        chatReference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                messageTextEnter.setText("");
            }
        });
    }

    public void addType(String type) {
        final long[] value = new long[1];
        DatabaseReference ref = chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child(type);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    value[0] = (long) dataSnapshot.getValue();
                    value[0] = value[0] + 1;
                    dataSnapshot.getRef().setValue(value[0]);
                } else dataSnapshot.getRef().setValue(1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static String getCurrentTime() {
        String time;
        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        String timeH, timeM;
        timeH = String.valueOf(hours);
        timeM = String.valueOf(minutes);
        if (minutes < 10)
            timeM = "0" + minutes;
        if (hours < 10)
            timeH = "0" + hours;
        time = timeH + ":" + timeM;
        return time;
    }

}
