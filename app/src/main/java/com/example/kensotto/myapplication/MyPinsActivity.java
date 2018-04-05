package com.example.kensotto.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MyPinsActivity extends AppCompatActivity {

    private PDKCallback myPinsCallback;
    private PDKResponse myPinsResponse;
    private GridView gridView;
    private PinsAdapter pinAdapter;
    private boolean loading = false;
    private static final String PIN_FIELDS = "id,link,creator,image,counts,note,created_at,board,metadata";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pins);
        setTitle("My Pins");

        // Initialize PinAdapter class
        pinAdapter = new PinsAdapter(this);

        // Calling init() method
        init();

        // Calling getResponse() method
        getResponse();

        // Set Adapter class
        gridView.setAdapter(pinAdapter);

        loading = true;

        // Calling fetchPins() method
        fetchPins();
    }

    public void init(){

        gridView = (GridView) findViewById(R.id.grid_view);

    }

    /**
     * Created PDKCallback method and get user pin list by calling getPinList() method.
     * Set the pin list to Adapter by calling  setPinList() method of PinAdapter class.
     */
    public void getResponse(){

        myPinsCallback = new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                loading = false;
                myPinsResponse = response;
                pinAdapter.setPinList(response.getPinList());
            }

            @Override
            public void onFailure(PDKException exception) {
                loading = false;
                Log.e(getClass().getName(), exception.getDetailMessage());
            }
        };
    }

    /**
     * Make a request to get Pins by calling getMyPins() method and pass the pin field and PDKCallback object
     */
    private void fetchPins() {

        pinAdapter.setPinList(null);
        PDKClient.getInstance().getMyPins(PIN_FIELDS, myPinsCallback);
    }

    /**
     * Load pins
     */
    private void loadNext() {

        if (!loading && myPinsResponse.hasNext()) {

            loading = true;
            myPinsResponse.loadNext(myPinsCallback);
        }
    }

    /**
     * GridView Adapter class
     */
    private class PinsAdapter extends BaseAdapter {

        private List<PDKPin> _pinList;
        private Context _context;
        public PinsAdapter(Context context) {
            _context = context;
        }

        /**
         *  Add list of item to List&lt;PDKPin&gt; array
         * @param list
         */
        public void setPinList(List<PDKPin> list) {

            if (_pinList == null)
                _pinList = new ArrayList<PDKPin>();

            if (list == null)
                _pinList.clear();

            else
                _pinList.addAll(list);

            notifyDataSetChanged();
        }

        /**
         * @return array list
         */
        public List<PDKPin> getPinList() {
            return _pinList;
        }

        /**
         * @return size of array
         */
        @Override
        public int getCount() {
            return _pinList == null ? 0 : _pinList.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         * @param position
         * @return position of item
         */
        @Override
        public Object getItem(int position) {
            return position;
        }

        /**
         * Get the row id associated with the specified position in the list.
         * @param position
         * @return
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Get a View that displays the data at the specified position in the data set.
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderItem viewHolder;

            //load more pins if about to reach end of list
            if (_pinList.size() - position < 5) {
                loadNext();
            }

            if (convertView == null) {

                LayoutInflater inflater = ((Activity) _context).getLayoutInflater();

                // inflate list_item_pin xml file
                convertView = inflater.inflate(R.layout.list_item_pin, parent, false);

                viewHolder = new ViewHolderItem();
                viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.title_view);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);

                convertView.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolderItem) convertView.getTag();
            }

            PDKPin pinItem = _pinList.get(position);

            if (pinItem != null) {

                // set note of pin to textview
                viewHolder.textViewItem.setText(pinItem.getNote());

                // load pin image
                Picasso.with(_context.getApplicationContext())
                        .load(pinItem.getImageUrl())
                        .into(viewHolder.imageView);
            }

            return convertView;
        }

        private class ViewHolderItem {

            TextView textViewItem;
            ImageView imageView;
        }
    }
}