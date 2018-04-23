package dima.it.polimi.blackboard.model;

import com.google.firebase.firestore.Exclude;

import java.util.List;
import java.util.Map;

/**
 * Caching user info for reuse during session
 * Created by Stefano on 11/03/2018.
 */

public class User {
    @Exclude private static User instance;
    private Map<String, Object> personal_info;
    private List<Object> houses;
    private List<Object> achievements;
    private String auth_id;

    private User(){

    }

    public void setPersonal_info(Map<String, Object> personalInfo){
        this.personal_info = personalInfo;
    }

    public void setHouses(List<Object> houses){
        this.houses = houses;
    }

    public void setAuth_id(String id){
        this.auth_id = id;
    }

    public Map<String, Object> getPersonal_info(){
        return this.personal_info;
    }

    public List<Object> getHouses(){
        return houses;
    }

    public String getAuth_id(){
        return this.auth_id;
    }

    public void setAchievements(List<Object> achievements){
        this.achievements = achievements;
    }

    public List<Object> getAchievements(){
        return this.achievements;
    }

    public static User getInstance(){
        if(instance == null){
            instance = new User();
        }
        return instance;
    }

    public static void setInstance(User u){
        instance = u;
    }
}
