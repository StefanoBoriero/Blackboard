package dima.it.polimi.blackboard.utils;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.model.User;

/**
 * Utility class to translate house id to house names
 * Created by Stefano on 19/03/2018.
 */

public class HouseDecoder {
    private static final String TAG = "house-decoder";
    private static HouseDecoder instance;
    private Map<String, String> mMap;

    private HouseDecoder(){
        mMap = new HashMap<>();
    }

    public void populateFromUser(User u){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Object> housesID = u.getHouses();
        if(housesID != null) {
            for (Object o : housesID) {
                String id = (String) o;
                db.collection("houses").document(id).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            String name = (String) doc.getData().get("name");
                            mMap.put(id, name);
                        }
                    } else {
                        Log.e(TAG, "Error in getting document: " + id);
                    }
                });
            }
        }
    }

    public String decodeId(String houseId){
        String name;
        name = mMap.get(houseId);
        if(name == null){
            //Todo get the correct one
            return houseId;
        }
        return name;
    }

    public static HouseDecoder getInstance(){
        if(instance == null){
            instance = new HouseDecoder();
        }
        return instance;
    }
}
