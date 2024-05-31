package step.learning.spd_111_android_dunaiev;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class GamePlayerName extends AppCompatActivity {

    public static String player_name = "";
    private EditText playerNameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_player_name);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String filename = "player_name.json";
        playerNameEditText = findViewById( R.id.player_name_edit_text );
        findViewById( R.id.player_name_button ).setOnClickListener( this::playerNameButtonClicked );
        player_name = loadPlayerName( filename );
        playerNameEditText.setText( player_name );

    }

    private void playerNameButtonClicked ( View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put( "player_name", playerNameEditText.getText().toString() );

            String string = jsonObject.toString();
            OutputStream os = openFileOutput( "player_name.json", MODE_PRIVATE);
            byte[] buffer = string.getBytes();
            os.write( buffer, 0, buffer.length );
            os.close();
            player_name = playerNameEditText.getText().toString();
            finish();
        }
        catch ( Exception ex ) {
            Log.e( "savePlayerName", ex.getMessage() == null ?
                    ex.getClass().getName() : ex.getMessage() );
        }
    }

    public String loadPlayerName( String filename ) {


        try {
            InputStream inputStream = openFileInput( filename );
            int size = inputStream.available();
            byte[] buffer = new byte[ size ];
            inputStream.read( buffer );
            inputStream.close();

            String json = new String( buffer, StandardCharsets.UTF_8 );
            JSONObject jsonObject = new JSONObject( json );
            return jsonObject.getString( "player_name" );
        }
        catch ( Exception ex ) {
            Log.e( "loadPlayerName", ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage()  );
        }

        return null;

    }
}