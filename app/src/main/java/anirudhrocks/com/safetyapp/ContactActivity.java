package anirudhrocks.com.safetyapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

//import android.support.v7.app.AppCompatActivity;
//-----------------------

public class ContactActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText contactName;
    private EditText contactNumber;
    private Button addBtn;
    private Button clearBtn;

    private ListView contactList;

    private ArrayList<String> displayList;
    private ArrayAdapter<String> adapter;
    private ArrayList<Contact> storedContacts;
    private ContactDbHelper dbHelper;

//    private ContactListAdapter adapter;

    public int counter;
    KeyEvent keyEvent;
    int key;
    //int n=0;
    String dir;
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    String latitude, longitude;


    TextView text;

    Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        contactName = findViewById(R.id.contact_name);
        contactNumber = findViewById(R.id.contact_number);
        addBtn = findViewById(R.id.add_btn);
        clearBtn = findViewById(R.id.clear_btn);

        contactList = findViewById(R.id.contact_list);

        dbHelper = new ContactDbHelper(this);
        // getAllContacts here.
//        storedContacts = FileHelper.readData(this);
//        storedContacts = new ArrayList<>();
        System.out.println("======================" + dbHelper.getAllContacts().size() + "=========================");
        storedContacts = dbHelper.getAllContacts();
        for (Contact contact : dbHelper.getAllContacts()) {
            System.out.println("======================" + contact.getName() + "=========================");
            System.out.println("======================" + contact.getPhoneNumber() + "=========================");
        }


        System.out.println("||||||||||||||||||||||||||_________" + storedContacts.size());
        displayList = new ArrayList<>();
        if (storedContacts != null && !storedContacts.isEmpty()) {
            for (Contact contact : storedContacts) {
                displayList.add(contact.getName());
            }
        }
        System.out.println("||||||||||||||||||||||||||_________" + displayList.size());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        if (displayList != null && !displayList.isEmpty()) {
            contactList.setAdapter(adapter);
        }

        addBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);

        // to delete an item
        contactList.setOnItemClickListener(this);

        
        //startService(new Intent(this, VolumeKeyUp.class));
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                String contactNameEntered = contactName.getText().toString();
                String contactNumberEntered = contactNumber.getText().toString();

                if (contactNameEntered.isEmpty() || contactNumberEntered.isEmpty()) {
                    Toast.makeText(this, "Contact Name or Number cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (isExist(contactNameEntered, contactNumberEntered, storedContacts)) {
                    Toast.makeText(this, "Contact with same data already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    contactList.setAdapter(adapter);
                    adapter.add(contactNameEntered);

                    System.out.println("==========" + contactNameEntered + "---------" + contactNumberEntered);

                    // here addContact method.
                    Contact newContact = new Contact(contactNameEntered, contactNumberEntered);
                    dbHelper.addContact(newContact);
                    storedContacts.add(newContact);
//                    FileHelper.writeData(storedContacts, this);
                    Toast.makeText(this, "Contact Added Successfully", Toast.LENGTH_SHORT).show();
                    toggleAddBtn(storedContacts);
                }
                break;

            case R.id.clear_btn:
                contactName.setText("");
                contactNumber.setText("");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String nameToBeRemoved = displayList.get(position);
        System.out.println("-----------------" + nameToBeRemoved + "--------------" + storedContacts.size());
        if (storedContacts != null && !storedContacts.isEmpty()) {
            for (Contact contact : storedContacts) {
                System.out.println("|||||||||||||||||||||||||||||||||||--------------------------------------" + contact.getName());

                if (nameToBeRemoved.equals(contact.getName())) {
                    System.out.println("-------------------------------Delete--------------------------------------");
                    storedContacts.remove(contact);
                    dbHelper.removeContact(contact);
                    // removeContact here.
                    break;
                }
            }
        }

        displayList.remove(position);
        toggleAddBtn(storedContacts);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Contact Deleted Successfully", Toast.LENGTH_SHORT).show();
    }

    public void toggleAddBtn(ArrayList<Contact> storedContacts) {
        if (storedContacts != null && !storedContacts.isEmpty() && storedContacts.size() >= 5) {
            addBtn.setEnabled(false);
            Toast.makeText(this, "Limit of Contacts full", Toast.LENGTH_LONG).show();
        } else if (storedContacts != null && !storedContacts.isEmpty() && storedContacts.size() < 5) {
            addBtn.setEnabled(true);
        }
    }

    private boolean isExist(String name, String phoneNumber, ArrayList<Contact> storedContacts) {
        if (storedContacts != null && !storedContacts.isEmpty()) {
            for (Contact contact : storedContacts) {
                return (name.equals(contact.getName()) && phoneNumber.equals(contact.getPhoneNumber()));
            }
        }
        return false;
    }

    // while adding see if name and number already exists.

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        text = findViewById(R.id.text1);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

            String phoneNumber = "9920073998";
            try {
                SmsManager.getDefault().sendTextMessage(phoneNumber, null, "Please Help Me SMS! http://maps.google.com/maps?q=\"", null, null);
//              SmsManager.getDefault().sendTextMessage(phoneNumber, null, "Please Help Me SMS! http://maps.google.com/maps?q=\"" + geoLocation, null, null);
                //SmsManager.sendTextMessage(buffer.toString(), null, "I am in a emergency and I need HELP!\nI am currenty not able to provide more information.\ni am at : http://maps.google.com/maps?q=" + geoLocation, null, null);
            } catch (Exception e) {
                AlertDialog.Builder alertDialogBuilder = new
                        AlertDialog.Builder(ContactActivity.this);
                AlertDialog dialog = alertDialogBuilder.create();

                dialog.setMessage(e.getMessage());

                dialog.show();
            }

            count++;
            String file = dir + count + ".jpg";
            File newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }
            //Uri outputFileUri = Uri.fromFile(newfile);
            Uri outputFileUri = FileProvider.getUriForFile(ContactActivity.this, BuildConfig.APPLICATION_ID + ".provider", newfile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

            Toast.makeText(this, "Volume Up Pressed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return onKeyLongPress(keyCode, event);
    }

//send msg diff. code
    private void configureLocationSettings() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
//            Log.d("CameraDemo", "Pic saved");
//        }
//    }


    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}













/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");
        }
    }

lol

// Here, the counter will be incremented each time, and the
// picture taken by camera will be stored as 1.jpg,2.jpg
// and likewise.
                count++;
                        String file = dir+count+".jpg";
                        File newfile = new File(file);
                        try {
                        newfile.createNewFile();
                        }
                        catch (IOException e)
                        {
                        }

                        Uri outputFileUri = Uri.fromFile(newfile);

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE)


                         public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

                        */