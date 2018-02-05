package com.example.android.pets.data;

import android.content.Context;
import android.database.Cursor;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.R;

/**
 * Created by PROBOOK on 26-Jan-18.
 */

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    //Creates a new view but does not bind any item onto it
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //These are the views that we'll bind data to.
        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView breedView = (TextView) view.findViewById(R.id.summary);
        //This is what we want to bind
        String nameField = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_NAME));
        String breedField = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_BREED));
        //Finally, we set/bind
        nameView.setText(nameField);

        //If nothing was saved in the db under the breed column, this will be displayed in the UI
        if (TextUtils.isEmpty(breedField)){
            breedView.setText("Unknown breed");
        } else
            breedView.setText(breedField);
    }
}
