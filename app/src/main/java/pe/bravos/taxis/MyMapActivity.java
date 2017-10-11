package pe.bravos.taxis;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;


public class MyMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int idmovilidad;
    private RelativeLayout linerInfo=null;
    private boolean banderadatosAuto = false;
    SharedPreferences preferences;
    private String pDireccionOrigen, origins, destinations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle datos = this.getIntent().getExtras();
        idmovilidad = datos.getInt("idmovilidad");
        linerInfo=(RelativeLayout)findViewById(R.id.LinearLayoutMyOrder);
        callAsynchronousTask();

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        pDireccionOrigen = preferences.getString("pDireccionOrigen", "");
        //pDireccionDestino = preferences.getString("pDireccionDestino", "");

        origins = pDireccionOrigen.replace(" ", "");
        //destinations = pDireccionDestino.replace(" ", "");
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();

        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                            @Override
                            protected String doInBackground(String... params) {
                                JSONObject jsonObject = new JSONObject();
                                String body="";
                                try {
                                    jsonObject.put("idmovilidad", idmovilidad);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    HttpClient httpClient = new DefaultHttpClient();
                                    HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/movilidades/consulta");
                                    httpPost.setEntity(new StringEntity(jsonObject.toString()));
                                    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                                    HttpResponse response = httpClient.execute(httpPost);
                                    body=getResponseBody(response);
                                }catch(ClientProtocolException e) {

                                }catch(IOException ee) {

                                }
                                return body;
                            }
                            @Override
                            protected void onPostExecute(String result) {
                                super.onPostExecute(result);
                                JSONObject respuesta= null;
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

                                        if (!banderadatosAuto){
                                            ImageView downloadedImg = (ImageView) linerInfo.findViewById(R.id.ImageTaxi);
                                            ((TextView)linerInfo.findViewById(R.id.txtPlaca)).setText("Nº PLACA: "+array.get("nplaca").toString());
                                            ((TextView)linerInfo.findViewById(R.id.txtNombre)).setText("CONDUCTOR: "+array.get("nombre").toString());
                                            ((TextView)linerInfo.findViewById(R.id.txtColor)).setText("TELEFONO: "+array.get("telefono").toString());
                                            // GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(downloadedImg);
                                            //Glide.with(v.getContext()).load(R.raw.loading).into(imageViewTarget);
                                            String foto=array.get("foto").toString();
                                            String imageUrl="https://reserva.taxipluscajamarca.com/public/files/server/php/files/"+foto;
                                            int pos = imageUrl.lastIndexOf('/') + 1;
                                            URI uri = new URI(imageUrl.substring(0, pos) + Uri.encode(imageUrl.substring(pos)));
                                            new DownloadImageTask(downloadedImg).execute(uri.toURL());
                                            //TODO: agregar foto del auto
                                            banderadatosAuto = true;
                                        }

                                        mMap.clear();

                                        Double latitud = Double.parseDouble(String.valueOf(array.get("latitud")));
                                        Double longitud = Double.parseDouble(String.valueOf(array.get("longitud")));
                                        MarkerOptions mp= new MarkerOptions();
                                        mp.position(new LatLng(latitud, longitud));
                                        mp.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi));
                                        mMap.addMarker(mp);
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud, longitud), 16));

                                        getAddress(latitud, longitud);
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }

                            }
                        }
                        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
                        sendPostReqAsyncTask.execute();
                    }
                });
            }

        };
        timer.schedule(doAsynchronousTask, 0, 1000);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
    }

    public class DownloadImageTask extends AsyncTask<URL, Void, Bitmap> {

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

    public void getTiempo(String origins, String destinations){
        JSONObject jsonObject = new JSONObject();
        String body = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + origins + ",Cajamarca,Perú&destinations=" + destinations + ",Cajamarca,Perú&key=AIzaSyBxR5ZtX21W54nxWfkXWXjOF2qYc50QI4k");
            //HttpPost httpPost = new HttpPost("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+o+"&destinations="+d +"&key=AIzaSyBxR5ZtX21W54nxWfkXWXjOF2qYc50QI4k");
            httpPost.setEntity(new StringEntity(jsonObject.toString()));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            HttpResponse response = httpClient.execute(httpPost);
            body=getResponseBody(response);
            JSONObject respuesta= new JSONObject(body);
            JSONArray e = respuesta.getJSONArray("rows");

            if (respuesta != null) {
                JSONObject arr=e.getJSONObject(0);
                JSONArray elements=arr.getJSONArray("elements");
                arr=elements.getJSONObject(0);
                JSONObject distance=arr.getJSONObject("duration");
                String hora = distance.getString("text");

                    ((TextView)linerInfo.findViewById(R.id.lblTiempo)).setText(hora.toString() + " Aprox.");

            }

        }catch(ClientProtocolException e) {
            Log.e("Error", e.getMessage());
        }catch(IOException ee) {
            Log.e("Error", ee.getMessage());
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void getAddress(Double latitud2, Double longitud2){
        JSONObject jsonObject = new JSONObject();
        String body = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitud2+","+longitud2+"&key=AIzaSyBxR5ZtX21W54nxWfkXWXjOF2qYc50QI4k");
            httpPost.setEntity(new StringEntity(jsonObject.toString()));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            HttpResponse response = httpClient.execute(httpPost);
            body=getResponseBody(response);
            JSONObject respuesta= new JSONObject(body);
            JSONArray e = respuesta.getJSONArray("results");

                JSONObject arr = e.getJSONObject(0);
                String dir = arr.getString("formatted_address").toString();

                destinations = dir.replace(" ", "");

            getTiempo(origins, destinations);

        }catch(ClientProtocolException e) {
            Log.e("Error", e.getMessage());
        }catch(IOException ee) {
            Log.e("Error", ee.getMessage());
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }
    }

}
