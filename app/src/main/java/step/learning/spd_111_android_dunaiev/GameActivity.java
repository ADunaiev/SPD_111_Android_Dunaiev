package step.learning.spd_111_android_dunaiev;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonWriter;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import step.learning.spd_111_android_dunaiev.orm.RecordItem;

public class GameActivity extends AppCompatActivity {
    private static final int FIELD_WIDTH = 16;
    private static final int FIELD_HEIGHT = 24;
    private static final double SPEED_CHANGE = .1;
    private static final int SPEED_CHANGE_APPLES_NUMBER = 3;
    private static final int MAXIMUM_BONUS_STEPS = 40;
    private TextView[][] gameField;
    private final LinkedList<Vector2> snake = new LinkedList<>();
    private final Handler handler = new Handler();
    private int fieldColor;
    private int snakeColor;
    private Direction moveDirection;
    private boolean isPlaying;
    private static final String food = new String( Character.toChars( 0x1F34E ) );
    private static final String bonus = new String( Character.toChars( 0x1F34C ) );
    private Vector2 foodPosition;
    private Vector2 bonusPosition;
    private static final Random _random = new Random();
    private int result;
    private TextView tvGameScore;
    private int stepDelay;
    private int bonusSteps;
    private List<RecordItem> records = new ArrayList<>();
    private static final String FILENAME = "new_records_file.json";

    private Animation opacityAnimation;

