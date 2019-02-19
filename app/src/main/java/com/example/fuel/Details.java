package com.example.fuel;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Details extends AppCompatActivity {

    EditText vehicleno1,fuelprice1,vehiclereading1,totalamount1;
    String vehiclenotext,fuelpricetext,vehiclereadingtext,totalamounttext;
    ActionBar bar;
    Button continue1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        vehicleno1=findViewById(R.id.viehicleno);
        fuelprice1=findViewById(R.id.fuelprice);
        vehiclereading1=findViewById(R.id.vehicleReading);
        totalamount1=findViewById(R.id.totalamount);
        continue1=findViewById(R.id.continue1);






        continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehiclenotext=vehicleno1.getText().toString();
                fuelpricetext=fuelprice1.getText().toString();
                vehiclereadingtext=vehiclereading1.getText().toString();
                totalamounttext=totalamount1.getText().toString();
                if(!vehiclenotext.isEmpty()&&!fuelpricetext.isEmpty()&&!vehiclereadingtext.isEmpty()&&!totalamounttext.isEmpty())
                {
                    Intent intent=new Intent(Details.this,SaveDeatils
                            .class);
                    intent.putExtra("vehicleno",vehiclenotext);
                    intent.putExtra("fuelprice",fuelpricetext);
                    intent.putExtra("vehiclereading",vehiclereadingtext);
                    intent.putExtra("totalamount",totalamounttext);
                    startActivity(intent);


                }else {

                    Toast.makeText(getApplicationContext(),"Please Enter Details",Toast.LENGTH_LONG).show();
                }

            }
        });






    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);

    }
}
