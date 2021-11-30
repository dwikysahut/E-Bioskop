package com.dwikyhutomo.e_bioskop.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.dwikyhutomo.e_bioskop.MovieListActivity;
import com.dwikyhutomo.e_bioskop.R;
import com.dwikyhutomo.e_bioskop.adapter.GridAdapter;
import com.dwikyhutomo.e_bioskop.model.Movie;
import com.dwikyhutomo.e_bioskop.utils.Constant;
import com.dwikyhutomo.e_bioskop.utils.MySingleton;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private CarouselView carouselView;
    private Button btnGoOrder;
    private TextView txtViewMore;
    private int[] srcImages = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3};
    private ImageView imageView;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private String TAG="New Movie request";
    private List<Movie> newMovieList = new ArrayList<>();
    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.rvGrid);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new GridAdapter(newMovieList);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
        carouselView=view.findViewById(R.id.carouselView);
        carouselView.setPageCount(srcImages.length);
        carouselView.setImageListener(imageListener);
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullRequest);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                carouselView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                btnGoOrder.setVisibility(View.INVISIBLE);

                recyclerView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                getMovies(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        imageView=view.findViewById(R.id.imageBanner);
        Glide.with(view.getContext())
                .load(R.drawable.bannerpromo)
                .into(imageView);

        btnGoOrder=view.findViewById(R.id.btnGoToOrder);
        txtViewMore=view.findViewById(R.id.text_view_more);
        txtViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent= new Intent(view.getContext(), MovieListActivity.class);
                startActivity(mIntent);
            }
        });
        sharedPreferences = view.getContext().getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
        if(sharedPreferences.getString("role","0").equals("1")){
            btnGoOrder.setText("Explore Movie");
        };
        btnGoOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent= new Intent(view.getContext(), MovieListActivity.class);
                startActivity(mIntent);
            }
        });
        progressBar=view.findViewById(R.id.progressBarGrid);

        carouselView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        btnGoOrder.setVisibility(View.INVISIBLE);

        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        getMovies();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(srcImages[position]);
        }
    };
    private void getMovies(){
        newMovieList.clear();
        adapter.notifyDataSetChanged();
        String url= Constant.BASE_URL+"getNewMovie.php";
        JsonArrayRequest jArr = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                // Parsing json
                for (int i = 0; i < 5; i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        newMovieList.add(new Movie(obj.getInt("id"),
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
                carouselView.setVisibility(View.VISIBLE);

                imageView.setVisibility(View.VISIBLE);
                btnGoOrder.setVisibility(View.VISIBLE);

                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });

        MySingleton.getInstance(getView().getContext()).addToRequestQueue(jArr);
    }


}
