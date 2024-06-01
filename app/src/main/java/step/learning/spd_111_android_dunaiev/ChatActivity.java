package step.learning.spd_111_android_dunaiev;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import step.learning.spd_111_android_dunaiev.orm.ChatMessage;
import step.learning.spd_111_android_dunaiev.orm.ChatResponse;

public class ChatActivity extends AppCompatActivity {
    private static final String CHAT_URL = "https://chat.momentfor.fun/";
    private final byte[] buffer = new byte[8096];
    // паралельні запити до кількох ресурсів не працюють, виконується лише один
    // це обмежує вибір виконавчого сервісу.
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.UK
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        CompletableFuture
                .supplyAsync( this::loadChat, executorService )
                .thenApplyAsync( this::processChatResponse )
                .thenAcceptAsync( this::displayChatMessages );
    }
    private String loadChat() {
        try( InputStream chatStream = new URL( CHAT_URL ).openStream() ) {
            String response = readString( chatStream );
            //runOnUiThread( () ->
            //      ((TextView)findViewById(R.id.chat_tv_title)).setText( response )
            //);
            return response;

        }
        catch ( Exception ex ) {
            Log.e( "ChatActivity::loadChat()", ex.getMessage() == null ?
                    ex.getClass().getName() : ex.getMessage() );
        }
        return null;
    }
    private boolean processChatResponse( String response ) {
        boolean wasNewMessage = false;
        try {
            ChatResponse chatResponse = ChatResponse.fromJsonString( response );
            for( ChatMessage message : chatResponse.getData() ) {
                if( this.chatMessages.stream().noneMatch( m -> m.getId().equals( message.getId() ) ) ) {
                    // немає жодного повідомлення з таким id, як у essage -- це нове повідомлення
                    this.chatMessages.add ( message );
                    wasNewMessage = true;
                }
            }
        }
        catch (IllegalArgumentException ex) {
            Log.e ("ChatActivity::processChatResponse", ex.getMessage() == null ?
                    ex.getClass().getName() : ex.getMessage() );
        }
        return wasNewMessage;
    }
    private void displayChatMessages( boolean wasNewMessage ) {
        if ( !wasNewMessage ) return;
        Drawable myBackground = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.chat_msg_my );
        Drawable friendBackground = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.chat_msg_friend);

        LinearLayout.LayoutParams myMsgParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        myMsgParams.setMargins( 0, 10, 8, 10);
        myMsgParams.gravity = Gravity.END;

        LinearLayout.LayoutParams friendMsgParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        friendMsgParams.setMargins( 0, 10, 0, 10 );
        friendMsgParams.gravity = Gravity.START;

        runOnUiThread( () -> {
            LinearLayout container = findViewById(R.id.chat_container);

            for (int i = 0; i < chatMessages.size(); i++ ) {
                //LinearLayout messageLayout самостійно
                LinearLayout messageLinearLayout = new LinearLayout( this );

                if ( i % 2 == 0 ) {
                    messageLinearLayout.setBackground( myBackground );
                    messageLinearLayout.setLayoutParams( myMsgParams );
                }
                else {
                    messageLinearLayout.setBackground( friendBackground );
                    messageLinearLayout.setLayoutParams( friendMsgParams );
                }

                messageLinearLayout.setPadding( 15, 5, 15, 5 );
                messageLinearLayout.setOrientation( LinearLayout.VERTICAL );

                // message author
                TextView tvAuthor = new TextView( this );
                tvAuthor.setText( chatMessages.get(i).getAuthor() );
                tvAuthor.setTextSize( 20 );
                tvAuthor.setTypeface( null, Typeface.BOLD );
                messageLinearLayout.addView( tvAuthor );

                // message text
                TextView tvMessage = new TextView(this);
                tvMessage.setText( chatMessages.get(i).getText() );
                tvMessage.setTextSize( 20 );
                messageLinearLayout.addView( tvMessage );

                // message date
                TextView tvDate = new TextView( this );
                tvDate.setText( chatMessages.get(i).getMoment().toString() );
                tvDate.setTypeface( null, Typeface.ITALIC );
                tvDate.setTextSize( 12 );
                messageLinearLayout.addView( tvDate );

                container.addView( messageLinearLayout );
            }
        });
    }
    private String readString(InputStream stream) throws IOException {
        ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream();
        int len;
        while( ( len = stream.read( buffer) ) != -1 ) {
            byteBuilder.write( buffer, 0, len );
        }
        String result = byteBuilder.toString();
        byteBuilder.close();
        return result;
    }
    @Override
    protected void onDestroy() {
        executorService.shutdownNow();
        super.onDestroy();
    }
}
/*
Робота з мережею Інтернет
Основу складає клас java.net.URL
традиційно для Java створення об'єкту не призводить до якоїсь активності,
лише створюється об'єкт.
Підключення та передача даних здійснюється при певних командах, зокрема,
відкриття потоку.
Читання даних з потоку має особливості
- мульти-байтове кодування: різні символи мають різну байтову довжину. Це
    формує пораду спочатку одержати всі дані у бінарному вигляді і потім
    декодувати рядок ( замість одержання фрагментів даних і їх перетворення ).
- запити до мережі не можуть виконуватись з основного (UI) потоку.
    Це спричиняє виняток - android.os.NetworkOnMainThreadException
    Варіанти рішень:
    = запустити в окремому потоці
        + простіше та наочніше
        - складність завершення різних потоків, особливо, якщо їх багато.
    = запустити у фоновому виконавці
        + централізоване завершення
        - не забути завершення
- Для того, щоб застосунок міг звертатись до мережі йому потрібні дозволи.
    Без них виняток - Permission denied (missing INTERNET permission?).
    Дозволи зазначаються у маніфесті.
    <uses-permission android:name="android.permission.INTERNET" />

 - Необхідність запуску мережних запитів у окремих потоках часто призводить до
    того, що з них обмежено доступ до елементів UI
    (Only the original thread that created a view hierarchy can touch its views. Expected: main Calling: pool-3-thread-1)
    Перехід до UI потоку здійснюється або runOnUiThread або переходом до синхронного режиму.

 */