package pe.bravos.taxis.model;

/**
 * Created by ssuar on 9/05/2017.
 */

public class Movilidad {
    public int idmovilidad;
    public int idchofer;
    public double latitud;
    public double longitud;
    public int estado;
    public String foto;
    public String fotoConductor;
    public String nombre;
    public String telefono;
    public String nplaca;

    public Movilidad() {
    }

    public Movilidad(int idmovilidad, int idchofer, double latitud, double longitud, int estado, String foto, String fotoConductor, String nombre, String telefono, String nplaca) {
        this.idmovilidad = idmovilidad;
        this.idchofer = idchofer;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado = estado;
        this.foto = foto;
        this.fotoConductor = fotoConductor;
        this.nombre = nombre;
        this.telefono = telefono;
        this.nplaca = nplaca;
    }

    public String getNplaca() {
        return nplaca;
    }

    public void setNplaca(String nplaca) {
        this.nplaca = nplaca;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFotoConductor() {
        return fotoConductor;
    }

    public void setFotoConductor(String fotoConductor) {
        this.fotoConductor = fotoConductor;
    }

    public int getIdmovilidad() {
        return idmovilidad;
    }

    public void setIdmovilidad(int idmovilidad) {
        this.idmovilidad = idmovilidad;
    }

    public int getIdchofer() {
        return idchofer;
    }

    public void setIdchofer(int idchofer) {
        this.idchofer = idchofer;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getnplaca() {
        return nplaca;
    }

    public void setnplaca(String nplaca) {
        this.nplaca = nplaca;
    }
}
