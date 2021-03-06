package com.example.android.openGate;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marco on 26/03/18.
 */

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private MainActivity mContext;
    private List<Place> mPlaceList;
    private SharedPreferences mSharedPrefs;

    /**
     * Constructor using the context and the db cursor
     *
     * @param context the calling context/activity
     */
    public PlaceListAdapter(MainActivity context) {
        this.mContext = context;
        mPlaceList = new ArrayList<>();
        mSharedPrefs =  context.getPreferences(MODE_PRIVATE);
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new PlaceViewHolder that holds a View with the item_place_card layout
     */
    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_place_card, parent, false);
        return new PlaceViewHolder(view);

    }

    /**
     * Binds the data from a particular position in the cursor to the corresponding view holder
     *
     * @param holder   The PlaceViewHolder instance corresponding to the required position
     * @param position The current position that needs to be loaded with data
     */
    @Override
    public void onBindViewHolder(final PlaceViewHolder holder, final int position) {
        final String placeName = mPlaceList.get(position).getName().toString();
        String placeAddress = mPlaceList.get(position).getAddress().toString();

        holder.nameTextView.setText(placeName);
        holder.addressTextView.setText(placeAddress);
        final String key = "checkbox" + placeAddress;
        holder.serviceSwitch.setChecked(mSharedPrefs.getBoolean(key, false));
        holder.serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                mSharedPrefs.edit().putBoolean(key, status).apply();
            }
        });

        // Open Settings from View
        holder.nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SettingsActivity.class);
                intent.putExtra("LOCATION", holder.addressTextView.getText());
                intent.putExtra("NAME", holder.nameTextView.getText());
                mContext.startActivity(intent);
            }
        });
        holder.nameTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeElement(position);
                return false;
            }
        });
    }

    /**
     * The below method will replace the current place buffer with the new one,
     * whenever new places are added or changed from the list
     */
    public void swapPlaces(PlaceBuffer newPlaces) {
        if (newPlaces != null) {
            mPlaceList.clear();
            for (Place p : newPlaces) {
                mPlaceList.add(p);
            }
            //Then force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    /**
     * Returns the number of items in the cursor
     *
     * @return Number of items in the cursor, or 0 if null
     */
    @Override
    public int getItemCount() {
        if (mPlaceList == null) return 0;
        return mPlaceList.size();
    }
    public void removeElement(int index) {
        mContext.removePlace(index, mPlaceList.get(index));
        mPlaceList.remove(index);
        //Then force the RecyclerView to refresh
        this.notifyDataSetChanged();
    }
    /**
     * PlaceViewHolder class for the recycler view item
     */
    class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView addressTextView;
        Switch serviceSwitch;


        public PlaceViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            addressTextView = itemView.findViewById(R.id.address_text_view);
            serviceSwitch = itemView.findViewById(R.id.service_switch);

        }
    }
}
