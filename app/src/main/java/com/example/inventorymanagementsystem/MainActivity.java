package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // creating variables for button
    private Button userList;
    // member fields for logged user
    private String mEmail, mUserKey;

    private static Users currentUser;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // prevents users from rotating screen
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // instantiating firebase firestore database object.
        // firebase firestore database object
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // initializing our buttons
        userList = findViewById(R.id.idBtnListUsers);

        // initializing FirebaseUser
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            // Name, email address, and profile photo Url
            // String name = user.getDisplayName();
            mEmail = user.getEmail();
            // Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            // boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            mUserKey = user.getUid();
        }
        CollectionReference adminRef = db.collection("Users");

        adminRef.whereEqualTo("userKey", mUserKey).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    List<DocumentSnapshot> list = value.getDocuments();
                    for (DocumentSnapshot d : list) {
                        currentUser = d.toObject(Users.class);
                    }
                } else {
                    Log.d("Error ", "Exception: " + error);
                }
//                if (!currentUser.getIsAdmin()) {
//                    userList.setVisibility(View.GONE);
//                } else if (currentUser.getIsAdmin()) {
//                    userList.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //Intent i = new Intent(MainActivity.this, );
//                        }
//                    });
//                }
            }
        });

        // Changing title of the action bar and color
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>WAREHOUSE 1</font>"));
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     *
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.idMenuProducts);
        menu.findItem(R.id.idMenuInventory);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.idMenuCompanyDetails:
                break;
            case R.id.idMenuUsers:
                break;
            case R.id.idMenuSystems:
                break;
            case R.id.idMenuProducts:
                Intent i = new Intent(MainActivity.this, ItemsSubMenu.class);
                i.putExtra("User", currentUser);
                startActivity(i);
                break;
            case R.id.idMenuOrders:
                break;
            case R.id.idMenuReceiving:
                break;
            case R.id.idMenuInventory:
                break;
            case R.id.idMenuLocations:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}