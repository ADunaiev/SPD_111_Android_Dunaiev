package step.learning.spd_111_android_dunaiev;

import android.os.Bundle;
import android.util.Log;
import android.util.MalformedJsonException;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import step.learning.spd_111_android_dunaiev.orm.RecordItem;

public class GameRecordsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_records);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String filename = "new_records_file.json";
        JSONArray resultsArray = loadJsonFromFile( filename );

        List<RecordItem> records = getListFromJsonArray( resultsArray );

        if ( records != null ) {
            Collections.sort( records );
            showRecordsInTextView( records );
        }
        else {
            showMessageInTextView( "records are empty!" );
        }


    }

    private void showMessageInTextView( String message ) {
        LinearLayout container = findViewById( R.id.game_records_container );
        TextView tv = new TextView( this );
        tv.setText( message );
        tv.setTextSize( 24 );
        container.addView( tv );
    }

    private void showRecordsInTextView ( List<RecordItem> records ) {
        LinearLayout container = findViewById( R.id.game_records_container );

        for (RecordItem item : records ) {
            TextView tv = new TextView( this );
            tv.setText( item.getName() + "     " + String.valueOf(  item.getScore() ) );
            tv.setTextSize( 24 );
            container.addView( tv );
        }
    }
    private List<RecordItem> getListFromJsonArray ( JSONArray jsonArray ) {
        try {
            int len = jsonArray.length();
            String name;
            int score;
            List<RecordItem> result2 = new ArrayList<>();

            for (int i = 0; i < len; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                name = jsonObject.getString("name");
                score = jsonObject.getInt("score");
                RecordItem recordItem = new RecordItem();
                recordItem.setName( name );
                recordItem.setScore( score );
                result2.add( recordItem );
            }

            return result2;
        }
        catch ( Exception ex ) {
            Log.e ( "showJsonInTextView", ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage() );
        }
        return null;
    }
    private JSONArray loadJsonFromFile( String filename ) {
        try {
            InputStream inputStream = openFileInput( filename );
            int size = inputStream.available();
            byte[] buffer = new byte[ size ];
            inputStream.read( buffer );
            inputStream.close();

            String json = new String( buffer, StandardCharsets.UTF_8 );

            return new JSONArray( json );

        }
        catch ( Exception ex ) {
            Log.e("loadJson",
                    ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage() );
        }
        return null;
    }

}