package pe.bravos.taxis.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;

import pe.bravos.taxis.CustomArrayAdapter;
import pe.bravos.taxis.MainActivity;
import pe.bravos.taxis.R;
import pe.bravos.taxis.model.Ruta;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrdenRapida.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrdenRapida#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdenRapida extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ProgressDialog progress;
    ArrayList<Ruta> lista;
    private EditText  dni, nombres, amaterno, apaterno, telefono;
    ListView listView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    boolean banderaRegistro=false;
    private OnFragmentInteractionListener mListener;
    EditText txtDni=null;
    EditText dniOter=null;
    EditText txtOrigen=null;
    EditText txtDestino=null;
    Dialog dialog;
    LinearLayout botones, registro;
    private Button btnEnviar;
    String pDni, pNombre, pPaterno, pMaterno, pTelefono;
    SharedPreferences preferences;

    public OrdenRapida() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyOrders.
     */
    // TODO: Rename and change types and number of parameters
    public static OrdenRapida newInstance(String param1, String param2) {
        OrdenRapida fragment = new OrdenRapida();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pDni = preferences.getString("pDni", "");
        pNombre = preferences.getString("pNombre", "");
        pPaterno = preferences.getString("pPaterno", "");
        pMaterno = preferences.getString("pMaterno", "");
        pTelefono = preferences.getString("pTelefono", "");

        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.activity_pedido, container, false);
       //txtDni=  (EditText) view.findViewById(R.id.txtDni);
       txtOrigen= (EditText) view.findViewById(R.id.txtOrigen);
        //txtDestino= (EditText) view.findViewById(R.id.txtDestino);
        final Button btnSolicitar= (Button) view.findViewById(R.id.btnSave);
        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String origen = txtOrigen.getText().toString();
                if (!origen.equals("")){
                    if (!pDni.equals("")){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setCancelable(false);
                        dialog.setTitle("¿Desea confirmar el pedido?");
                        dialog.setMessage("Pedido para " + txtOrigen.getText().toString() );
                        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Action for "Delete".
                                insertToDatabase(pDni, pNombre, pPaterno, pMaterno, pTelefono);
                                Toast.makeText(getActivity().getBaseContext(), "Su taxi llegara en unos minutos", Toast.LENGTH_LONG).show();
                            }
                        })
                                .setNegativeButton("No ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Action for "Cancel".
                                        Toast.makeText(getActivity().getBaseContext(), "Cancelo el pedido", Toast.LENGTH_LONG).show();
                                    }
                                });

                        final AlertDialog alert = dialog.create();
                        alert.show();
                    } else {
                        //TODO: registrar
                        dialog = new Dialog(getActivity().getBaseContext());
                        dialog.setContentView(R.layout.dialog_main);
                        dialog.getWindow().setTitle("Ingrese datos");
                        banderaRegistro = true;

                        btnEnviar= (Button) dialog.findViewById(R.id.enviarSolicitud);
                        dni = (EditText) dialog.findViewById(R.id.dni);
                        nombres = (EditText) dialog.findViewById(R.id.nombres);
                        apaterno = (EditText) dialog.findViewById(R.id.apaterno);
                        amaterno= (EditText) dialog.findViewById(R.id.amaterno);
                        telefono= (EditText) dialog.findViewById(R.id.telefono);
                        dialog.show();

                        btnEnviar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("pDni", dni.getText().toString());
                                editor.putString("pNombre", nombres.getText().toString());
                                editor.putString("pPaterno", apaterno.getText().toString());
                                editor.putString("pMaterno", amaterno.getText().toString());
                                editor.putString("pTelefono", telefono.getText().toString());
                                editor.commit();

                                pDni = preferences.getString("pDni", "");
                                pNombre = preferences.getString("pNombre", "");
                                pPaterno = preferences.getString("pPaterno", "");
                                pMaterno = preferences.getString("pMaterno", "");
                                pTelefono = preferences.getString("pTelefono", "");

                                insertToDatabase(pDni, pNombre, pPaterno, pMaterno, pTelefono);

                                dialog.dismiss();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "Ingrese una Dirección válida", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;

    }

    public void insertToDatabase(final String pDni, final String pNombre, final String pPaterno, final String pMaterno, final String pTelefono){
        //final String d= txtDni.getText().toString();
        if(pDni.length()==8) {
            progress = new ProgressDialog(getActivity());
            progress.setTitle("Consultado");
            progress.setMessage("Por favor espere...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            if(!banderaRegistro) {
                class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                    @Override
                    protected String doInBackground(String... params) {
                        JSONObject jsonObject = new JSONObject();
                        progress.setTitle("Consultando Dni");
                        try {
                            jsonObject.put("dni", pDni);

                            jsonObject.put("latitudo", 0);
                            jsonObject.put("latitudd", 0);
                            jsonObject.put("longitudo", 0);
                            jsonObject.put("longitudd", 0);

                            jsonObject.put("precio", -1);
                            jsonObject.put("origen", txtOrigen.getText().toString());
                            jsonObject.put("destino", 0);
                            jsonObject.put("estado", 1);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {

                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/clientes/consulta");

                            httpPost.setEntity(new StringEntity(jsonObject.toString()));
                            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                            HttpResponse response = httpClient.execute(httpPost);
                            String body=getResponseBody(response);
                            JSONObject respuesta= new JSONObject(body);
                            String e=respuesta.getString("estado");
                            if(!e.equals("5")) {
                                banderaRegistro=true;
                                //progress.hide();
                            }
                        }catch(ClientProtocolException e) {
                            Toast.makeText(getActivity().getBaseContext(),e.toString(), Toast.LENGTH_LONG).show();
                        }catch(IOException ee) {

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return "success";
                    }
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        progress.dismiss();
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_main, new MyOrders(), "NewFragmentTag");
                        ft.commit();
                    }
                }
                SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
                sendPostReqAsyncTask.execute();
            }
            else {
                class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                    @Override
                    protected String doInBackground(String... params) {
                        JSONObject jsonObject = new JSONObject();
                        //   progress.setTitle("Registrando usuario y ruta");
                        banderaRegistro=false;
                        try {
                            jsonObject.put("nombre", pNombre);
                            jsonObject.put("apaterno", pPaterno);
                            jsonObject.put("amaterno", pMaterno);
                            jsonObject.put("dni", pDni);
                            jsonObject.put("telefono", pTelefono);
                            jsonObject.put("latitudo", 0);
                            jsonObject.put("latitudd", 0);
                            jsonObject.put("longitudo", 0);
                            jsonObject.put("longitudd", 0);
                            jsonObject.put("precio", -1);
                            jsonObject.put("origen", txtOrigen.getText().toString());
                            jsonObject.put("destino", 0);
                            jsonObject.put("estado", 1);
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
                            String e=respuesta.getString("estado");
                        }catch(ClientProtocolException e) {
                            Toast.makeText(getActivity().getBaseContext(),e.toString(), Toast.LENGTH_LONG).show();
                        }catch(IOException ee) {

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return "success";
                    }
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        dialog.dismiss();
                        progress.dismiss();
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_main, new MyOrders(), "NewFragmentTag");
                        ft.commit();
                    }
                }
                SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
                sendPostReqAsyncTask.execute();
            }

        }
        else {
            txtDni.setError("Dni debe tener 8 carácteres");

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
