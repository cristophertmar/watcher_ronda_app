package com.example.watcher.entity;

import android.content.Context;

    public class SupervisionEntity {

        private String Abonado;
        private String Estado;
        private String Hora;

        public SupervisionEntity() {
        }

        public String getAbonado() {
            return Abonado;
        }

        public void setAbonado(String abonado) {
            Abonado = abonado;
        }

        public String getEstado() {
            return Estado;
        }

        public void setEstado(String estado) {
            Estado = estado;
        }

        public String getHora() {
            return Hora;
        }

        public void setHora(String hora) {
            Hora = hora;
        }
    }
