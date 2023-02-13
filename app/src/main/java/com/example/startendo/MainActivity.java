package com.example.startendo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.startendo.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private EditText mPhoneNumber;
    private CountryCodePicker ccp;
    private Button mSend;
    String mVerificationId;
    private EditText mcode;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        getSupportActionBar().hide();
        super.onCreate(bundle);
        ActivityMainBinding inflate = ActivityMainBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView((View) inflate.getRoot());
        FirebaseApp.initializeApp(this);
        UserIsLoggedIn();
        ccp=findViewById(R.id.ccp);
        mPhoneNumber = (EditText) findViewById(R.id.PhoneNumber);
        ccp.registerCarrierNumberEditText(mPhoneNumber);
        mcode = (EditText) findViewById(R.id.Code);
       /* Button button = (Button) findViewById(R.id.send);
        mSend = button;*/
        mSend=findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mPhoneNumber.getText().toString().isEmpty()){
                    mPhoneNumber.setError("Enter your Phone");
                    return;
                }
                if (mSend.getText().equals("Verify")&&mcode.getText().toString().isEmpty()){
                    mcode.setError("Enter OTP");
                    return;
                }

                if (MainActivity.this.mVerificationId != null) {
                    MainActivity.this.verifyPhoneNumberWithCode();
                } else {
                    MainActivity.this.startPhoneNumberVerification();
                }
            }
        });
        this.mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            public void onVerificationFailed(FirebaseException firebaseException) {
            }

            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                MainActivity.this.SignInWithPhoneAuthCredentials(phoneAuthCredential);
            }

            public void onCodeSent(String str, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(str, forceResendingToken);
                MainActivity.this.mVerificationId = str;
                MainActivity.this.mSend.setText("Verify");

            }
        };
        getPermissions();
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.WRITE_CONTACTS", "android.permission.READ_CONTACTS"}, 1);
        }
    }

    /* access modifiers changed from: private */
    public void verifyPhoneNumberWithCode() {
        SignInWithPhoneAuthCredentials(PhoneAuthProvider.getCredential(this.mVerificationId, this.mcode.getText().toString()));
    }

    /* access modifiers changed from: private */
    public void startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(ccp.getFullNumberWithPlus().replace(" ",""), 60, TimeUnit.SECONDS, this, this.mCallBacks);
    }

    /* access modifiers changed from: private */
    public void SignInWithPhoneAuthCredentials(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener((Activity) this, new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "User Is Logged In", 0).show();
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        final DatabaseReference child = FirebaseDatabase.getInstance().getReference().child("user").child(currentUser.getUid());
                        child.addListenerForSingleValueEvent(new ValueEventListener() {
                            public void onCancelled(DatabaseError databaseError) {
                            }

                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("phone", currentUser.getPhoneNumber());
                                    hashMap.put("name", currentUser.getPhoneNumber());
                                    hashMap.put("profilePic",currentUser.getPhoneNumber());
                                    child.updateChildren(hashMap);

                                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                                    finish();
                                }
                                else
                                 UserIsLoggedIn();

                            }
                        });
                        return;
                    }
                    return;
                }
                /*Toast.makeText(MainActivity.this, "Login Failed", 0).show();*/
            }
        });
    }


    public void UserIsLoggedIn() {

     /*  FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   startActivity(new Intent(getApplicationContext(), ChatmainActivity.class));
                   finish();
               }

               else{
                   startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                   finish();
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });*/




        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), ChatmainActivity.class));
            finish();
        }
        /*else{
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            finish();
        }*/


    }
}
