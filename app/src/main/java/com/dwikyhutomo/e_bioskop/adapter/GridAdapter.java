package com.dwikyhutomo.e_bioskop.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dwikyhutomo.e_bioskop.DetailMovieActivity;
import com.dwikyhutomo.e_bioskop.FormMovieActivity;
import com.dwikyhutomo.e_bioskop.R;
import com.dwikyhutomo.e_bioskop.model.Movie;
import com.dwikyhutomo.e_bioskop.utils.Constant;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MovieViewHolder> {
    public List<Movie> movieList = new ArrayList<>();

    public GridAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @Override
    public GridAdapter.MovieViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_gridview, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MovieViewHolder holder, int position) {
        if (movieList.get(position).getTitle().length() > 8) {
            holder.txtTitle.setText(movieList.get(position).getTitle().substring(0,8)+"...");
        }
        else {
            holder.txtTitle.setText(movieList.get(position).getTitle());
        }

        Glide.with(holder.itemView.getContext()).load(movieList.get(position).getImage()).apply(RequestOptions.placeholderOf(R.drawable.ic_loading_24)
                .apply(new RequestOptions().transform(new RoundedCorners(25)))
                .error(R.drawable.ic_baseline_error_24))
                .into(holder.imageView);

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;
        private ImageView imageView;

        public MovieViewHolder(View view) {
            super(view);
            txtTitle = view.findViewById(R.id.titleGrid);

            imageView = view.findViewById(R.id.imageViewGrid);

        }

    }
}