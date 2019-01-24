package com.example.swat.axistechnical;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.karan.churi.PermissionManager.PermissionManager;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    //Login class

    PermissionManager permissionManager;
    TextInputEditText etUserName, etPassWord;
    TextInputLayout tfUserName, tfPassWord;
    ProgressDialog progressDialog;
    FloatingActionButton FABligon;
    CheckBox cbRemember;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.login_activity);

        if(!isConnected(LoginActivity.this))
        {
            buildDialog(LoginActivity.this).show();
        }


        //------------App permission requirement----------------------------------------------------
        permissionManager = new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(LoginActivity.this);

        //--------------User name login-------------------------------------------------------------
        etUserName = findViewById(R.id.etUserName);
        tfUserName = findViewById(R.id.tfUserName);

        //--------------Password login--------------------------------------------------------------
        etPassWord = findViewById(R.id.etPassWord);
        tfPassWord = findViewById(R.id.tfPassWord);

        //-------------Login button-----------------------------------------------------------------
        FABligon = findViewById(R.id.FABlogin);

        //-------------Progress dialog while login operation----------------------------------------
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        //---------Remember me check box------------------------------------------------------------
        cbRemember = findViewById(R.id.cbRemember);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin) {
            etUserName.setText(loginPreferences.getString("username", ""));
            cbRemember.setChecked(true);
        }




        //----------------Login button click event--------------------------------------------------
        FABligon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                hideKeyboard(LoginActivity.this);


                    if (cbRemember.isChecked()) {
                        loginPrefsEditor.putBoolean("saveLogin", true);
                        loginPrefsEditor.putString("username", etUserName.getText().toString()); //To save username
                        loginPrefsEditor.apply();
                    } else {
                        loginPrefsEditor.clear();
                        loginPrefsEditor.commit();
                    }



                if(checkEmptyEditText(etUserName, tfUserName, "Enter user name !") &&
                   checkEmptyEditText(etPassWord, tfPassWord, "Enter password !") &&
                   checkpasswordlenght(etPassWord, tfPassWord,"7 digit only !"))
                {
                    progressDialog.show();
                    final String username = etUserName.getText().toString();
                    final String password = etPassWord.getText().toString();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                     @Override
                       public void onResponse(String response) {
                          try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success)
                            {


                                int id = jsonResponse.getInt("tec_ID"); // Get ID number of tecnchen
                                String name = jsonResponse.getString("First_Name"); // Get name of tecnchen


                                Intent intent = new Intent(LoginActivity.this, UserAreaActivity.class);
                                intent.putExtra("tec ID", id); // to send id number to user area
                                intent.putExtra("name", name);
                                LoginActivity.this.startActivity(intent);
                                progressDialog.cancel();

                            }
                            else
                            {
                                progressDialog.cancel();
                                /*AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("User not valid !")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();*/

                                Snackbar snackbar = Snackbar.make(v, "User not valid !", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };


                    LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Cannot login", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions,grantResults);
    } // To check all permissions need to app when app run for first time

    private boolean checkEmptyEditText(EditText text, TextInputLayout TFB, String massage) {
        if (text.getText().toString().isEmpty())
        {
            TFB.setErrorEnabled(true);
            TFB.setError(massage);
            return false;

        }
        else
        {
            TFB.setErrorEnabled(false);
            return true;
        }

    } // To check empty edit text

    private  boolean checkpasswordlenght(EditText text, TextInputLayout TFB, String massage) {
        if(text.getText().toString().length() > 7)
        {
            TFB.setErrorEnabled(true);
            TFB.setError(massage);
            return false;

        }
        else
        {
            TFB.setErrorEnabled(false);
            return true;
        }

    } // To check password length

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting())
        {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        else
        {
            return false;
        }

    } // Check network connection

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to close dialog");

        builder.setPositiveButton("Ok", null);


        return builder;
    } // Dialog for network connection


    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    } // To hide/close keyboard

}
