package com.example.cardiacrecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecordListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Record> list;
    DatabaseReference databaseReference;
    RecordAdapter adapter;

    String usrname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        usrname = HomePage.usrname;

        recyclerView = findViewById(R.id.recyclerViewId);
        databaseReference = FirebaseDatabase.getInstance().getReference("Records").child(usrname);
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecordAdapter(this,list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecordAdapter.ClickListener() {
            @Override
            public void onItemClick(int position) {
                Record record = list.get(position);
                String key = record.getKey();
                databaseReference.child(key).removeValue();

                list.remove(position);

                adapter.notifyItemRemoved(position);
            }
        });

        adapter.setEditClickListener(new RecordAdapter.EditClickListener() {
            @Override
            public void onEditClick(Record record) {
//                Record record = list.get(position);
//                heart_rate_editText.setText(record.getHeart_rt());
//                diastolic_pressure_editText.setText(record.getDia_press());
//                systolic_pressure_editText.setText(record.getSys_press());
//                date_editText.setText(record.getDate());
//                time_editText.setText(record.getTime());
//                comment_edittext.setText(record.getComment());
//                key = record.getKey();
                Intent intent = new Intent(RecordListActivity.this,EditRecordActivity.class);
                intent.putExtra("record", (Parcelable) record);
                startActivity(intent);
            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Record record = dataSnapshot.getValue(Record.class);
                    list.add(record);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        adapter.setOnItemClickListener(new RecordAdapter.ClickListener() {
//            @Override
//            public void onItemClick(int position, View v) {
//                Toast.makeText(recordlist.this, "onitem clicked", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(recordlist.this,MainActivity.class));
//            }
//        });
    }
}