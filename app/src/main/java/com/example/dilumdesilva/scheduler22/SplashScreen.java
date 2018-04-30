package com.example.dilumdesilva.scheduler22;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @DevelopedBy Dilum De Silva 2016142 | w16266371
 *
 */
public class SplashScreen extends AppCompatActivity {

    //declaring splash screen related components
    private ImageView ss_imageView;
    private TextView ss_textView;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //initializing the components by Id
        ss_imageView =  (ImageView) findViewById(R.id.ss_imageView);
        ss_textView = (TextView) findViewById(R.id.ss_textView);

        //setting the splash screen animation to the splash screen
        Animation splashScreenAnim = AnimationUtils.loadAnimation(this, R.anim.splash_transition);
        ss_imageView.startAnimation(splashScreenAnim);
        ss_textView.startAnimation(splashScreenAnim);

        //creating an intent to continue with the menu screen activity
        final Intent intent = new Intent(this, MainScreen.class);

        //setting a timer to automatically continue to the main menu screen after splash screen
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                     startActivity(intent);
                     finish();
                }
            }
        };
        timer.start();
    }
}
