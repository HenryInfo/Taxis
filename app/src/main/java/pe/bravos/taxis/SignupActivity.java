package pe.bravos.taxis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;

import pe.bravos.taxis.model.insertToUsuario;

public class SignupActivity extends AppCompatActivity {
    private EditText iptNombre, iptApaterno, iptAmaterno, iptDni, iptTelefono, iptPassword, iptRePassword;
    private Button btnRegistar;
    private TextView txtLogin;
    private String dni, nombres, paterno, materno, telefono, password, rePassword;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        iptDni = (EditText) findViewById(R.id.iptDni);
        iptNombre = (EditText) findViewById(R.id.iptNombre);
        iptAmaterno = (EditText) findViewById(R.id.iptAmaterno);
        iptApaterno = (EditText) findViewById(R.id.iptApaterno);
        iptTelefono = (EditText) findViewById(R.id.iptTelefono);
        iptPassword = (EditText) findViewById(R.id.iptPassword);
        iptRePassword = (EditText) findViewById(R.id.iptRePassword);

        btnRegistar = (Button) findViewById(R.id.btnRegistrar);
        btnRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dni = iptDni.getText().toString();
                nombres = iptNombre.getText().toString();
                paterno = iptApaterno.getText().toString();
                materno = iptAmaterno.getText().toString();
                telefono = iptTelefono.getText().toString();
                password = iptPassword.getText().toString();
                rePassword = iptRePassword.getText().toString();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("pDni", dni);
                editor.putString("pNombre", nombres);
                editor.putString("pPaterno", paterno);
                editor.putString("pMaterno", materno);
                editor.putString("pTelefono", telefono);
                editor.commit();

                registrar(dni, nombres, paterno, materno, telefono, password, rePassword);
            }
        });

        txtLogin = (TextView) findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    private void registrar(String dni, String nombres, String paterno, String materno, String telefono, String password, String rePassword) {
        if (!dni.equals("") && !nombres.equals("") && !paterno.equals("") && !materno.equals("") && !telefono.equals("") &&
                !password.equals("") && !rePassword.equals("")){
            if (password.equals(rePassword)){

                insertToNewUsuario(dni, nombres, paterno, materno, telefono, password);

            } else {
                Toast.makeText(getApplicationContext(), "La contraseña no coinciden!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void insertToNewUsuario(final String pDni, final String pNombre, final String pPaterno, final String pMaterno, final String pTelefono, final String pContrasena){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("dni", pDni);
                    jsonObject.put("nombre", pNombre);
                    jsonObject.put("apaterno", pPaterno);
                    jsonObject.put("amaterno", pMaterno);
                    jsonObject.put("telefono", pTelefono);
                    jsonObject.put("contrasena", pContrasena);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/usuarios/registro");
                    httpPost.setEntity(new StringEntity(jsonObject.toString()));
                    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    HttpResponse response = httpClient.execute(httpPost);
                    String body=getResponseBody(response);
                    JSONObject respuesta= new JSONObject(body);
                    String estado = respuesta.getString("estado");

                    switch (estado){
                        case "1":
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                            break;
                        case "-1":
                            Toast.makeText(getApplicationContext(), "Fallo el Login!", Toast.LENGTH_LONG).show();
                            break;
                        case "3":
                           // Toast.makeText(getApplicationContext(), "Error de Data Base", Toast.LENGTH_LONG).show();
                            break;
                        case "4":
                            Toast.makeText(getApplicationContext(), "URL incorrecto!", Toast.LENGTH_LONG).show();
                            break;
                        case "5":
                            Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos!", Toast.LENGTH_LONG).show();
                            break;
                        case "6":
                            Toast.makeText(getApplicationContext(), "Mantenimiento!", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "estado = null!", Toast.LENGTH_LONG).show();
                            break;
                    }
                }catch(ClientProtocolException e) {
                    e.toString();
                }catch(IOException ee) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "success";
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    public static String getResponseBody(HttpResponse response) {

        String response_text = null;
        HttpEntity entity = null;
        try {
            entity = response.getEntity();
            response_text = _getResponseBody(entity);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (IOException e1) {
                }
            }
        }
        return response_text;
    }

    public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException {

        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }

        InputStream instream = entity.getContent();

        if (instream == null) {
            return "";
        }

        if (entity.getContentLength() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(

                    "HTTP entity too large to be buffered in memory");
        }

        String charset = getContentCharSet(entity);

        if (charset == null) {

            charset = HTTP.DEFAULT_CONTENT_CHARSET;

        }

        Reader reader = new InputStreamReader(instream, charset);

        StringBuilder buffer = new StringBuilder();

        try {

            char[] tmp = new char[1024];

            int l;

            while ((l = reader.read(tmp)) != -1) {

                buffer.append(tmp, 0, l);

            }

        } finally {

            reader.close();

        }

        return buffer.toString();

    }

    public static String getContentCharSet(final HttpEntity entity) throws ParseException {

        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }

        String charset = null;

        if (entity.getContentType() != null) {

            HeaderElement values[] = entity.getContentType().getElements();

            if (values.length > 0) {

                NameValuePair param = values[0].getParameterByName("charset");

                if (param != null) {

                    charset = param.getValue();

                }

            }

        }

        return charset;

    }
}
