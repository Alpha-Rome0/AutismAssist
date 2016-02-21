package mhacks.autismassist;

import com.firebase.client.Firebase;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by manavdutta1 on 2/20/16.
 */
public class FirebaseHelper {
    private Firebase myRef;
    public FirebaseHelper(String url){
        myRef = new Firebase(url);
    }
    public void saveArray(int[] array) {
        Firebase convoRef = myRef.child(((Integer)array[2]).toString());
        Map<String, Integer> valueMap = new HashMap<String, Integer>();
        valueMap.put("Engagement", array[0]);
        valueMap.put("Attention", array[1]);
        convoRef.setValue(valueMap);
    }
}
