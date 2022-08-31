package com.zpb.zchat.chat;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.zpb.zchat.CONST;
import com.zpb.zchat.MainFragment;
import com.zpb.zchat.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    private EditText messageTextEnter;
    private ImageButton sendMessage;
    private ImageButton backToAllChats;
    private ImageButton sendVoice;
    private CircleImageView userImage;
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
    private String imageUrl = "";
    private Uri imageUri;
    private Chat chat;
    private Intent intent;

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
        userImage = view.findViewById(R.id.user_image);
        lastSeen = view.findViewById(R.id.last_seen);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        messagesViewList.setLayoutManager(linearLayoutManager);

        database = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl);
        chatReference = database.getReference("users");
        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        userReceiver = chat.getName();

        Query query = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").child(chat.getReceiverUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("avatar").exists()) {
                    Picasso.get().load(String.valueOf(snapshot.child("avatar").getValue())).into(userImage);
                }
                if (snapshot.child("status").toString().contains("on")){
                    lastSeen.setText("online");
                }
                else {
                    lastSeen.setText(snapshot.child("inAppTime").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userSender = preferences.getString("userNick", null);
        senderUid = preferences.getString("uid", null);
        nickname.setText(userReceiver);
        messageAdapter = new MessageAdapter(messageList, userSender, userReceiver, this);
        messagesViewList.setAdapter(messageAdapter);
        messagesViewList.setItemViewCacheSize(20);

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendImage();
            }
        });

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

    private void SendImage(){
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 443);
    }

    private void SendMessage() {

        String messageText = messageTextEnter.getText().toString();
        if (messageText.equals(" ")){
            Toast.makeText(getContext(), "Wrong text", Toast.LENGTH_SHORT).show();
            messageTextEnter.setText("");
        } else {
            if (TextUtils.isEmpty(messageText)) {
                Toast.makeText(getContext(), "first write your message...", Toast.LENGTH_SHORT).show();
            } else {
                Send(messageText, "text");
            }
        }
    }

    private void addLastMessage(String type, String Message) {
        switch (type) {
            case "text":
                chatReference.child(senderUid).child("Chats").child(userReceiver).child("LastMessage").setValue(Message);
                chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("LastMessage").setValue(Message);
                break;
            case "voice":
                chatReference.child(senderUid).child("Chats").child(userReceiver).child("LastMessage").setValue("Голосовое сообщение");
                chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("LastMessage").setValue("Голосовое сообщение");
                break;
            case "image":
                chatReference.child(senderUid).child("Chats").child(userReceiver).child("LastMessage").setValue("Фотография");
                chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("LastMessage").setValue("Фотография");
                break;
        }
        Calendar calendar = Calendar.getInstance();
        chatReference.child(senderUid).child("Chats").child(userReceiver).child("LastTime").setValue(getCurrentTime());
        chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("LastTime").setValue(getCurrentTime());
        chatReference.child(senderUid).child("Chats").child(userReceiver).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
        chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
        chatReference.child(senderUid).child("Chats").child(userReceiver).child("nickName").setValue(userReceiver);
        chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("nickName").setValue(userSender);
        chatReference.child(senderUid).child("Chats").child(userReceiver).child("uid").setValue(chat.getReceiverUid());
        chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child("uid").setValue(senderUid);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 443 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images");

            DatabaseReference userKeyRef = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").getRoot().child(senderUid).child("Chats").push();

            final String messagePushID = userKeyRef.getKey();

            final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUrl = task.getResult();
                    imageUrl = downloadUrl.toString();
                    Send(imageUrl, "image");
                }
            });
        }
    }

    public void deleteMessage(Message message){
        chatReference.child(senderUid).child("Chats").child(userReceiver).child(message.getMessageID()).removeValue();
        chatReference.child(chat.getReceiverUid()).child("Chats").child(userSender).child(message.getMessageID()).removeValue();
        messageAdapter.notifyDataSetChanged();
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

    public static String getCurrentTime() {
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
