package pe.bravos.taxis;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import pe.bravos.taxis.fragment.MyOrders;
import pe.bravos.taxis.model.Ruta;


import static pe.bravos.taxis.MainActivity.NOTIFICATION_ID;
/**
 * Created by hbs on 1/10/16.
 */

public class CustomArrayAdapter extends ArrayAdapter<Ruta> implements
        View.OnClickListener {
    //private TextView resultadoTodal;
    private LayoutInflater layoutInflater;
    List<Ruta> rutas=null;
    private SharedPreferences preferences;
    private Button b, cancelar;
    ProgressDialog progress;
    Dialog dialog;
    private ImageView downloadedImg, imgAuto;
    private ProgressDialog simpleWaitDialog;
    String imageUrl = "";
    BitmapFactory.Options bmOptions;
    TextView ruteEstado, txtFecha;
    Context context;

    int idSlected=0;
    // private boolean bandera=false;
    public CustomArrayAdapter(Context context, List<Ruta> objects, ProgressDialog p) {
        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);
        rutas=objects;
        this.context=context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // holder pattern
        CustomArrayAdapter.Holder holder = null;

        if (convertView == null) {
            holder = new CustomArrayAdapter.Holder();

            convertView = layoutInflater.inflate(R.layout.ruta, parent, false);
            //ImageView im=(ImageView)convertView.findViewById(R.id.image);
            //im.setOnClickListener(this);
            //holder.setImage(im);
            //convertView.setTag(holder);
        } else {
            holder = (CustomArrayAdapter.Holder) convertView.getTag();
        }


        final Ruta row = getItem(position);
        //holder.getImageView().setImageResource(row.getId());
        //  if(!bandera)
        {
            // bandera=true;

            String stringD = row.getDestino();
            String[] partsD = stringD.split(",");
            String part1D = partsD[0]; // 004
            //((TextView)convertView.findViewById(R.id.ruteDestino)).setText(part1D.toString());
            String stringO = row.getOrigen();
            String[] partsO = stringO.split(",");
            String part1O = partsO[0]; // 004
            ((TextView)convertView.findViewById(R.id.ruteOrigen)).setText(part1O);

            //b = (Button) convertView.findViewById(R.id.VerMovilidad);
            //b.setTag(row.getId());
            //b.setOnClickListener(this);
            //b.setVisibility(View.GONE);

            cancelar = (Button) convertView.findViewById(R.id.CancelarPedido);
            cancelar.setTag(row.getId());
            cancelar.setOnClickListener(this);

            char estado = row.getEstado();
            //char id = row.get
            double precio=row.getPrecio();
            if(precio==-1) {
                ((TextView)convertView.findViewById(R.id.rutePrecio)).setText("No asignado");
            }
            else {
                ((TextView)convertView.findViewById(R.id.rutePrecio)).setText(row.getPrecio().toString());
            }
            String e ="";
            String estadoNoti = "";
            switch (estado) {
                case '1':
                    e="Solicitando...";
                    estadoNoti = "1";
                    //sendNotification(e);
                    break;
                case '2':
                    e="En Camino";
                    estadoNoti = "2";
                    sendNotification(e);
                    //Context context = getContext();
                    break;
                case '3':
                    //e="Finalizado";
                    estadoNoti = "3";
                    //sendNotification(e);
                    break;
                case '4':
                    //e="Ocupado";
                    estadoNoti = "4";
                    //sendNotification(e);
                    break;
                case '5':
                    //e="Cancelado";
                    estadoNoti = "5";
                    //sendNotification(e);
                    break;
                case '6':
                    e="Taxi Llegó";
                    estadoNoti = "6";
                    sendNotification(e);
                    break;
            }
            ((TextView)convertView.findViewById(R.id.ruteEstado)).setText(e);
            //txtFecha = (TextView)convertView.findViewById(R.id.txtFecha);
            //txtFecha.setVisibility(View.GONE);
            /*
            SharedPreferences.Editor editor2 = preferences.edit();
            editor2.putString("pNoti", estadoNoti.toString());
            editor2.commit();
            */

        }
        return convertView;
    }


    public void onClick(final View v) {
        //Siempre y cuando v.getTag() guarde el idRuta;
        Ruta ruta = null;
        for (Ruta r: rutas) {
            if (r.getId() == (int)v.getTag()){
                ruta = r;
                break;
            }
        }
        switch (v.getId()){
            case R.id.CancelarPedido:
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setCancelable(false);
                dialog.setTitle("¿Desea cancelar el pedido?");
                //dialog.setMessage("Ped");
                final Ruta ruta1 = ruta;
                dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        updateEstado(ruta1.getId());
                    }
                })
                        .setNegativeButton("No ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getContext(), "No se ", Toast.LENGTH_LONG).show();
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
                break;
            default:
                break;
        }

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

    /**
     * Set the background of a row based on the value of its checkbox value.
     * Checkbox has its own style.
     */
    @SuppressWarnings("deprecation")
    private void changeBackground(Context context, CheckBox checkBox) {
        // View row = (View) checkBox.getParent();
        //Drawable drawable;
        //if (checkBox.isChecked()) {
        //  drawable = context.getResources().getDrawable(
        //         R.drawable.listview_selector_checked);
        //} else {
        //   drawable = context.getResources().getDrawable(
        //         R.drawable.listview_selector);
        //}
        //row.setBackgroundDrawable(drawable);
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

    static class Holder {
        ImageView image;
        public ImageView getImageView()
        {
            return image;
        }
        public void setImage(ImageView i){
            this.image=i;
        }

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


    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(getContext(), MyOrders.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.local_taxi)
                .setContentTitle("Estado de Su Pedido: ")
                .setSubText(msg)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText("Ver detalles!");

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void updateEstado(final int idRuta) {
        progress = new ProgressDialog(layoutInflater.getContext());
        progress.setTitle("Cancelando su Pedido");
        progress.setMessage("Por favor espere...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("idruta", idRuta);
                    jsonObject.put("estado", 5);
                    jsonObject.put("idmovilidad", 0);
                    jsonObject.put("estadomovilidad", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/rutas/updateEstadoRutaMovilidad");

                    httpPost.setEntity(new StringEntity(jsonObject.toString()));
                    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                    HttpResponse response = httpClient.execute(httpPost);
                    String body = getResponseBody(response);
                    JSONObject respuesta = new JSONObject(body);
                    String e = respuesta.getString("estado");

                } catch (ClientProtocolException e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                } catch (IOException ee) {

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
                Toast.makeText(context, "Su pedido a sido cancelado", Toast.LENGTH_LONG).show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

}