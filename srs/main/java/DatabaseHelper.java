package com.example.chat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * the ones in ** are specific generated/user entered name
 * the ones not in ** are tags( child names ) int the database
 * methods follow the patterns of get.../set.../initialise...
 *      get methods return the reference to data so get the values by DataSnaphot.getValue()
 * <p>
 *                                    DataBase Structure
 * Chat Requests
 *              *UID*
 *              *Sender/Receiver ID*
 *              *Sent or Received*
 * Groups
 *          *Group Name*
 *          *Message ID*
 *                      date
 *                      message
 *                      name
 *                      time
 * Users
 *      *User ID*
 *                  name
 *                  uid
 *                  email
 *                  question
 *                  answer
 *                  contacts
 * LostPost
 *          *Post ID*
 *                      name
 *                      uid
 *                      category
 *                      tags
 *                      date
 *                      key
 *                      uploader_name
 * SellPost
 *          *Post ID*
 *                      name
 *                      uid
 *                      category
 *                      tags
 *                      price
 *                      key
 *                      uploader_name
 */


public class DatabaseHelper {

    //User
    /**
     * @return the current users's id
     */
    public static String getUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * @param uid UserID to get info from
     * @return name of the user with the specified key
     */
    public static String getName(String uid) {
        return FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .child("name").toString();
    }

    /**
     * @param uid UserID to get info from
     * @return email of the user with the specified key
     */
    public static String getEmail(String uid) {
        return FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .child("email").toString();
    }

    /**
     * @param uid UserID to get info from
     * @return Security question of the user with the specified key
     */
    public static String getQuestion(String uid) {
        return FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .child("question").toString();
    }

    /**
     * @param uid UserID to get info from
     * @return Answer to the security question of the user with the specified key
     */
    public static String getAnswer(String uid) {
        return FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .child("answer").toString();
    }

    //LostPost
    /**
     * @param infos String array of information to put in the database with the indexes representing;
     *              0 - name
     *              1 - uid
     *              2 - category
     *              3 - tags
     *              4 - date
     *              5 - uploader's name
     * @return key of the post
     */
    public static DatabaseReference initialiseLostPost(String[] infos) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("LostPost")
                .push();

        HashMap<String, String> properties = new HashMap<>();
        properties.put("name", infos[0]);
        properties.put("uid", infos[1]);
        properties.put("category", infos[2]);
        properties.put("tags", infos[3]);
        properties.put("date", infos[4]);
        properties.put("key", ref.getKey());
        properties.put("uploader_name", infos[5]);

        ref.setValue(properties);

