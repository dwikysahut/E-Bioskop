package com.dwikyhutomo.e_bioskop.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dwikyhutomo.e_bioskop.DetailMovieActivity;
import com.dwikyhutomo.e_bioskop.FormMovieActivity;
import com.dwikyhutomo.e_bioskop.MainActivity;
import com.dwikyhutomo.e_bioskop.MovieListActivity;
import com.dwikyhutomo.e_bioskop.OrderActivity;
import com.dwikyhutomo.e_bioskop.R;
import com.dwikyhutomo.e_bioskop.model.Movie;
import com.dwikyhutomo.e_bioskop.utils.Constant;
import com.dwikyhutomo.e_bioskop.utils.MySingleton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final String TAG = "Movie Adapter";
    public List<Movie> movieList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    int success;
    EditText titleForm, jumlahForm;
    private OnItemClickCallback onItemClickCallback;
    public MovieAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_movie, parent, false);
        return new MovieViewHolder(view);
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
    public void onBindViewHolder(@NonNull @NotNull MovieAdapter.MovieViewHolder holder, int position) {
        if (sharedPreferences.getString("role", "0").equals("2")&&movieList.get(position).getStock()<1) {
            holder.btnOrder.setVisibility(View.GONE);
            holder.txtStock.setText("Sold Out");
            holder.txtStock.setTextColor(Color.parseColor("#DC143C"));
        }
        else{
            holder.txtStock.setText(String.valueOf(movieList.get(position).getStock()));
        }
        holder.txtTitle.setText(movieList.get(position).getTitle());
        holder.txtShowTime.setText(movieList.get(position).getShowTime());
        holder.txtRating.setText(String.valueOf(movieList.get(position).getRating()));
        holder.btnGenre.setText(movieList.get(position).getGenre());
        holder.txtDate.setText(movieList.get(position).getDate());

        Glide.with(holder.itemView.getContext()).load(movieList.get(position).getImage()).apply(RequestOptions.placeholderOf(R.drawable.ic_loading_24)
                .apply(new RequestOptions().transform(new RoundedCorners(30)))
                .error(R.drawable.ic_baseline_error_24))
                .into(holder.imageView);


        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(view.getContext(), FormMovieActivity.class);
                mIntent.putExtra("TYPE", "EDIT");
                mIntent.putExtra(Constant.ID, String.valueOf(movieList.get(position).getId()));
                mIntent.putExtra(Constant.TITLE, movieList.get(position).getTitle());
                mIntent.putExtra(Constant.DESC, movieList.get(position).getDescription());
                mIntent.putExtra(Constant.DATE, movieList.get(position).getDate());
                mIntent.putExtra(Constant.RATING, String.valueOf(movieList.get(position).getRating()));
                mIntent.putExtra(Constant.IMAGE, movieList.get(position).getImage());
                mIntent.putExtra(Constant.GENRE, movieList.get(position).getGenre());
                mIntent.putExtra(Constant.SHOW_START, movieList.get(position).getShowTime().substring(0, 5));
                mIntent.putExtra(Constant.SHOW_END, movieList.get(position).getShowTime().substring(8, 13));
                mIntent.putExtra(Constant.STOCK, String.valueOf(movieList.get(position).getStock()));
                view.getContext().startActivity(mIntent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(view.getContext(), DetailMovieActivity.class);
                mIntent.putExtra(Constant.ID, movieList.get(position).getId());
                mIntent.putExtra(Constant.TITLE, movieList.get(position).getTitle());
                mIntent.putExtra(Constant.DESC, movieList.get(position).getDescription());
                mIntent.putExtra(Constant.DATE, movieList.get(position).getDate());
                mIntent.putExtra(Constant.RATING, String.valueOf(movieList.get(position).getRating()));
                mIntent.putExtra(Constant.IMAGE, movieList.get(position).getImage());
                mIntent.putExtra(Constant.GENRE, movieList.get(position).getGenre());
                view.getContext().startActivity(mIntent);

//                ((MovieListActivity)view.getContext()).finish();
            }

        });
        holder.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogForm(String.valueOf(movieList.get(position).getId()), movieList.get(position).getTitle(), movieList.get(position).getStock(), sharedPreferences.getString("id", "none"), view,movieList.get(position).getDate());
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(movieList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    private void DialogForm(String id, String title, int stock, String id_user, View view,String date) {
        dialog = new AlertDialog.Builder(view.getContext());

        inflater = LayoutInflater.from(view.getContext());
        dialogView = inflater.inflate(R.layout.dialog_layout_order, null);
        titleForm = dialogView.findViewById(R.id.edt_form_title);
        titleForm.setText(title);
        jumlahForm = dialogView.findViewById(R.id.edt_form_jumlah);
//        seatForm=dialogView.findViewById(R.id.edt_form_seat);

//        ArrayAdapter<String> adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_spinner_dropdown_item, dialogView.getResources().getStringArray(R.array.seat));
//        seatForm.setAdapter(adapter);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setTitle("Order Form");
        String randomCode = generateCode(15);
        String status = "1";

//        String seat=seatForm.getSelectedItem().toString();

//id jumlah id_user status
        Log.d("code", "codeku: " + randomCode.toString());


        dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int jumlah = Integer.parseInt(jumlahForm.getText().toString());
                int result;
                if (jumlahForm.getText().toString().isEmpty()) {
                    Toast.makeText(dialogView.getContext(), "Semua Field harus Diisi,Order Gagal ", Toast.LENGTH_LONG).show();
                } else {
                    if (jumlah > stock) {

                        Toast.makeText(dialogView.getContext(), "Jumlah Pemesanan melebihi stock tersedia, Order gagal ", Toast.LENGTH_LONG).show();
                    } else {
                        result=stock-jumlah;
                        orderTicket(id, title, jumlah,result, id_user, status, randomCode, view,date);
                    }
                    dialog.dismiss();
                }

            }
        });

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                kosong();
            }
        });

        dialog.show();
    }

    private void orderTicket(String idMovie, String title, int jumlah,int result, String id_user, String status, String kode, View view,String date) {


        String url = Constant.BASE_URL + "addOrder.php";


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Responsekuuu: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                    if (success == 1) {
                        //update movie stock
                        String url2 = Constant.BASE_URL + "updateMovieStock.php";
                        StringRequest request2 = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Responsekuuu: " + response.toString());
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    success = jsonObject.getInt("success");
                                    if (success == 1) {
                                        ((MovieListActivity) view.getContext()).refreshActivtiy();
                                    } else {
                                        Toast.makeText(view.getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                    ((MovieListActivity) view.getContext()).refreshActivtiy();
                                } catch (JSONException e) {
                                    Log.e(TAG, "onResponse: " + e.getMessage());

                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Error: " + error.getMessage());
                                Toast.makeText(view.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                HashMap<String, String> param = new HashMap<>();
                                param.put("id", idMovie);
                                param.put("jumlah", String.valueOf(result));
                                return param;
                            }
                        };
                        MySingleton.getInstance(view.getContext()).addToRequestQueue(request2);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                ((MovieListActivity) view.getContext()).refreshActivtiy();
                            }
                        }, 500);

                        showNotification(title, view,date);
                        Toast.makeText(view.getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(view.getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: " + e.getMessage());

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(view.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("id_movie", idMovie);
                param.put("id_user", id_user);
                param.put("jumlah", String.valueOf(jumlah));
                param.put("kode", kode);
                param.put("status", status);

                return param;
            }
        };
        MySingleton.getInstance(view.getContext()).addToRequestQueue(request);

//           }
//           else{
//               Toast.makeText(FormMovieActivity.this, "Url Image tidak valid", Toast.LENGTH_LONG).show();
//           }
    }

    private String generateCode(int length) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder kode = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            kode.append(AB.charAt(rnd.nextInt(AB.length())));
        return kode.toString();
    }

    private void showNotification(String title, View view,String date) {

        NotificationManager mNotificationManager =
                (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new
                    NotificationChannel("my_channel_01",
                    "my_channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription("ini channelku");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(view.getContext(),
                        "my_channel_01")
                        .setSmallIcon(R.drawable.logoku) // notification icon
                        .setContentTitle("Pemesanan Tiket "+title+" Berhasil") // title for notification
//                        .setContentText()// message for notification
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Segera datang ke loket dan lakukan Pembayaran sebelum "+date+" jam 12.00"))
                        .setAutoCancel(true); // clear notification after click
        Intent mIntent = new Intent(view.getContext(), OrderActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(),
                0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public interface OnItemClickCallback {
        void onItemClicked(Movie data);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtShowTime, txtRating, txtDate, txtStock;
        private ImageView imageView;
        private RelativeLayout layoutBtn;
        private Button btnDelete, btnEdit, btnGenre, btnOrder;

        public MovieViewHolder(View view) {
            super(view);
            txtTitle = view.findViewById(R.id.tv_title_item_movie);
            txtShowTime = view.findViewById(R.id.tv_showtime_item_movie);
            txtRating = view.findViewById(R.id.tv_rating_item_movie);
            btnGenre = view.findViewById(R.id.btn_genre_item_movie);
            imageView = view.findViewById(R.id.img_item_movie);
            btnDelete = view.findViewById(R.id.btnDelete);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnOrder = view.findViewById(R.id.btnOrder);
            txtDate = view.findViewById(R.id.tv_date_item_movie);
            layoutBtn = view.findViewById(R.id.layout_btnAdmin);
            txtStock = view.findViewById(R.id.tv_stock_item_movie);
            sharedPreferences = view.getContext().getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
            if (sharedPreferences.getString("role", "0").equals("2")) {
                layoutBtn.setVisibility(View.GONE);

                btnOrder.setVisibility(View.VISIBLE);
            }
            if (sharedPreferences.getString("role", "0").equals("1")) {
                btnOrder.setVisibility(View.GONE);
            }


//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Movie m = movieList.get(getAdapterPosition());
//                    Toast.makeText(view.getContext(),"kamu memilih "+ String.valueOf(m.getId()),Toast.LENGTH_LONG).show();
//                }
//            });
        }

    }
}
