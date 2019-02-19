package com.example.fuel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SaveDeatils extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {

    String placeName1,placeAttributuion1;
    Button save;

    String currentlocation;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    protected GoogleApiClient mGoogleApiClient;
    double totol,pricefuel,quantityliter;
      private TextView drivername,mobilenumber,vehicleno,vehiclereading,totalamount,quantity,date,time,location,fuelprice1;
    Date currentTime;
  private String vehiclnober,fuelprice,vehicleread,totalamount1,liter,date1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_deatils);


        drivername=findViewById(R.id.drivername1);
        mobilenumber=findViewById(R.id.mobilenumber1);
        vehicleno=findViewById(R.id.viehicleno1);
        vehiclereading=findViewById(R.id.vehicleReading1);
        totalamount=findViewById(R.id.totalamount1);
        fuelprice1=findViewById(R.id.fuelprice1);
        quantity=findViewById(R.id.quantity1);
        date=findViewById(R.id.date1);
        time=findViewById(R.id.time1);
        location=findViewById(R.id.location1);
        save=findViewById(R.id.save);

        Intent intent=getIntent();
        vehiclnober=intent.getStringExtra("vehicleno");
        fuelprice=intent.getStringExtra("fuelprice");
        vehicleread=intent.getStringExtra("vehiclereading");
        totalamount1=intent.getStringExtra("totalamount");

        totol=Double.parseDouble(totalamount1);
        pricefuel=Double.parseDouble(fuelprice);

        quantityliter=totol/pricefuel;
        currentTime= Calendar.getInstance().getTime();


        SimpleDateFormat df=new SimpleDateFormat("dd-MMM-yyyy");
        date1=df.format(Calendar.getInstance().getTime());
        liter=String.valueOf(quantityliter);

       vehicleno.setText(vehiclnober);
        vehiclereading.setText(vehicleread);
        totalamount.setText(totalamount1);
        fuelprice1.setText(fuelprice);
        date.setText(date1);
        time.setText(""+currentTime);

        if (liter.length()>6)
        {
            quantity.setText(""+liter.substring(0,6)+" Lit");
        }else {
            quantity.setText(""+liter);
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

          location();

          save.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {


                  if(currentlocation.isEmpty())
                  {
                      Toast.makeText(getApplicationContext(),"Please wait...to find location",Toast.LENGTH_LONG).show();
                  }else{
                      final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

                      FirebaseAuth auth=FirebaseAuth.getInstance();
                      final FirebaseUser user =auth.getCurrentUser();
                      final String phone=user.getPhoneNumber().toString();
                      myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              String name=String.valueOf(dataSnapshot.child("Users").child(phone).child("name").getValue());
                              myRef.child("Vehicles").child(vehiclnober).child(vehicleread).child("DriverName").setValue(name);
                              myRef.child("Vehicles").child(vehiclnober).child(vehicleread).child("MobileNumber").setValue(phone);
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });


                      myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              for (DataSnapshot dsp: dataSnapshot.child("Users").getChildren()){
                                  String username=String.valueOf(dsp.child("name").getValue());
                                  Toast.makeText(SaveDeatils.this, username, Toast.LENGTH_SHORT).show();


                              }
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });
                      myRef.child("Vehicles").child(vehiclnober).child(vehicleread).child("Date").setValue(date1);
                      myRef.child("Vehicles").child(vehiclnober).child(vehicleread).child("Time").setValue(""+currentTime);
                      myRef.child("Vehicles").child(vehiclnober).child(vehicleread).child("Location").setValue(currentlocation);
                      myRef.child("Vehicles").child(vehiclnober).child(vehicleread).child("VehicleReading").setValue(vehicleread);
                      myRef.child("Vehicles").child(vehiclnober).child(vehicleread).child("FuelPrice").setValue(fuelprice);
                      myRef.child("Vehicles").child(vehiclnober).child(vehicleread).child("Quantity").setValue(liter);
                      myRef.child("Vehicles").child(vehiclnober).child(vehicleread).child("TotalAmount").setValue(totalamount1);
                      Toast.makeText(getApplicationContext(),"Data save Successful",Toast.LENGTH_LONG).show();
                  }




              }
          });



    }
    void location() {
        if (ActivityCompat.checkSelfPermission(SaveDeatils.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {


                if (!likelyPlaces.getStatus().isSuccess()) {
                    // Request did not complete successfully
                    //Log.e(TAG, "Place query did not complete. Error: " + likelyPlaces.getStatus().toString());
                    likelyPlaces.release();
                    return;
                }
                placeName1 = String.format("%s", likelyPlaces.get(0).getPlace().getName());
                placeAttributuion1 = String.format("%s", likelyPlaces.get(0).getPlace().getAddress());

               // Toast.makeText(SaveDeatils.this,""+placeName1+" hello  "+placeAttributuion1,Toast.LENGTH_SHORT).show();


                location.setText(placeName1+"  "+placeAttributuion1);

                currentlocation=placeName1+"  "+placeAttributuion1;
                likelyPlaces.release();
            }
        });


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}