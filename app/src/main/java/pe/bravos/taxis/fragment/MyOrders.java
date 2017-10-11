package pe.bravos.taxis.fragment;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import pe.bravos.taxis.CustomArrayAdapter;
import pe.bravos.taxis.LoginActivity;
import pe.bravos.taxis.PreferencesActivity;
import pe.bravos.taxis.R;
import pe.bravos.taxis.SignupActivity;
import pe.bravos.taxis.model.Ruta;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyOrders.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyOrders#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyOrders extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ProgressDialog progress;
    ArrayList<Ruta> lista;
    ListView listView;
    SharedPreferences preferences;
    String pDni;
    private final static int INTERVAL = 10000;
    Handler mHandler;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyOrders() {
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
    public static MyOrders newInstance(String param1, String param2) {
        MyOrders fragment = new MyOrders();
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
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pDni = preferences.getString("pDni", "");

        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_my_orders, container, false);
        //final TextView textView= (TextView) view.findViewById(R.id.dniBuscarText);

        //ImageView iB= (ImageView) view.findViewById(R.id.dniBuscar);
        listView=(ListView)view.findViewById(R.id.content);
        lista= new ArrayList<>();

        if (!pDni.equals("")){
            startRepeatingTask();
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        return view;
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

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            callAsynchronousTask();
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    public void startRepeatingTask() {
        mHandlerTask.run();
    }

    public void stopRepeatingTask() {
        mHandler.removeCallbacks(mHandlerTask);
    }

    public void callAsynchronousTask() {
        try {
            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    JSONObject jsonObject = new JSONObject();
                    String body = "";
                    //progress.setTitle("Consultando Dni");
                    //Toast.makeText(view.getContext(), "Actualizando", Toast.LENGTH_SHORT).show();
                    try {
                        //TODO: Cambiar por dni del logueado
                        jsonObject.put("dni", pDni);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/rutas/buscar");
                        httpPost.setEntity(new StringEntity(jsonObject.toString()));
                        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                        HttpResponse response = httpClient.execute(httpPost);
                        body = getResponseBody(response);

                        if (body.equals(null)) {
                            Toast.makeText(getActivity(), "Haga un Pedido para, ver su estado!", Toast.LENGTH_LONG).show();
                        }

                    } catch (ClientProtocolException e) {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    } catch (IOException ee) {

                    }
                    return body;
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    JSONObject respuesta = null;
                    try {
                        respuesta = new JSONObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONArray array;
                        if (respuesta != null) {
                            array = respuesta.getJSONArray("resultado");
                            lista.clear();
                            for (int i = 0; i < array.length(); i++) {
                                int idmovilidad = 0;
                                double latitudo = 0.0;
                                double latitudd = 0.0;
                                double longitudo = 0.0;
                                double longitudd = 0.0;
                                if (!array.getJSONObject(i).isNull("idmovilidad")) {
                                    idmovilidad = Integer.parseInt(array.getJSONObject(i).get("idmovilidad").toString());
                                }
                                if (!array.getJSONObject(i).isNull("latitudo")) {
                                    latitudo = Double.parseDouble(array.getJSONObject(i).get("latitudo").toString());
                                }
                                if (!array.getJSONObject(i).isNull("latitudd")) {
                                    latitudd = Double.parseDouble(array.getJSONObject(i).get("latitudd").toString());
                                }
                                if (!array.getJSONObject(i).isNull("longitudo")) {
                                    longitudo = Double.parseDouble(array.getJSONObject(i).get("longitudo").toString());
                                }
                                if (!array.getJSONObject(i).isNull("longitudd")) {
                                    longitudd = Double.parseDouble(array.getJSONObject(i).get("longitudd").toString());
                                }

                                String des = array.getJSONObject(i).get("destino").toString();
                                String mosDes = "";
                                if (des.equals("0")) {
                                    mosDes = "";
                                } else {
                                    mosDes = des;
                                }

                                Ruta ruta = new Ruta(
                                        Integer.parseInt(array.getJSONObject(i).get("idruta").toString()),
                                        idmovilidad,
                                        latitudo,
                                        latitudd,
                                        longitudo,
                                        longitudd,
                                        array.getJSONObject(i).get("estado").toString().charAt(0),
                                        Double.parseDouble(array.getJSONObject(i).get("precio").toString()),
                                        array.getJSONObject(i).get("origen").toString(),
                                        mosDes,
                                        array.getJSONObject(i).get("fecha").toString()
                                );
                                lista.add(ruta);
                            }
                            if (getActivity() != null)
                                listView.setAdapter(new CustomArrayAdapter(getActivity(), lista, progress));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute();
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }
}