        return ref;
    }

    /**
     * @param postID key of the post to get info from
     * @return The UserID of the user that created the post
     */
    public static DatabaseReference getLostPostUID(String postID) {
        return FirebaseDatabase.getInstance().getReference().child("LostPost")
                .child(postID).child("uid");
    }

    /**
     * @param postID key of the post to get info from
     * @return Tags of the post in String array form
     */
    public static DatabaseReference getLostPostTags(String postID) {
        return FirebaseDatabase.getInstance().getReference().child("LostPost")
                .child(postID).child("tags");
        // return s.split("\\W+");
    }

    /**
     * @param postID key of the post to get info from
     * @return Date of the post in DD-MM-YYYY form
     */
    public static DatabaseReference getLostPostDate(String postID) {
        return FirebaseDatabase.getInstance().getReference().child("LostPost")
                .child(postID).child("date");
    }

    /**
     * @param postID key of the post to get info from
     * @return Name of the post
     */
    public static DatabaseReference getLostPostName(String postID) {
        return FirebaseDatabase.getInstance().getReference().child("LostPost")
                .child(postID).child("name");
    }

    /**
     * @param postID key of the post to get info from
     * @return Category of the post
     */
    public static DatabaseReference getLostPostCategory(String postID) {
        return FirebaseDatabase.getInstance().getReference().child("LostPost")
                .child(postID).child("category");
    }

    /**
     * @param postID ID of the post to change the values
     * @param data New name of the post
     */
    public static void setLostPostName(String postID, String data) {
        FirebaseDatabase.getInstance().getReference().child("LostPost")
                .child(postID).child("name").setValue(data);
    }

    /**
     * @param postID ID of the post to change the values
     * @param data New UID of the post
     */
    public static void setLostPostUID(String postID, String data) {
        FirebaseDatabase.getInstance().getReference().child("LostPost")
                .child(postID).child("uid").setValue(data);
    }

    /**
     * @param postID ID of the post to change the values
     * @param data New category of the post
     */
    public static void setLostPostCategory(String postID, String data) {
        FirebaseDatabase.getInstance().getReference().child("LostPost")
                .child(postID).child("category").setValue(data);
    }

    /**
     * @param postID ID of the post to change the values
     * @param data New tags of the post in the form of a String where each tag is separated with a space
     */
    public static void setLostPostTags(String postID, String data) {
        FirebaseDatabase.getInstance().getReference().child("LostPost")
                .child(postID).child("tags").setValue(data);
    }

    /**
     * @param postID ID of the post to change the values
     * @param data New date of the post
     */
    public static void setLostPostDate(String postID, String data) {
        FirebaseDatabase.getInstance().getReference().child("LostPost")
                .child(postID).child("date").setValue(data);
    }

    /**
     * @param key key of the post to be deleted
     */
    public static void deleteLostPost( String key ) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "LostPost" ).child(key);
        ref.removeValue();
    }

    //SellPost
    /**
     * @param infos String array of information to put in the database with the indexes representing;
     *              0 - name
     *              1 - uid
     *              2 - category
     *              3 - tags
     *              4 - date
     *              5 - uploader's name
     * @return key of the post
     */
    public static DatabaseReference initialiseSellPost(String[] infos) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SellPost").push();

        HashMap<String, String> properties = new HashMap<>();
        properties.put("name", infos[0]);
        properties.put("uid", infos[1]);
        properties.put("category", infos[2]);
        properties.put("tags", infos[3]);
        properties.put("price", infos[4]);
        properties.put("key", ref.getKey());
        properties.put("uploader_name", infos[5]);

        ref.setValue(properties);

        return ref;
    }

    /**
     * @param postID UserID to get info from
     * @return UID of the creator of the post with the specified key
     */
    public static DatabaseReference getSellPostUID(String postID) {
        return FirebaseDatabase.getInstance().getReference().child("SellPost")
                .child(postID).child("uid");
    }

    /**
     * @param postID UserID to get info from
     * @return Tags of the post with the specified key
     */
    public static DatabaseReference getSellPostTags(String postID) {
        return FirebaseDatabase.getInstance().getReference().child("SellPost")
                .child(postID).child("tags");
        //return s.split("\\W+");
    }

    /**
     * @param postID UserID to get info from
     * @return Name of the post with the specified key
     */
    public static DatabaseReference getSellPostName(String postID) {
        return FirebaseDatabase.getInstance().getReference().child("SellPost")
                .child(postID).child("name");
    }

    /**
     * @param postID UserID to get info from
     * @return Category of the post with the specified key
     */
    public static DatabaseReference getSellPostCategory(String postID) {
        return FirebaseDatabase.getInstance().getReference().child("SellPost")
                .child(postID).child("category");
    }

    /**
     * @param postID ID of the post to change the values
     * @param data New name of the post
     */
    public static void setSellPostName(String postID, String data) {
        FirebaseDatabase.getInstance().getReference().child("SellPost")
                .child(postID).child("name").setValue(data);
    }

    /**
     * @param postID ID of the post to change the values
     * @param data New UID of the post
     */
    public static void setSellPostUID(String postID, String data) {
        FirebaseDatabase.getInstance().getReference().child("SellPost")
                .child(postID).child("uid").setValue(data);
    }

    /**
     * @param postID ID of the post to change the values
     * @param data New category of the post
     */
    public static void setSellPostCategory(String postID, String data) {
        FirebaseDatabase.getInstance().getReference().child("SellPost")
                .child(postID).child("category").setValue(data);
    }

    /**
     * @param postID ID of the post to change the values
     * @param data New tags of the post in the form of a String where each tag is separated with a space
     */
    public static void setSellPostTags(String postID, String data) {
        FirebaseDatabase.getInstance().getReference().child("SellPost")
                .child(postID).child("tags").setValue(data);
    }

    /**
     * @param postID ID of the post to change the values
     * @param data New price of the post
     */
    public static void setSellPostPrice(String postID, String data) {
        FirebaseDatabase.getInstance().getReference().child("SellPost")
                .child(postID).child("price").setValue(data);
    }

    /**
     * @param key key of the post to be deleted
     */
    public static void deleteSellPost( String key ) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "SellPost" ).child(key);
        ref.removeValue();
    }
}
