package com.example.watcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.watcher.Model.ApiGeneralRespuesta;
import com.example.watcher.Model.Confirmacion;
import com.example.watcher.Model.Resultado;
import com.example.watcher.Utils.SupervisionService;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
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


    Bitmap bitmap;
    int PICK_IMAGE_REQUEST = 1;
    String UPLOAD_URL = "http://IP_DEL_WEBSERVICE/ARCHIVO_QUE_RECIBE_LA_PETICION.php";

    String KEY_IMAGE = "foto";
    String KEY_NOMBRE = "nombre";

    private Retrofit retrofit;

    private static final int PICK_IMAGE = 1;


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

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        Bundle objetoEnviado = getIntent().getExtras();
        com.example.watcher.Model.Supervision supervision = null;

        if(objetoEnviado != null) {
            supervision = (com.example.watcher.Model.Supervision) objetoEnviado.getSerializable("supervision");
            tv_abonado.setText( supervision.getId() + " - " + supervision.getAbonado().replace("- ", "") );
            tv_riesgo.setText( supervision.getEstado() );
        }

        btn_guardar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Resultado resultado = new Resultado(1, "");
                supervisionService = retrofit.create( SupervisionService.class );
                Call<ApiGeneralRespuesta> call = supervisionService.postResultado(resultado);
                call.enqueue( new Callback<ApiGeneralRespuesta>() {
                    @Override
                    public void onResponse(Call<ApiGeneralRespuesta> call, Response<ApiGeneralRespuesta> response) {
                        if(response.isSuccessful()) {
                            ApiGeneralRespuesta respuesta = response.body();
                            Toast.makeText(getApplicationContext(), respuesta.getMessage(), Toast.LENGTH_SHORT).show();
                            enviarImagenData();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiGeneralRespuesta> call, Throwable t) {

                    }
                } );

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
                            Toast.makeText(getApplicationContext(), respuesta.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(Supervision.this, DestinoRonda.class);
                            startActivity(intent);
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
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void enviarImagenData(){
        try{

            File file = bitmapToFile(getApplicationContext(), bitmap, "test");

            OkHttpClient client = new OkHttpClient();
            RequestBody body = new MultipartBody.Builder().setType( MultipartBody.FORM)
                    .addFormDataPart("UploadedImage", file.getName(), RequestBody.create( MediaType.parse("application/octet-stream"), file))
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder().url( URL_BASE + "FileUpload").post(body).build();

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
            } );

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

