package pe.bravos.taxis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;


/**
 * Created by ssuar on 11/07/2017.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "CustomInfoWindowAdapter";
    private LayoutInflater inflater;
    private String datos;
    private ImageView imgConductor;
    private TextView txtColor;

    public CustomInfoWindowAdapter(LayoutInflater inflater, String dato){
        this.inflater = inflater;
        datos = dato;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        View v = inflater.inflate(R.layout.conductor, null);

        String[] info = datos.split("smsb");

        try {
            ((TextView) v.findViewById(R.id.txtNombre)).setText("Conductor: " + info[0]);
            ((TextView) v.findViewById(R.id.txtPlaca)).setText("Placa: " + info[1]);
            ((TextView) v.findViewById(R.id.txtColor)).setText("Teléfono: " + info[3]);
            imgConductor = (ImageView) v.findViewById(R.id.imgConductor);

            Picasso.with(v.getContext())
                    .load("https://reserva.taxipluscajamarca.com/public/files/server/php/files/" + info[2])
                    .placeholder(R.drawable.taxi)
                    .resize(80, 80)
                    .centerCrop()
                    .error(R.drawable.ic_delete_forever_pink_a400_36dp)
                    .into(imgConductor);

        }catch (Exception e){

        }
        return v;
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

}
