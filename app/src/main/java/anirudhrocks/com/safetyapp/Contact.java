package anirudhrocks.com.safetyapp;

public class Contact {
    private String name;
    private String phoneNumber;

    public Contact(String Name, String phoneNumber) {
        this.name = Name;
        this.phoneNumber = phoneNumber;
    }

    public Contact() {}

    public void setName(String name) {
        name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
