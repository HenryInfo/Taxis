package pe.bravos.taxis.model;

import android.os.AsyncTask;

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

public class insertToUsuario {

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

    public void insertToLogin(final String pDni, final String pContrasena){

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("dni", pDni);
                    jsonObject.put("contrasena", pContrasena);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("https://reserva.taxipluscajamarca.com/webapi/v1/usuarios/login");
                    httpPost.setEntity(new StringEntity(jsonObject.toString()));
                    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    HttpResponse response = httpClient.execute(httpPost);
                    String body=getResponseBody(response);
                    JSONObject respuesta= new JSONObject(body);
                    String estado = respuesta.getString("estado");

                }catch(ClientProtocolException e) {
                    e.toString();
                }catch(IOException ee) {

                }catch (JSONException e) {
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
