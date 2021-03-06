package com.example.profiscooter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileDashboard extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private ImageButton logout, goDashBoard, goFindMap;

    RecyclerView recyclerViewHistory;
    DatabaseReference historyReference;
    HistoryAdapter myHistoryAdapter;
    ArrayList listHistory;
    ImageButton buttonRemoveHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_dashboard);

        logout = (ImageButton) findViewById(R.id.signOut);
        goDashBoard = (ImageButton) findViewById(R.id.goDashBoard);
        buttonRemoveHistory = (ImageButton) findViewById(R.id.buttonRemoveHistory);
        goFindMap = (ImageButton) findViewById(R.id.goFindMap);

        buttonRemoveHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                historyReference.child("trips").removeValue();
                getHistory();
            }
        });

        goDashBoard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileDashboard.this, ScooterDashboard.class));
            }
        });

        goFindMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileDashboard.this, MapsActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileDashboard.this, MainActivity.class));
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView welcomeTextView = (TextView) findViewById(R.id.welcome);
        final TextView nickTextView = (TextView) findViewById(R.id.nick);
        final TextView emailTextView = (TextView) findViewById(R.id.emailAddress);
        final TextView ageTextView = (TextView) findViewById(R.id.age);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null) {


                    String nick = userProfile.nick;
                    String email = userProfile.email;
                    String age = userProfile.age;


                    welcomeTextView.setText("Welcome, " + nick + "!");
                    emailTextView.setText(email);
                    ageTextView.setText(age);

                    getHistory();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileDashboard.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });


    //HISTORY LIST



    }

    private void getHistory() {
        recyclerViewHistory = findViewById(R.id.historyList);
        historyReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("trips");
        recyclerViewHistory.setHasFixedSize(true);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        listHistory = new ArrayList<>();
        myHistoryAdapter = new HistoryAdapter(this,listHistory);
        recyclerViewHistory.setAdapter(myHistoryAdapter);
        historyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Trip trip = dataSnapshot.getValue(Trip.class);
                    listHistory.add(trip);
                }
                myHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
