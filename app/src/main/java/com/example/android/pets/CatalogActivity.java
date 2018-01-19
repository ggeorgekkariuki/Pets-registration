/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDBHelper;
import com.example.android.pets.data.PetProvider;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    public PetDBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {


        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        String [] projection = null;
        String selection = null;

        Cursor cursor = getContentResolver().query(PetContract.PetEntry.CONTENT_URI,projection,
                selection,null,null);
//        Cursor cursor = db.rawQuery("SELECT * FROM " + PetContract.PetEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount() + "\n" + "\n");

            //Get the column index where the value is stored
            int nameColumnID = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_NAME);
            int breedColumnID = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_BREED);
            int genderColumnID = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_GENDER);
            int weightColumnID = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_WEIGHT);
            int idColumnID = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_ID);

            displayView.append("_id - name - breed - gender - weight" + "\n");

            while (cursor.moveToNext()){
                //The value of whatever is stored in the column index
                int IDValue = cursor.getInt(idColumnID);
                String nameValue = cursor.getString(nameColumnID);
                String breedValue = cursor.getString(breedColumnID);
                int genderValue = cursor.getInt(genderColumnID);
                int weightValue = cursor.getInt(weightColumnID);

                displayView.append(IDValue + " - " + nameValue + " - " +
                        breedValue + " - " + genderValue + " - " + weightValue + "\n" + "\n");
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private void insertDummyPet(){

        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_NAME, "Barry");
        values.put(PetContract.PetEntry.COLUMN_BREED, "Goldberg");
        values.put(PetContract.PetEntry.COLUMN_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_WEIGHT, 14);

        //long newRowID = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        //Toast.makeText(this, "Entry added to "+newRowID, Toast.LENGTH_SHORT).show();

        Uri newUri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI,values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteDummyPet () {
        mDBHelper = new PetDBHelper(this);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String [] projection = {PetContract.PetEntry.COLUMN_NAME};
        String selection = PetContract.PetEntry.COLUMN_NAME + "=?";
        String [] selectionArgs = new String []{"Barry"};

//       db.execSQL("DELETE FROM " +
//                PetContract.PetEntry.TABLE_NAME +
//                " WHERE " + PetContract.PetEntry.COLUMN_NAME
//                +  " == 'Barry'");


        db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                 //Do nothing for now
                deleteDummyPet();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
