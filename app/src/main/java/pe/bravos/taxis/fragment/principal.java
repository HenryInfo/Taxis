package pe.bravos.taxis.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.gms.gcm.Task;
import com.google.android.gms.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.plus.Plus;
import com.squareup.picasso.Picasso;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import pe.bravos.taxis.CustomInfoWindowAdapter;
import pe.bravos.taxis.DataParser;
import pe.bravos.taxis.EncenderPantallaActivity;
import pe.bravos.taxis.MainActivity;
import pe.bravos.taxis.MetodosComunes;
import pe.bravos.taxis.R;
import pe.bravos.taxis.model.Movilidad;
import pe.bravos.taxis.model.Ruta;
import pe.bravos.taxis.servicio.TTSService;

import static android.content.Context.LOCATION_SERVICE;
import static pe.bravos.taxis.MainActivity.NOTIFICATION_ID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link principal.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link principal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class principal extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static ArrayList<Address> retList;
    public static GoogleApiClient mGoogleApiClient;
    public static GoogleMap mMap;
    public MainActivity activity;
    NotificationManager manager;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Geocoder geocoder;
    ArrayList<LatLng> MarkerPoints;
    Location mLastLocation;
    Dialog dialog;
    Marker mCurrLocationMarker;
    ProgressDialog progress;
    TextView txtOrigen, txtDestino, txtMyUbicacion, txtAyuda, UnidadSelecionada, lblTiempo, txtPlaca, txtNombre, txtEstado, txtColor;
    LocationRequest mLocationRequest;
    String origen, destino;
    private EditText dni, nombres, amaterno, apaterno, telefono;
    private Button btnEnviar, btnSolicitar, btnAbordar;
    private boolean banderaRegistro=false, banderaPrimera=false;
    LinearLayout botones;
    public static double latitudD=0,longitudO=0, latitudO=0, longitudD=0;
    Polyline polyline=null;
    LatLng origin = null,dest = null;
    LinearLayout lblAvisoTaxi, detallePedido, lyOrigenDestino;
    ArrayList<Movilidad> movilidads = new ArrayList<Movilidad>();
    String pDni, pNombre, pPaterno, pMaterno, pTelefono, fotochofer;
    SharedPreferences preferences;
    LocationManager locationManager;
    private int pIdMovilidad;
    private MapView mapView;

    private ImageView imgUpdate, ImageTaxi, imgChofer;
    //boolean bandera = false;
    private final static int INTERVAL = 5000;
    Handler mHandler;
    private String pDireccionOrigen, origins, destinations, nombre, fotoMovilidad, nombreConductor, telefonoConductor;
    private boolean banderadatosAuto = false;
    ArrayList<Ruta> lista;
    ListView listView;
    private static final int MY_PERMISSIONS_REQUEST_WAKE_LOCK= 1001;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    //Marker inforWindows
    private String idMyRuta, pIdMyRuta, telefonoMyTaxi;

    private ImageButton btnSilenciar;
    private String mParam1;
    private String mParam2;
    private Intent intentTalk;
    private Map<String, String> notificaciones= new HashMap<>();
    private OnFragmentInteractionListener mListener;

    public principal() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment principal.
     */
    // TODO: Rename and change types and number of parameters
    public static principal newInstance(String param1, String param2) {
        principal fragment = new principal();
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
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.activity_maps, container, false);

        intentTalk= new Intent(getActivity(), TTSService.class);
        getActivity().stopService(intentTalk);
        lyOrigenDestino = (LinearLayout) v.findViewById(R.id.lyOrigenDestino);
        imgUpdate = (ImageView) v.findViewById(R.id.imgUpdate);
        btnSilenciar= (ImageButton) v.findViewById(R.id.btnSilenciar);
        btnSolicitar = (Button) v.findViewById(R.id.btnSolicitar);
        btnAbordar = (Button) v.findViewById(R.id.btnAbordar);
        txtDestino= (TextView) v.findViewById(R.id.textDestino);
        detallePedido = (LinearLayout) v.findViewById(R.id.detallePedido);
        ImageTaxi = (ImageView) v.findViewById(R.id.ImageTaxi);
        txtMyUbicacion = (TextView) v.findViewById(R.id.txtMyUbicacion);
        UnidadSelecionada = (TextView) v.findViewById(R.id.UnidadSelecionada);
        lblAvisoTaxi= (LinearLayout) v.findViewById(R.id.lblPedirTaxi);
        txtAyuda=(TextView) v.findViewById(R.id.txtAyuda);
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        botones = (LinearLayout) v.findViewById(R.id.LinearPedidoOpciones);
        txtOrigen= (TextView) v.findViewById(R.id.textOrigen);
        mapView = (MapView) v.findViewById(R.id.map);
        lblTiempo = (TextView) v.findViewById(R.id.lblTiempo);
        imgChofer = (ImageView) v.findViewById(R.id.imgChofer);
        txtPlaca = (TextView) v.findViewById(R.id.txtPlaca);
        txtNombre = (TextView) v.findViewById(R.id.txtNombre);
        txtColor = (TextView) v.findViewById(R.id.txtColor);
        listView = (ListView) v.findViewById(R.id.content);
        txtEstado = (TextView) v.findViewById(R.id.txtEstado);

        //eventos click
        imgUpdate.setOnClickListener(this);
        btnSolicitar.setOnClickListener(this);
        btnAbordar.setOnClickListener(this);
        btnSilenciar.setOnClickListener(this);

        //Visivilidad inicial de elementos
        ImageTaxi.setVisibility(View.GONE);
        UnidadSelecionada.setVisibility(View.GONE);
        lyOrigenDestino.setVisibility(View.VISIBLE);
        detallePedido.setVisibility(View.GONE);
        ImageTaxi.setVisibility(View.GONE);
        imgChofer.setVisibility(View.GONE);
        txtDestino.setVisibility(View.GONE);
        imgUpdate.setVisibility(View.INVISIBLE);
        detallePedido.setVisibility(View.GONE);


        //Preference
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pDireccionOrigen = preferences.getString("pDireccionOrigen", "");
        String idmovlidad=preferences.getString("pIdMovilidad", "");
        if(!equals(idmovlidad,""))
            pIdMovilidad = Integer.parseInt(idmovlidad);
        pDni = preferences.getString("pDni", "");
        pNombre = preferences.getString("pNombre", "");
        pPaterno = preferences.getString("pPaterno", "");
        pMaterno = preferences.getString("pMaterno", "");
        pTelefono = preferences.getString("pTelefono", "");


        // Initializing


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(mapView!=null)
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP) {
            txtMyUbicacion.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.animation));
        } else {

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDisabledAlertToUser();
            }
        }
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //Iniciar listas a usar
        lista = new ArrayList<>();
        MarkerPoints = new ArrayList<>();

        //id de solicitud actual
        idMyRuta = null;

        iniciarServicioGPS();
        return v;
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
    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.imgUpdate:
                limpiar(null);
                LocationChange(mLastLocation);
                imgUpdate.setVisibility(View.INVISIBLE);
                txtDestino.setVisibility(View.GONE);
                UnidadSelecionada.setVisibility(View.GONE);
                txtDestino.setVisibility(View.GONE);
                btnSolicitar.setVisibility(View.GONE);
                break;
            case  R.id.enviarSolicitud://Enviar solicitud de usuario no registrado
                if(!TextUtils.isEmpty(dni.getText())&&
                        !TextUtils.isEmpty(nombres.getText())&&
                        !TextUtils.isEmpty(apaterno.getText())&&
                        !TextUtils.isEmpty(amaterno.getText())&&
                        !TextUtils.isEmpty(telefono.getText())
                        ) {
                    if(dni.getText().toString().length()==8) {
                        if(nombres.getText().toString().trim()!=""&&
                                apaterno.getText().toString().trim()!=""&&
                                telefono.getText().toString().trim()!=""&&
                                amaterno.getText().toString().trim()!=""
                                ){
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
                            insertToDatabaseTask(pDni, pNombre, pPaterno, pMaterno, pTelefono, pIdMovilidad, true);//TRUE por que se va a registrar tbn el usuario
                            dialog.dismiss();
                            detallePedido.setVisibility(View.VISIBLE);
                            ImageTaxi.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if(dni.requestFocus()) {
                            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            Toast.makeText(getActivity().getApplicationContext(), "Dni debe tener 8 caráctees", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Todos los campos son importantes", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnSolicitar:
                btnSolicitar.setEnabled(false);
                if (!equals(pDni,"")){
                    if((mLastLocation!=null&&retList!=null)||origin!=null) {
                        insertToDatabaseTask(pDni, pNombre, pPaterno, pMaterno, pTelefono, pIdMovilidad, false);
                        detallePedido.setVisibility(View.VISIBLE);
                        ImageTaxi.setVisibility(View.VISIBLE);
                    }
                } else {
                    //Si no está registrao
                    dialog = new Dialog(getActivity());
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
                    btnEnviar.setOnClickListener(this);
                }
                break;
            case R.id.btnAbordar:
                getActivity().stopService(intentTalk);//Paramos la alerta "Su taxi lo esta esperando"
                updateEstadoMovilidadTask(0,0, Integer.parseInt(pIdMyRuta), 7);
                btnAbordar.setVisibility(View.GONE);
                break;
            case R.id.btnSilenciar:
                getActivity().stopService(intentTalk);
                btnSilenciar.setVisibility(View.GONE);
                break;

        }


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

    //Iniciar servicio para hablar
    public  void iniciarServicioTalk(String key, String text)
    {
        //Si ya existe la notificacion enviada no se iniciará el servicio nuevamente
        //Key idRuta+estado para enviar por estados
        if(!notificaciones.containsKey(key))
        {
            if(intentTalk!=null&&getActivity()!=null)
            {
                notificaciones.put(key, text);
                intentTalk.putExtra("talk", text);
                getActivity().getBaseContext().startService(intentTalk);
            }
            if(getActivity()!=null){
                Intent i = new Intent(getActivity(), EncenderPantallaActivity.class);
                startActivityForResult(i, 1);
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WAKE_LOCK);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WAKE_LOCK}, MY_PERMISSIONS_REQUEST_WAKE_LOCK);
                } else {
                    turnScreenOn(50, getActivity());
                }
            }

        }
    }
    public static void turnScreenOn(int sec, final Context context)
    {
        final int seconds = sec;

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        if( !isScreenOn )
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(seconds*1000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(seconds*1000);
        }
    }

    public void cambiar(boolean title) {
        dialog= new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_editubica);
        if(title){
            dialog.getWindow().setTitle("Inicio");
            final EditText cambio = ((EditText)dialog.findViewById(R.id.rutaIF));
            cambio.setText(origen);
            ((Button)dialog.findViewById(R.id.cambiarRuta)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    origen= String.valueOf(cambio.getText());
                    txtOrigen.setText(origen);
                    dialog.dismiss();
                }
            });
        } else {
            dialog.getWindow().setTitle("Destino");
            final EditText cambio=((EditText)dialog.findViewById(R.id.rutaIF));
            cambio.setText(destino);
            ((Button)dialog.findViewById(R.id.cambiarRuta)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    destino= String.valueOf(cambio.getText());
                    txtDestino.setText(destino);
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }
    public void limpiar(View v){
        lblAvisoTaxi.setVisibility(View.VISIBLE);
        if(polyline!=null){
            polyline.remove();
        }
        // animar(false);
        origin=null;
        dest=null;
        origen="";
        destino="";
        latitudD=0;
        latitudO=0;
        longitudD=0;
        longitudO=0;
        //txtOrigen.setText("Inicio");
        txtDestino.setText("Destino");
        MarkerPoints.clear();
        mMap.clear();
        UnidadSelecionada.setText("");
        UnidadSelecionada.setVisibility(View.GONE);
        if((nombres!=null&&apaterno!=null&&amaterno!=null&&dni!=null&&telefono!=null)) {
            nombres.setText("");
            apaterno.setText("");
            amaterno.setText("");
            dni.setText("");
            telefono.setText("");
        }
        txtAyuda.setText("Seleccione un taxi");
        txtAyuda.setVisibility(View.VISIBLE);
        imgUpdate.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Inicializar Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Activity context=getActivity();
            if(context!=null)
                if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                    LatLng latLng= new LatLng(-7.16175, -78.5128);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            LatLng latLng= new LatLng(-7.16175, -78.5128);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }

        actualizarMapa();
        pIdMyRuta = preferences.getString("idMyRuta", "");
        if(equals(pIdMyRuta, ""))
            startTaskTaxisDisponibles();
        else {
            traerInfoTaxiTask();
            startTaskTasiSolicitado();
        }
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
    /*
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
    */

    public void LocationChange(Location location) {
        mLastLocation = location;

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //move map camera
        if(location!=null)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            if(!banderaPrimera) {
                banderaPrimera=true;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
            }
            //stop location updates
            /*
            if (mGoogleApiClient != null) {
                try{
                   // LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

                }catch (Exception e)
                {
                    Toast.makeText(getActivity(), "Asegurece de estar conectado a Internet!", Toast.LENGTH_SHORT).show();
                }
            }
            */
            //TODO: mi ubicacion
            List<Address> miAddress = myUbicacion(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            try {
                if(miAddress.size()!=0) {
                    MarkerPoints.add(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                    //dejar de mostar el icono de partida
                    //BitmapDescriptor iconorigen = BitmapDescriptorFactory.fromResource(R.drawable.green);
                    if (MarkerPoints.size() == 1) {
                        latitudO=mLastLocation.getLatitude();
                        longitudO=mLastLocation.getLongitude();
                        origin = MarkerPoints.get(0);
                        options.title("inicio");
                        //  botones.setVisibility(View.VISIBLE);
                    }

                    // Add new marker to the Google Map Android API V2
                    if(MarkerPoints.size() <=2)
                        mMap.addMarker(options);
                    String value=miAddress.get(0).getAddressLine(0);
                    try {
                        value= URLDecoder.decode(value, "UTF-8");
                        String[] partsO = value.split(",");
                        String part1D = partsO[0]; // 004
                        txtOrigen.setText(part1D);
                        origen=value;
                        txtAyuda.setText("Seleccione un taxi");
                        txtMyUbicacion.setVisibility(View.GONE);
                        lblAvisoTaxi.setVisibility(View.VISIBLE);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                Toast.makeText(getActivity(), "Asegurece de estar conectado a Internet!", Toast.LENGTH_SHORT).show();
            }

        }

    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_principal, menu);

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navLlamar:
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.CALL_PHONE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    callPhone();
                }
                break;

            case R.id.navEliminar:
                preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                pIdMyRuta = preferences.getString("idMyRuta", "");
                final String idMovilidad = preferences.getString("pIdMovilidad", "");
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setCancelable(false);
                dialog.setTitle("¿Desea cancelar el pedido?");
                dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity().getBaseContext(), "Estamos cancelando su pedido, un momento por favor", Toast.LENGTH_SHORT).show();
                        updateEstadoMovilidadTask(Integer.parseInt(idMovilidad), 1, Integer.parseInt(pIdMyRuta),5);
                    }
                }).setNegativeButton("No ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getContext(), "No se ", Toast.LENGTH_LONG).show();
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
                break;
        }
        return true;
    }

    public void traerDisponiblesTask() {
        try {
            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    JSONObject jsonObject = new JSONObject();
                    String body = "";
                    //progress.setTitle("Consultando Dni");
                    //Toast.makeText(view.getContext(), "Actualizando", Toast.LENGTH_SHORT).show();
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/movilidades/disponibles");
                        httpPost.setEntity(new StringEntity(jsonObject.toString()));
                        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                        HttpResponse response = httpClient.execute(httpPost);
                        body = MetodosComunes.getResponseBody(response);
                    } catch (ClientProtocolException e) {
                        Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
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
                    //if (!bandera) {
                    try {
                        JSONArray array;
                        if (respuesta != null) {
                            array = respuesta.getJSONArray("datos");
                            //if (MarkerPoints.size() != 0)
                            {
                                movilidads.clear();

                               // if (pIdMyRuta == "")
                                    mMap.clear();
                                    for (int i = 0; i < array.length(); i++) {
                                        int idmovilidad = 0;
                                        int idchofer = 0;
                                        double latitud = 0.0;
                                        double longitud = 0.0;
                                        int estado = 0;
                                        String foto = "";
                                        String nplaca = "";

                                        if (!array.getJSONObject(i).isNull("idmovilidad")) {
                                            idmovilidad = Integer.parseInt(array.getJSONObject(i).get("idmovilidad").toString());
                                        }
                                        if (!array.getJSONObject(i).isNull("idchofer")) {
                                            idchofer = Integer.parseInt(array.getJSONObject(i).get("idchofer").toString());
                                        }
                                        if (!array.getJSONObject(i).isNull("latitud")) {
                                            latitud = Double.parseDouble(array.getJSONObject(i).get("latitud").toString());
                                        }
                                        if (!array.getJSONObject(i).isNull("longitud")) {
                                            longitud = Double.parseDouble(array.getJSONObject(i).get("longitud").toString());
                                        }
                                        if (!array.getJSONObject(i).isNull("estado")) {
                                            estado = Integer.parseInt(array.getJSONObject(i).get("estado").toString());
                                        }
                                        if (!array.getJSONObject(i).isNull("fotomovilidad")) {
                                            fotoMovilidad = array.getJSONObject(i).get("fotomovilidad").toString();
                                        }
                                        if (!array.getJSONObject(i).isNull("foto")) {
                                            foto = array.getJSONObject(i).get("foto").toString();
                                        }
                                        if (!array.getJSONObject(i).isNull("nombre")) {
                                            nombreConductor = array.getJSONObject(i).get("nombre").toString();
                                        }
                                        if (!array.getJSONObject(i).isNull("telefono")) {
                                            telefonoConductor = array.getJSONObject(i).get("telefono").toString();
                                        }
                                        if (!array.getJSONObject(i).isNull("nplaca")) {
                                            nplaca = array.getJSONObject(i).get("nplaca").toString();
                                        }

                                        Movilidad movilidad = new Movilidad(
                                                idmovilidad,
                                                idchofer,
                                                latitud,
                                                longitud,
                                                estado,
                                                fotoMovilidad,
                                                foto,
                                                nombreConductor,
                                                telefonoConductor,
                                                nplaca
                                        );
                                        movilidads.add(movilidad);
                                        // Creating MarkerOptions
                                        MarkerOptions options = new MarkerOptions();
                                        // Setting the position of the marker
                                        LatLng point = new LatLng(movilidad.getLatitud(), movilidad.getLongitud());

                                        options.position(point);

                                        Marker m = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(movilidad.getLatitud(), movilidad.getLongitud())).
                                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi)).
                                                        title(movilidad.getNombre()+"smsb"+movilidad.getnplaca()+"smsb"+movilidad.getFotoConductor()+"smsb"+movilidad.getTelefono())
                                        );
                                        m.setTag(movilidad.getIdmovilidad());
                                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi));
                                        options.title(movilidad.getnplaca());
                                        mMap.addMarker(options);
                                    }
                                }

                        }
                        Calendar calendarNow = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        //final String fecha = ano+"-"+month+"-"+monthDay;
                        final String fecha = dateFormat.format(calendarNow.getTime());
                        //Toast.makeText(getContext(), "Si Actualiza", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    /*
                        } else {
                            Toast.makeText(getActivity(), "No Actualiza", Toast.LENGTH_SHORT).show();
                        }
                        */
                }
            }
            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute();
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
    }

    public void traerInfoTaxiTask() {
        try {
            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    JSONObject jsonObject = new JSONObject();
                    String body = "";
                    try {
                        jsonObject.put("idmovilidad", pIdMovilidad);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/movilidades/consulta");
                        httpPost.setEntity(new StringEntity(jsonObject.toString()));
                        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                        HttpResponse response = httpClient.execute(httpPost);
                        body = MetodosComunes.getResponseBody(response);
                    } catch (ClientProtocolException e) {

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
                        JSONObject array;
                        if (respuesta != null) {
                            JSONObject parentObject = new JSONObject(result);
                            array = parentObject.getJSONObject("datos");
                            if (!banderadatosAuto) {
                                telefonoMyTaxi = array.get("telefono").toString();
                                nombre = array.get("nombre").toString();
                                txtPlaca.setText("Nº PLACA: " + array.get("nplaca").toString());
                                txtNombre.setText("CONDUCTOR: " + nombre);
                                txtColor.setText("TELÉFONO: " + telefonoMyTaxi);
                                if (!array.isNull("foto") && !array.isNull("fotochofer")){
                                    String foto = array.get("foto").toString();
                                    if(!principal.equals(foto,"")){
                                        String imageUrl = "https://reserva.taxipluscajamarca.com/public/files/server/php/files/" + foto;
                                        int pos = imageUrl.lastIndexOf('/') + 1;
                                        URI uri = new URI(imageUrl.substring(0, pos) + Uri.encode(imageUrl.substring(pos)));
                                        new DownloadImageTask(ImageTaxi).execute(uri.toURL());
                                    }
                                    String fotochofer = array.get("fotochofer").toString();
                                    if(!principal.equals(fotochofer, ""))
                                    {
                                        String imageUrl2 = "https://reserva.taxipluscajamarca.com/public/files/server/php/files/" + fotochofer;
                                        int pos2 = imageUrl2.lastIndexOf('/') + 1;
                                        URI uri2 = new URI(imageUrl2.substring(0, pos2) + Uri.encode(imageUrl2.substring(pos2)));
                                        new DownloadImageTask(imgChofer).execute(uri2.toURL());
                                    }
                                }

                                //TODO: agregar foto del auto
                                banderadatosAuto = true;
                            }

                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void traerTaxiSolicitado() {
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
                        jsonObject.put("idmovilidad", pIdMovilidad);
                        jsonObject.put("idruta", pIdMyRuta);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/rutas/mipedidocliente");
                        httpPost.setEntity(new StringEntity(jsonObject.toString()));
                        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                        HttpResponse response = httpClient.execute(httpPost);
                        body = MetodosComunes.getResponseBody(response);
                        if (body == null){
                            Toast.makeText(getActivity(), "Haga un Pedido para ver su estado!", Toast.LENGTH_LONG).show();
                        }
                    }catch(ClientProtocolException e) {
                        Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_LONG).show();
                    }catch(IOException ee) {

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
                            if(array.length()==0)
                            {
                                stopTaskTasiSolicitado();
                                startTaskTaxisDisponibles();
                                SharedPreferences.Editor editor2 = preferences.edit();
                                editor2.putString("idMyRuta", "");
                                editor2.putString("pdIdMovilidad", "");
                                editor2.apply();
                            }
                            for(int i=0; i<array.length(); i++) {
                                int idruta=0;
                                int idmovilidad=0;
                                double latitudo=0.0;
                                double latitudd=0.0;
                                double longitudo=0.0;
                                double longitudd=0.0;
                                if(!array.getJSONObject(i).isNull("idruta")) {
                                    idruta=Integer.parseInt(array.getJSONObject(i).get("idruta").toString());
                                }
                                if(!array.getJSONObject(i).isNull("idmovilidad")) {
                                    idmovilidad=Integer.parseInt(array.getJSONObject(i).get("idmovilidad").toString());
                                }if(!array.getJSONObject(i).isNull("latitudo")) {
                                    latitudo=Double.parseDouble(array.getJSONObject(i).get("latitudo").toString());

                                }if(!array.getJSONObject(i).isNull("latitudd")) {
                                    latitudd=Double.parseDouble(array.getJSONObject(i).get("latitudd").toString());

                                }if(!array.getJSONObject(i).isNull("longitudo")) {
                                    longitudo=Double.parseDouble(array.getJSONObject(i).get("longitudo").toString());

                                }if(!array.getJSONObject(i).isNull("longitudd")) {
                                    longitudd=Double.parseDouble(array.getJSONObject(i).get("longitudd").toString());
                                }

                                String des = array.getJSONObject(i).get("destino").toString();
                                String mosDes = "";
                                if (principal.equals(des, "0")){
                                    mosDes = "";
                                } else {
                                    mosDes = des;
                                }
                                Ruta ruta= new Ruta(
                                        idruta,
                                        idmovilidad,
                                        latitudo,
                                        latitudd,
                                        longitudo,
                                        longitudd,
                                        array.getJSONObject(i).get("estado").toString().charAt(0),
                                        Double.parseDouble(array.getJSONObject(i).get("precio").toString()),
                                        array.getJSONObject(i).get("origen").toString(),
                                        mosDes,
                                        ""
                                );

                                //Actualizamos ubicacion de taxi
                                mMap.clear();
                                Double latitud = Double.parseDouble(String.valueOf(array.getJSONObject(i).get("latitudm").toString()));
                                Double longitud = Double.parseDouble(array.getJSONObject(i).get("longitudm").toString());
                                MarkerOptions mp = new MarkerOptions();
                                mp.position(new LatLng(latitud, longitud));
                                mp.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi));
                                mMap.addMarker(mp);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud, longitud), 16));
                                //FIN Actualizamos ubicacion de taxi

                                //Calculamos el tiempo aprox
                                getTiempoTask(new LatLng(latitud, longitud), new LatLng(ruta.getLatitudO(), ruta.getLongitudO()));

                                char not = ruta.getEstado();
                                String e ="",estadoNoti = "", mensajeVoz="";
                                idMyRuta = String.valueOf(ruta.getId());
                                switch (not) {
                                    case '1':
                                        e = "Solicitando...";
                                        estadoNoti = "1";
                                        mensajeVoz = "Estamos Procesando su solicitud";
                                        btnAbordar.setVisibility(View.GONE);
                                        setHasOptionsMenu(true);

                                        break;
                                    case '2':
                                        e = "En Camino";
                                        estadoNoti = "2";
                                        mensajeVoz = "La movilidad está en camino";
                                        btnAbordar.setVisibility(View.GONE);
                                        setHasOptionsMenu(true);

                                        break;
                                    case '3':
                                        estadoNoti = "3";
                                        mensajeVoz = "Gracias por su preferencia";
                                        stopTaskTasiSolicitado();
                                        e = "Finalizado";
                                        startTaskTaxisDisponibles();
                                        idMyRuta=null;
                                        pIdMovilidad=0;
                                        btnSolicitar.setVisibility(View.GONE);
                                        btnSolicitar.setEnabled(true);//habilitar boton para solicitar de nuevo
                                        btnAbordar.setVisibility(View.GONE);
                                        if(intentTalk==null)
                                        intentTalk= new Intent(getActivity(), TTSService.class);
                                        if(getActivity()!=null)
                                        getActivity().stopService(intentTalk);
                                        limpiar(null);
                                        setHasOptionsMenu(true);

                                        break;
                                    case '4':
                                        mensajeVoz = "La movilidad está ocupada, seleccione otra movilidad. gracias";
                                        stopTaskTasiSolicitado();
                                        startTaskTaxisDisponibles();
                                        idMyRuta=null;
                                        pIdMovilidad=0;
                                        estadoNoti = "4";
                                        limpiar(null);
                                        btnSolicitar.setVisibility(View.GONE);
                                        btnSolicitar.setEnabled(true);//habilitar boton para solicitar de nuevo
                                        lyOrigenDestino.setVisibility(View.GONE);
                                        btnAbordar.setVisibility(View.GONE);
                                        setHasOptionsMenu(false);

                                        break;
                                    case '5':
                                        mensajeVoz = "Usted ha cancelado el servicio";
                                        estadoNoti = "5";
                                        stopTaskTasiSolicitado();
                                        startTaskTaxisDisponibles();
                                        idMyRuta=null;
                                        pIdMovilidad=0;
                                        btnSolicitar.setVisibility(View.GONE);
                                        btnSolicitar.setEnabled(true);//habilitar boton para solicitar de nuevo
                                        limpiar(null);
                                        lyOrigenDestino.setVisibility(View.GONE);
                                        btnAbordar.setVisibility(View.GONE);
                                        setHasOptionsMenu(false);

                                        break;
                                    case '6':
                                        e = "Taxi Llegó";
                                        mensajeVoz = "Su taxi lo está esperando";
                                        btnAbordar.setVisibility(View.VISIBLE);
                                        estadoNoti = "6";
                                        btnSilenciar.setVisibility(View.VISIBLE);
                                        setHasOptionsMenu(true);
                                        break;
                                    case '7':
                                        btnAbordar.setVisibility(View.GONE);
                                        e = "Arribado";
                                        mensajeVoz = "Disfrute de la trayectoria";
                                        estadoNoti = "7";
                                        setHasOptionsMenu(false);

                                        break;
                                }
                                txtEstado.setText(e);
                                SharedPreferences.Editor editor2 = preferences.edit();
                                editor2.putString("idMyRuta", String.valueOf(ruta.getId()));
                                editor2.apply();
                                iniciarServicioTalk(String.valueOf(ruta.getId())+ruta.getEstado(), mensajeVoz);
                                try {
                                    if (principal.equals(estadoNoti,"3")||principal.equals(estadoNoti,("5")) || principal.equals(estadoNoti, "4")){
                                        botones.setVisibility(View.VISIBLE);
                                        lyOrigenDestino.setVisibility(View.VISIBLE);
                                        detallePedido.setVisibility(View.GONE);
                                        ImageTaxi.setVisibility(View.GONE);
                                        imgChofer.setVisibility(View.GONE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("pIdMovilidad", "");
                                        editor.putString("idMyRuta", "");
                                        editor.apply();

                                    } else {
                                        botones.setVisibility(View.GONE);
                                        lyOrigenDestino.setVisibility(View.GONE);
                                        detallePedido.setVisibility(View.VISIBLE);
                                        ImageTaxi.setVisibility(View.VISIBLE);
                                        imgChofer.setVisibility(View.VISIBLE);
                                        imgUpdate.setVisibility(View.GONE);
                                    }
                                } catch (NullPointerException nE){
                                    nE.printStackTrace();
                                }
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void getTiempoTask(LatLng origen, LatLng destino){
        JSONObject jsonObject = new JSONObject();
        String body = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + origen.latitude+"%2C" +origen.longitude+ "&destinations=" + destino.latitude+"%2C"+destino.longitude + "&key=AIzaSyBxR5ZtX21W54nxWfkXWXjOF2qYc50QI4k");
            httpPost.setEntity(new StringEntity(jsonObject.toString()));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            HttpResponse response = httpClient.execute(httpPost);
            body=MetodosComunes.getResponseBody(response);
            JSONObject respuesta= new JSONObject(body);
            JSONArray e = respuesta.getJSONArray("rows");
            JSONObject arr=e.getJSONObject(0);
            JSONArray elements=arr.getJSONArray("elements");
            arr=elements.getJSONObject(0);
            JSONObject distance=arr.getJSONObject("duration");
            String hora = distance.getString("text");
            lblTiempo.setText(hora + " Aprox.");

        }catch(ClientProtocolException e) {
            Log.e("Error", e.getMessage());
        }catch(IOException ee) {
            Log.e("Error", ee.getMessage());
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void updateEstadoMovilidadTask(final int idMovilidad, final int estadomovilidad, final int idruta, final int estadoruta) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                JSONObject jsonObject = new JSONObject();
                //progress.setTitle("Concelado Su Pedido");
                try {
                    jsonObject.put("idruta", idruta);
                    jsonObject.put("estado", estadoruta);
                    jsonObject.put("idmovilidad", idMovilidad);
                    jsonObject.put("estadomovilidad", estadomovilidad);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/rutas/updateEstadoRutaMovilidad");

                    httpPost.setEntity(new StringEntity(jsonObject.toString()));
                    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                    HttpResponse response = httpClient.execute(httpPost);
                    String body = MetodosComunes.getResponseBody(response);
                    JSONObject respuesta = new JSONObject(body);
                    String e = respuesta.getString("estado");

                } catch (ClientProtocolException e) {
                    Toast.makeText(getActivity().getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                } catch (IOException ee) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "success";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(estadoruta==5){
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    //startRepeatingTaskNotificacion();
                    //callAsynchronousTask();
                    if(getActivity()!=null)
                        Toast.makeText(getActivity().getBaseContext(), "Su pedido a sido cancelado, actualizando aplicación", Toast.LENGTH_LONG).show();
                    reiniciarFragment();
                    startTaskTaxisDisponibles();
                    stopTaskTasiSolicitado();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pIdMovilidad", "");
                    editor.putString("idMyRuta", "");
                    editor.apply();
                    //startRepeatingTaskNotificacion();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
    public void insertToDatabaseTask(final String pDni, final String pNombre, final String pPaterno, final String pMaterno,
                                 final String pTelefono, final int pIdMovilidad, final boolean registro){
        //final String d= dni.getText().toString();
        // if(pDni.length()==8) {
        final ProgressDialog pd = new ProgressDialog(getActivity());

        final String[] respuestaTalk = {""};
        if(!registro) {
            pd.setMessage("loading");
            pd.show();
            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    JSONObject jsonObject = new JSONObject();
                    //progress.setTitle("Consultando Dni");
                    try {
                        jsonObject.put("dni", pDni);

                        if (origin==null && dest==null){
                            jsonObject.put("latitudo", mLastLocation.getLatitude());
                            jsonObject.put("latitudd", 0);
                            jsonObject.put("longitudo", mLastLocation.getLongitude());
                            jsonObject.put("longitudd", 0);
                            jsonObject.put("origen", retList.get(0).getAddressLine(0));
                            jsonObject.put("destino", 0);
                        } else {
                            if(origin!=null && dest==null) {
                                jsonObject.put("latitudo", latitudO);
                                jsonObject.put("latitudd", 0);
                                jsonObject.put("longitudo", longitudO);
                                jsonObject.put("longitudd", 0);
                                jsonObject.put("origen", txtOrigen.getText().toString() + ", Cajamarca, Perú");
                                jsonObject.put("destino", 0);
                            } else {
                                jsonObject.put("latitudo", latitudO);
                                jsonObject.put("latitudd", latitudD);
                                jsonObject.put("longitudo", longitudO);
                                jsonObject.put("longitudd", longitudD);
                                jsonObject.put("origen", txtOrigen.getText().toString()+", Cajamarca, Perú");
                                jsonObject.put("destino", txtDestino.getText().toString()+", Cajamarca, Perú");
                            }
                        }
                        jsonObject.put("precio", -1);
                        jsonObject.put("estado", 1);
                        jsonObject.put("idmovilidad", pIdMovilidad);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/clientes/consulta");

                        httpPost.setEntity(new StringEntity(jsonObject.toString()));
                        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                        HttpResponse response = httpClient.execute(httpPost);
                        String body=MetodosComunes.getResponseBody(response);
                        JSONObject respuesta= new JSONObject(body);
                        String estado=respuesta.getString("estado");
                        if(principal.equals(estado,"-3")){
                            banderaRegistro=true;
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("pIdMovilidad", ""+ "");
                            editor.putString("idMyRuta", ""+ "");
                            pIdMyRuta="";
                            editor.apply();
                            pd.dismiss();
                            respuestaTalk[0] ="error";
                        }
                        else {
                            String idRuta=respuesta.getString("ruta");
                            if(!principal.equals(idRuta,"")) {
                                banderaRegistro=true;
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("pIdMovilidad", ""+ pIdMovilidad);
                                editor.putString("idMyRuta", ""+ idRuta);
                                pIdMyRuta=idRuta;
                                editor.apply();
                                respuestaTalk[0] ="success";
                                pd.dismiss();

                            }
                        }

                    }catch(ClientProtocolException e) {
                        Toast.makeText(getActivity().getBaseContext(),e.toString(), Toast.LENGTH_LONG).show();
                    }catch(IOException ee) {

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return respuestaTalk[0];
                }
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if(!principal.equals(result,"error")){
                        stopTaskTaxisDisponibles();
                        startTaskTasiSolicitado();
                        traerInfoTaxiTask();
                        limpiar(null);
                        lyOrigenDestino.setVisibility(View.GONE);

                    }
                    else {
                        Toast.makeText(getContext(), "Unidad no disponible, seleccione una nueva unidad, por favor ", Toast.LENGTH_LONG).show();
                        detallePedido.setVisibility(View.GONE);
                        ImageTaxi.setVisibility(View.GONE);
                        imgChofer.setVisibility(View.GONE);
                        UnidadSelecionada.setText("");
                        btnSolicitar.setVisibility(View.GONE);
                        btnSolicitar.setEnabled(true);
                        txtAyuda.setVisibility(View.VISIBLE);

                    }
                }
            }
            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute();
        } else {
            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    JSONObject jsonObject = new JSONObject();
                    //progress.setTitle("Registrando usuario y ruta");
                    banderaRegistro=false;
                    try {
                        jsonObject.put("dni", pDni);
                        jsonObject.put("nombre", pNombre);
                        jsonObject.put("apaterno", pPaterno);
                        jsonObject.put("amaterno", pMaterno);

                        if (origin==null && dest==null){
                            jsonObject.put("latitudo", mLastLocation.getLatitude());
                            jsonObject.put("latitudd", 0);
                            jsonObject.put("longitudo", mLastLocation.getLongitude());
                            jsonObject.put("longitudd", 0);

                        } else {
                            jsonObject.put("latitudo", latitudO);
                            jsonObject.put("latitudd", latitudD);
                            jsonObject.put("longitudo", longitudO);
                            jsonObject.put("longitudd", longitudD);
                        }
                        jsonObject.put("precio", -1);
                        jsonObject.put("origen", retList.get(0).getAddressLine(0));
                        jsonObject.put("destino", 0);
                        jsonObject.put("estado", 1);
                        jsonObject.put("idmovilidad", pIdMovilidad);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/clientes/registro");
                        httpPost.setEntity(new StringEntity(jsonObject.toString()));
                        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                        HttpResponse response = httpClient.execute(httpPost);
                        String body=MetodosComunes.getResponseBody(response);
                        JSONObject respuesta= new JSONObject(body);
                        String estado=respuesta.getString("estado");
                        if(principal.equals(estado,"-3")){
                            banderaRegistro=true;
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("pIdMovilidad", ""+ "");
                            editor.putString("idMyRuta", ""+ "");
                            pIdMyRuta="";
                            editor.apply();
                            pd.dismiss();
                            respuestaTalk[0] ="error";
                        }
                        else {
                            String idRuta=respuesta.getString("ruta");
                            if(!principal.equals(idRuta,"")) {
                                banderaRegistro=true;
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("pIdMovilidad", ""+ pIdMovilidad);
                                editor.putString("idMyRuta", ""+ idRuta);
                                pIdMyRuta=idRuta;
                                editor.apply();
                                respuestaTalk[0] ="success";
                                pd.dismiss();

                            }
                        }
                    }catch(ClientProtocolException e) {
                        Toast.makeText(getActivity().getBaseContext(),e.toString(), Toast.LENGTH_LONG).show();
                    }catch(IOException ee) {

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return respuestaTalk[0];
                }
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    pd.dismiss();
                    if(!principal.equals(result,"error")){
                        stopTaskTaxisDisponibles();
                        startTaskTasiSolicitado();
                        traerInfoTaxiTask();
                        limpiar(null);
                        lyOrigenDestino.setVisibility(View.GONE);

                    }
                    else {
                        Toast.makeText(getContext(), "Unidad no disponible, seleccione una nueva unidad, por favor ", Toast.LENGTH_LONG).show();
                        detallePedido.setVisibility(View.GONE);
                        ImageTaxi.setVisibility(View.GONE);
                        imgChofer.setVisibility(View.GONE);
                        UnidadSelecionada.setText("");
                        btnSolicitar.setVisibility(View.GONE);
                        btnSolicitar.setEnabled(true);
                        txtAyuda.setVisibility(View.VISIBLE);

                    }
                }
            }
            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute();
        }
        traerInfoTaxiTask();
    }
    //Inicialización de asyncTask

    Runnable mHandlerTaskTaxisDisponibles = new Runnable() {
        @Override
        public void run() {
            traerDisponiblesTask();
            mHandler.postDelayed(mHandlerTaskTaxisDisponibles, INTERVAL);
        }
    };


    Runnable mHandlerTaskNotificacion = new Runnable() {
        @Override
        public void run() {
            traerTaxiSolicitado();
            mHandler.postDelayed(mHandlerTaskNotificacion, INTERVAL);
        }
    };

    public void startTaskTaxisDisponibles() {
        mHandlerTaskTaxisDisponibles.run();
    }

    public void stopTaskTaxisDisponibles() {
        mHandler.removeCallbacks(mHandlerTaskTaxisDisponibles);
    }

    public void startTaskTasiSolicitado() {
        mHandlerTaskNotificacion.run();
    }
    public void stopTaskTasiSolicitado() {
        mHandler.removeCallbacks(mHandlerTaskNotificacion);
    }


    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("El GPS de su dispositivo esta desactivado, para un mejor servicio por favor Activelo")
                .setCancelable(false)
                .setPositiveButton("Activar", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                        mapView.getMapAsync(principal.this);
                    }
                });
        alertDialogBuilder.setNegativeButton("Calcelar", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                //TODO:
                // startActivity(new Intent(getActivity().getApplicationContext(), prin.class));
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //Prompt the user once explanation has been shown

                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                Toast.makeText(getContext(), "Procure tener la precisión alta", Toast.LENGTH_LONG).show();
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                showGPSDisabledAlertToUser();
            }
            return true;
        }
    }
    public static List<Address> myUbicacion(double lat, double lng) {
        String address = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
                + Locale.getDefault().getCountry(), lat, lng);
        HttpGet httpGet = new HttpGet(address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
            String line=null;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            retList = new ArrayList<Address>();

            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    String indiStr = result.getString("formatted_address");
                    Address addr = new Address(Locale.getDefault());
                    addr.setAddressLine(0, indiStr);
                    retList.add(addr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retList;
    }

    public static class DownloadImageTask extends AsyncTask<URL, Void, Bitmap> {

        private static final String LOG_E_TAG = "DownloadImageTask";
        private final WeakReference<ImageView> containerImageView;

        public DownloadImageTask(ImageView imageView) {
            this.containerImageView = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            URL imageURL = params[0];
            Bitmap downloadedBitmap = null;
            try {
                InputStream inputStream = imageURL.openStream();
                downloadedBitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                Log.e(LOG_E_TAG, e.getMessage());
                e.printStackTrace();
            }
            return downloadedBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            ImageView imageView = this.containerImageView.get();
            if (imageView != null) {
                imageView.setImageBitmap(result);
            }
        }
    }

    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + telefonoMyTaxi));
        if (ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        }
    }

    public void reiniciarFragment(){
        if(getFragmentManager()!=null){
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, new principal()).commit();
        }
    }

    public void actualizarMapa() {
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                String title=arg0.getTitle();
                if(principal.equals(title, "inicio")) {
                    origin=arg0.getPosition();
                } else {
                    dest=arg0.getPosition();
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                if (MarkerPoints.size() >= 2) {
                    //dibujarRuta();
                    try {
                        mostrarNombres(false);
                        mostrarNombres(true);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
                String title=arg0.getTitle();
                if(principal.equals(title, "inicio")) {
                    origin=arg0.getPosition();
                    try {
                        mostrarNombres(false);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    dest=arg0.getPosition();
                    try {
                        mostrarNombres(true);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (idMyRuta == null) {
                    try {
                        pIdMovilidad = (int) marker.getTag();
                    } catch (Exception e){

                    }
                    String title = marker.getTitle();
                    if(title!=null){
                        if (principal.equals(title,"fin")) {
                            cambiar(false);
                        } else if (principal.equals(title,("inicio"))) {
                            cambiar(true);
                        } else {
                            if (marker.getTag() != null) {
                                txtAyuda.setVisibility(View.GONE);
                                String[] infom = marker.getTitle().split("smsb");
                                UnidadSelecionada.setText("PLACA SELECCIONADA: " + infom[1]);
                                UnidadSelecionada.setVisibility(View.VISIBLE);
                                btnSolicitar.setVisibility(View.VISIBLE);
                                String datitos = nombre + "-" + imgChofer;
                                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getActivity()), title));
                            }
                        }
                    }
                    imgUpdate.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });


    }
    private void iniciarServicioGPS() {

        android.location.LocationListener locationListenerRED = new MyLocationReD();
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION,getActivity())) {

            locationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER, 10000, 1, locationListenerRED);        }
        else {
            Toast.makeText(getContext(), "Insufficent Permissions, Please grant this app GPS permissions to continue.",Toast.LENGTH_LONG).show();
        }

    }
    public static boolean isPermissionGranted(String permission, Context c) {
        //int res = ContextCompat.checkSelfPermission(context, permission);
        return (ContextCompat.checkSelfPermission(c, permission) == PackageManager.PERMISSION_GRANTED);
    }
    public class MyLocationReD implements android.location.LocationListener {


        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            mLastLocation = loc;
            LocationChange(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            // Toast.makeText(getApplicationContext(), "GPS Desactivado", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            //Toast.makeText(getApplicationContext(), "GPS Activado", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Este metodo se ejecuta cada vez que se detecta un cambio en el
            // status del proveedor de localizacion (GPS)
            // Los diferentes Status son:
            // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
            // TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
            // espera que este disponible en breve
            // AVAILABLE -> Disponible
        }

    }/* Fin de la clase localizacion */


    private void mostrarNombres(boolean tipo) throws UnsupportedEncodingException {
        geocoder = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
        if(tipo) {
            latitudD=dest.latitude;
            longitudD=dest.longitude;
            List<Address> addresses =myUbicacion(latitudD, longitudD);
            if(addresses.size()!=0) {
                String value=addresses.get(0).getAddressLine(0);
                value= URLDecoder.decode(value, "UTF-8");
                String[] partsD = value.split(",");
                String part1D = partsD[0]; // 004
                txtDestino.setText(part1D.toString());
            }
        }
        else {
            latitudO = origin.latitude;
            longitudO = origin.longitude;
            List<Address> addresses = myUbicacion(latitudO, longitudO);
            if(addresses.size()!=0){
                String value=addresses.get(0).getAddressLine(0);
                value= URLDecoder.decode(value, "UTF-8");
                String[] partsO = value.split(",");
                String part1O = partsO[0]; // 004
                txtOrigen.setText(part1O);
            }
        }
    }

    public  synchronized void buildGoogleApiClient() {

        /*
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                    .addApi(LocationServices.API)
                    .build();
            Log.i("Api is",""+mGoogleApiClient);
        }
        mGoogleApiClient.connect();
*/
    }

}