package com.dwikyhutomo.e_bioskop;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dwikyhutomo.e_bioskop.utils.Constant;
import com.dwikyhutomo.e_bioskop.utils.MySingleton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends Activity {
    private Button btnRegister,btnLogin;
    private MaterialEditText username,password;
    CheckBox checkedStatus;
    SharedPreferences sharedPreferences;
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        checkedStatus=findViewById(R.id.checkbox);
        btnRegister=findViewById(R.id.goRegister);
        btnLogin=findViewById(R.id.login);
        sharedPreferences = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
        String loginStatus=sharedPreferences.getString(getResources().getString(R.string.prefStatus),"");
        if(loginStatus.equals("loggedIn")){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = Objects.requireNonNull(username.getText().toString());
                String txt_password=Objects.requireNonNull(password.getText().toString());
                if(TextUtils.isEmpty(txt_username )|| TextUtils.isEmpty(txt_password)){
                    Toast.makeText(LoginActivity.this,"ALl Fields Required", Toast.LENGTH_SHORT).show();
                }
                else {
                    login(txt_username,txt_password);
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent= new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(mIntent);
            }
        });
    }
    public void login(String username, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Processing your account");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String url = Constant.BASE_URL + "login.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(LoginActivity.class.getSimpleName(), "Response: " + response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");

                    // Cek error node pada json
                    if (success == 1) {
                        Toast.makeText(LoginActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        Log.d("get edit data", jsonObject.toString());
                        String id = jsonObject.getString("id");
                        String nama = jsonObject.getString("nama");
                        String email = jsonObject.getString("email");
                        String role = jsonObject.getString("role");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putString("email", email);
                        editor.putString("nama", nama);
                        editor.putString("id", id);
                        editor.putString("role", role);


                        if (checkedStatus.isChecked()) {

                            editor.putString(getResources().getString(R.string.prefStatus), "loggedIn");

                        } else {

                            editor.putString(getResources().getString(R.string.prefStatus), "loggedOut");
                        }
                        editor.apply();
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    // JSON error
                    progressDialog.dismiss();
                    e.printStackTrace();
//                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LoginActivity.class.getSimpleName(), "Error: " + error.getMessage());
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                , DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);

//        StringRequest request = new StringRequest(Request.Method.POST,url
//                , (String response) -> {
//            if(response.equals("Login Success")){
//                Toast.makeText(LoginActivity.this,
//                        response, Toast.LENGTH_SHORT).show();
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("username",username);
////                editor.putString("email",email);
//
//                if(checkedStatus.isChecked()){
//                    editor.putString(getResources().getString(R.string.prefStatus),"loggedIn");
//                    editor.putString("username",username);
//                }
//                else{
//                    editor.putString(getResources().getString(R.string.prefStatus),"loggedOut");
//                }
//                editor.apply();
//                startActivity(new Intent(LoginActivity.this,MainActivity.class));
//                progressDialog.dismiss();
//                finish();
//            }
//            else{
//                Toast.makeText(LoginActivity.this,
//                        response, Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//            }
//
//        },error -> {
//            Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
//            progressDialog.dismiss();
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                HashMap<String, String> param = new HashMap<>();
//                param.put("username", username);
//                param.put("password", password);
//
//                return param;
//            }
//        };
//        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES
//                ,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);
//    }
    }
    @Override
    public void onBackPressed() {

            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Yakin ingin keluar");

            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.this.finish();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }

}