package step.learning.spd_111_android_dunaiev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_menu2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.game_menu_new_game).setOnClickListener( this::onNewGameClick );
        findViewById(R.id.game_menu_records).setOnClickListener( this::onRecordsClick );
        findViewById(R.id.game_menu_quit).setOnClickListener( this::onQuitClick );
        findViewById( R.id.game_menu_player_namee).setOnClickListener( this::onPlayerNameClick );
    }

    private void onPlayerNameClick( View view ) {
        Intent intent = new Intent( this, GamePlayerName.class );
        startActivity( intent );
    }

    private void  onNewGameClick( View view ) {
        Intent intent = new Intent( this, GameActivity.class );
        startActivity( intent );
    }
    private void onRecordsClick( View view ) {
        Intent intent = new Intent( this, GameRecordsActivity.class);
        startActivity( intent );
    }
    private void onQuitClick( View view ) {
        finish();
    }
}