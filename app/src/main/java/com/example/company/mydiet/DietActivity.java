package com.example.company.mydiet;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.company.mydiet.utils.AlarmReceiver;
import com.example.company.mydiet.utils.DateUtil;
import com.example.company.mydiet.utils.MyAlertDialog;
import com.example.company.mydiet.utils.MySharedPreferences;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DietActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference motivationReference;
    private FirebaseStorage firebaseStorage;
    private TextView pmiET;
    ImageView imageView;
    String dietImage;
    private ProgressDialog progressDialog;
    private Button goToDietBtn, videosBtn;
    private ArrayList<String> myMotivation = new ArrayList<String>();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        preferences = MySharedPreferences.getReference(DietActivity.this).getSharedPreferences();
        final boolean exists = MySharedPreferences.getReference(DietActivity.this).isSharedPreferencesExists();
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        motivationReference = FirebaseDatabase.getInstance().getReference("motivation");
        motivationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    myMotivation.add(name);
                }
                if (!exists) {
                    sendNotificationList();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebaseStorage = FirebaseStorage.getInstance();
        goToDietBtn = (Button) findViewById(R.id.goToDietBtn);
        videosBtn = (Button) findViewById(R.id.videosBtn);
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
                if (days > 30) {
                    goToDietBtn.setEnabled(false);
                    videosBtn.setEnabled(false);
                    sendNotification("Update Information", "please update your information", false);
                }
                if (!exists) {
                    sendNotification("Keep Going", "Drink Water Every Day", true);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(MySharedPreferences.MYKEY, true);
                    editor.commit();
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
                } else {
                    progressDialog.dismiss();
                    AlertDialog alertDialog=MyAlertDialog.createAlertDialog(DietActivity.this,"No Diet");
                    alertDialog.setMessage("You do not need diet");
                    alertDialog.show();
                    goToDietBtn.setEnabled(false);
                    videosBtn.setEnabled(false);
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

    public void youtubeBtnClicked(View view) {
        Intent intent = new Intent(this, DietVideoActivity.class);
        intent.putExtra("diet",dietImage);
        startActivity(intent);

    }

    private void sendNotification(String title, String message, boolean repeating) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("repeating", repeating);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, repeating ? 1 : 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (repeating) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.clear();
            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.set(Calendar.MINUTE, 30);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        }
    }

    private void sendNotificationList() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putStringArrayListExtra("motivation", myMotivation);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 2,
                intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.clear();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pendingIntent);
    }
}