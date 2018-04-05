package com.example.kensotto.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.PDKUser;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private final String USER_FIELDS = "id,image,counts,created_at,first_name,last_name,bio";
    private static boolean DEBUG = true;
    private TextView name;
    private ImageView profile;
    PDKUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        name = (TextView) findViewById(R.id.name_textview);
        profile = (ImageView) findViewById(R.id.profile_imageview);

        getUserDetails();
    }

    /**
     * Get authenticated user details by calling getMe() method of PDKClient.
     * Pass the user field and PDKCallback object.
     */
    private void  getUserDetails() {

        PDKClient.getInstance().getMe(USER_FIELDS, new PDKCallback() {

            /**
             * It called, when successfully retrieve details of user.
             * @param response
             */
            @Override
            public void onSuccess(PDKResponse response) {

                if (DEBUG)
                    Log.e("Response", String.format("status: %d", response.getStatusCode()));

                user = response.getUser();

                // Call setUser() method
                setUser();
            }

            /**
             * It called , when request to get user details failed
             * @param exception
             */
            @Override
            public void onFailure(PDKException exception) {
                if (DEBUG) Log.e("Exception", exception.getDetailMessage());
                Toast.makeText(HomeActivity.this, "/me Request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *  set User name nad profile image
     */
    private void setUser() {

        name.setText(user.getFirstName() + " " + user.getLastName() );
        Picasso.with(this).load(user.getImageUrl()).into(profile);
    }

    public void myPins(View view){
        Intent pin = new Intent(HomeActivity.this, MyPinsActivity.class);
        startActivity(pin);
    }

    /**
     * Logout from pinterest and start another activity
     * @param view
     */
    public void logout(View view){

        PDKClient.getInstance().logout();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}