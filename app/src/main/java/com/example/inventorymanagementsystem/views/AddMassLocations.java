package com.example.inventorymanagementsystem.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.inventorymanagementsystem.R;
import com.example.inventorymanagementsystem.adapters.CustomSpinnerAdapter;
import com.example.inventorymanagementsystem.models.Location;
import com.example.inventorymanagementsystem.models.Users;
import com.example.inventorymanagementsystem.models.Zone;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddMassLocations extends AppCompatActivity {
    private FirebaseFirestore db;
    private static Users mCurrentUser;
    private String mWarehouse;
    private Spinner spinnerZones;
    private EditText edtLength, edtLpLimit, edtWeight, edtHeight, edtWidth, edtStartingAisle, edtEndingAisle, edtStartingRack, edtEndingRack, edtStartingLevel, edtEndingLevel;

    private boolean locationExists = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mass_locations);

        // prevents users from rotating screen
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // change action support bar title and font color
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Inventory</font>"));

        //Initializing firebase
        db = FirebaseFirestore.getInstance();

        // Initializing widgets
        edtStartingAisle = findViewById(R.id.edtAddMassLocationsStartingAisle);
        edtEndingAisle = findViewById(R.id.edtAddMassLocationsEndingAisle);

        edtStartingRack = findViewById(R.id.edtAddMassLocationsStartingRack);
        edtEndingRack = findViewById(R.id.edtAddMassLocationsEndingRack);

        edtStartingLevel = findViewById(R.id.edtAddMassLocationsStartingLevel);
        edtEndingLevel = findViewById(R.id.edtAddMassLocationsEndingLevel);

        edtLpLimit = findViewById(R.id.edtAddMassLocationsLpLimit);
        edtWeight = findViewById(R.id.edtAddMassLocationsWeight);
        edtHeight = findViewById(R.id.edtAddMassLocationsHeight);
        edtWidth = findViewById(R.id.edtAddMassLocationsWidth);
        edtLength = findViewById(R.id.edtAddMassLocationsLength);

        spinnerZones = findViewById(R.id.spinnerAddMassLocationsZone);

        Button btnAddLocation = findViewById(R.id.btnAddMassLocationsSubmit);

        // Spinner properties
        String defaultTextForSpinner = "Zone";
        List<String> listZones= new ArrayList<>();


        // getting intent from previous activities
        Intent i = getIntent();
        mCurrentUser = (Users)i.getSerializableExtra("User");
        mWarehouse = (String) i.getSerializableExtra("Warehouse");

        btnAddLocation.setOnClickListener(v -> {

            if(edtStartingAisle.getText().toString().isEmpty()) {
                edtStartingAisle.setError("Location must contain at least Aisle property");
            }
            if(edtStartingRack.getText().toString().isEmpty()) {
                edtStartingRack.setError("Location must contain at least Rack property");
            }
            if(edtStartingLevel.getText().toString().isEmpty()) {
                edtStartingLevel.setError("Location must contain at least Level property");
            }
            if(spinnerZones.getSelectedItem().toString().equals("Zone")) {
                Toast.makeText(AddMassLocations.this, "Zone is empty", Toast.LENGTH_SHORT).show();
            }
            if(!edtStartingAisle.getText().toString().isEmpty()
                    && !edtStartingRack.getText().toString().isEmpty()
                    && !edtStartingLevel.getText().toString().isEmpty()
                    && !spinnerZones.getSelectedItem().toString().equals("Zone")) {

                addDataToFireStore(edtStartingAisle.getText().toString(), edtEndingAisle.getText().toString(),
                        edtStartingRack.getText().toString(), edtEndingRack.getText().toString(),
                        edtStartingLevel.getText().toString(), edtEndingLevel.getText().toString());
            }
        });

        CollectionReference unitTypeRef = db.collection("Warehouses/"+mWarehouse+"/Zones");

        unitTypeRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty())
                {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot d : list) {
                        String zoneName = d.getId();
                        listZones.add(zoneName);
                    }

                    String[] arrayForSpinner = new String[listZones.size()];
                    for(int i = 0; i < listZones.size(); i++) {
                        arrayForSpinner[i] = listZones.get(i);
                    }
                    spinnerZones.setAdapter(new CustomSpinnerAdapter(AddMassLocations.this, R.layout.spinner_row, arrayForSpinner, defaultTextForSpinner));
                } else {
                    Toast.makeText(AddMassLocations.this, "No data found in Database", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddMassLocations.this, "Failed to get data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addDataToFireStore(String _startingAisle,
                                    String _endingAisle, String _startingRack,
                                    String _endingRack, String _startingLevel, String _endingLevel) {

        String[] startingPart = _startingLevel.split("(?<=\\D)(?=\\d)");
        String[] endingPart = _endingLevel.split("(?<=\\D)(?=\\d)");

        boolean zeroPrefixOnAisle = false;
        boolean zeroPrefixOnRack = false;
        boolean zeroPrefixOnLevel = false;

        String startingLetter = "";
        String startingNumber;
        String endingLetter = "";
        String endingNumber;

        char c = _startingLevel.trim().charAt(0);
        char c2 = _endingLevel.trim().charAt(0);

        if(c >= 'A' && c <= 'Z') {
            startingLetter = startingPart[0].trim();
            startingNumber = startingPart[1];
        } else {
            startingNumber = startingPart[0];
        }

        if(c2 >= 'A' && c2 <= 'Z') {
            endingLetter = endingPart[0].trim();
            endingNumber = endingPart[1];
        } else {
            endingNumber = endingPart[0];
        }

        if(_startingAisle.startsWith("0")) {
            zeroPrefixOnAisle = true;
        }
        if(_startingRack.startsWith("0")) {
            zeroPrefixOnRack = true;
        }
        if (startingNumber.startsWith("0")) {
            zeroPrefixOnLevel = true;
        }

        int currentAisle = Integer.parseInt(_startingAisle);

        do {
            int currentRack = Integer.parseInt(_startingRack);
            for(int j = 0; j < Integer.parseInt(_endingRack); j++) {
                int currentLevel = Integer.parseInt(startingNumber);
                for(int k = 0; k < Integer.parseInt(endingNumber); k++) {
                    if(!startingLetter.isEmpty()) {
                        Location location = new Location();

                        if(!zeroPrefixOnAisle)
                        {
                            location.setAisle(String.valueOf(currentAisle));
                        } else {
                            location.setAisle("0"+ currentAisle);
                        }

                        if(!zeroPrefixOnRack) {
                            location.setRack(String.valueOf(currentRack));
                        } else {
                            location.setRack("0"+currentRack);
                        }

                        if(!zeroPrefixOnLevel) {
                            location.setLevel((startingLetter+(currentLevel)));
                        } else {
                            location.setLevel(startingLetter+"0"+currentLevel);
                        }

                        location.setZone(spinnerZones.getSelectedItem().toString());
                        location.setLpLimit(edtLpLimit.getText().toString());
                        location.setWeight(edtWeight.getText().toString());
                        location.setHeight(edtHeight.getText().toString());
                        location.setWidth(edtWidth.getText().toString());
                        location.setLength(edtLength.getText().toString());
                        location.setCanBeDeleted(true);
                        location.setHasInventory(false);
                        location.setStatus("OPEN");
                        location.setName(location.getAisle()+"-"+location.getRack()+"-"+location.getLevel());

                        addLocationToDatabase(location);

                    } else {

                        Location location = new Location();

                        if(!zeroPrefixOnAisle)
                        {
                            location.setAisle(String.valueOf(currentAisle));
                        } else {
                            location.setAisle("0"+ currentAisle);
                        }

                        if(!zeroPrefixOnRack) {
                            location.setRack(String.valueOf(currentRack));
                        } else {
                            location.setRack("0"+currentRack);
                        }

                        if(!zeroPrefixOnLevel) {
                            location.setLevel((startingLetter+(currentLevel)));
                        } else {
                            location.setLevel(startingLetter+"0"+currentLevel);
                        }

                        location.setZone(spinnerZones.getSelectedItem().toString());
                        location.setLpLimit(edtLpLimit.getText().toString());
                        location.setWeight(edtWeight.getText().toString());
                        location.setHeight(edtHeight.getText().toString());
                        location.setWidth(edtWidth.getText().toString());
                        location.setLength(edtLength.getText().toString());
                        location.setCanBeDeleted(true);
                        location.setHasInventory(false);
                        location.setStatus("OPEN");
                        location.setName(location.getAisle()+"-"+location.getRack()+"-"+location.getLevel());

                        addLocationToDatabase(location);

                    }
                    currentLevel++;
                }
                currentRack++;
            }
            currentAisle++;
        } while(currentAisle <= Integer.parseInt(_endingAisle));
        finish();
    }

    private void addLocationToDatabase(Location _location) {

        CollectionReference collectionReference = db.collection("Warehouses/" + mWarehouse + "/Locations");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                boolean locationExists = false;
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    if (d.getId().equals(_location.getName())) {
                        locationExists = true;
                        Toast.makeText(AddMassLocations.this, "Location exists", Toast.LENGTH_LONG).show();
                    }
                }
                if(!locationExists) {
                    db.collection("Warehouses/"+mWarehouse+"/Locations").document(_location.getName()).set(_location).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            db.collection("Warehouses/"+mWarehouse+"/Zones").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(!queryDocumentSnapshots.isEmpty()) {
                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                        for(DocumentSnapshot d : list) {
                                            if(d.getId().equals(_location.getZone())) {
                                                Zone z = d.toObject(Zone.class);
                                                if(z.getCanBeDeleted()){
                                                    z.setCanBeDeleted(false);
                                                    db.collection("Warehouses/"+mWarehouse+"/Zones").document(z.getZoneId()).set(z).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }

}