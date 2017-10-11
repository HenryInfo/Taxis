package pe.bravos.taxis;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class PerfilActivity extends AppCompatActivity {
    SharedPreferences preferences;
    private TextView txtUsuario;
    private String pDni, pNombre, pPaterno, pMaterno, pTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        pDni = preferences.getString("pDni", "");
        pNombre = preferences.getString("pNombre", "");
        pPaterno = preferences.getString("pPaterno", "");
        pMaterno = preferences.getString("pMaterno", "");
        pTelefono = preferences.getString("pTelefono", "");

        txtUsuario = (TextView) findViewById(R.id.txtUsuario);
        txtUsuario.setText(pNombre+" "+pPaterno+" "+pMaterno);
    }
}
