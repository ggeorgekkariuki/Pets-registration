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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetCursorAdapter;
import com.example.android.pets.data.PetDBHelper;
import com.example.android.pets.data.PetProvider;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements
        LoaderCallbacks<Cursor>{

    public PetDBHelper mDBHelper;

    private static final int PET_LOADER = 1;

    PetCursorAdapter mPetCursorAdapter;

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


        /*If there is no data in the database, use the empty list view*/
        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.

            View emptyView = findViewById(R.id.empty_view);
            petListView.setEmptyView(emptyView);

        //Set up an Adapter to create a list item for each row of pets data in the database
        //There is no pet data yet(until the loader finishes) so pass in null for the cursor
        mPetCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(mPetCursorAdapter);

        //Start the loader
        getSupportLoaderManager().initLoader(PET_LOADER,null, this);

        //When a list item is clicked, move into the EditorActivity
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //On key list view pressed, we want to move to the new class/ activity
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                //We want to specify that it is the nth option we've clicked on.
                //That is why we use the .withAppendedId to fix the location id to the URI
                Uri intentUri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);

                intent.setData(intentUri);
                startActivity(intent);
            }
        });
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

//    private void deleteDummyPet () {
//        mDBHelper = new PetDBHelper(this);
//        SQLiteDatabase db = mDBHelper.getWritableDatabase();
//
//        String [] projection = {PetContract.PetEntry.COLUMN_NAME};
//        String selection = PetContract.PetEntry.COLUMN_NAME + "=?";
//        String [] selectionArgs = new String []{"Barry"};
//
////       db.execSQL("DELETE FROM " +
////                PetContract.PetEntry.TABLE_NAME +
////                " WHERE " + PetContract.PetEntry.COLUMN_NAME
////                +  " == 'Barry'");
//
//
////        db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
////        db.close();
//
////        int deleteUri = getContentResolver().delete(PetContract.PetEntry.CONTENT_URI, selection,
////                selectionArgs);
//    }

    private void deletePet() {
        int id = getContentResolver().delete(PetContract.PetEntry.CONTENT_URI,null, null);

        if(id == -1){
            Toast.makeText(this, R.string.editor_delete_pet_failed,Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, "Rows deleted " + id,Toast.LENGTH_SHORT)
                    .show();
        }
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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                 //Deletes all pets in the database
                deletePet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that defines the columns that we care about
        //For a cursor adapter, the _id column has to be included
        String [] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_NAME,
                PetContract.PetEntry.COLUMN_BREED
        };
        //This method will execute a content provider on a background thread

        return new CursorLoader(this,
                PetContract.PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    //Update with this new cursor to get the updated data
        mPetCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    //This call back is called when the data needs to be deleted
        mPetCursorAdapter.swapCursor(null);
    }
}
