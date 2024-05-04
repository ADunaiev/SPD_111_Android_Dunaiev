package step.learning.spd_111_android_dunaiev;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalcActivity extends AppCompatActivity {

    private TextView tvHistory;
    private TextView tvResult;

    private double operand1 = 0.0;
    private double operand2 = 0.0;
    private String operation = "";

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvHistory = findViewById( R.id.calc_tv_history );
        tvResult  = findViewById( R.id.calc_tv_result  );

        if( savedInstanceState == null ) {  // немає збереженого стану -- перший запуск
            tvResult.setText("0");
        }
        /* Задача: циклом перебрати ресурсні кнопки calc_btn_{i} і для
           кожної з них поставити один обробник onDigitButtonClick */
        for (int i = 0; i < 10; i++) {
            findViewById(   // На заміну R.id.calc_btn_0 приходить наступний вираз
                    getResources().getIdentifier(        // R
                            "calc_btn_" + i,     //     .calc_btn_0
                            "id",                //  .id
                            getPackageName()
                    )
            ).setOnClickListener( this::onDigitButtonClick );
        }
        findViewById(R.id.calc_btn_inverse).setOnClickListener(this::onInverseClick);
        findViewById(R.id.calc_btn_plus).setOnClickListener(this::onPlusClick);
        findViewById(R.id.calc_btn_minus).setOnClickListener(this::onMinusClick);
        findViewById(R.id.calc_btn_equal).setOnClickListener(this::onEqualClick);
        findViewById(R.id.calc_btn_c).setOnClickListener(this::onClearClick);
        findViewById(R.id.calc_btn_ce).setOnClickListener(this::onClearEntryClick);
        findViewById(R.id.calc_btn_pm).setOnClickListener(this::onChangeSignClick);
        findViewById(R.id.calc_btn_square).setOnClickListener(this::onSquareClick);
        findViewById(R.id.calc_btn_sqrt).setOnClickListener(this::onSqrtClick);
    }
    private void onSqrtClick (View view) {
        String result = tvResult.getText().toString();

        if (result.isEmpty()) { return; }

        String history_str = " sqrt( " + result + " ) = ";
        double x = Double.parseDouble(result);

        if (x < 0) {
            Toast.makeText(this, R.string.calc_sqrt_negative, Toast.LENGTH_SHORT).show();
            return;
        }
        x = Math.sqrt(x);

        String str = ( x == (int)x ) ? String.valueOf((int)x) : String.valueOf(x);
        if(str.length() > 13) {
            str = str.substring(0,13);
        }
        tvResult.setText(str);
        tvHistory.setText(history_str);
    }
    private void onSquareClick (View view) {
        String result = tvResult.getText().toString();

        if (result.isEmpty()) { return; }

        String history_str = result + " * " + result + " = ";
        double x = Double.parseDouble(result);

        x *= x;

        String str = ( x == (int)x ) ? String.valueOf((int)x) : String.valueOf(x);
        if(str.length() > 13) {
            str = str.substring(0,13);
        }
        tvResult.setText(str);
        tvHistory.setText(history_str);
    }
    private void onChangeSignClick (View view) {
        String result = tvResult.getText().toString();
        double x = Double.parseDouble(result);
        x *= -1;
        String str = (x == (int)x) ?  String.valueOf((int)x) : String.valueOf(x);
        tvResult.setText(str);
    }
    private void onClearEntryClick (View view) {
        tvResult.setText("");
        operand1 = 0.0;
    }
    public void onClearClick (View view) {
        operand1 = 0.0;
        operand2 = 0.0;
        operation = "";
        tvResult.setText("");
        tvHistory.setText("");
    }
    public void onEqualClick (View view) {
        String result = tvResult.getText().toString();
        String history_str = tvHistory.getText().toString();

        /* перевіряємо чи задана операція */
        if ( operation.isEmpty() ) {
            return;
        }

        if (operand2 != 0) {
            operand1 = (result.isEmpty()) ? 0.0 : Double.parseDouble(result);
            history_str = (operand1 == (int)operand1) ? String.valueOf((int)operand1) : String.valueOf(operand1);
            history_str += " " + operation + " ";
            history_str += (operand2 == (int)operand2) ? String.valueOf((int)operand2) : String.valueOf(operand2);
            history_str += " = ";
        }
        else {
            if (result.isEmpty()) { return;}

            operand2 = Double.parseDouble(result);
            history_str += (operand2 == (int)operand2) ? String.valueOf((int)operand2) : String.valueOf(operand2);
            history_str += " = ";
        }

        double x = 0.0;

        switch (operation) {
            case "+":
                x =  operand1 + operand2;
                break;
            case "-":
                x = operand1 - operand2;
                break;
        }
        operand1 = x;

        String str = (x == (int)x) ? String.valueOf((int)x) : String.valueOf(x);
        if(str.length() > 13) {
            str = str.substring(0,13);
        }
        tvHistory.setText(history_str);
        tvResult.setText(str);


    }
    private void onPlusClick(View view) {
        String result = tvResult.getText().toString();

        if (!result.isEmpty()) {
            operand1 = Double.parseDouble(result);
            operation = "+";
            operand2 = 0.0;
        }
        else {
            return;
        }

        String history_str = result + " " + operation + " ";
        tvHistory.setText(history_str);
        tvResult.setText("");

    }
    private void onMinusClick(View view) {
        String result = tvResult.getText().toString();

        if (!result.isEmpty()) {
            operand1 = Double.parseDouble(result);
            operation = "-";
            operand2 = 0.0;
        }
        else {
            return;
        }

        String history_str = result + " " + operation + " ";
        tvHistory.setText(history_str);
        tvResult.setText("");

    }
    private void  onInverseClick(View view) {
        String result = tvResult.getText().toString();

        if (result.isEmpty()) { return; }
        double x = Double.parseDouble(result);

        if(x == 0) {
            Toast.makeText(this, R.string.calc_zero_division, Toast.LENGTH_SHORT).show();
            return;
        }
        String history_str = "1 / " + result + " =";
        tvHistory.setText(history_str);
        x = 1.0 / x;
        String str = (x == (int)x) ? String.valueOf((int)x) : String.valueOf(x);
        if(str.length() > 13) {
            str = str.substring(0,13);
        }
        tvResult.setText(str);
    }

    /*
    При зміні конфігурації пристрою (поворотах, змінах налаштувань, тощо) відбувається
    перезапуск активності. При цьому подаються події життєвого циклу
    onSaveInstanceState - при виході з активності перед перезапуском
    onRestoreInstanceState - при відновленні активності після перезапуску
    До обробників передається Bundle, що є сховищем, яке дозволяє зберегти та відновити дані
    Також збережений Bundle передається до onCreate, що дозволяє визначити
     чи це перший запуск, чи перезапуск через зміну конфігурації
     */

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);  // потрібно
        outState.putCharSequence( "tvResult", tvResult.getText() );
        outState.putCharSequence("tvHistory", tvHistory.getText() );
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText( savedInstanceState.getCharSequence( "tvResult" ) );
        tvHistory.setText( savedInstanceState.getCharSequence( "tvHistory" ) );
    }

    private void onDigitButtonClick(View view) {
        String result = tvResult.getText().toString();
        if(result.length() >= 10) {
            Toast.makeText(this, R.string.calc_limit_exceeded, Toast.LENGTH_SHORT).show();
            return;
        }
        if( result.equals("0") ) {
            result = "";
        }
        result += ((Button) view).getText();
        tvResult.setText( result );
    }
}
/*
Д.З. Завершити розмітку активності для калькулятора
Підібрати необхідні Юнікод-символи
Забезпечити вирівнювання та відступи, підібрати заокруглення
 */