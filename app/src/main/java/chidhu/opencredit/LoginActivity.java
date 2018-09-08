package chidhu.opencredit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    Button Login;
    EditText email,password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();

        Login = findViewById(R.id.loginBtn);
        email = findViewById(R.id.uname);
        password = findViewById(R.id.pswd);

        if(mAuth.getCurrentUser()!=null){
            Intent toWorkspaceIntent = new Intent(getApplicationContext(), BottomNavActivity.class);
            startActivity(toWorkspaceIntent);
            finish();
        }
        
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() ){
                    Toast.makeText(LoginActivity.this, "Please fill the credentials first.", Toast.LENGTH_SHORT).show();
                }else {

                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent toWorkspaceIntent = new Intent(getApplicationContext(), BottomNavActivity.class);
                                startActivity(toWorkspaceIntent);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed. Please Check your credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean b) {
                if (!b) {
                    hideKeyboard(v);
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean b) {
                if (!b) {
                    hideKeyboard(v);
                }
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
