package chidhu.opencredit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreenActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch(Exception e){
            System.out.println(e);
        }

        Animation anim = AnimationUtils.loadAnimation(this,R.anim.slide_up);
        Button getStrted = findViewById(R.id.getStrtedBtn);
        getStrted.startAnimation(anim);
        getStrted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toWorkspaceIntent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(toWorkspaceIntent);
                finish();
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
