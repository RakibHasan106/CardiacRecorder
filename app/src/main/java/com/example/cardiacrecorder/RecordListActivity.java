package com.example.cardiacrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
/**
 * activity for viewing the  records
 * */
public class RecordListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Record> list;
    DatabaseReference databaseReference;
    RecordAdapter adapter;

    String usrname;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            usrname = bundle.getString("username");
        }

        mAuth = FirebaseAuth.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recyclerViewId);
        databaseReference = FirebaseDatabase.getInstance().getReference("Records").child(uid);
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
                Intent intent = new Intent(RecordListActivity.this,EditRecordActivity.class);
                intent.putExtra("record", (Parcelable) record);
                intent.putExtra("username",usrname);
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

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RecordListActivity.this,HomePage.class);
        intent.putExtra("username",usrname);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}