package com.example.letmeeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroEmpresa extends AppCompatActivity implements View.OnClickListener {

    EditText email, contrasenia, confirmContra, nombre, apellidos, empresa;
    Button registrarse;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();//Esconderemos la barra de accion que sale por defecto en la parte superior
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_empresa);

        mAuth = FirebaseAuth.getInstance();

        //Recolectamos la informacion en sus respectivas variables
        email = (EditText) findViewById(R.id.Email);
        contrasenia = (EditText) findViewById(R.id.Password);
        confirmContra = (EditText) findViewById(R.id.ConfirmPassword);
        nombre = (EditText) findViewById(R.id.Nombre);
        empresa = (EditText) findViewById(R.id.Empresa);
        apellidos = (EditText) findViewById(R.id.Apellidos);

        //Añadiremos un progressBar para temas visuales
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //Ponemos un OnClick para que nos haga el registro al pulsar el boton
        registrarse = (Button) findViewById(R.id.but_Registrarse);
        registrarse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        registrarEmpresa();
    }

    private void registrarEmpresa() {
        //Pasamos los valores a string ignorando cualquier espacio en blanco que haya a principio o final
        String correo = email.getText().toString().trim(),
                contra = contrasenia.getText().toString().trim(),
                confirmarContra = confirmContra.getText().toString().trim(),
                nom = nombre.getText().toString().trim(),
                nomEmpresa = empresa.getText().toString().trim(),
                apellidosString = apellidos.getText().toString().trim();

        //Control de errores
        if (correo.isEmpty()){
            email.setError("Se requiere un correo");
            email.requestFocus();
            return;
        }
        if (contra.isEmpty()){
            contrasenia.setError("Se requiere una contraseña");
            contrasenia.requestFocus();
            return;
        }
        if (confirmarContra.isEmpty()){
            confirmContra.setError("Se requiere que insertes de nuevo la contraseña");
            confirmContra.requestFocus();
            return;
        }
        if (nom.isEmpty()){
            nombre.setError("Se requiere tu nombre");
            nombre.requestFocus();
            return;
        }
        if (apellidosString.isEmpty()){
            apellidos.setError("Se requieren tus apellidos");
            apellidos.requestFocus();
            return;
        }
        if (nomEmpresa.isEmpty()){
            empresa.setError("Se requieren un nombre de empresa");
            empresa.requestFocus();
            return;
        }

        //Verificamos el formato del correo
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            email.setError("Recuerda insertar un correo valido con su '@'");
            email.requestFocus();
            return;
        }

        //Firebase no acepta contraseñas menores a 6 caracteres
        if (contra.length() < 6){
            contrasenia.setError("Se necesita un mínimo de 6 carácteres");
            contrasenia.requestFocus();
            return;
        }

        //Comprobamos que las contraseñas coincidan
        if (!contra.equals(confirmarContra)){
            Toast.makeText(this,"Las contraseñas no coinciden, por favor revisalo", Toast.LENGTH_SHORT).show();
            return;
        }

        //Mostramos el ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        //Utilizaremos el Firestore del firebase para almacenar nuestra informacion
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> infoUsuario = new HashMap<>();

        //Añadimos la informacion al hashmap que sera subido a la base de datos
        infoUsuario.put("Nombre", nom);
        infoUsuario.put("Apellidos", apellidosString);
        infoUsuario.put("Email", correo);
        infoUsuario.put("Empresa", nomEmpresa);

        mAuth.createUserWithEmailAndPassword(correo, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    db.collection("Empresas")
                            .add(infoUsuario)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(RegistroEmpresa.this, "Usuario añadido correctamente", Toast.LENGTH_LONG).show();
                                    cambioActividad();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistroEmpresa.this, "Ha sucedido un error al añadir el usuario, intentelo de nuevo", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistroEmpresa.this, "Ha sucedido un error al añadir el usuario, es posible que ya exista alguien con este correo", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cambioActividad() {
        Intent intent = new Intent(this.getApplicationContext(), Login.class);
        startActivity(intent);
    }
}