package com.example.kensotto.myapplication;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PDKClient pdkClient;


    private static final String appID = "4959345165245365177";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PDKClient.configureInstance(this, "4959345165245365177");
        PDKClient.getInstance().onConnect(this);

        // Call configureInstance() method with context and App Id
        pdkClient = PDKClient.configureInstance(this, appID);

        // Call onConnect() method to make link between App id and Pinterest SDK
        pdkClient.onConnect(this);
        pdkClient.setDebugMode(true);

    }
    /**
     * Create an array list and add PDKClient permission that will be used by user.
     * Called login method and pass context, array list and PDKCallback object.
     * It will make authentication request to authenticate user.
     * @param view
     */
    public void onLogin(View view) {

        List scopes = new ArrayList<String>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PRIVATE);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PRIVATE);

        pdkClient.login(this, scopes, new PDKCallback() {

            /**
             * It called, when Authentication success
             * @param response
             */
            @Override
            public void onSuccess(PDKResponse response) {

                Log.e(getClass().getName(), response.getData().toString());
                onLoginSuccess();

            }

            /**
             * It called, when Authentication failed
             * @param exception
             */
            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
            }
        });
    }
    /**
     * It handle reuslt and switch back to own app when authentication process complete
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pdkClient.onOauthResponse(requestCode, resultCode,
                data);
        Log.e(getClass().getName(), "hello world");
        // call onLoginSuccess() method
        onLoginSuccess();
    }
    /**
     * Start HomeActivity class
     */
    private void onLoginSuccess() {
        Intent i = new Intent(this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

}

