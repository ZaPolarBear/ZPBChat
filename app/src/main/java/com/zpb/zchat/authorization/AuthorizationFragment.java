package com.zpb.zchat.authorization;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.zpb.zchat.MainFragment;
import com.zpb.zchat.R;

public class AuthorizationFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText enterMail, enterPassword;
    private Button enterButton;

    public AuthorizationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_authorization, container, false);
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        enterMail = view.findViewById(R.id.mail_enter);
        enterPassword = view.findViewById(R.id.passsword_enter);
        enterButton = view.findViewById(R.id.enter);

        enterButton.setOnClickListener(this::OnClick);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void OnClick(View view){
        switch (view.getId()){
            case R.id.enter:
                enterUser();
                break;
        }
    }

    private void enterUser() {
        String email = enterMail.getText().toString().trim();
        String password = enterPassword.getText().toString().trim();

        if (email.isEmpty()) {
            enterMail.setError("Требуется почта!");
            enterMail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            enterPassword.setError("Требуется пароль!");
            enterPassword.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enterMail.setError("Почта введена неверно!");
            enterMail.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    MainFragment mainPage = new MainFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, mainPage)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }
}
