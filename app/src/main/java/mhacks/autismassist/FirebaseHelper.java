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
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Firebase convoRef = myRef.child(dateFormat.format(date));
        Map<String, Integer> valueMap = new HashMap<String, Integer>();
        valueMap.put("Gender", array[0]);
        valueMap.put("Glasses", array[1]);
        valueMap.put("Anger", array[2]);
        valueMap.put("Contempt", array[3]);
        valueMap.put("Disgust", array[4]);
        valueMap.put("Engagement", array[5]);
        valueMap.put("Attention", array[6]);
        valueMap.put("Fear", array[7]);
        valueMap.put("Joy", array[8]);
        valueMap.put("Sad", array[9]);
        valueMap.put("Surprise", array[10]);
        valueMap.put("Valence", array[11]);
        valueMap.put("Smile", array[12]);
        valueMap.put("Time", array[13]);
        convoRef.push().setValue(valueMap);
    }
}
