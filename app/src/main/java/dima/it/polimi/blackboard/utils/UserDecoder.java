package dima.it.polimi.blackboard.utils;

import android.text.TextUtils;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class to store pairs of (id,name) for translation
 * Created by Stefano on 13/03/2018.
 */

public class UserDecoder {
    private static UserDecoder instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, String> mMap;

    private UserDecoder(){
        mMap = new HashMap<>();
    }

    public String getNameFromId(String uid){
        String name = mMap.get(uid);

        if(name == null){
            //Todo go get it from Firestore
            return uid;
        }


        return name;
    }

    @SuppressWarnings("unchecked")
    public void populateFromHouse(String housename){
        db.collection("houses").document(housename).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        Map<String, Object> roommates = (Map<String, Object>)doc.get("roommates");
                        List<Object> uids = (List<Object>)roommates.get("roommates");
                        for(Object o : uids){
                            String uid = (String)o;
                            db.collection("users").document(uid).get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            DocumentSnapshot doc1 = task1.getResult();
                                            if(doc1.exists()) {
                                                Map<String, Object> personalInfo = (Map<String, Object>) doc1.get("personal_info");
                                                String name = (String) personalInfo.get("name");
                                                mMap.put(uid, name);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public static UserDecoder getInstance(){
        if(instance == null){
            instance = new UserDecoder();
        }
        return instance;
    }
}
