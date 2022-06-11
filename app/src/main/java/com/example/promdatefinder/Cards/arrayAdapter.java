package com.example.promdatefinder.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.promdatefinder.R;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<cards> {
    Context context;

    public arrayAdapter (Context context, int resourceId, List<cards> items)
    {
        super(context,resourceId,items);
    }

    public View getView (int position, View convertView, ViewGroup parent)
    {
        cards card_item = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView ime = convertView.findViewById(R.id.ime);
        ImageView image = convertView.findViewById(R.id.image);
        ime.setText(card_item.getIme());

        /*
        switch (card_item.getProfileImageUrl())
        {
            case "https://bit.ly/2tEcWAM":
                Glide.with(convertView.getContext()).load(R.mipmap.ic_launcher).into(image);
                break;

            default:*/

                //Glide.clear(image);
                Glide.with(convertView.getContext()).clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);

                //break;
       // }


        return convertView;

    }
}
