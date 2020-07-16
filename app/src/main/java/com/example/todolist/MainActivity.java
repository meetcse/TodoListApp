package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView textView;
    Button addButton;
    DatabaseHelper myDB;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayList<String> listId = new ArrayList<>();


    FloatingActionButton fab;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fab = findViewById(R.id.add);
        myDB = new DatabaseHelper(this);

        listView = findViewById(R.id.list);


        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, listItems);
        listView.setAdapter(arrayAdapter);

        viewAll();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Add", 0, 0);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                showDialog("Update", position, Integer.parseInt(listId.get(position)));
            }
        });

    }

    public void viewAll() {
        Cursor cursor = myDB.getAllData();
        if (cursor.getCount() == 0) {
            return;
        }

        listId.clear();
        listItems.clear();
        while (cursor.moveToNext()) {

            listItems.add(cursor.getString(1));
            listId.add(cursor.getString(0));


        }

        arrayAdapter.notifyDataSetChanged();

    }


    public void showDialog(final String buttonName, final int id, final int databaseId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your ToDo");
        final View view = getLayoutInflater().inflate(R.layout.activitydialog, null);

        final EditText editText = view.findViewById(R.id.yourtodo);


        //Add Todo
        if (buttonName.equals("Add")) {
            builder.setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {


                    if (editText.getText().toString().isEmpty()) {
                        editText.setError("Enter Your TODO First");
                        Toast.makeText(MainActivity.this, "ADD Your Todo First", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    boolean check = myDB.insertData(editText.getText().toString());
                    if (check) {

                        viewAll();


                    } else {
                        Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }

                }


            });
        }

        //Update Code
        if (buttonName.equals("Update")) {
            editText.setText(listItems.get(id));

            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (editText.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this, "ADD Your Todo First", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }

                    boolean check = myDB.updateData(databaseId, editText.getText().toString());
                    if (check) {
                        Toast.makeText(MainActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();

                        viewAll();

                    }


                }
            });


            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Integer check = myDB.deleteData(databaseId);
                    if (check > 0) {
                        listItems.remove(id);
                        listId.remove(id);
                        arrayAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Delete Successful", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity.this, "Delete Error", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

        builder.setIcon(R.drawable.pen);


        builder.setView(view);


        builder.show();

    }


}
