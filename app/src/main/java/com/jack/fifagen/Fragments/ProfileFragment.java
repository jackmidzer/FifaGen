package com.jack.fifagen.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jack.fifagen.Activities.MainActivity;
import com.jack.fifagen.Adapters.AdapterMatch;
import com.jack.fifagen.Models.ModelMatch;
import com.jack.fifagen.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private RecyclerView recyclerView;
    private AdapterMatch adapterMatch;
    private List<ModelMatch> matchList;

    //views
    private ImageView avatarIv, coverIv;
    private TextView nameTv, emailTv, phoneTv;
    private FloatingActionButton editFab;

    //progress dialog
    private ProgressDialog progressDialog;

    //permissions
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //uri of picked image
    private Uri image_uri;

    //for checking if profile or cover photo
    private String profileOrCoverPhoto;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        storageReference = getInstance().getReference();

        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        avatarIv = view.findViewById(R.id.avatarId);
        coverIv = view.findViewById(R.id.coverId);
        nameTv = view.findViewById(R.id.nameId);
        emailTv = view.findViewById(R.id.emailId);
        phoneTv = view.findViewById(R.id.phoneId);
        editFab = view.findViewById(R.id.editId);

        //init recycler view
        recyclerView = view.findViewById(R.id.matches_recyclerViewId);
        //set its properties
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //init progress dialog
        progressDialog = new ProgressDialog(getActivity());


        //query firebase database
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get data from database
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String avatar = "" + ds.child("avatar").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    //set data into the views
                    if (!name.isEmpty()) {
                        nameTv.setText(name);
                    }
                    emailTv.setText(email);
                    if (!phone.isEmpty()) {
                        phoneTv.setText(phone);
                    }
                    if (!avatar.isEmpty()) {
                        try {
                            //set the profile picture
                            Picasso.get().load(avatar).placeholder(R.drawable.ic_default_img_white).into(avatarIv);
                        } catch (Exception e) {
                            //set a default image
                            Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
                        }
                    }
                    if (!cover.isEmpty()) {
                        try {
                            //set the cover photo
                            Picasso.get().load(cover).into(coverIv);
                        } catch (Exception e) {
                            //set a default image
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //handle editFab button click
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        checkUserStatus();

        //init recycler view
        recyclerView = view.findViewById(R.id.matches_recyclerViewId);
        //set its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init match list
        matchList = new ArrayList<>();
        //get all users
        getAllMatches();

        return view;
    }

    private void getAllMatches() {
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = database.getReference("Matches");

        //get all data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelMatch modelMatch = ds.getValue(ModelMatch.class);

                    //get all matches for currently signed in user
                    if ((modelMatch.getHomeUid().equals(user.getUid()) || modelMatch.getAwayUid().equals(user.getUid())) && modelMatch.getIsApproved().equals("approved")) {
                        matchList.add(modelMatch);
                    }
                    Collections.reverse(matchList);

                    //adapter
                    adapterMatch = new AdapterMatch(getActivity(), matchList);
                    adapterMatch.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterMatch);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchMatches(final String query) {
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = database.getReference("Matches");

        //get all data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelMatch modelMatch = ds.getValue(ModelMatch.class);

                    //get all searched matches of currently signed in user
                    if (modelMatch != null && (modelMatch.getHomeUid().equals(user.getUid()) || modelMatch.getAwayUid().equals(user.getUid()))) {
                        if (modelMatch.getAwayPlayer().toLowerCase().contains(query.toLowerCase()) || modelMatch.getHomePlayer().toLowerCase().contains(query.toLowerCase()) || modelMatch.getHomeTeam().toLowerCase().contains(query.toLowerCase()) || modelMatch.getAwayTeam().toLowerCase().contains(query.toLowerCase())) {
                            matchList.add(modelMatch);
                        }
                    }

                    //adapter
                    adapterMatch = new AdapterMatch(getActivity(), matchList);
                    //refresh adapter
                    adapterMatch.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterMatch);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        //check if storage permission is enabled or not
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        //request runtime storage permission
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
        //options to show in dialog
        String[] options = {"Edit Profile Picture", "Edit Cover Photo", "Edit Name", "Edit Phone"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0) {
                    //edit profile picture
                    progressDialog.setMessage("Updating Profile Picture");
                    profileOrCoverPhoto = "avatar";
                    showImagePickDialog();
                }else if (which == 1) {
                    //edit cover photo
                    progressDialog.setMessage("Updating Cover Photo");
                    profileOrCoverPhoto = "cover";
                    showImagePickDialog();
                }else if (which ==2) {
                    //edit name
                    progressDialog.setMessage("Updating Name");
                    showNamePhoneUpdateDialog("name"); //uses database key: name
                }else if (which == 3) {
                    //edit phone
                    progressDialog.setMessage("Updating Phone");
                    showNamePhoneUpdateDialog("phone"); //uses database key: phone
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(final String key) {
        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update " + key);

        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        //add edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter " + key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add update button in dialog
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                String value = editText.getText().toString().trim();
                //validate if user has entered something or not
                if (!TextUtils.isEmpty(value)) {
                    progressDialog.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //updated, dismiss progress dialog
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed, dismiss progress dialog, get and show error message
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "ProfileFragment <1>: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //update name in stats if changed

        //add cancel button in dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void showImagePickDialog() {
        //options to show in the dialog to choose the image from i.e. Camera and Gallery
        String[] options = {"Camera", "Gallery"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image From");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0) {
                    //choose from camera
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }
                }else if (which == 1) {
                    //choose from gallery
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //handle allow or deny permissions
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                //choosing from camera
                //check if permissions are allowed
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        //permissions enabled
                        pickFromCamera();
                    }else {
                        //permissions denied
                        Toast.makeText(getActivity(), "Please enable camera and storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                //choosing from gallery
                //check if permissions are allowed
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        //permissions enabled
                        pickFromGallery();
                    }else {
                        //permissions denied
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //called after picking image
        if (resultCode == RESULT_OK){
                if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                    //image was picked from camera, get uri
                    uploadProfileAndCoverPhoto(image_uri);
                }
                if (requestCode ==IMAGE_PICK_GALLERY_CODE) {
                    //image was picked from gallery, get uri
                    image_uri = data.getData();
                    uploadProfileAndCoverPhoto(image_uri);
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileAndCoverPhoto(Uri uri) {
        //show progress bar
        progressDialog.show();

        //path and name of image to be stored in firebase storage
        String storagePath = "Users_Profile_Cover_Imgs/";
        String filePathName = storagePath + "" + profileOrCoverPhoto + "" + user.getUid();

        StorageReference storageReference1 = storageReference.child(filePathName);
        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image uploaded to storage, store its uri in users database
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                //check if image is uploaded or not and url is receivec
                if (uriTask.isSuccessful()) {
                    //image uploaded
                    //add/update url in users database
                    HashMap<String, Object> results = new HashMap<>();
                    results.put(profileOrCoverPhoto, downloadUri.toString());

                    databaseReference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //url in database of user is added successfully
                            //dismiss progress bar
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Image Updated...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //error adding url in database of user
                            //dismiss progress bar
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Error Updating Image...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //some error(s), get and show error message, dismiss progress dialog
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "ProfileFragment <2>: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickFromCamera() {
        //choose from camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //start camera
        Intent cameraIntent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //choose from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            //user not signed in, go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // to show menu options in fragment
        super.onCreate(savedInstanceState);
    }

    //inflate options menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        //search view
        MenuItem item = menu.findItem(R.id.searchId);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //if search query not empty then search
                if (!TextUtils.isEmpty(s.trim())) {
                    searchMatches(s);
                }else {
                    getAllMatches();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //if search query not empty then search
                if (!TextUtils.isEmpty(s.trim())) {
                    searchMatches(s);
                }else {
                    getAllMatches();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    //handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
