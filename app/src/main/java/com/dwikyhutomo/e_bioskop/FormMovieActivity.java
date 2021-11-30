package com.dwikyhutomo.e_bioskop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dwikyhutomo.e_bioskop.utils.Constant;
import com.dwikyhutomo.e_bioskop.utils.MySingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class FormMovieActivity extends AppCompatActivity {
    private TextView title, desc, date, rating, genre, image,stock;
    private MaterialEditText showStart, showEnd;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    ImageView imageUpload;
    ProgressBar progressBarUpload;
    private Button btnSubmit,btnPilihGambar,btnUpload,btnCancel;
    private String id,txt_title,txt_desc,txt_date,txt_rating,txt_genre,txt_image,txt_showtime,txt_stock;
    private static final String TAG = FormMovieActivity.class.getSimpleName();
    private StorageReference reference;
    File f;
    String uriImage;
    //Kode permintaan untuk memilih metode pengambilan gamabr
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;
    int count=0;
    int success;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_movie);
        title = findViewById(R.id.edtTitle);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        desc = findViewById(R.id.editDesc);
        date = findViewById(R.id.edtDate);
        rating = findViewById(R.id.edtRating);
        genre = findViewById(R.id.edtGenre);
        stock=findViewById(R.id.edtStock);
        image = findViewById(R.id.edtImage);
        showStart = findViewById(R.id.edtStartTime);
        showEnd = findViewById(R.id.edtEndTime);
        showStart.setInputType(InputType.TYPE_NULL);
        showEnd.setInputType(InputType.TYPE_NULL);
        progressBarUpload=findViewById(R.id.progressBarUpload);
        progressBarUpload.setVisibility(View.INVISIBLE);
        btnPilihGambar=findViewById(R.id.select_Image);
        btnUpload=findViewById(R.id.upload_image);
        date.setInputType(InputType.TYPE_NULL);
        btnCancel=findViewById(R.id.btnCancelForm);
        imageUpload=findViewById(R.id.imageUpload);
        imageUpload.setVisibility(View.INVISIBLE);
        reference = FirebaseStorage.getInstance().getReference();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent= new Intent(FormMovieActivity.this,MovieListActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// agar activity tidak kembali saat dipencet back
                if(count>0){
                    try {
                        reference.child("gambar/"+title.getText().toString()+".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                Log.d(TAG, "onResponse: "+"delete by firebase storage" );

                                startActivity(mIntent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!

                                Toast.makeText(FormMovieActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                                startActivity(mIntent);
                                finish();
                            }
                        });

                    }catch (Exception e){
                        startActivity(mIntent);
                        finish();
                    }
            }
                else{
                    startActivity(mIntent);
                    finish();
                }
                finish();
            }
        });
        btnPilihGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();

            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txt_title=title.getText().toString();
                if(txt_title.isEmpty()){
                    Toast.makeText(FormMovieActivity.this, "Isi Title Movie terlebih dahulu", Toast.LENGTH_LONG).show();
                }
                else {
                    uploadImage();
                    count=1;
                }
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        showStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog1("start");
            }
        });
        showEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog1("end");
            }
        });
        btnSubmit = findViewById(R.id.btnSubmit);
        if (getIntent().getStringExtra(Constant.TYPE).contains("ADD")) {
            getSupportActionBar().setTitle("Add Movie");
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(FormMovieActivity.this);
                    builder.setMessage("Yakin ingin Menambahkan Data ? ");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addMovie();
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
            });
        }
        else{
            getSupportActionBar().setTitle("Edit Movie");
            Bundle extras = getIntent().getExtras();
            id=extras.getString(Constant.ID);
            title.setText(extras.getString(Constant.TITLE));
            desc.setText(extras.getString(Constant.DESC));
            date.setText(extras.getString(Constant.DATE));
            genre.setText(extras.getString(Constant.GENRE));
            rating.setText(extras.getString(Constant.RATING));
            image.setText(extras.getString(Constant.IMAGE));
            showStart.setText(extras.getString(Constant.SHOW_START));
            stock.setText(extras.getString(Constant.STOCK));
            showEnd.setText(extras.getString(Constant.SHOW_END));
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(FormMovieActivity.this);
                    builder.setMessage("Apakah Data sudah Benar ?");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editMovie(id);
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
            });
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int dataSize=0;
        //Menghandle hasil data yang diambil dari kamera atau galeri untuk ditampilkan pada ImageView
        switch(requestCode){

            case REQUEST_CODE_GALLERY:
                if(resultCode == RESULT_OK){

                    Uri uri = data.getData();

                    String scheme = uri.getScheme();
                    System.out.println("Scheme type " + scheme);
                    if(scheme.equals(ContentResolver.SCHEME_CONTENT))
                    {
                        try {
                            InputStream fileInputStream=getApplicationContext().getContentResolver().openInputStream(uri);
                            dataSize = fileInputStream.available();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("File size in bytes1"+dataSize);

                    }
                    else if(scheme.equals(ContentResolver.SCHEME_FILE))
                    {
                        String path = uri.getPath();
                        try {
                            f = new File(path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("File size in bytes2"+f.length());
                    }
                    if(dataSize>2000000){
                        Toast.makeText(FormMovieActivity.this, "Ukuran file maksimal 2 MB", Toast.LENGTH_SHORT).show();
                        break;
                    }
                        imageUpload.setVisibility(View.VISIBLE);
                        imageUpload.setImageURI(uri);

                }
                break;
        }
    }

    private void uploadImage(){
        //Mendapatkan data dari ImageView sebagai Bytes
        try {
            imageUpload.setDrawingCacheEnabled(true);
            imageUpload.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageUpload.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            //Mengkompress bitmap menjadi JPG dengan kualitas gambar 100%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();

            //Lokasi lengkap dimana gambar akan disimpan
            String namaFile = title.getText().toString() + ".jpg";
            String pathImage = "gambar/" + namaFile;
            UploadTask uploadTask = reference.child(pathImage).putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBarUpload.setVisibility(View.GONE);
                    Toast.makeText(FormMovieActivity.this, "Upload Berhasil", Toast.LENGTH_SHORT).show();
                    reference.child(pathImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // mendapatkan uri dari web firebase dan cloud storage
                            image.setText(uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBarUpload.setVisibility(View.GONE);
                            Toast.makeText(FormMovieActivity.this, "Upload Gagal", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBarUpload.setVisibility(View.VISIBLE);
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressBarUpload.setProgress((int) progress);
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(FormMovieActivity.this, "pilih file terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
    }

    private void getImage(){
        CharSequence[] menu = {"Galeri"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Upload Image")
                .setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){

                            case 0:
                                //Mengambil gambar dari galeri
                                Intent imageIntentGallery = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(imageIntentGallery, REQUEST_CODE_GALLERY);
                                break;
                        }
                    }
                });
        dialog.create();
        dialog.show();
    }
    private void addMovie(){
        txt_image=image.getText().toString();


//           if(URLUtil.isValidUrl(txt_image)) {


               String url = Constant.BASE_URL + "insertMovie.php";
               txt_title = title.getText().toString();
               txt_desc = desc.getText().toString();
               txt_date = date.getText().toString();
               txt_genre = genre.getText().toString();
               txt_rating = rating.getText().toString();
               txt_stock = stock.getText().toString();

               txt_showtime = showStart.getText().toString() + " - " + showEnd.getText().toString();


               StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {
                       Log.d(TAG, "Responsekuuu: " + response.toString());
                       try {
                           JSONObject jsonObject = new JSONObject(response);
                           success = jsonObject.getInt("success");
                           if (success == 1) {

                               Toast.makeText(FormMovieActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                               Intent mIntent = new Intent(FormMovieActivity.this, MovieListActivity.class);
                               mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// agar activity tidak kembali saat dipencet back
                               startActivity(mIntent);
                               finish();
                           } else {
                               Toast.makeText(FormMovieActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                           }
                       } catch (JSONException e) {
                           Log.e(TAG, "onResponse: " + e.getMessage());

                       }
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Log.e(TAG, "Error: " + error.getMessage());
                       Toast.makeText(FormMovieActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                   }
               }) {
                   @Override
                   protected Map<String, String> getParams() {
                       HashMap<String, String> param = new HashMap<>();
                       param.put("title", txt_title);
                       param.put("description", txt_desc);
                       param.put("date", txt_date);
                       param.put("rating", txt_rating);
                       param.put("genre", txt_genre);
                       param.put("image", txt_image);
                       param.put("showtime", txt_showtime);
                       param.put("stock", txt_stock);

                       return param;
                   }
               };
               MySingleton.getInstance(FormMovieActivity.this).addToRequestQueue(request);
//           }
//           else{
//               Toast.makeText(FormMovieActivity.this, "Url Image tidak valid", Toast.LENGTH_LONG).show();
//           }
        }




    private void editMovie(String id){

//        if(URLUtil.isValidUrl(txt_image)) {
            String url = Constant.BASE_URL + "updateMovie.php";
        txt_image=image.getText().toString();
            txt_title = title.getText().toString();
            txt_desc = desc.getText().toString();
            txt_date = date.getText().toString();
            txt_genre = genre.getText().toString();
            txt_rating = rating.getText().toString();
            txt_image = image.getText().toString();
            txt_stock = stock.getText().toString();
            txt_showtime = showStart.getText().toString() + " - " + showEnd.getText().toString();
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response: " + response.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        success = jsonObject.getInt("success");
                        if (success == 1) {

                            Toast.makeText(FormMovieActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            Intent mIntent = new Intent(FormMovieActivity.this, MovieListActivity.class);
                            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mIntent);

                            finish();
                        } else {
                            Toast.makeText(FormMovieActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "onResponse: " + e.getMessage());

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error: " + error.getMessage());
                    Toast.makeText(FormMovieActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("id", id);
                    param.put("title", txt_title);
                    param.put("description", txt_desc);
                    param.put("date", txt_date);
                    param.put("rating", txt_rating);
                    param.put("genre", txt_genre);
                    param.put("image", txt_image);
                    param.put("showtime", txt_showtime);
                    param.put("stock", txt_stock);

                    return param;
                }
            };
            MySingleton.getInstance(FormMovieActivity.this).addToRequestQueue(request);
//        }
//        else{
//            Toast.makeText(FormMovieActivity.this, "Url Image tidak valid", Toast.LENGTH_LONG).show();
//        }

    }

//    private void showTimePickerDialog2() {
//        Calendar mCurrentTime = Calendar.getInstance();
//        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
//        int minute = mCurrentTime.get(Calendar.MINUTE);
//        TimePickerDialog mTimePicker;
//        mTimePicker = new TimePickerDialog(FormMovieActivity.this, new TimePickerDialog.OnTimeSetListener() {
//
//            @Override
//            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                showEnd.setText(String.format("%02d:%02d",selectedHour , selectedMinute));
//            }
//        }, hour, minute, true);
//        mTimePicker.show();
//    }

    private void showTimePickerDialog1(String type) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(FormMovieActivity.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                if(selectedHour.)
                if(type.equals("start")) {
                    showStart.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                }
                else{
                    showEnd.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                }
            }
        }, hour, minute, true);
        mTimePicker.show();
    }

    private void showDateDialog() {

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                date.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
}