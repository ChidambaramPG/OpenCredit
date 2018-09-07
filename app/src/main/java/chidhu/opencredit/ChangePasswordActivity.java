package chidhu.opencredit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChangePasswordActivity extends AppCompatActivity {

    TextView oldPswd,newPswd,cnfrmNewPswd;
    Button chngPswd;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        user = FirebaseAuth.getInstance().getCurrentUser();

        oldPswd = findViewById(R.id.oldPswdTxt);
        newPswd = findViewById(R.id.newPswdTxt);
        cnfrmNewPswd = findViewById(R.id.newPswdCnfrmTxt);
        chngPswd = findViewById(R.id.changePswdBtn);

        chngPswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(oldPswd.getText().toString().isEmpty() || newPswd.getText().toString().isEmpty() || cnfrmNewPswd.getText().toString().isEmpty()){
                    Toast.makeText(ChangePasswordActivity.this, "Do not leave any fields empty !", Toast.LENGTH_SHORT).show();
                }else{
                    if(newPswd.getText().toString().equals(cnfrmNewPswd.getText().toString())){

                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), oldPswd.getText().toString());
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    user.updatePassword(newPswd.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ChangePasswordActivity.this, "Password changed successfully !", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent sn = new Intent(getApplicationContext(),LoginActivity.class);
                                            startActivity(sn);
                                            finish();
                                        }
                                    });
                                }
                            }
                        });

                    }else{
                        Toast.makeText(ChangePasswordActivity.this, "Passwords doesn't match. Please recheck and try again !", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
