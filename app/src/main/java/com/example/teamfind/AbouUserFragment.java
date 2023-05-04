package com.example.teamfind;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.annotations.concurrent.Background;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AbouUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView nickname;
    ImageView iconView;
    Button writeStory, editUserInfo;
    DatabaseReference databaseReference;
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    public AbouUserFragment() {
        // Required empty public constructor
    }
    public static AbouUserFragment newInstance(String param1, String param2) {
        AbouUserFragment fragment = new AbouUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_abou_user, container, false);
        nickname = view.findViewById(R.id.aboutUserNickname);
        iconView = view.findViewById(R.id.aboutUserImage);
        editUserInfo = view.findViewById(R.id.aboutUserChangeInfo);
        writeStory = view.findViewById(R.id.aboutUserWriteStory);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.child(currentFirebaseUser.getUid()).child("icon").getValue(String.class);
                String nick = dataSnapshot.child(currentFirebaseUser.getUid()).child("nickname").getValue(String.class);


                nickname.setText("Здравствуйте, " + nick);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        DownloadImageFromPath(imageUrl);
                    }
                }).start();


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

        editUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ActivityUser.class);
                startActivity(intent);
            }
        });
        writeStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),WriteStoryActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    @Background
    public void DownloadImageFromPath(String path) {
        InputStream in = null;
        Bitmap bmp = null;
        int responseCode = -1;
        try {
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.connect();
            responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = con.getInputStream();
                bmp = BitmapFactory.decodeStream(in);
                in.close();
                Bitmap finalBmp = bmp;
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        iconView.setImageBitmap(finalBmp);

                    }
                });

            }

        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }



    }
}


