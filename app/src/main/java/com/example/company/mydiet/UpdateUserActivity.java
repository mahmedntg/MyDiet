package com.example.company.mydiet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.company.mydiet.utils.DateUtil;
import com.example.company.mydiet.utils.Diet;
import com.example.company.mydiet.utils.MyAlertDialog;
import com.example.company.mydiet.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.Date;

public class UpdateUserActivity extends AppCompatActivity {
    private EditText nameET, weightET, heightET, waistET, ageET, userNameET, passwordET, confirmPasswordET;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        progressDialog = new ProgressDialog(this);
        String alertTitle = getString(R.string.validation_title_alert);
        alertDialog = MyAlertDialog.createAlertDialog(this, alertTitle);
        nameET = (EditText) findViewById(R.id.edit_user_name);
        weightET = (EditText) findViewById(R.id.edit_user_weight);
        waistET = (EditText) findViewById(R.id.edit_user_waist);
        heightET = (EditText) findViewById(R.id.edit_user_height);
        ageET = (EditText) findViewById(R.id.edit_user_age);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameET.setText(user.getName());
                weightET.setText(user.getWeight());
                waistET.setText(user.getWaist());
                heightET.setText(user.getHeight());
                ageET.setText(user.getAge());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateUserInfoBtnClicked(View view) {
        final String name = nameET.getText().toString().trim();
        final String weight = weightET.getText().toString().trim();
        final String height = heightET.getText().toString().trim();
        final String waist = waistET.getText().toString().trim();
        final String age = ageET.getText().toString().trim();
        String message = getString(R.string.value_required_msg).trim();
        User user = new User(name, weight, height, waist, age);
        if (!isDataValid(message, user)) {
            return;
        }
        progressDialog.setMessage(getString(R.string.update_user));
        progressDialog.show();
        databaseReference.child("name").setValue(name);
        databaseReference.child("weight").setValue(weight);
        databaseReference.child("height").setValue(height);
        databaseReference.child("waist").setValue(waist);
        databaseReference.child("age").setValue(age);
        databaseReference.child("modifiedDate").setValue(DateUtil.getInstance().getDate());
        startActivity(new Intent(UpdateUserActivity.this, DietActivity.class));
        progressDialog.dismiss();
    }

    private boolean isDataValid(String message, User user) {
        boolean valid = true;
        if (TextUtils.isEmpty(user.getName())) {
            message = MessageFormat.format(message, "Name");
            alertDialog.setMessage(message);
            alertDialog.show();
            valid = false;
        } else if (TextUtils.isEmpty(user.getWeight())) {
            message = MessageFormat.format(message, "Weight");
            alertDialog.setMessage(message);
            alertDialog.show();
            valid = false;
        } else if (TextUtils.isEmpty(user.getHeight())) {
            message = MessageFormat.format(message, "Height");
            alertDialog.setMessage(message);
            alertDialog.show();
            valid = false;
        } else if (TextUtils.isEmpty(user.getWaist())) {
            message = MessageFormat.format(message, "Waist");
            alertDialog.setMessage(message);
            alertDialog.show();
            valid = false;
        }
        return valid;
    }


}
