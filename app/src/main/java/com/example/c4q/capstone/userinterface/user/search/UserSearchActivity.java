package com.example.c4q.capstone.userinterface.user.search;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.c4q.capstone.R;
import com.example.c4q.capstone.database.publicuserdata.PublicUser;
import com.example.c4q.capstone.database.publicuserdata.UserSearch;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.c4q.capstone.utils.Constants.FRIENDS;
import static com.example.c4q.capstone.utils.Constants.FRIEND_REQUESTS;
import static com.example.c4q.capstone.utils.Constants.NOTIFICATIONS;
import static com.example.c4q.capstone.utils.Constants.NOT_FRIENDS;
import static com.example.c4q.capstone.utils.Constants.RECEIVED;
import static com.example.c4q.capstone.utils.Constants.REQUEST_RECEIVED;
import static com.example.c4q.capstone.utils.Constants.REQUEST_SENT;
import static com.example.c4q.capstone.utils.Constants.REQUEST_TYPE;
import static com.example.c4q.capstone.utils.Constants.SENT;
import static com.example.c4q.capstone.utils.Constants.USER_FRIENDS;
import static com.example.c4q.capstone.utils.Constants.USER_SEARCH;

public class UserSearchActivity extends AppCompatActivity {
    private RecyclerView searchResultsList;
    private FirebaseAuth authentication;
    private DatabaseReference rootRef, searchUserDatabase, friendReqDatabase;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseUser currentUser;
    private String currentState, currentUserID, currentUserEmail;
    String requestedUserEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        rootRef = FirebaseDatabase.getInstance().getReference();
        searchUserDatabase = rootRef.child(USER_SEARCH);
        friendReqDatabase = rootRef.child(FRIEND_REQUESTS);


        linearLayoutManager = new LinearLayoutManager(this);

        authentication = FirebaseAuth.getInstance();
        currentUser = authentication.getCurrentUser();
        currentUserID = currentUser.getUid();
        currentUserEmail = currentUser.getEmail();

        searchResultsList = findViewById(R.id.search_users_rv);
        searchResultsList.setHasFixedSize(true);
        searchResultsList.setLayoutManager(linearLayoutManager);

        currentState = NOT_FRIENDS;

    }


    @Override
    protected void onStart() {
        super.onStart();
        callFirebaseAdapter();
    }

    private void callFirebaseAdapter() {
        FirebaseRecyclerAdapter<User, UserSearchViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<User, UserSearchViewHolder>(
                        User.class,
                        R.layout.search_user_itemview,
                        UserSearchViewHolder.class,
                        searchUserDatabase
                ) {
                    @Override
                    protected void populateViewHolder(UserSearchViewHolder viewHolder, User model, int position) {
                        viewHolder.setEmail(model.getEmail());

                        final String requestFriend = getRef(position).getKey();

                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                /**
                                 * need to fix logic. the email isn't showing up in
                                 * the database on the first click
                                 */

                                rootRef.child(USER_SEARCH).child(requestFriend).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserSearch userSearch = dataSnapshot.getValue(UserSearch.class);
                                        requestedUserEmail = userSearch.getEmail();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                sendRequest(requestFriend);
                            }
                        });

                        /**
                         * Everything commented out below to prevent NullPointerException errors.
                         * will uncomment as data is made available in database
                         */

//                        viewHolder.setFirst_name(model.getFirst_name());
//                        viewHolder.setLast_name(model.getLast_name());
//                        viewHolder.setUsername(model.getUsername());
                    }
                };

        searchResultsList.setAdapter(firebaseRecyclerAdapter);
    }

    private void sendRequest(final String requestedID) {

        /**
         *sends friend requests
         */

        if (currentState.equals(NOT_FRIENDS)) {

            searchUserDatabase = rootRef.child(requestedID).push();
            String newNotificationId = searchUserDatabase.getKey();


            HashMap<String, String> notificationData = new HashMap<>();
            notificationData.put("to", requestedUserEmail);
            notificationData.put("from", currentUserEmail);
            notificationData.put("type", FRIEND_REQUESTS);

            Map requestMap = new HashMap();
            requestMap.put(FRIEND_REQUESTS + "/" + currentUserID + "/" + requestedID + "/" + REQUEST_TYPE, SENT);
            requestMap.put(FRIEND_REQUESTS + "/" + requestedID + "/" + currentUserID + "/" + REQUEST_TYPE, RECEIVED);
            requestMap.put(NOTIFICATIONS + "/" + requestedID + "/" + newNotificationId, notificationData);

            rootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(UserSearchActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();
                    } else {
                        currentState = REQUEST_SENT;
                    }
                }
            });
        }

        /**
         *cancels pending friend requests
         */

        if (currentState.equals(REQUEST_SENT)) {

            friendReqDatabase.child(currentUserID).child(requestedID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void v) {

                    friendReqDatabase.child(requestedID).child(currentUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void v) {

                            currentState = NOT_FRIENDS;

                        }
                    });
                }
            });
        }

        /**
         *current user's pending friend requests
         */

        if (currentState.equals(REQUEST_RECEIVED)) {

            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

            Map friendsMap = new HashMap();
            friendsMap.put(USER_FRIENDS + "/" + currentUserID + "/" + requestedID + "/date", currentDate);
            friendsMap.put(USER_FRIENDS + "/" + requestedID + "/" + currentUserID + "/date", currentDate);

            friendsMap.put(FRIEND_REQUESTS + "/" + currentUserID + "/" + requestedID, null);
            friendsMap.put(FRIEND_REQUESTS + "/" + requestedID + "/" + currentUserID, null);

            rootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {

                        currentState = FRIENDS;

                    } else {

                        String error = databaseError.getMessage();

                        Toast.makeText(UserSearchActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        /**
         *removes friend from friend list
         */

        if (currentState.equals(FRIENDS)) {

            Map unfriendMap = new HashMap();
            unfriendMap.put(USER_FRIENDS + "/" + currentUserID + "/" + requestedID, null);
            unfriendMap.put(USER_FRIENDS + "/" + requestedID + "/" + currentUserID, null);

            rootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError == null) {

                        currentState = NOT_FRIENDS;

                    } else {

                        String error = databaseError.getMessage();
                        Toast.makeText(UserSearchActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


}