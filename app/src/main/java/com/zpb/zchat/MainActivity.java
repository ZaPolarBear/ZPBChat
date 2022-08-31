package com.zpb.zchat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zpb.zchat.authorization.AuthorizationFragment;
import com.zpb.zchat.authorization.RegistrationFragment;
import com.zpb.zchat.chat.ChatFragment;

public class MainActivity extends FragmentActivity {

    private RecyclerView chatListView;
    private TextView text;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLogin = preferences.getBoolean("isLogin", false);
        uid = preferences.getString("uid", null);

        if (isLogin) {
            Query query = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").child(uid).child("nickname");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    preferences.edit().putString("userNick", snapshot.getValue().toString()).commit();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Fragment mFragment = null;
            mFragment = new MainFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, mFragment).commit();
        } else {
            Fragment mFragment = null;
            mFragment = new AuthorizationFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, mFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setStatus("online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        setStatus("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setStatus("offline");
    }

    private void setStatus(String status) {
        if (!uid.isEmpty()) {
            FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").child(uid).child("status").setValue(status);
            FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").child(uid).child("inAppTime").setValue(ChatFragment.getCurrentTime());
        }
    }
}