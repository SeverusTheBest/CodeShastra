package anirudhrocks.com.safetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

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
        System.out.println("======================"+dbHelper.getAllContacts().size()+"=========================");
        storedContacts = dbHelper.getAllContacts();
        for(Contact contact : dbHelper.getAllContacts()) {
            System.out.println("======================"+contact.getName()+"=========================");
            System.out.println("======================"+contact.getPhoneNumber()+"=========================");
        }


        System.out.println("||||||||||||||||||||||||||_________"+storedContacts.size());
        displayList = new ArrayList<>();
        if(storedContacts != null && !storedContacts.isEmpty()) {
            for(Contact contact : storedContacts) {
                displayList.add(contact.getName());
            }
        }
        System.out.println("||||||||||||||||||||||||||_________"+displayList.size());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        if(displayList != null && !displayList.isEmpty()) {
            contactList.setAdapter(adapter);
        }

        addBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);

        // to delete an item
        contactList.setOnItemClickListener(this);
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

                    System.out.println("=========="+contactNameEntered+"---------"+contactNumberEntered);

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
        System.out.println("-----------------"+nameToBeRemoved+"--------------"+storedContacts.size());
        if(storedContacts != null && !storedContacts.isEmpty()) {
            for(Contact contact : storedContacts) {
                System.out.println("|||||||||||||||||||||||||||||||||||--------------------------------------"+contact.getName());

                if(nameToBeRemoved.equals(contact.getName())) {
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
        if(storedContacts != null && !storedContacts.isEmpty() && storedContacts.size() >= 5) {
            addBtn.setEnabled(false);
            Toast.makeText(this, "Limit of Contacts full", Toast.LENGTH_LONG).show();
        } else if(storedContacts != null && !storedContacts.isEmpty() && storedContacts.size() < 5) {
            addBtn.setEnabled(true);
        }
    }

    private boolean isExist(String name, String phoneNumber, ArrayList<Contact> storedContacts) {
        if(storedContacts != null && !storedContacts.isEmpty()) {
            for(Contact contact : storedContacts) {
                return (name.equals(contact.getName()) && phoneNumber.equals(contact.getPhoneNumber()));
            }
        }
        return false;
    }

    // while adding see if name and number already exists.
}
