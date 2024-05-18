package step.learning.spd_111_android_dunaiev;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.LinkedList;

public class GameActivity extends AppCompatActivity {
    private static final int FIELD_WIDTH = 16;
    private static final int FIELD_HEIGHT = 24;
    private TextView[][] gameField;
    private final LinkedList<Vector2> snake = new LinkedList<>();
    private final Handler handler = new Handler();
    private int fieldColor;
    private int snakeColor;
    private Direction moveDirection;
    private boolean isPlaying;
    private static final String food = new String( Character.toChars( 0x1F34E ) );
    private Vector2 foodPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // додоємо аналізатор (слухач) свайпів на всю активність. (R.id.main)
        findViewById(R.id.main).setOnTouchListener(new OnSwipeListner(this) {
            @Override
            public void onSwipeBottom() {
                if ( moveDirection != Direction.top ) {
                    moveDirection = Direction.bottom;
                }
            }

            @Override
            public void onSwipeLeft() {
                if( moveDirection != Direction.right ) {
                    moveDirection = Direction.left;
                }
            }

            @Override
            public void onSwipeRight() {
                if( moveDirection != Direction.left ) {
                    moveDirection = Direction.right;
                }
            }

            @Override
            public void onSwipeTop() {
                if( moveDirection != Direction.bottom ) {
                    moveDirection = Direction.top;
                }
            }
        });
        fieldColor = getResources().getColor(R.color.game_field, getTheme());
        snakeColor = getResources().getColor(R.color.game_snake, getTheme());
        initField();
        newGame();
    }
    private void step() {
        if( !isPlaying ) {
            return;
        }
        Vector2 tail = snake.getLast();
        Vector2 head = snake.getFirst();
        Vector2 newHead = new Vector2(head.x, head.y);
        switch (moveDirection) {
            case bottom: newHead.y += 1; break;
            case left:   newHead.x -= 1; break;
            case right:  newHead.x += 1; break;
            case top:    newHead.y -= 1; break;
        }

        if( newHead.x < 0 || newHead.x >= FIELD_WIDTH ||
            newHead.y < 0 || newHead.y >= FIELD_HEIGHT) {
            gameOver();
            return;
        }

        snake.addFirst(newHead);
        gameField[newHead.x][newHead.y].setBackgroundColor( snakeColor );

        snake.remove(tail);
        gameField[tail.x][tail.y].setBackgroundColor( fieldColor );

        handler.postDelayed(this::step, 700);
    }
    private void initField() {
        LinearLayout field = findViewById(R.id.game_field);

        LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        tvLayoutParams.weight = 1f;
        tvLayoutParams.setMargins( 4, 4, 4, 4 );

        LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
        );
        rowLayoutParams.weight = 1f;

        gameField = new TextView[FIELD_WIDTH][FIELD_HEIGHT];

        for (int j = 0; j < FIELD_HEIGHT; j ++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation( LinearLayout.HORIZONTAL );
            row.setLayoutParams( rowLayoutParams );

            for ( int i=0; i < FIELD_WIDTH; i++) {
                TextView tv = new TextView(this);
                tv.setBackgroundColor( fieldColor );
                // tv.setText( "0" );
                tv.setLayoutParams( tvLayoutParams );
                row.addView( tv );
                gameField[i][j] = tv;
            }
            field.addView( row );
        }
    }
    private void newGame() {
        for(Vector2 v:snake) {
            gameField[v.x][v.y].setBackgroundColor(fieldColor);
        }
        snake.clear();

        snake.add( new Vector2(8, 10) );
        snake.add( new Vector2(8, 11) );
        snake.add( new Vector2(8, 12) );
        snake.add( new Vector2(8, 13) );
        snake.add( new Vector2(8, 14) );
        for(Vector2 v:snake) {
            gameField[v.x][v.y].setBackgroundColor(snakeColor);
        }
        moveDirection = Direction.top;
        isPlaying = true;
        step();
    }
    private void gameOver() {
        isPlaying = false;
        new AlertDialog.Builder(this)
                .setTitle( "Game Over" )
                .setMessage( "Play one more time?" )
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable( false )
                .setPositiveButton("Yes", (dialog, which) -> newGame())
                .setNegativeButton( "No", (dialog, which) -> finish() )
                .show();
    }
    static class Vector2 {
        int x;
        int y;

        public Vector2(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    @Override
    protected void onPause() { // подія деактивації
        super.onPause();
        isPlaying = false;
    }
    @Override
    protected void onResume() { // подія активації
        super.onResume();
        isPlaying = true;
        step();
    }

    enum Direction {
        bottom,
        left,
        right,
        top
    }
}