package com.dwikyhutomo.e_bioskop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dwikyhutomo.e_bioskop.model.Movie;
import com.dwikyhutomo.e_bioskop.utils.Constant;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class DetailMovieActivity extends AppCompatActivity {
    TextView title,description,date,rating,genre;
    RatingBar ratingBar;
    ImageView imageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    private boolean appBarExpanded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        Bundle extras = getIntent().getExtras();
        toolbar=findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        title=findViewById(R.id.tv_title_detail);
        description=findViewById(R.id.tv_desc_detail);
        date=findViewById(R.id.tv_date_detail);
        rating=findViewById(R.id.tv_rating_detail);
        genre=findViewById(R.id.tv_genre_detail);
        ratingBar=findViewById(R.id.rating_detail);
        imageView=findViewById(R.id.image_detail_poster);
        collapsingToolbarLayout=findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(extras.getString(Constant.TITLE));
        appBarLayout=findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(Math.abs(verticalOffset) > 200){
                    appBarExpanded = false;
                }else{
                    appBarExpanded = true;
                }
                invalidateOptionsMenu();
            }
        });


        populateContent(extras);
    }
    private void populateContent(Bundle bundle) {
        title.setText(bundle.getString(Constant.TITLE));
        description.setText(bundle.getString(Constant.DESC));
        date.setText(bundle.getString(Constant.DATE));
        genre.setText(bundle.getString(Constant.GENRE));
        rating.setText(bundle.getString(Constant.RATING));
        float rate=Float.parseFloat(String.valueOf(bundle.getString(Constant.RATING)));
        rate=(rate/10)*5;
        Glide.with(this).load(bundle.getString(Constant.IMAGE)).apply(RequestOptions.placeholderOf(R.drawable.ic_loading_24))
                .into(imageView);
        ratingBar.setRating(rate);



    }
}