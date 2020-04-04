package com.example.hotelapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class MainActivity extends AppCompatActivity {

    private EditText emailET,passwordET;
    private Button signUpBtn,signInBtn;

    private ProgressBar objectProgressBar;
    private FirebaseAuth objectFirebaseAuth; //Step 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        objectFirebaseAuth=FirebaseAuth.getInstance(); //Step 2
        connectXMLObjects();
    }

    private void connectXMLObjects()
    {
        try
        {
            emailET=findViewById(R.id.emailET);
            passwordET=findViewById(R.id.passwordET);

            signUpBtn=findViewById(R.id.signUpBtn);
            objectProgressBar=findViewById(R.id.signUpProgressBar);

            signInBtn=findViewById(R.id.signInBtn);
            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //signUpUser(); //Step 4
                    checkIfUserExists(); //Step 5
                }
            });

            signInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signInUser(); //step 7
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "connectXMLObjects:"
                    +e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfUserExists()
    {
        try
        {
            if(!emailET.getText().toString().isEmpty())
            {
                if(objectFirebaseAuth!=null)
                {
                    objectProgressBar.setVisibility(View.VISIBLE);
                    signUpBtn.setEnabled(false);

                    objectFirebaseAuth.fetchSignInMethodsForEmail(emailET.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean check=task.getResult().getSignInMethods().isEmpty();
                                    if(!check)
                                    {
                                        signUpBtn.setEnabled(true);
                                        objectProgressBar.setVisibility(View.INVISIBLE);

                                        Toast.makeText(MainActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(check)
                                    {

                                        signUpUser(); //Step 6
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    signUpBtn.setEnabled(true);
                                    objectProgressBar.setVisibility(View.INVISIBLE);

                                    Toast.makeText(MainActivity.this, "Fails to check if user exists:"
                                            +e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else
            {
                emailET.requestFocus();
                Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            signUpBtn.setEnabled(true);
            objectProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "checkIfUserExists:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Step 3
    private void signUpUser()
    {
        try
        {
            if(!emailET.getText().toString().isEmpty()
                    && !passwordET.getText().toString().isEmpty())
            {
                if(objectFirebaseAuth!=null)
                {
                    objectProgressBar.setVisibility(View.VISIBLE);
                    signUpBtn.setEnabled(false);
                    objectFirebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString(),
                            passwordET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(MainActivity.this, "User created Successfully", Toast.LENGTH_SHORT).show();
                                    if(authResult.getUser()!=null)
                                    {
                                        objectFirebaseAuth.signOut();
                                        emailET.setText("");

                                        passwordET.setText("");
                                        emailET.requestFocus();

                                        signUpBtn.setEnabled(true);
                                        objectProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    signUpBtn.setEnabled(true);
                                    emailET.requestFocus();

                                    objectProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.this, "Fails to create user:"
                                            +e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else if(emailET.getText().toString().isEmpty())
            {
                signUpBtn.setEnabled(true);
                objectProgressBar.setVisibility(View.INVISIBLE);

                emailET.requestFocus();
                Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show();
            }
            else if(passwordET.getText().toString().isEmpty())
            {
                signUpBtn.setEnabled(true);
                objectProgressBar.setVisibility(View.INVISIBLE);

                passwordET.requestFocus();
                Toast.makeText(this, "Please enter the password", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            signUpBtn.setEnabled(true);
            emailET.requestFocus();

            objectProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "signUpUser:"+
                    e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void signInUser()
    {
        try
        {
            if(!emailET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty())
            {
                //Check if user already sign in
                if(objectFirebaseAuth.getCurrentUser()!=null)
                {
                    objectFirebaseAuth.signOut();
                    Toast.makeText(this, "User logged out successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    objectProgressBar.setVisibility(View.VISIBLE);
                    signInBtn.setEnabled(false);

                    objectFirebaseAuth.signInWithEmailAndPassword(emailET.getText().toString(),
                            passwordET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    objectProgressBar.setVisibility(View.INVISIBLE);
                                    signInBtn.setEnabled(true);

                                    Toast.makeText(MainActivity.this, "User log in", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this,SignInAct.class));

                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    signInBtn.setEnabled(true);
                                    emailET.requestFocus();

                                    objectProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.this, "Fails to sign in user:"
                                            +e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else if(emailET.getText().toString().isEmpty())
            {
                signInBtn.setEnabled(true);
                objectProgressBar.setVisibility(View.INVISIBLE);

                emailET.requestFocus();
                Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show();
            }
            else if(passwordET.getText().toString().isEmpty())
            {
                signInBtn.setEnabled(true);
                objectProgressBar.setVisibility(View.INVISIBLE);

                passwordET.requestFocus();
                Toast.makeText(this, "Please enter the password", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            emailET.requestFocus();
            signInBtn.setEnabled(true);

            objectProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "signInUser:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(objectFirebaseAuth.getCurrentUser()!=null)
        {
            objectFirebaseAuth.signOut();
        }
    }
}
