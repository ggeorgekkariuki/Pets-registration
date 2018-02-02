package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pets.R;

/**
 * Created by PROBOOK on 18-Jan-18.
 */

public class PetProvider extends ContentProvider {

    //Database helper object
    private PetDBHelper mDbHelper;

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    //Initialising the URI matcher object
    //UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;
    /** URI matcher code for the content URI for a SINGLE ROW in the pets table*/
    private static final int PETS_ID = 101;

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY_URI, PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY_URI, PetContract.PATH_PETS + "/#",PETS_ID);
    }


    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.

        mDbHelper = new PetDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match){
            case PETS:

                cursor = db.query(PetContract.PetEntry.TABLE_NAME,projection,selection,
                        selectionArgs,null,null,sortOrder,null);
                break;
            case PETS_ID:
                selection = PetContract.PetEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                
                cursor = db.query(PetContract.PetEntry.TABLE_NAME,projection,
                        selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //Every time something is queried, the client will be notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues contentValues){

        //We can extract out an attribute from the ContentValues object based on the key name

        String name = contentValues.getAsString(PetContract.PetEntry.COLUMN_NAME);
        Integer gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_GENDER);
        Integer  weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_WEIGHT);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues);
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        if(id != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int id;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                id = database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                if(id != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return id;
            case PETS_ID:
                // Delete a single row given by the ID in the URI
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                id = database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                if(id != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return id;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }
    }

    public int updatePet(Uri uri, ContentValues contentValues, String selection, String [] selectionArgs){
        //We can extract out an attribute from the ContentValues object based on the key name

        String name = contentValues.getAsString(PetContract.PetEntry.COLUMN_NAME);
        Integer gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_GENDER);
        Integer  weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_WEIGHT);

        //Sanity checking here to make sure we only use the proper data
        if (name == null || name.isEmpty() || name.length() == 0) {
            throw new IllegalArgumentException("Field requires a name");
        }
//        else if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
//           throw new IllegalArgumentException("Field requires a gender");
//        }
        else if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Field requires a weight");
        }
        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }
        //If the checks are all okay, we can update an entry or more
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Method returns a long which shows where an entry has been updated
        long id = db.update(PetContract.PetEntry.TABLE_NAME,contentValues,selection,selectionArgs);

        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
        } else{
            Toast.makeText(getContext(), "Updated at row: " + id, Toast.LENGTH_SHORT).show();
        }

        if(id != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ((int) id);
    }
}
