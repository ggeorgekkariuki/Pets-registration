package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by PROBOOK on 16-Jan-18.
 */

public final class PetContract {

    //BUILDING THE CONTENT URI
    //The content authority
    public static final String CONTENT_AUTHORITY_URI = "com.example.android.pets";
    //The URI
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY_URI);
    //THE Path
    public static final String PATH_PETS = "pets";
    /**
     * The MIME type for a list of pets.
     */
    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY_URI + "/" + PATH_PETS;

    /**
     * The MIME type for a single pet.
     */
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY_URI + "/" + PATH_PETS;





    /* Create an empty constructor so that no class will implement it */
    private PetContract () {}

    public static final class PetEntry implements BaseColumns{
        //Table name
        public static final String TABLE_NAME = "pets";
        //Column for the _id
        public static final String COLUMN_ID = BaseColumns._ID;
        //Column for the name
        public static final String COLUMN_NAME = "name";
        //Column for the breed
        public static final String COLUMN_BREED = "breed";
        //Column for the gender
        public static final String COLUMN_GENDER = "gender";
        //Column for weight
        public static final String COLUMN_WEIGHT = "weight";

        //Constants for the gender categories
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;
        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }
        //The final CONTENT URI
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_URI,PATH_PETS);

    }
}
