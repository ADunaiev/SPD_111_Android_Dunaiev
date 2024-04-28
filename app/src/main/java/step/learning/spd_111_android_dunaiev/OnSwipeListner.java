package step.learning.spd_111_android_dunaiev;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OnSwipeListner implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public OnSwipeListner(Context context) {
        /*
        Детектор жестів створюється у контексті - він обмеженний  цим
        контекстом, та передає події до його обробників. У якості контексту
        має бути View, свайпи по якому аналізуються.
         */
        this.gestureDetector = new GestureDetector(context, new SwipeGestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // передаємо аналіз жесту на наш жестовий детектор
        return gestureDetector.onTouchEvent(event);
    }
    // оголошуємо інтерфейсні методи для переозначення у класах-слухачах
    public void onSwipeBottom() {}
    public void onSwipeLeft() {}
    public void onSwipeRight() {}
    public void onSwipeTop() {}

    // створюємо аналізатор для жестового детектора
    private final class SwipeGestureListener
            extends GestureDetector.SimpleOnGestureListener {
        private static final int MIN_DISTANCE = 70;
        private static final int MIN_VELOCITY = 80;
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true; // ознака оброблення - запобігаємо кліку
        }

        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            boolean isDispatched = false;

            if(e1 != null) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();

                if(Math.abs(distanceX) > Math.abs(distanceY)) { // горизонтальний
                    // перевіряємо горизонтальні швидкість та відстань
                    if (Math.abs(distanceX) > MIN_DISTANCE &&
                        Math.abs(velocityX) > MIN_VELOCITY) {
                        if(distanceX > 0) { // вправо
                             onSwipeRight();
                        }
                        else { // вліво
                            onSwipeLeft();
                        }
                        isDispatched = true;
                    }
                }
                else { // вертикальний
                    if (Math.abs(distanceY) > MIN_DISTANCE &&
                            Math.abs(velocityY) > MIN_VELOCITY) {
                        if(distanceY > 0) { // вниз
                            onSwipeBottom();
                        }
                        else { // вверх
                            onSwipeTop();
                        }
                        isDispatched = true;
                    }
                }
            }

            return isDispatched;
        }
    }
}

/*
Оброблення swipe - жестів проведення по екрану.

Базові детекторі жестів не налаштовані на розрізнення
свайпів, для них є узагальнений жест - Fling -
торкання у точці e1, проведення та завершення жесту у точці e2
Визначаються коордінати точок та швидкість проведення.

Задача: визначити до якого з 4х свайпів належатиме жест, за умови,
що ідеально вертикальних чи горизонтальних жестів фактично не існує.

Також бажано встановити ліміти (гранічні значення) як до відстані
проведення, так і до швидкості.


 */
