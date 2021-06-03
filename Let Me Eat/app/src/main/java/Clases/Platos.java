package Clases;

public class Platos {
    private String id, nombre, detalles;
    private double precio;

    String idNegocio; //Esto nos servira para pasarlo al AdaptadorLista el id de negocio para hacer el editar y el borrar en la BD

    public Platos(String id, String nombre, String detalles, double precio, String idNegocio) {
        this.id = id;
        this.nombre = nombre;
        this.detalles = detalles;
        this.precio = precio;
        this.idNegocio = idNegocio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getIdNegocio() {
        return idNegocio;
    }

    public void setIdNegocio(String idNegocio) {
        this.idNegocio = idNegocio;
    }

}
