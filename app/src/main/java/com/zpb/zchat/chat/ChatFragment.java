package com.zpb.zchat.chat;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zpb.zchat.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    private EditText messageTextEnter;
    private ImageButton sendMessageButton;
    private ImageButton backToAllChats;
    private Image userImage;
    private ImageButton sendImage;
    private RecyclerView messagesViewList;
    private List<Message> messageList;
    private int voiceDuration;
    private ImageButton sendVoice;
    private FirebaseDatabase database;
    private DatabaseReference chatReference = database.getReference("users");
    private String userSender, userReceiver;
    private String link = "";

    public ChatFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
      //  View root = inflater.inflate(R.layout.activity_main, container, false);

       // root.findViewById();



   //     return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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

    private void addLastMessage(String type, String Message) {
        switch (type) {
            case "text":
                addType("text");
                chatReference.child(userSender).child("Chats").child(userReceiver).child("LastMessage").setValue(Message);
                chatReference.child(userReceiver).child("Chats").child(userSender).child("LastMessage").setValue(Message);
                break;
            case "voice":
                addType("voice");
                chatReference.child(userSender).child("Chats").child(userReceiver).child("LastMessage").setValue("Голосовое сообщение");
                chatReference.child(userReceiver).child("Chats").child(userSender).child("LastMessage").setValue("Голосовое сообщение");
                break;
            case "image":
                chatReference.child(userSender).child("Chats").child(userReceiver).child("LastMessage").setValue("Фотография");
                chatReference.child(userReceiver).child("Chats").child(userSender).child("LastMessage").setValue("Фотография");
                addType("image");
                break;
        }
        Calendar calendar = Calendar.getInstance();
        chatReference.child(userSender).child("Chats").child(userReceiver).child("LastTime").setValue(getCurrentTime());
        chatReference.child(userReceiver).child("Chats").child(userSender).child("LastTime").setValue(getCurrentTime());
        chatReference.child(userSender).child("Chats").child(userReceiver).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
        chatReference.child(userReceiver).child("Chats").child(userSender).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
    }

    public void addUnread() {
        final long[] value = new long[1];
        DatabaseReference ref = chatReference.child(userReceiver).child("Chats").child(userSender).child("Unread");
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
        String messageSenderRef = userReceiver + "/Chats/" + userSender + "/Messages";
        String messageReceiverRef = userSender + "/Chats/" + userReceiver + "/Messages";

        DatabaseReference userMessageKeyRef = chatReference.getRoot().child(userSender).child("Chats").child(userReceiver).child("Messages").push();
        String messagePushID = userMessageKeyRef.getKey();
        Map<String, String> messageTextBody = new HashMap<String, String>();
        messageTextBody.put("message", message);
        messageTextBody.put("type", type);
        messageTextBody.put("from", userSender);
        messageTextBody.put("to", userReceiver);
        messageTextBody.put("time", getCurrentTime());
        messageTextBody.put("messageID", messagePushID);
        addLastMessage(type, link);

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
        DatabaseReference ref = chatReference.child(userReceiver).child("Chats").child(userSender).child(type);
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
