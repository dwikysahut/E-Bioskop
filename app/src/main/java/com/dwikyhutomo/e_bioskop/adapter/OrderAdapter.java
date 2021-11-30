package com.dwikyhutomo.e_bioskop.adapter;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.format.DateFormat;
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
import com.dwikyhutomo.e_bioskop.model.Order;
import com.dwikyhutomo.e_bioskop.utils.Constant;
import com.dwikyhutomo.e_bioskop.utils.MySingleton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    public List<Order> OrderList = new ArrayList<>();
    private final String TAG = "Order Adapter";
    private SharedPreferences sharedPreferences;
    int success;
    private OnItemClickCallback onItemClickCallback;


    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private String getDate(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
        String date = format.format(newDate);
        return date;
    }
    public interface OnItemClickCallback {
        void onItemClicked(Order data);
    }
    public OrderAdapter(List<Order> OrderList) {
        this.OrderList = OrderList;
    }
    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderAdapter.OrderViewHolder holder, int position) {
        if (OrderList.get(position).getStatus().equals("Waiting")) {
            holder.tvStatus.setTextColor(Color.parseColor("#FFD700"));

        } else if (OrderList.get(position).getStatus().equals("Complete")) {
            holder.tvStatus.setTextColor(Color.parseColor("#006400"));
        }
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dateNow = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        //24 jam mengambil jam saja
        SimpleDateFormat timeNow = new SimpleDateFormat("kk", Locale.getDefault());
        //mencoba ketika jam saat ini diatas jam 12
        int jam= Integer.parseInt(timeNow.format(c));
//        int jam=12;

        String formattedDate = dateNow.format(c);
        if(formattedDate.equals(OrderList.get(position).getDateShowMovie()) && OrderList.get(position).getStatus().equals("Waiting")&&sharedPreferences.getString("role", "0").equals("2")){
            if(jam>=12){
                showNotification(OrderList.get(position).getTitleMovie(),holder.itemView);
                deleteOrder(OrderList.get(position).getIdOrder(),OrderList.get(position).getIdMovie(),OrderList.get(position).getJumlah(),holder.itemView);

            }
//            onItemClickCallback.onItemClicked(OrderList.get(holder.getAdapterPosition()));

      }
        holder.tvStatus.setText(OrderList.get(position).getStatus());
        holder.txtTitle.setText(OrderList.get(position).getTitleMovie());
        holder.txtShowTime.setText(OrderList.get(position).getShowTimeMovie());
        holder.txtDateShow.setText(OrderList.get(position).getDateShowMovie());
        holder.txtDateAdded.setText(getDate(OrderList.get(position).getDateAdded()));
        holder.txtJumlah.setText(OrderList.get(position).getJumlah());
        holder.txtCode.setText(OrderList.get(position).getKode());
        Glide.with(holder.itemView.getContext()).load(OrderList.get(position).getImageMovie())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading_24)
                        .apply(new RequestOptions().transform(new RoundedCorners(30)))
                        .error(R.drawable.ic_baseline_error_24))
                .into(holder.imageMovie);
        if(OrderList.get(position).getStatus().equals("Waiting")&&sharedPreferences.getString("role", "0").equals("1")){
            holder.layoutBtn.setVisibility(View.VISIBLE);
        }
        else{
            holder.layoutBtn.setVisibility(View.GONE);
        }
        holder.btnConfirmAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmOrder(OrderList.get(position).getIdOrder(),view);

            }
        });
        holder.btnCancelAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Batalkan pesanan ? ");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                deleteOrder(OrderList.get(position).getIdOrder(),OrderList.get(position).getIdMovie(),OrderList.get(position).getJumlah(),view);
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

    @Override
    public int getItemCount() {
        return OrderList != null ? OrderList.size() : 0;
    }



    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtShowTime, txtDateAdded, txtDateShow, txtCode, txtJumlah, tvStatus;
        private ImageView imageMovie;
        private RelativeLayout layoutBtn;
        private Button btnCancelAdmin, btnConfirmAdmin;


        public OrderViewHolder(View view) {
            super(view);


            txtTitle = view.findViewById(R.id.tv_title_movie_order);
            txtShowTime = view.findViewById(R.id.tv_showtime_order);
            txtDateAdded = view.findViewById(R.id.tv_date_added_order);
            txtDateShow = view.findViewById(R.id.tv_dateShow_order);
            layoutBtn = view.findViewById(R.id.layout_btnAdmin_order);
            txtJumlah = view.findViewById(R.id.tv_text_jumlah_order);
            tvStatus = view.findViewById(R.id.tv_status_order);
            imageMovie = view.findViewById(R.id.img_movie_order);
            btnCancelAdmin = view.findViewById(R.id.btnAdminCancel);
            btnConfirmAdmin = view.findViewById(R.id.btnConfirmAdmin);
            txtCode = view.findViewById(R.id.tv_code_transaction);
            sharedPreferences = view.getContext().getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
            if (sharedPreferences.getString("role", "0").equals("2")) {
                layoutBtn.setVisibility(View.GONE);
            }


        }

    }
    private void showNotification(String title, View view) {

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
                        .setContentTitle("Tiket film "+title+" Dibatalkan") // title for notification
//                        .setContentText()// message for notification
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Pemesanan dibatalkan karena Anda tidak melakukan pembayaran"))
                        .setAutoCancel(true); // clear notification after click
        Intent mIntent = new Intent(view.getContext(), OrderActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(),
                0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }
    private void deleteOrder(String id,String idMovie,String jumlah,View view){

                String url = Constant.BASE_URL + "deleteOrder.php";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Responsekuuu: " + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getInt("success");
                            if (success == 1) {
                                String url2 = Constant.BASE_URL + "updateMovieStock.php";
                                StringRequest request2 = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d(TAG, "Responsekuuu: " + response.toString());
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            success = jsonObject.getInt("success");
                                            if (success == 1) {

                        Toast.makeText(view.getContext(), "Pesanan Dibatalkan", Toast.LENGTH_LONG).show();
//                                                ((MovieListActivity) view.getContext()).refreshActivtiy();
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
                                        param.put("id", idMovie);
                                        param.put("jumlah", String.valueOf(jumlah));
                                        param.put("type", "cancel");

                                        return param;
                                    }
                                };
                                MySingleton.getInstance(view.getContext()).addToRequestQueue(request2);

                                Toast.makeText(view.getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                ((OrderActivity) view.getContext()).refreshActivtiy();

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
                        param.put("id", id);
                        return param;
                    }
                };
                MySingleton.getInstance(view.getContext()).addToRequestQueue(request);





    }
    private void confirmOrder(String id,View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Konfirmasi pesanan ? ");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String url = Constant.BASE_URL + "confirmOrder.php";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Responsekuuu: " + response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getInt("success");
                            if (success == 1) {
                                Toast.makeText(view.getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                ((OrderActivity) view.getContext()).refreshActivtiy();

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
                        param.put("id", id);
                        return param;
                    }
                };
                MySingleton.getInstance(view.getContext()).addToRequestQueue(request);

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
