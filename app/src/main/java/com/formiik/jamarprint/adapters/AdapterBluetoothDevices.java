package com.formiik.jamarprint.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formiik.jamarprint.activities.ActivitySearchPrinter;
import com.formiik.jamarprint.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 17/03/17.
 */

public class AdapterBluetoothDevices extends RecyclerView.Adapter<AdapterBluetoothDevices.ViewHolder> {

    // Store a member variable for the contacts
    private List<ActivitySearchPrinter.BluetoothDeviceFound> mDevices;
    private List<ViewHolder> mViewHolders = new ArrayList<>();
    // Store the context for easy access
    private Context mContext;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        // each data item is just a string in this case
        public TextView mNameTextView, mAddressTextView, mConectedTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.name);
            mAddressTextView = (TextView) itemView.findViewById(R.id.address);
            mConectedTextView = (TextView) itemView.findViewById(R.id.conected);

            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {

            final int position = getAdapterPosition(); // gets item position

            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(R.string.conect_title)
                        .setMessage(mDevices.get(position).mName +
                                "\n" + mDevices.get(position).mAddress);

                builder.setPositiveButton(R.string.conect, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConnectDeviceListener connectDeviceListener = (ConnectDeviceListener) getContext();
                        connectDeviceListener.connectDevice(position);
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });

                builder.show();
            }
        }
    }

    // Pass in the contact array into the constructor
    public AdapterBluetoothDevices(Context context, List<ActivitySearchPrinter.BluetoothDeviceFound> devices) {
        mDevices = devices;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterBluetoothDevices.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View deviceView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_device, parent, false);

        ViewHolder holder = new ViewHolder(deviceView);
        mViewHolders.add(holder);

        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(AdapterBluetoothDevices.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ActivitySearchPrinter.BluetoothDeviceFound device = mDevices.get(position);

        viewHolder.mNameTextView.setText(device.mName);
        viewHolder.mAddressTextView.setText(device.mAddress);

        if (device.isSelected){
            viewHolder.mNameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            viewHolder.mAddressTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            viewHolder.mNameTextView.setTypeface(null, Typeface.BOLD);
            viewHolder.mAddressTextView.setTypeface(null, Typeface.BOLD);
            viewHolder.mConectedTextView.setVisibility(View.VISIBLE);
        } else{
            viewHolder.mNameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.darkGray));
            viewHolder.mAddressTextView.setTextColor(ContextCompat.getColor(mContext, R.color.darkGray));
            viewHolder.mNameTextView.setTypeface(null, Typeface.NORMAL);
            viewHolder.mAddressTextView.setTypeface(null, Typeface.NORMAL);
            viewHolder.mConectedTextView.setVisibility(View.GONE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void clear(){
        mDevices.clear();
        mViewHolders.clear();
    }

    public interface ConnectDeviceListener{
        void connectDevice(int position);
    }

}

