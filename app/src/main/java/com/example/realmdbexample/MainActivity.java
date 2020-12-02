package com.example.realmdbexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.realmdbexample.Adapter.CustomAdapter;
import com.example.realmdbexample.Interfaces.IClickListener;
import com.example.realmdbexample.Model.PersonTable;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements IClickListener {

    private Realm realm;
    private RecyclerView recyclerView;
    private EditText userNameEditText, userSurnameEditText, userAgeEditText, userDepartmentEditText;
    private Button addPersonButton, dismissButton;
    private CustomAdapter customAdapter;
    private List<PersonTable> personTables = new ArrayList<>();
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        Init();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        customAdapter = new CustomAdapter(getApplicationContext(),personTables,MainActivity.this);
        recyclerView.setAdapter(customAdapter);

        refreshList();
    }

    private void Init() {
        userNameEditText = (EditText)findViewById(R.id.personNameEdit);
        userSurnameEditText = (EditText)findViewById(R.id.personSurnameEdit);
        userDepartmentEditText = (EditText)findViewById(R.id.personDepartmentEdit);
        userAgeEditText = (EditText)findViewById(R.id.personAgeEdit);
        addPersonButton = (Button)findViewById(R.id.addBttn);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
    }

    public void addPerson(View view) {
        if(addPersonButton.getText().toString().equalsIgnoreCase("KAYDET")){
            if(checkFields()){

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        Number maxId = bgRealm.where(PersonTable.class).max("id");
                        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                        PersonTable personTable = bgRealm.createObject(PersonTable.class,nextId);
                        personTable.setName(userNameEditText.getText().toString());
                        personTable.setSurname(userSurnameEditText.getText().toString());
                        personTable.setDepartment(userDepartmentEditText.getText().toString());
                        personTable.setAge(Integer.parseInt(userAgeEditText.getText().toString()));
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Kayıt eklendi.", Toast.LENGTH_SHORT).show();
                        refreshList();
                        clearAllFields();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else{
                Toast.makeText(MainActivity.this, "Zorunlu alanları giriniz!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if(checkFields()){
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        RealmResults<PersonTable> realmResults = Realm.getDefaultInstance().where(PersonTable.class).findAll();
                        final PersonTable updateTable = realmResults.get(pos);
                        updateTable.setName(userNameEditText.getText().toString());
                        updateTable.setSurname(userSurnameEditText.getText().toString());
                        updateTable.setDepartment(userDepartmentEditText.getText().toString());
                        updateTable.setAge(Integer.parseInt(userAgeEditText.getText().toString()));
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Kayıt güncellendi.", Toast.LENGTH_SHORT).show();
                        refreshList();
                        clearAllFields();
                        addPersonButton.setText("Kaydet");
                        dismissButton.setVisibility(View.GONE);
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void clearAllFields() {
        userNameEditText.setText("");
        userSurnameEditText.setText("");
        userDepartmentEditText.setText("");
        userAgeEditText.setText("");
        userNameEditText.setText("");
        userNameEditText.setText("");
        userNameEditText.setText("");
    }

    private void refreshList() {
        RealmResults<PersonTable> realmResults = realm.where(PersonTable.class).findAll();
        personTables.clear();
        for(PersonTable personTable: realmResults){
            personTables.add(personTable);
        }
        customAdapter.notifyDataSetChanged();
    }

    private boolean checkFields() {
        if(userNameEditText.getText().toString().length()>0 && userSurnameEditText.getText().toString().length()>0 &&
                userAgeEditText.getText().toString().length()>0  && userDepartmentEditText.getText().toString().length()>0 ){
            return true;
        }
        return false;
    }

    private void fillAllFields(String name,String surname, String department, String age){
        userNameEditText.setText(name);
        userSurnameEditText.setText(surname);
        userDepartmentEditText.setText(department);
        userAgeEditText.setText(age);
    }

    @Override
    public void onMenuClick(View view, final int position) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), view);
        popup.inflate(R.menu.menu_item);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editPersonItem:
                        updatePerson(position);
                        break;
                    case R.id.deletePersonItem:
                        deletePerson(position);
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    private void updatePerson(int position) {
        RealmResults<PersonTable> realmResults = realm.where(PersonTable.class).findAll();
        final PersonTable updateTable = realmResults.get(position);
        fillAllFields(updateTable.getName(),updateTable.getSurname(),updateTable.getDepartment(),String.valueOf(updateTable.getAge()));
        addPersonButton.setText("Güncelle");
        dismissButton.setVisibility(View.VISIBLE);
        this.pos = position;
    }

    private void deletePerson(final int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PersonTable personTable = personTables.get(position);
                personTable.deleteFromRealm();
                refreshList();
            }
        });
    }
}