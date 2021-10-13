package com.example.watcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.watcher.Model.AlarmaNotificacion;
import com.example.watcher.Model.ApiGeneralRespuesta;
import com.example.watcher.Model.Confirmacion;
import com.example.watcher.Model.ImagenRespuesta;
import com.example.watcher.Model.Resultado;
import com.example.watcher.Model.SupervisionRespuesta;
import com.example.watcher.Utils.RealPathUtil;
import com.example.watcher.Utils.SupervisionService;
import com.example.watcher.Utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.watcher.Utils.Apis.URL_BASE;

public class Supervision extends AppCompatActivity  {

    Spinner spinner;
    ImageView mImagenview;
    Button mChooseBtn, btn_guardar, btn_mapa;
    Uri imageUri;
    TextView tv_abonado, tv_riesgo;
    Context context;
    SupervisionService supervisionService;

    String filePath = "";
    String OPERACION;
    String id_gestion;
    com.example.watcher.Model.Supervision supervision = null;
    AlarmaNotificacion alarma;


    Bitmap bitmap, bitmap_vacio;

    private Retrofit retrofit;
    TextInputEditText et_comentario;

    private static final int PICK_IMAGE = 1;

    TextView tv_coordenadas, tv_hora_llegada, tv_fecha_reporte;
    LinearLayout ly_color_riesgo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_supervision );

        spinner = findViewById( R.id.sp_resultado );
        mImagenview = findViewById( R.id.iv_imagen );
        mChooseBtn = findViewById( R.id.btn_changeImg );
        btn_guardar = findViewById( R.id.btn_guardar );
        btn_mapa = findViewById( R.id.btn_mapa );
        tv_abonado = findViewById( R.id.tv_abonado );
        tv_riesgo = findViewById( R.id.tv_riesgo );
        et_comentario = findViewById( R.id.et_comentario );
        tv_coordenadas = findViewById( R.id.tv_coordenadas );
        tv_hora_llegada = findViewById( R.id.tv_hora_llegada );
        tv_fecha_reporte = findViewById( R.id.tv_fecha_reporte );
        ly_color_riesgo = findViewById( R.id.ly_color_riesgo );

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        Bundle objetoEnviado = getIntent().getExtras();
        OPERACION = getIntent().getStringExtra("OPERACION");

        /*if(objetoEnviado != null) {


        }*/

        if(objetoEnviado != null) {

            switch (OPERACION) {
                case "SUPERVISION":
                    supervision = (com.example.watcher.Model.Supervision) objetoEnviado.getSerializable("supervision");
                    tv_abonado.setText( supervision.getId() + " - " + supervision.getAbonado().replace("- ", "") );
                    id_gestion = supervision.getId();
                    tv_coordenadas.setText( supervision.getLat() + ", " + supervision.getLng() );
                    tv_riesgo.setText( supervision.getRiesgo() );

                    ly_color_riesgo.setBackgroundColor( Color.parseColor( obtenerColorRiesgo(supervision.getId_riesgo()) ) );


                    break;
                default:
                    alarma = (AlarmaNotificacion) objetoEnviado.getSerializable("alarma");
                    tv_abonado.setText( alarma.getId() + " - " + alarma.getAbonado().replace("- ", "") );
                    id_gestion = alarma.getId();
                    tv_coordenadas.setText( alarma.getLat() + ", " + alarma.getLng() );
                    tv_riesgo.setText( "ALTO" );
                    break;
            }

            String hora_llegada = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format( Calendar.getInstance().getTime());
            String fecha_reporte = new SimpleDateFormat("dd/MM/yyyy").format( Calendar.getInstance().getTime());
            tv_hora_llegada.setText( hora_llegada );
            tv_fecha_reporte.setText( fecha_reporte );


        }


        btn_guardar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(), "Seleccione un resultado", Toast.LENGTH_SHORT).show(); return;
                }else if(et_comentario.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese un comentario", Toast.LENGTH_SHORT).show(); return;
                }else if(bitmap == bitmap_vacio) {
                    Toast.makeText(getApplicationContext(), "Seleccione una imagen", Toast.LENGTH_SHORT).show(); return;
                }

                Resultado resultado = new Resultado("", et_comentario.getText().toString(), OPERACION, id_gestion);

                supervisionService = retrofit.create( SupervisionService.class );
                Call<ApiGeneralRespuesta> call = supervisionService.postResultado(resultado);
                call.enqueue( new Callback<ApiGeneralRespuesta>() {
                    @Override
                    public void onResponse(Call<ApiGeneralRespuesta> call, Response<ApiGeneralRespuesta> response) {
                        if(response.isSuccessful()) {
                            ApiGeneralRespuesta respuesta = response.body();
                            enviarImagenData();
                            Toast.makeText(getApplicationContext(), respuesta.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(Supervision.this, Menu.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiGeneralRespuesta> call, Throwable t) {

                    }
                });

                }
        } );

        btn_mapa.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Confirmacion confirmacion = new Confirmacion(true, "", 1, "", "");

                supervisionService = retrofit.create( SupervisionService.class );
                Call<ApiGeneralRespuesta> call = supervisionService.postConfirmacion(confirmacion);
                call.enqueue( new Callback<ApiGeneralRespuesta>() {
                    @Override
                    public void onResponse(Call<ApiGeneralRespuesta> call, Response<ApiGeneralRespuesta> response) {
                        if(response.isSuccessful()) {
                            ApiGeneralRespuesta respuesta = response.body();
                            /*Toast.makeText(getApplicationContext(), respuesta.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(Supervision.this, DestinoRonda.class);
                            startActivity(intent);*/
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiGeneralRespuesta> call, Throwable t) {

                    }
                } );
            }
        } );


        mChooseBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galeria = new Intent();
                galeria.setType( "image/*" );
                galeria.setAction( Intent.ACTION_GET_CONTENT );

                startActivityForResult( Intent.createChooser( galeria, "Seleccionar Imagen" ), PICK_IMAGE );

            }

        } );


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.Resultado, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner.setAdapter( adapter );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if( requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null  ) {
            imageUri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                mImagenview.setImageBitmap(bitmap);

                Uri uri = data.getData();

                /*String pathtt = "";
                String[] projection = { MediaStore.MediaColumns.DATA };
                ContentResolver cr = getApplicationContext().getContentResolver();
                Cursor metaCursor = cr.query( uri, projection, null, null, null );
                if(metaCursor != null) {
                    try {
                        if(metaCursor.moveToFirst()) {
                            pathtt = metaCursor.getString( 0 );
                        }
                    } finally {
                        metaCursor.close();
                    }
                }*/
                /*Context context = getApplicationContext();
                filePath = RealPathUtil.getRealPathFromURI_BelowAPI11( context, uri);*/
                filePath = RealPathUtil.getRealPath(getApplicationContext(), uri);
                Toast.makeText(getApplicationContext(), filePath, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private String obtenerColorRiesgo(int id_riesgo) {

        String color = "";

        switch (id_riesgo) {
            case 1:
                color = "#CBDBA5";
                break;
            case 2:
                color = "#7C9E38";
                break;
            case 3:
                color = "#F1BB3A";
                break;
            case 4:
                color = "#F27354";
                break;
            default:
                color = "#C11F1F";
                break;
        }

        return color;
    }


    public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void enviarImagenData(){

            File file =  new File(filePath);

            OkHttpClient client = new OkHttpClient();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("UploadedImage", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .build();

            Request request = new Request.Builder()
                    .url(URL_BASE + "FileUpload")
                    .post(body)
                    .build();

            client.newCall( request ).enqueue( new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("Ocurrió un error: ", e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                    Log.e("Exitoso: ", response.message());

                    if(response.isSuccessful()) {
                        //Toast.makeText(getApplicationContext(), "Imagen guardada" , Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(getApplicationContext(), "Ocurrió un problema al guardar la imagen" , Toast.LENGTH_SHORT).show();
                    }

                /*if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Ocurrió un problema al guardar la imagen" , Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Imagen guardada" , Toast.LENGTH_SHORT).show();
                    try {
                        String responseStr = response.body().string(); //respuesta del servidor ==> OK
                        Toast.makeText(getApplicationContext(), responseStr , Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject objectResponse = new JSONObject(responseStr);
                            Toast.makeText(getApplicationContext(), "Carga exitosa" , Toast.LENGTH_SHORT).show();
                        } catch (JSONException je) {
                            Toast.makeText(getApplicationContext(), "" + je.getMessage() , Toast.LENGTH_SHORT).show();
                            Log.e("je", "" + je.getMessage());
                        }
                    } catch (Exception e){
                        Toast.makeText(getApplicationContext(), "" + e.getMessage() , Toast.LENGTH_SHORT).show();
                        Log.e("Excep", "" + e.getMessage());
                    }
                }*/

                }
            } );

    }


    public void enviarImagenData2(){
        try{

            //File file = bitmapToFile(getApplicationContext(), bitmap, "test");

            File file = new File( filePath );

            RequestBody requestBody = RequestBody.create( MediaType.parse( "image/jpg" ), file );


            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("UploadedImage", "imagen.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .build();

            MultipartBody.Part part = MultipartBody.Part.createFormData( "uploadFile", file.getName(), body );

            /*OkHttpClient client = new OkHttpClient();
            RequestBody requestFile = new MultipartBody.Builder().setType( MultipartBody.FORM)
                    .addFormDataPart("UploadedImage", file.getName(), RequestBody.create( MediaType.parse("application/octet-stream"), file))
                    .build();*/
            /*RequestBody requestFile = RequestBody.create( MediaType.parse("image/jpg"), file );*/
            //MultipartBody.Part body2 = MultipartBody.Part.createFormData("uploadFile", "uploadFile", requestFile);

            supervisionService = retrofit.create( SupervisionService.class );
            Call<ImagenRespuesta> call = supervisionService.cargarImagen(part);

            call.enqueue( new Callback<ImagenRespuesta>() {
                @Override
                public void onResponse(Call<ImagenRespuesta> call, Response<ImagenRespuesta> response) {
                    if(response.isSuccessful()) {
                        ImagenRespuesta respuesta = response.body();
                        Toast.makeText(getApplicationContext(), respuesta.getValue(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ImagenRespuesta> call, Throwable t) {

                }
            } );

            /*okhttp3.Request request = new okhttp3.Request.Builder().url( URL_BASE + "FileUpload").post(body).build();

            client.newCall( request ).enqueue( new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("uploadFile", "44.Error:" + e.getMessage()); //Error antes de consumir
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    if(response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Imagen guardada", Toast.LENGTH_LONG).show();
                    }
                }
            } );*/

        } catch(Exception exc) {
            Log.e("ExecptionSendImage", "" + exc.getMessage());
        }
    }



    public File bitmapToFile(Context context,Bitmap bitmap, String fileNameToSave) { // File name like "image.png"
        //create a file to write bitmap data
        File file = null;
        try {
            file = new File( Environment.getExternalStorageDirectory() + File.separator + fileNameToSave);
            file.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return file; // it will return null
        }
    }



}

