package anirudhrocks.com.safetyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ContactDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactDataDb.db";
    public static final String TABLE_NAME = "contact_data";
    public static final String KEY_ID = "id";
    public static final String CONTACT_NAME = "contact_name";
    public static final String CONTACT_NUMBER = "contact_number";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        final String SQL_CREATE_CONTACT_DATA_TABLE = "CREATE TABLE " +
                this.TABLE_NAME + " ( " + this.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                this.CONTACT_NAME + " TEXT," +
                this.CONTACT_NUMBER + " TEXT ) ";

        db.execSQL(SQL_CREATE_CONTACT_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + this.TABLE_NAME);
        onCreate(db);
    }

    public void addContact(Contact contact) {
        ContentValues cv = new ContentValues();
        cv.put(this.CONTACT_NAME, contact.getName());
        cv.put(this.CONTACT_NUMBER, contact.getPhoneNumber());
        db.insert(this.TABLE_NAME, null, cv);
    }

    public void removeContact(Contact contact) {
        db = getWritableDatabase();
        db.delete(this.TABLE_NAME, this.CONTACT_NAME + "=? and " +
                this.CONTACT_NUMBER+"=?", new String[]{contact.getName(), contact.getPhoneNumber()});
//        db.execSQL("DELETE FROM " + this.TABLE_NAME + " WHERE " + this.CONTACT_NAME + " = " +contact.getName() + " AND " +
//                this.CONTACT_NUMBER + " = " + contact.getPhoneNumber());
    }

//    public ArrayList<Contact> getAllContacts() {
//        ArrayList<Contact> contactList = new ArrayList<>();
//        db = getReadableDatabase();
//
//        Cursor c = db.rawQuery("SELECT " + this.CONTACT_NAME + ", " + this.CONTACT_NUMBER + " FROM " + this.TABLE_NAME, null);
//        if(c.moveToFirst()) {
//            do {
//                Contact contact = new Contact();
//                contact.setName(c.getString(c.getColumnIndex(this.CONTACT_NAME)));
//                contact.setPhoneNumber(c.getString(c.getColumnIndex(this.CONTACT_NUMBER)));
//                contactList.add(contact);
//            } while (c.moveToNext());
//        }
//        c.close();
//        return contactList;
//    }

    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contactList = new ArrayList<>();
        db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT " + this.CONTACT_NAME + ", " + this.CONTACT_NUMBER + " FROM " + this.TABLE_NAME, null);
//        if(c.moveToFirst()) {
//            do {
//                Contact contact = new Contact();
//                contact.setName(c.getString(c.getColumnIndex(this.CONTACT_NAME)));
//                contact.setPhoneNumber(c.getString(c.getColumnIndex(this.CONTACT_NUMBER)));
//                contactList.add(contact);
//            } while (c.moveToNext());
//        }
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                String name = c.getString(c.getColumnIndex(this.CONTACT_NAME));
                String phoneNumber = c.getString(c.getColumnIndex(this.CONTACT_NUMBER));
                contactList.add(new Contact(name, phoneNumber));
                System.out.println(name+"|||||||||||-------------|||||||||||||"+phoneNumber);
                c.moveToNext();
            }
        }
        c.close();
        return contactList;
    }




}
