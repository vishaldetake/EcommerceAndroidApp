package com.solodroid.ecommerce;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class Login extends Activity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    EditText _emailText;
    EditText _passwordText;
    Button _loginButton,_signupLink;

    private boolean loggedIn = false;
    public static final String LOGIN_SUCCESS = "success";

    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the email of current logged in user
    public static final String EMAIL_SHARED_PREF = "email";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _emailText = (EditText)findViewById(R.id.username);
        _passwordText = (EditText)findViewById(R.id.password);
        _loginButton = (Button)findViewById(R.id.signin);
        _signupLink = (Button)findViewById(R.id.Signup);

        ButterKnife.bind(this);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

    }
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        loggedIn=sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF,false);
        if(loggedIn){
            Intent intent=new Intent(Login.this,MainActivity.class);
            startActivity(intent);
        }


    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        loginUser();

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                         //onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                /*Intent intent=new Intent(Login.this,MainActivity.class);
                startActivity(intent);*/
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();

            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        // finish();
        //Login Code Placed Here
        /*Intent intent=new Intent(Login.this,MainActivity.class);
        startActivity(intent);
*/
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        /*Intent faild=new Intent(Login.this,Login.class);
        startActivity(faild);*/
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void loginUser(){
        String url ="http://192.168.0.16/Login/LoginUser.php";
        final String email = _emailText.getText().toString().trim();
        final String password = _passwordText.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               /*Intent intent=new Intent(Login.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(Login.this,response,Toast.LENGTH_LONG).show();*/
                if(response.trim().equals("success")){
                    SharedPreferences sharedPreferences=Login.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean(LOGGEDIN_SHARED_PREF,true);
                    editor.putString(EMAIL_SHARED_PREF,email);
                    editor.commit();
                    openProfile();
                }else{
                    Toast.makeText(Login.this,response,Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
               /* if (volleyError instanceof TimeoutError) {
                    *//*Intent faild=new Intent(Login.this,Login.class);
                    startActivity(faild);*//*
                }*/
                Toast.makeText(Login.this,volleyError.toString(),Toast.LENGTH_LONG ).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("email", _emailText.getText().toString());
                headers.put("password", _passwordText.getText().toString());
               /* Intent success=new Intent(Login.this,MainActivity.class);
                startActivity(success);
*/
                return headers;
            }

            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void openProfile() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}