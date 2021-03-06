package com.proyecto.colere.easyfood;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.proyecto.colere.easyfood.Common.Common;
import com.proyecto.colere.easyfood.Model.MyResponse;
import com.proyecto.colere.easyfood.Model.Notification;
import com.proyecto.colere.easyfood.Model.Order;
import com.proyecto.colere.easyfood.Model.Request;
import com.proyecto.colere.easyfood.Model.Sender;
import com.proyecto.colere.easyfood.Model.Token;
import com.proyecto.colere.easyfood.Remote.APIService;
import com.proyecto.colere.easyfood.ViewHolder.CartAdapter;
import com.proyecto.colere.easyfood.database.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init Service
        mService = Common.getFCMService();

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (FButton) findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size() > 0)

                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Tu carrito esta vacio", Toast.LENGTH_SHORT).show();

            }
        });
        
        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Un paso más!");
        alertDialog.setMessage("Introduce tu dirección: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment,null);

        final MaterialEditText edtAddress = (MaterialEditText) order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment = (MaterialEditText) order_address_comment.findViewById(R.id.edtComment);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0", //status
                        edtComment.getText().toString(),
                        cart
                );

                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number)
                        .setValue(request);

                new Database(getBaseContext()).cleanCart();

                sendNotificationOrder(order_number);
                //Toast.makeText(Cart.this, "Gracias, Pedido realizado", Toast.LENGTH_SHORT).show();
                //finish();

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    Token serverToken = postSnapShot.getValue(Token.class);

                    //Create raw payload to send
                    Notification notification = new Notification("EDMT Dev", "Tienes order nuevo " +order_number);
                    Sender content = new Sender(serverToken.getToken(), notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success == 1){
                                            Toast.makeText(Cart.this, "Gracias, Pedido realizado", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else {
                                            Toast.makeText(Cart.this, "Ha fallado!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onContextItemSelected (MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
        
    }

    private void deleteCart(int position) {
        //We will remove item at List<Order> by position
        cart.remove(position);
        //After that, we will delete all old data from SQLite
        new Database(this).cleanCart();
        //And final, we will update new data from List<Order> to Sqlite
        for(Order item:cart)
            new Database(this).addToCart(item);

        //Refresh
        loadListFood();

    }


    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculate total price
        int total = 0;
        for (Order order:cart){
            total+=(Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
            Locale locale = new Locale("en", "US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));
        }

        //Press Ctrl+o



    }
}
