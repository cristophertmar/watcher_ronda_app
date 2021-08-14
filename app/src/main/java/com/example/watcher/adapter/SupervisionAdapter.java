package com.example.watcher.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watcher.ItemClickListener;
import com.example.watcher.Model.Supervision;
import com.example.watcher.R;
import com.example.watcher.entity.SupervisionEntity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;

/* SupervisionHolder */
public class SupervisionAdapter extends RecyclerView.Adapter<SupervisionHolder> {

    Context context;
    ArrayList<Supervision> models;
    //private Context mContext;

    public SupervisionAdapter(Context context, List<Supervision> models) {
        this.context = context;
        this.models = (ArrayList<Supervision>) models;
        //this.mContext = context;
    }

    @NonNull
    @Override
    public SupervisionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.row, null );
        return new SupervisionHolder( view );

    }

    @Override
    public void onBindViewHolder(@NonNull SupervisionHolder myHolder, int i) {

        myHolder.tv_abonado.setText( models.get(i).getAbonado() );
        myHolder.tv_estado.setText( models.get(i).getEstado() );
        myHolder.tv_hora.setText( models.get(i).getFecha() + " " +  models.get(i).getHora() + " hrs.");

        myHolder.setItemClickListener( new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {

                /*CharSequence text = "Posición: " + position;
                //int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Posición: " + position, Toast.LENGTH_LONG).show();
                //toast.show();*/

                Supervision supervision = models.get(position);

                Intent intent = new Intent( context, com.example.watcher.Supervision.class );
                Bundle bundle = new Bundle();
                bundle.putSerializable("supervision", supervision);
                intent.putExtras(bundle);
                context.startActivity( intent );

            }
        } );

    }

    @Override
    public int getItemCount() {
        return models.size();
    }


}
