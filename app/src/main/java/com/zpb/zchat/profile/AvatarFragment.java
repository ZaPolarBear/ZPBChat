package com.zpb.zchat.profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AvatarFragment extends Fragment {

    private CircleImageView avatar;
    private Button save;
    private Intent intent;
    private String imageUrl = "";
    private Uri imageUri;
    private View back;
    private String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_avatar, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        save = view.findViewById(R.id.save_avatar);
        avatar = view.findViewById(R.id.avatar);
        back = view.findViewById(R.id.back);

        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        back.setOnClickListener(this::OnClick);
        avatar.setOnClickListener(this::OnClick);
        save.setOnClickListener(this::OnClick);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        id = preferences.getString("uid", null);

        Query query = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").child(id).child("avatar");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Picasso.get().load(String.valueOf(snapshot.getValue())).into(avatar);
                    Log.d("image", snapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void OnClick(View view) {
        switch (view.getId()) {
            case R.id.save_avatar:
                SaveImage();
                break;
            case R.id.avatar:
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 443);
                break;
            case R.id.back:
                Fragment mFragment = null;
                mFragment = new MainFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame, mFragment).commit();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 443 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();

            Picasso.get().load(imageUri).into(avatar);
        }
    }

    private void SaveImage() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images");

        DatabaseReference userKeyRef = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").getRoot().child(id).child("avatar").push();

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

                FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl).getReference("users").child(id).child("avatar").setValue(imageUrl);
            }
        });
    }
}

