package com.example.watcher.adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watcher.ItemClickListener;
import com.example.watcher.R;


public class SupervisionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView tv_abonado, tv_estado, tv_hora;
    ItemClickListener itemClickListener;

    public SupervisionHolder(@NonNull View itemView) {
        super( itemView );

        this.tv_abonado = itemView.findViewById( R.id.tv_abonado );
        this.tv_estado = itemView.findViewById( R.id.tv_estado );
        this.tv_hora = itemView.findViewById( R.id.tv_hora );

        itemView.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {

        this.itemClickListener.onItemClickListener( v, getLayoutPosition() );
    }

    public void setItemClickListener(ItemClickListener ic) {
        this.itemClickListener = ic;
    }
}
