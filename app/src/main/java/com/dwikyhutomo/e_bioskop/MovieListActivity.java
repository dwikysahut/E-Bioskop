package com.dwikyhutomo.e_bioskop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.dwikyhutomo.e_bioskop.adapter.MovieAdapter;
import com.dwikyhutomo.e_bioskop.model.Movie;
import com.dwikyhutomo.e_bioskop.utils.Constant;
import com.dwikyhutomo.e_bioskop.utils.MySingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {
    private DatabaseReference database;
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();
    private String TAG="Movie request";
    private ProgressBar progressBar;
    private StorageReference reference;
    private FloatingActionButton fabAdd;
    private int success;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MovieListActivity.this);
        recyclerView.setHasFixedSize(true);
        getSupportActionBar().setTitle("Movie List");
        recyclerView.setLayoutManager(layoutManager);
        reference = FirebaseStorage.getInstance().getReference();
        adapter = new MovieAdapter(movieList);
        fabAdd=findViewById(R.id.fabAdd);
        sharedPreferences = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
        if(sharedPreferences.getString("role","0").equals("2")){
            fabAdd.setVisibility(View.INVISIBLE);
        };
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullRequestOrder);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.VISIBLE);
                getMovies(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        adapter.setOnItemClickCallback(new MovieAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Movie data) {
                deleteMovie(data);

            }
        });
        recyclerView.setVisibility(View.INVISIBLE);
        getMovies();
        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent= new Intent(MovieListActivity.this,FormMovieActivity.class);
                mIntent.putExtra(Constant.TYPE,"ADD");
                startActivity(mIntent);

            }
        });

    }

    private void getMovies(){
        movieList.clear();
        adapter.notifyDataSetChanged();
        String url=Constant.BASE_URL+"getAllMovies.php";
        // membuat request JSON
        JsonArrayRequest jArr = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        movieList.add(new Movie(obj.getInt("id"),
                                        obj.getString("title"),
                                        obj.getString("description"),
                                        obj.getString("date"),
                                        obj.getDouble("rating"),
                                        obj.getString("genre"),
                                        obj.getString("image"),
                                        obj.getInt("stock"),
                                        obj.getString("showtime")
                                ));

                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }
                // notifikasi adanya perubahan data pada adapter
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });

        MySingleton.getInstance(MovieListActivity.this).addToRequestQueue(jArr);
    }
private void deleteMovie(Movie data){
        String url=Constant.BASE_URL+"deleteMovie.php";
    final AlertDialog.Builder builder = new AlertDialog.Builder(MovieListActivity.this);
    builder.setMessage("Yakin Menghapus Data?");

    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response: " + response.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        success = jsonObject.getInt("success");
                        if (success == 1) {
                            Toast.makeText(MovieListActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onResponse: "+"delete by query" );
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.VISIBLE);
                            reference.child("gambar/"+data.getTitle()+".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    Log.d(TAG, "onResponse: "+"delete by firebase storage" );
                                    Toast.makeText(MovieListActivity.this, "File deleted successfully", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Log.d(TAG, "onFailure: ");
                                }
                            });
                            getMovies();

                        } else {
                            Toast.makeText(MovieListActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "onResponse: "+e.getMessage() );

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error: " + error.getMessage());
//                    Toast.makeText(MovieListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("id",String.valueOf(data.getId()));

                    return param;
                }
            };
            MySingleton.getInstance(MovieListActivity.this).addToRequestQueue(request);

            dialogInterface.dismiss();
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
    public void refreshActivtiy(){
        progressBar.setVisibility(View.VISIBLE);
        getMovies();
    }

//    private void getData(){
//        movieList.add(new Movie(1,
//                "Mortal Kombat",
//                "Washed-up MMA fighter Cole Young, unaware of his heritage, and hunted by Emperor Shang Tsung\\'s best warrior, Sub-Zero, seeks out and trains with Earth s greatest champions as he prepares to stand against the enemies of Outworld in a high stakes battle for the universe."
//        ,"April 23, 2021"
//        ,8.9d
//        ,"Action, Fantasy, Adventure"
//        ,"https://www.themoviedb.org/t/p/w600_and_h900_bestv2/nkayOAUBUu4mMvyNf9iHSUiPjF1.jpg"));
//    }
}