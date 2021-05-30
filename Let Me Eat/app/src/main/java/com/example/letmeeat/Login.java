package com.example.letmeeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    FirebaseUser usuario;

    Button login;

    public static final int REQUEST_CODE = 85672;

    public void registrarUsuario(View view){
        Intent intent = new Intent(this.getApplicationContext(), RegistroUsuario.class);
        startActivity(intent);
    }

    public void registrarEmpresa(View view){
        Intent intent = new Intent(this.getApplicationContext(), RegistroEmpresa.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();//Esconderemos la barra de accion que sale por defecto en la parte superior

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();

        if(fUser!=null){
            Intent intent = new Intent(this.getApplicationContext(), Home.class);
            startActivity(intent);
        }

        TextView contraPerdida = (TextView) findViewById(R.id.tv_ContraPerdida);
        contraPerdida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ContraPerdida.class);
                startActivity(intent);
            }
        });

        //Ponemos un OnClick para que nos haga el registro al pulsar el boton
        login = (Button) findViewById(R.id.but_Login);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EditText login =(EditText)findViewById(R.id.Email),
                password = (EditText) findViewById(R.id.Password) ;

        String loginString = login.getText().toString(),
                passwordString = password.getText().toString();

        //Control de errores
        if (loginString.isEmpty()){
            login.setError("Se requiere un correo");
            login.requestFocus();
            return;
        }
        if (passwordString.isEmpty()){
            password.setError("Se requiere una contraseña");
            password.requestFocus();
            return;
        }

        //Verificamos el formato del correo
        if (!Patterns.EMAIL_ADDRESS.matcher(loginString).matches()){
            login.setError("Recuerda insertar un correo valido con su '@'");
            login.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(loginString,passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(Login.this, Home.class);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Usuario/Contraseña incorrectos", Toast.LENGTH_LONG).show();
            }
        });
    }
}