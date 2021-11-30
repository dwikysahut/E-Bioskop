package com.dwikyhutomo.e_bioskop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.dwikyhutomo.e_bioskop.utils.Constant;
import com.dwikyhutomo.e_bioskop.utils.MySingleton;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends Activity {

    MaterialEditText email,password,username,nama;
    RadioButton rbLaki,rbPerempuan;
    RadioGroup radioGroupGender;
    Button btnRegister;
    String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email=findViewById(R.id.email_reg);
        password=findViewById(R.id.password_reg);
        username=findViewById(R.id.username_reg);
        radioGroupGender=findViewById(R.id.rdGroup_gender);
        nama=findViewById(R.id.nama_reg);
        btnRegister=findViewById(R.id.register);
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkedRadioButtonId = radioGroupGender.getCheckedRadioButtonId();
                RadioButton radioBtn = findViewById(checkedRadioButtonId);
//                Toast.makeText(RegisterActivity.this, radioBtn.getText(), Toast.LENGTH_SHORT).show();
                gender=radioBtn.getText().toString();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = Objects.requireNonNull(username.getText().toString());
                String txt_email = Objects.requireNonNull(email.getText().toString());
                String txt_password=Objects.requireNonNull(password.getText().toString());
                String txt_nama=Objects.requireNonNull(nama.getText().toString());
                String txt_gender=gender;
                if(TextUtils.isEmpty(txt_email )|| TextUtils.isEmpty(txt_password)|| TextUtils.isEmpty(txt_username) ||TextUtils.isEmpty(txt_nama)
                ||radioGroupGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(RegisterActivity.this,"ALl Fields Required", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(txt_username,txt_email,txt_password,txt_gender,txt_nama);
                }
            }
        });
    }
    private void register(final String username,final  String email,final String password,final String gender,final String nama){
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Registering your account");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String url= Constant.BASE_URL+"register.php";
        StringRequest request = new StringRequest(Request.Method.POST,url
                , (String response) -> {
            if(response.contains("Register Success")){
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,
                        response, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();


            }
            else{
                Toast.makeText(RegisterActivity.this,
                        response, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        },error -> {
            Toast.makeText(RegisterActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("email", email);
                param.put("password", password);
                param.put("username", username);
                param.put("gender", gender);
                param.put("nama", nama);


                return param;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                ,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(RegisterActivity.this).addToRequestQueue(request);
    }
}