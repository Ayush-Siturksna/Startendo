package com.example.startendo;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;


public class ApplicationClass extends Application {


    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
