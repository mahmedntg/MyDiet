package com.example.company.mydiet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.company.mydiet.utils.DateUtil;
import com.example.company.mydiet.utils.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Date;

public class DietActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private TextView pmiET;
    ImageView imageView;
    String dietImage;
    private ProgressDialog progressDialog;
    private Button goToDietBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        goToDietBtn = (Button) findViewById(R.id.goToDietBtn);
        pmiET = (TextView) findViewById(R.id.pmiET);
        imageView = (ImageView) findViewById(R.id.dietImage);
        progressDialog.setMessage(getString(R.string.loading_diet));
        progressDialog.show();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference userRef = databaseReference.child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                int days = DateUtil.getInstance().getDays(user.getModifiedDate());
                if(days>30){
                   goToDietBtn.setEnabled(false);
                }
                double height = Double.parseDouble(user.getHeight());
                double weight = Double.parseDouble(user.getWeight());
                DecimalFormat df = new DecimalFormat("#.##");
                double PMI = weight / (Math.pow((height / 100), 2));
                pmiET.setText("PMI= " + df.format(PMI));
                if (PMI > 25 && PMI <= 30) {
                    dietImage = "1";
                } else if (PMI > 30 && PMI <= 35) {
                    dietImage = "2";
                } else if (PMI > 35) {
                    dietImage = "3";
                }
                firebaseStorage.getReference().child("dietImages/" + dietImage + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Picasso.with(DietActivity.this).load(url).into(imageView);
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void dietBtnClicked(View view) {
        Intent intent = new Intent(this, DietInformationActivity.class);
        intent.putExtra("diet", dietImage);
        startActivity(intent);
    }

    public void updateUserBtnClicked(View view) {
        Intent intent = new Intent(this, UpdateUserActivity.class);
        startActivity(intent);

    }
}
