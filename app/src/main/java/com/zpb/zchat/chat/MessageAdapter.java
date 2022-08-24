package com.zpb.zchat.chat;


import static com.google.android.material.transition.MaterialSharedAxis.X;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zpb.zchat.CONST;
import com.zpb.zchat.R;
import com.zpb.zchat.chat.holders.ImageViewerActivity;
import com.zpb.zchat.chat.holders.VoicePlayer;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> userMessagesList;
    private DatabaseReference usersRef;
    private String messageSenderNick = "", messageReceiverNick = "";
    private String fromUserID;
    private DatabaseReference reference;
    private String messageID;
    private MessageAdapter instance = this;
    private ChatFragment chatFragment;



    public MessageAdapter(List<Message> userMessagesList, String messageSenderId, String messageReceiverId, ChatFragment chatFragment) {
        this.userMessagesList = userMessagesList;
        this.messageSenderNick = messageSenderId;
        this.messageReceiverNick = messageReceiverId;
        this.chatFragment = chatFragment;
    }

    public MessageAdapter(List<Message> userMessagesList, String messageSenderId) {
        this.userMessagesList = userMessagesList;
        this.messageSenderNick = messageSenderId;
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText, senderMessageTime, receiverMessageTime, senderTimeVoice;
        public RelativeLayout outMessage, inMessage, outVoice, inVoice;
        public ImageView messageSenderPicture, senderPlay, senderPause;
        public ImageView messageReceiverPicture;
        public SeekBar  senderSeekBar;




        private void handleShowView(View view) {
            if (getAdapterPosition() > X - 1) {
                view.setVisibility(View.GONE);
                return;
            }
            view.setVisibility(View.VISIBLE);
        }

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            handleShowView(itemView);
            outMessage = itemView.findViewById(R.id.textMessageOutcoming);
            inMessage = itemView.findViewById(R.id.textMessageIncoming);
            inVoice = itemView.findViewById(R.id.incomingVoice);
            receiverMessageTime = itemView.findViewById(R.id.receiver_time);
            senderMessageTime = itemView.findViewById(R.id.sender_time);
            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            //   receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            senderPlay = itemView.findViewById(R.id.imgPlay);
            senderPause = itemView.findViewById(R.id.imgPause);
            senderSeekBar = itemView.findViewById(R.id.seekBar);
            senderTimeVoice = itemView.findViewById(R.id.txtTime);
            outVoice = itemView.findViewById(R.id.outcomingVoice);

        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_types, viewGroup, false);
        reference = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").child(messageSenderNick).child("Chats").child(messageReceiverNick).child("Messages");
        usersRef = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference().child("users");
        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, @SuppressLint("RecyclerView") final int position) {


        Message messages = userMessagesList.get(position);
        fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();
        VoicePlayer voicePlayer = new VoicePlayer(messageViewHolder.itemView.getContext());
        voicePlayer.setMediaPlayerListener(new VoicePlayer.MediaPlayerListener() {
            @Override
            public void isPlaying(int currentDuration) {

            }

            @Override
            public void onPause() {
                voicePlayer.release();
                messageViewHolder.senderPlay.setVisibility(View.VISIBLE);
                messageViewHolder.senderPause.setVisibility(View.GONE);
            }

            @Override
            public void onStart() {
                messageViewHolder.senderPlay.setVisibility(View.GONE);
                messageViewHolder.senderPause.setVisibility(View.VISIBLE);
            }
        });

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        messageViewHolder.outMessage.setVisibility(View.GONE);
        messageViewHolder.inMessage.setVisibility(View.GONE);
        messageViewHolder.outVoice.setVisibility(View.GONE);
        messageViewHolder.inVoice.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);

        messageViewHolder.outMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatFragment.deleteMessage(messages);
            }
        });

        switch (fromMessageType) {
            case "text":
                if (fromUserID.equals(messageSenderNick)) {
                    messageViewHolder.outMessage.setVisibility(View.VISIBLE);
                    messageViewHolder.outMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            usersRef.child(messageSenderNick)
                                    .child("Chats").child(messageReceiverNick).child("Messages")
                                    .child(messages.getMessageID()).setValue(null);
                            usersRef.child(messageReceiverNick)
                                    .child("Chats").child(messageSenderNick).child("Messages")
                                    .child(messages.getMessageID()).setValue(null);
                        }
                    });
                    messageViewHolder.senderMessageText.setText(messages.getMessage());
                    messageViewHolder.senderMessageTime.setText(messages.getTime());
                } else {
//                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.inMessage.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMessageText.setText(messages.getMessage());
                    messageViewHolder.receiverMessageTime.setText(messages.getTime());
                }
                break;
            case "image":
                if (fromUserID.equals(messageSenderNick)) {
                    messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);
                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                            intent.putExtra("url", userMessagesList.get(position).getMessage());
                            messageViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });

                } else {
                    messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                            intent.putExtra("url", userMessagesList.get(position).getMessage());
                            messageViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });
                }
                break;
            case "pdf":

            case "docx":
                if (fromUserID.equals(messageSenderNick)) {
                    messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                            messageViewHolder.itemView.getContext().startActivity(intent);
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                }
                break;
            case "voice":
                if (fromUserID.equals(messageSenderNick)) {
                    messageViewHolder.outVoice.setVisibility(View.VISIBLE);
                    messageViewHolder.senderPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            voicePlayer.setUrlSource(messages.getMessage());
                            if (voicePlayer.isPlaying()){
                                messageViewHolder.senderPlay.setVisibility(View.VISIBLE);
                                messageViewHolder.senderPause.setVisibility(View.GONE);
                                voicePlayer.pause();
                            } else {
                                messageViewHolder.senderPlay.setVisibility(View.GONE);
                                messageViewHolder.senderPause.setVisibility(View.VISIBLE);
                                voicePlayer.start();
                            }
                        }
                    });

                }
                else {
                    messageViewHolder.inVoice.setVisibility(View.VISIBLE);
//                    messageViewHolder.senderPlay.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Log.d("Voice", messages.getMessage());
//                            voicePlayer.setUrlSource(messages.getMessage());
//                            if (voicePlayer.isPlaying()){
//                                voicePlayer.pause();
//                            } else {
//                                voicePlayer.start();
//                            }
//                        }
//                    });
                }
                break;

        }

//        messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (fromUserID.equals(messageSenderNick)) {
//                    FirebaseDatabase.getInstance().getReference("users")
//                            .child(messageSenderNick)
//                            .child("Chats").child(messageReceiverNick).child("Messages")
//                            .child(userMessagesList.get(position).getMessageID()).removeValue();
//                    FirebaseDatabase.getInstance().getReference("users")
//                            .child(messageReceiverNick)
//                            .child("Chats").child(messageSenderNick).child("Messages")
//                            .child(userMessagesList.get(position).getMessageID()).removeValue();
//                    delete(position);
//                }
//            }
//        });




    }




    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    public void delete(int position){
        userMessagesList.remove(position);
        notifyItemRemoved(position);
    }


}