    private String playerName = "";

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
        opacityAnimation = AnimationUtils.loadAnimation( this, R.anim.opacity );
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
        tvGameScore = findViewById(R.id.game_score);
        if( savedInstanceState == null ) {  // немає збереженого стану -- перший запуск
            tvGameScore.setText("0");
        }
        initField();
        loadRecords();
        playerName = loadPlayerName( "player_name.json" );
        newGame();
    }
    private void loadRecords() {
        JSONArray resultsArray = loadJsonFromFile2( );
        records = getListFromJsonArray( resultsArray );
        if (records != null) {
            Collections.sort( records );
        }
    }

    private List<RecordItem> getListFromJsonArray( JSONArray jsonArray ) {
        try {
            int len = jsonArray.length();
            if(len == 0) return new ArrayList<>();

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
            Log.e( "getListFromJson", ex.getMessage() == null ?
                    ex.getClass().getName() : ex.getMessage() );
        }
        return null;
    }
    private void saveRecordsToFile( ) {

        try {
            JSONArray jsonArray = new JSONArray();

            for ( RecordItem item : records ) {
                JSONObject json = new JSONObject();
                json.put( "name", item.getName() );
                json.put( "score", item.getScore() );
                jsonArray.put( json );
            }

            String recordsString = jsonArray.toString();

            OutputStream os = openFileOutput( FILENAME, MODE_PRIVATE );
            byte[] buffer = recordsString.getBytes();

            os.write( buffer, 0, buffer.length );
            os.close();


        }
        catch( Exception ex ) {
            throw new IllegalArgumentException( ex.getMessage() );
        }
    }
    private void step() {
        if( !isPlaying ) {
            return;
        }

        Vector2 head = snake.getFirst();
        Vector2 newHead = new Vector2(head.x, head.y);
        switch (moveDirection) {
            case bottom: newHead.y += 1; break;
            case left:   newHead.x -= 1; break;
            case right:  newHead.x += 1; break;
            case top:    newHead.y -= 1; break;
        }

        // перевіряємо що ми в межах екрану
        if( newHead.x < 0 || newHead.x >= FIELD_WIDTH ||
            newHead.y < 0 || newHead.y >= FIELD_HEIGHT) {
            gameOver();
            return;
        }

        // перевіряємо чи не їсть змія сама себе
        if (isCellInSnake(newHead)) {
            gameOver();
            return;
        }

        if( bonusPosition != null) {
            if (newHead.x == bonusPosition.x && newHead.y == bonusPosition.y) {
                // ми отримали бонус
                bonusSteps = 0;
                // вкорочуємо змійку на 3 клітинки
                int snakeLengthCut = 3;

                for (int i = 0; i < snakeLengthCut; i++) {
                    Vector2 temp = snake.getLast();
                    snake.remove( temp );
                    gameField[temp.x][temp.y].setBackgroundColor( fieldColor );
                }

                gameField[bonusPosition.x][bonusPosition.y].setText( "" );
                bonusPosition = null;
            }
            else {
                bonusSteps += 1;

                if ( bonusSteps >= MAXIMUM_BONUS_STEPS ) {
                    gameField[bonusPosition.x][bonusPosition.y].setText( "" );
                    bonusPosition = null;
                    bonusSteps = 0;
                }
            }
        }


        if (newHead.x == foodPosition.x && newHead.y == foodPosition.y) {
            // видовження - не прибирати хвіст
            result += 1;
            tvGameScore.setText( String.valueOf( result ) );

            // додаємо бонус
            if (result % ( SPEED_CHANGE_APPLES_NUMBER * 2 ) == 0) {
                do {
                    bonusPosition = Vector2.random();
                } while (isCellInSnake( bonusPosition ) ||
                        ( bonusPosition.x == foodPosition.x && bonusPosition.y == foodPosition.y ));
                gameField[bonusPosition.x][bonusPosition.y].setText( bonus );
            }

            if ( result % SPEED_CHANGE_APPLES_NUMBER == 0) stepDelay = (int)( stepDelay * ( 1 - SPEED_CHANGE ));
            // перенести їжу, але щоб не на змійку
            gameField[foodPosition.x][foodPosition.y].setText( "" );
            do {
                foodPosition = Vector2.random();
            } while ( isCellInSnake( foodPosition ) );
            gameField[foodPosition.x][foodPosition.y].setText( food );
            gameField[foodPosition.x][foodPosition.y].startAnimation(opacityAnimation);
        }
        else {
            Vector2 tail = snake.getLast();
            snake.remove(tail);
            gameField[tail.x][tail.y].setBackgroundColor( fieldColor );
        }

        snake.addFirst(newHead);
        gameField[newHead.x][newHead.y].setBackgroundColor( snakeColor );

        handler.postDelayed(this::step, stepDelay);
    }
    private boolean isCellInSnake (Vector2 cell) {
        for(Vector2 v : snake) {
            if (v.x == cell.x & v.y == cell.y) return true;
        }
        return false;
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
        result = 0;
        bonusSteps = 0;
        tvGameScore.setText( String.valueOf( result ) );
        stepDelay = 600;
        if(foodPosition != null) {
            gameField[foodPosition.x][foodPosition.y].setText("");
        }

        if(bonusPosition != null) {
            gameField[bonusPosition.x][bonusPosition.y].setText("");
        }

        snake.add( new Vector2(8, 10) );
        snake.add( new Vector2(8, 11) );
        snake.add( new Vector2(8, 12) );
        snake.add( new Vector2(8, 13) );
        snake.add( new Vector2(8, 14) );
        for(Vector2 v:snake) {
            gameField[v.x][v.y].setBackgroundColor(snakeColor);
        }
        foodPosition = new Vector2( 3,14 );
        gameField[foodPosition.x][foodPosition.y].setText( food );
        moveDirection = Direction.top;
        isPlaying = true;
        step();
    }
    private void saveRecord( String name, int score) {
        RecordItem recordItem = new RecordItem();
        recordItem.setName( name );
        recordItem.setScore( score );
        if ( records != null) {
            records.add( recordItem );
        }
        else {
            records = new ArrayList<>();
            records.add( recordItem );
        }

    }
    private void gameOver() {
        isPlaying = false;


        if (records != null) {

            for (int i = 0; i < records.size(); i++) {
                if (result >= records.get(i).getScore()) {
                    saveRecord( playerName, result);
                    saveRecordsToFile();
                    break;
                }
            }
        }
        else if ( result > 0 ) {
            saveRecord(playerName, result );
            saveRecordsToFile();
        }



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
        public static Vector2 random() {
            return new Vector2(
                    _random.nextInt(FIELD_WIDTH),
                    _random.nextInt(FIELD_HEIGHT)
                    );
        }

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
        if(!isPlaying) {
            isPlaying = true;
            step();
        }
    }

    enum Direction {
        bottom,
        left,
        right,
        top
    }

    private JSONArray loadJsonFromFile2( ) {
        try {
            InputStream inputStream = openFileInput( FILENAME );
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

    private String loadPlayerName( String filename ) {


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