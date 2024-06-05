package step.learning.spd_111_android_dunaiev;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AnimActivity extends AppCompatActivity {

    private Animation opacityAnim;
    private Animation sizeAnim;
    private Animation size2Anim;
    private Animation arcAnim;
    private Animation arc2Anim;
    private Animation moveAnim;
    private boolean isMovePlaying;
    private AnimationSet comboAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_anim);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        opacityAnim = AnimationUtils.loadAnimation( this, R.anim.opacity );
        sizeAnim = AnimationUtils.loadAnimation( this, R.anim.size );
        size2Anim = AnimationUtils.loadAnimation( this, R.anim.size2 );
        arcAnim = AnimationUtils.loadAnimation( this, R.anim.arc );
        arc2Anim = AnimationUtils.loadAnimation( this, R.anim.arc2 );
        moveAnim = AnimationUtils.loadAnimation( this, R.anim.move );
        comboAnim = new AnimationSet( false);
        comboAnim.addAnimation( arc2Anim );
        comboAnim.addAnimation( size2Anim );


        findViewById( R.id.anim_opacity_block ).setOnClickListener( this::opacityCLick );
        findViewById( R.id.anim_size_block ).setOnClickListener( this::sizeCLick );
        findViewById( R.id.anim_size2_block ).setOnClickListener( this::size2CLick );
        findViewById( R.id.anim_arc_block ).setOnClickListener( this::arcCLick );
        findViewById( R.id.anim_arc2_block ).setOnClickListener( this::arc2CLick );
        findViewById( R.id.anim_move_block ).setOnClickListener( this::moveCLick );
        findViewById( R.id.anim_combo_block ).setOnClickListener( this::comboCLick );

        isMovePlaying = false;
    }

    private void opacityCLick( View view ) {
        view.startAnimation( opacityAnim );
    }
    private void moveCLick( View view ) {

        if(isMovePlaying) {
            view.clearAnimation();
        }
        else {
            view.startAnimation( arc2Anim );
        }

        isMovePlaying = !isMovePlaying;
    }

    private void arcCLick( View view ) {
        view.startAnimation( arcAnim );
    }
    private void arc2CLick( View view ) {
        view.startAnimation( arc2Anim );
    }

    private void sizeCLick( View view ) {
        view.startAnimation( sizeAnim );
    }
    private void size2CLick( View view ) {
        view.startAnimation( size2Anim );
    }

    private void comboCLick( View view ) {
        view.startAnimation( comboAnim );
    }
}