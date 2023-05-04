package com.example.teamfind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class WriteStoryActivity extends AppCompatActivity {
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    DatabaseReference myRef = database.getReference("stories/");

    Button btnChooseImage, btnSubmitStory;
    EditText editTextTitle, editTextContent;
    CheckBox checkBoxImage;
    Spinner spinnerGenre;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    String chosenGenre = "Страшилки";
    String id = UUID.randomUUID().toString();



    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_story);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btnChooseImage = (Button)findViewById(R.id.ButtonChoosePic);
        btnSubmitStory = (Button)findViewById(R.id.ButtonSubmitStory);
        editTextContent = (EditText)findViewById(R.id.editTextContent);
        editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        spinnerGenre = (Spinner)findViewById(R.id.spinnerGenres);
        checkBoxImage = (CheckBox)findViewById(R.id.checkBoxImage);

        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.genres,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerGenre.setAdapter(adapter);


        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] chooseGenre = getResources().getStringArray(R.array.genres);
                chosenGenre = chooseGenre[selectedItemPosition];

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnSubmitStory.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                String content = editTextContent.getText().toString();
                if(title.matches("")){
                    Toast.makeText(getApplicationContext(), "Введите название", Toast.LENGTH_LONG).show();
                }else if(content.matches("")){
                    Toast.makeText(getApplicationContext(), "Напишите историю", Toast.LENGTH_LONG).show();
                }else if(content.length() < 300){
                    Toast.makeText(getApplicationContext(), "История слишком коротка", Toast.LENGTH_LONG).show();
                }else if(chosenGenre != ""){
                    myRef.child(id).child("title").setValue(title);
                    myRef.child(id).child("content").setValue(content);
                    myRef.child(id).child("genre").setValue(chosenGenre);
                    myRef.child(id).child("authorId").setValue(currentFirebaseUser.getUid());
                    if(checkBoxImage.isChecked()){
                        uploadImage();
                    }else{
                        myRef.child(id).child("story_imageURL").setValue("null");
                    }
                }
            }
        });
        checkBoxImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    btnChooseImage.setEnabled(true);
                else {
                    btnChooseImage.setEnabled(false);
                }
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
        }
    }
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Загрузка...");
            progressDialog.show();

            StorageReference ref = storageReference.child("story/images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    String stringUrl = downloadUrl.toString();
                                    myRef.child(id).child("imgUrl").setValue(stringUrl);
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(WriteStoryActivity.this, "Загружено", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(WriteStoryActivity.this, "Не удалось "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Загружено "+(int)progress+"%");
                        }
                    });
        }
    }


}
