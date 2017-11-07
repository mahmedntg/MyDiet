package com.example.company.mydiet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.company.mydiet.utils.DateUtil;
import com.example.company.mydiet.utils.Gender;
import com.example.company.mydiet.utils.MyAlertDialog;
import com.example.company.mydiet.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.MessageFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameET, weightET, heightET, waistET, ageET, userNameET, passwordET, confirmPasswordET;
    private RadioGroup genderRG;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        progressDialog = new ProgressDialog(this);
        String alertTitle = getString(R.string.validation_title_alert);
        alertDialog = MyAlertDialog.createAlertDialog(this, alertTitle);
        nameET = (EditText) findViewById(R.id.edit_name);
        weightET = (EditText) findViewById(R.id.edit_weight);
        waistET = (EditText) findViewById(R.id.edit_waist);
        heightET = (EditText) findViewById(R.id.edit_height);
        ageET = (EditText) findViewById(R.id.edit_age);
        userNameET = (EditText) findViewById(R.id.edit_userName);
        passwordET = (EditText) findViewById(R.id.edit_password);
        confirmPasswordET = (EditText) findViewById(R.id.edit_confirm_password);
        genderRG = (RadioGroup) findViewById(R.id.genderRG);
        ((RadioButton) findViewById(R.id.maleRadioBtn)).setChecked(true);

    }

    public void registerUserBtnClicked(View view) {
        final String name = nameET.getText().toString().trim();
        final String weight = weightET.getText().toString().trim();
        final String height = heightET.getText().toString().trim();
        final String waist = waistET.getText().toString().trim();
        final String age = ageET.getText().toString().trim();
        final String userName = userNameET.getText().toString().trim();
        final String password = passwordET.getText().toString().trim();
        String confirmPassword = confirmPasswordET.getText().toString().trim();
        int genderId = genderRG.getCheckedRadioButtonId();
        final String gender = ((RadioButton) findViewById(genderId)).getText().toString();
        String message = getString(R.string.value_required_msg).trim();
        User user = new User(name, userName, password, confirmPassword, weight, height, waist, gender, age);
        if (!isDataValid(message, user)) {
            return;
        }
        progressDialog.setMessage(getString(R.string.register_user));
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(userName, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference ref = databaseReference.child(userId);
                    ref.child("name").setValue(name);
                    ref.child("weight").setValue(weight);
                    ref.child("height").setValue(height);
                    ref.child("waist").setValue(waist);
                    ref.child("age").setValue(age);
                    ref.child("gender").setValue(gender);
                    ref.child("userName").setValue(userName);
                    ref.child("createdDate").setValue(DateUtil.getInstance().getDate());
                    ref.child("modifiedDate").setValue(DateUtil.getInstance().getDate());
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                } else {
                    alertDialog.setMessage(task.getException().getMessage());
                    alertDialog.show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private boolean isDataValid(String message, User user) {
        boolean valid = true;
        if (TextUtils.isEmpty(user.getEmail())) {
            message = MessageFormat.format(message, "userName");
            alertDialog.setMessage(message);
            alertDialog.show();
            valid = false;
        } else if (TextUtils.isEmpty(user.getName())) {
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
        } else if (TextUtils.isEmpty(user.getGender())) {
            message = MessageFormat.format(message, "Gender");
            alertDialog.setMessage(message);
            alertDialog.show();
            valid = false;
        } else if (TextUtils.isEmpty(user.getPassword())) {
            message = MessageFormat.format(message, "Password");
            alertDialog.setMessage(message);
            alertDialog.show();
            valid = false;
        } else if (TextUtils.isEmpty(user.getConfirmPassword())) {
            message = MessageFormat.format(message, "confirmPassword");
            alertDialog.setMessage(message);
            alertDialog.show();
            valid = false;
        } else if (!user.getPassword().equals(user.getConfirmPassword())) {
            alertDialog.setMessage("Password does not match the confirm password");
            alertDialog.show();
            valid = false;
        }
        return valid;
    }
}
