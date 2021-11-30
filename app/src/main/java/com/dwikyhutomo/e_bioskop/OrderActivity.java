package com.dwikyhutomo.e_bioskop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.dwikyhutomo.e_bioskop.adapter.MovieAdapter;
import com.dwikyhutomo.e_bioskop.adapter.OrderAdapter;
import com.dwikyhutomo.e_bioskop.model.Movie;
import com.dwikyhutomo.e_bioskop.model.Order;
import com.dwikyhutomo.e_bioskop.utils.Constant;
import com.dwikyhutomo.e_bioskop.utils.MySingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private String TAG = "Order request";
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private int success;
    TextView emptyText;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        sharedPreferences = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recycler_view_order);
//        getSupportActionBar().setTitle("My Order");
//        getSupportActionBar().setSubtitle(sharedPreferences.getString("nama","unknown"));
        progressBar = findViewById(R.id.progressBarOrder);
        emptyText = findViewById(R.id.text_empty_order);
        emptyText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        toolbar = findViewById(R.id.toolbar_order);
        setSupportActionBar(toolbar);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullRequestOrder);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.VISIBLE);
                getOrder(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OrderAdapter(orderList);
        recyclerView.setVisibility(View.INVISIBLE);
        getOrder();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getOrder() {
        orderList.clear();
        adapter.notifyDataSetChanged();
        String url;
        String id_user = sharedPreferences.getString("id", "0");
        if (sharedPreferences.getString("role", "0").equals("1")) {

            url = Constant.BASE_URL + "getAllOrder.php";
        } else {
            url = Constant.BASE_URL + "getOrderById.php?id=" + id_user;
        }

        // membuat request JSON

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                // Parsing json
                try {
                    JSONObject jsonobject = new JSONObject(response);
                    Log.d("urlnih", sharedPreferences.getString("role", "0"));
                    Log.d("coba", jsonobject.toString());
                    success = jsonobject.getInt("success");
                    if (success == 1) {
                        JSONArray jsonarray = jsonobject.getJSONArray("data");

                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject obj = jsonarray.getJSONObject(i);
                            Log.d("data" + i, String.valueOf(jsonarray.length()));
                            orderList.add(new Order(obj.getString("id"),
                                    obj.getString("id_movie"),
                                    obj.getString("movie_title"),
                                    obj.getString("movie_image"),
                                    obj.getString("movie_dateShow"),
                                    obj.getString("movie_showTime"),
                                    obj.getString("id_user"),
                                    obj.getString("jumlah"),
                                    obj.getString("status_name"),
                                    obj.getString("kode"),
                                    obj.getString("date_added")
                            ));

                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.INVISIBLE);
                    } else {
//                    Toast.makeText(OrderActivity.this,jsonobject.getString("message"),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
                // notifikasi adanya perubahan data pada adapter


                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });

        MySingleton.getInstance(OrderActivity.this).addToRequestQueue(request);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    public void refreshActivtiy() {
        progressBar.setVisibility(View.VISIBLE);
        getOrder();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public class BroadcastManager extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String yourDate = "04/01/2016";
                String yourHour = "16:45:23";
                Date d = new Date();
                SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss");
                if (date.equals(yourDate) && hour.equals(yourHour)) {
                    Intent it = new Intent(context, MainActivity.class);
                    createNotification(context, it, "new mensage", "body!", "this is a mensage");
                }
            } catch (Exception e) {
                Log.i("date", "error == " + e.getMessage());
            }
        }


        public void createNotification(Context context, Intent intent, CharSequence ticker, CharSequence title, CharSequence descricao) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setTicker(ticker);
            builder.setContentTitle(title);
            builder.setContentText(descricao);
            builder.setSmallIcon(R.drawable.logoku);
            builder.setContentIntent(p);
            Notification n = builder.build();
            //create the notification
            n.vibrate = new long[]{150, 300, 150, 400};
            n.flags = Notification.FLAG_AUTO_CANCEL;
            nm.notify(R.drawable.logoku, n);
            //create a vibration
            try {

                Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone toque = RingtoneManager.getRingtone(context, som);
                toque.play();
            } catch (Exception e) {
            }
        }
    }
}