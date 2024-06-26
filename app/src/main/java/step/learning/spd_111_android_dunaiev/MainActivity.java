package step.learning.spd_111_android_dunaiev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import step.learning.spd_111_android_dunaiev.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.main_btn_calc).setOnClickListener(this::onCalcButtonClick);
        findViewById(R.id.main_btn_game).setOnClickListener(this::onGameButtonClick);
        findViewById(R.id.main_btn_chat).setOnClickListener(this::onChatButtonClick);
        findViewById(R.id.main_btn_anim).setOnClickListener(this::onAnimButtonClick);
    }

    private void onAnimButtonClick(View view) {
        Intent intent = new Intent(this, AnimActivity.class);
        startActivity(intent);
    }

    private void onCalcButtonClick(View view) {
        Intent intent = new Intent(this, CalcActivity.class);
        startActivity(intent);
    }

    private void onGameButtonClick(View view) {
        Intent intent = new Intent(this, GameMenuActivity.class);
        startActivity(intent);
    }
    private void onChatButtonClick(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}