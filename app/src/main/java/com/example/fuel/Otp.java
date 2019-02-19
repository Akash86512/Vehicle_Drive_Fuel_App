package com.example.fuel;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Otp extends AppCompatActivity {
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth mAuth;
   String mVerificationId;
   ActionBar bar;
   PhoneAuthProvider.ForceResendingToken mResendToken;
    Button login;
    TextInputLayout otp;
        String phone,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mAuth=FirebaseAuth.getInstance();

        bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        login=findViewById(R.id.login);
        otp=findViewById(R.id.otp);

        Intent intent=getIntent();
        phone=intent.getStringExtra("mobno");
        name=intent.getStringExtra("name");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,otp.getEditText().getText().toString().trim());
                signInWithPhoneAuthCredential(credential);



                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


                myRef.child("Users").child("+91"+phone).child("name").setValue(name);
                myRef.child("Users").child("+91"+phone).child("phone").setValue(phone);

            }
        });


       mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

           @Override
           public void onVerificationCompleted(PhoneAuthCredential credential) {

               otp.getEditText().setText(credential.getSmsCode());

           }

           @Override
           public void onVerificationFailed(FirebaseException e) {
               // This callback is invoked in an invalid request for verification is made,
               // for instance if the the phone number format is not valid.
//               Log.w(TAG, "onVerificationFailed", e);

               if (e instanceof FirebaseAuthInvalidCredentialsException) {
                   // Invalid request
                   // ...
               } else if (e instanceof FirebaseTooManyRequestsException) {
                   // The SMS quota for the project has been exceeded
                   // ...
               }

               // Show a message and update the UI
               // ...
           }

           @Override
           public void onCodeSent(String verificationId,
                                  PhoneAuthProvider.ForceResendingToken token) {
               // The SMS verification code has been sent to the provided phone number, we
               // now need to ask the user to enter the code and then construct a credential
               // by combining the code with a verification ID.
//               Log.d(TAG, "onCodeSent:" + verificationId);

               // Save verification ID and resending token so we can use them later
               mVerificationId = verificationId;
              mResendToken = token;

               // ...
           }
       };

       sendotp();

   }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Intent intent=new Intent(Otp.this,Details.class);
                            startActivity(intent);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    void sendotp(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
