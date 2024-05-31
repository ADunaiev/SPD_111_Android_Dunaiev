package step.learning.spd_111_android_dunaiev.orm;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;

public class RecordItem implements Comparable<RecordItem>, Serializable {
    private String name;
    private int score;

    public static RecordItem fromJsonString ( String jsonString ) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String name = jsonObject.getString("name");
            int score = jsonObject.getInt( "score" );
            RecordItem recordItem = new RecordItem();
            recordItem.setName( name );
            recordItem.setScore( score );
            return recordItem;
        }
        catch ( Exception ex ) {
            throw new IllegalArgumentException( ex.getMessage() );
        }
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(RecordItem o) {
        return - this.getScore() + o.getScore();
    }
}
