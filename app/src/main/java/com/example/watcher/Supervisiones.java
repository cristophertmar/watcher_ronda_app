package com.example.watcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.watcher.Model.Supervision;
import com.example.watcher.Model.SupervisionRespuesta;
import com.example.watcher.Utils.SupervisionService;
import com.example.watcher.adapter.SupervisionAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.watcher.Utils.Apis.URL_BASE;

public class Supervisiones extends AppCompatActivity {

    private static final String TAG = "SUPERVISION";

    private Retrofit retrofit;
    RecyclerView mRecyclerView;
    SupervisionAdapter myAdapter;
    SupervisionService supervisionService;
    List<Supervision> listaSupervisiones = new ArrayList<Supervision>();
    TextView tv_cantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_supervisiones );

        tv_cantidad = findViewById( R.id.tv_cantidad );

        mRecyclerView = findViewById( R.id.recyclerView );
        mRecyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        /*myAdapter = new SupervisionAdapter(this, getListDenuncia());
        mRecyclerView.setAdapter(myAdapter);*/

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        listarSupervisiones();

    }

    public void listarSupervisiones() {

        int id_usuario = ((MyApplication) this.getApplication()).getUsuario().getId();

        supervisionService = retrofit.create( SupervisionService.class );
        Call<SupervisionRespuesta> call = supervisionService.getSupervisiones(id_usuario);
        call.enqueue( new Callback<SupervisionRespuesta>() {
            @Override
            public void onResponse(Call<SupervisionRespuesta> call, Response<SupervisionRespuesta> response) {

                if(response.isSuccessful()) {
                    SupervisionRespuesta supervisionRespuesta = response.body();
                    listaSupervisiones = supervisionRespuesta.getData();
                    tv_cantidad.setText( supervisionRespuesta.getTotal() + "" );

                    myAdapter = new SupervisionAdapter(Supervisiones.this, listaSupervisiones);
                    mRecyclerView.setAdapter(myAdapter);

                    /*for(int i = 0; i < listaSupervisiones.size(); i++) {
                        Supervision s = listaSupervisiones.get(i);
                        Log.i(TAG, " Supervisión: " + s.getAbonado());
                    }*/
                } else {
                    Log.e(TAG, " onResponse: " + response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<SupervisionRespuesta> call, Throwable t) {
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        } );

    }

    private ArrayList<Supervision> getListDenuncia(){

        ArrayList<Supervision> listado = new ArrayList<>(  );

        Supervision s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);

        s = new Supervision();
        s.setAbonado("ATM - Real Plaza");
        s.setEstado("Pendiente");
        s.setHora("30/07/2020 14:00 hrs.");
        listado.add(s);



        return listado;



    }
}