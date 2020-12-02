package com.example.realmdbexample.Apps;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().name("exampleapp.realm").deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(configuration);
    }
}
