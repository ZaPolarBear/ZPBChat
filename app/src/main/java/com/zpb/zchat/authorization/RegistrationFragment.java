package com.zpb.zchat.authorization;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zpb.zchat.CONST;
import com.zpb.zchat.MainFragment;
import com.zpb.zchat.R;
import com.zpb.zchat.User;

import java.util.zip.CheckedOutputStream;


public class RegistrationFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText enterPassword, enterNick, enterMail, confirmPassword;
    private Button registerButton;
    private ImageView back;

    public static RegistrationFragment newInstance( ) {
        return new RegistrationFragment();
    }

    public RegistrationFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        enterPassword = view.findViewById(R.id.password_enter);
        enterNick = view.findViewById(R.id.nick_enter);
        enterMail = view.findViewById(R.id.mail_enter);
        confirmPassword = view.findViewById(R.id.confirm_password);
        registerButton = view.findViewById(R.id.register);
        back = view.findViewById(R.id.back);

        registerButton.setOnClickListener(this::OnClick);
        back.setOnClickListener(this::OnClick);
    }


    private void OnClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                registerUser();
                break;
            case R.id.back:
                AuthorizationFragment authPage = new AuthorizationFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, authPage)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    private void registerUser() {
        String email = enterMail.getText().toString().trim();
        String password = enterPassword.getText().toString().trim();
        String confirmedPassword = confirmPassword.getText().toString().trim();
        String userNick = enterNick.getText().toString().trim();

        if (email.isEmpty()) {
            enterMail.setError("?????????????????? ??????????!");
            enterMail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            enterPassword.setError("?????????????????? ????????????!");
            enterPassword.requestFocus();
            return;
        }

        if (confirmedPassword.isEmpty()) {
            confirmPassword.setError("?????????????????? ????????????!");
            confirmPassword.requestFocus();
            return;
        }

        if (userNick.isEmpty()) {
            enterNick.setError("?????????????????? ??????????????!");
            enterMail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enterMail.setError("?????????? ?????????????? ??????????????!");
            enterMail.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(userNick, FirebaseAuth.getInstance().getCurrentUser().getUid(), "noAvatar");
                            FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                            FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("avatar").setValue("noAvatar");

                            MainFragment mainPage = new MainFragment();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame, mainPage)
                                    .addToBackStack(null)
                                    .commit();

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            preferences.edit().putBoolean("isLogin", true).commit();
                            preferences.edit().putString("uid", mAuth.getCurrentUser().getUid()).commit();
                        }
                    }
                });
    }

}
