package Clases;

public class Platos {
    private String id, nombre, detalles;
    private double precio;


    public Platos(String id, String nombre, String detalles, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.detalles = detalles;
        this.precio = precio;
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

    //TODO: Insertar la imagen

}
