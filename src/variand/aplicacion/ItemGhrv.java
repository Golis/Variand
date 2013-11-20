package variand.aplicacion;

//Clase que genera nuestro tipo de dato
public class ItemGhrv {
  protected long id;
  protected String rutaImagen;
  protected String nombre;
  protected String tipo;
  //Constructor por defecto       
  public ItemGhrv() {
    this.nombre = "";
    this.tipo = "";
    this.rutaImagen = "";
  }
   //Constructor tipo 1  
  public ItemGhrv(long id, String nombre, String tipo) {
    this.id = id;
    this.nombre = nombre;
    this.tipo = tipo;
    this.rutaImagen = "";
  }
  //Constructor tipo 2  
  public ItemGhrv(long id, String nombre, String tipo, String rutaImagen) {
    this.id = id;
    this.nombre = nombre;
    this.tipo = tipo;
    this.rutaImagen = rutaImagen;
  }
  //Métodos get y set
  public long getId() {
    return id;
  }
     
  public void setId(long id) {
    this.id = id;
  }
     
  public String getRutaImagen() {
    return rutaImagen;
  }
     
  public void setRutaImagen(String rutaImagen) {
    this.rutaImagen = rutaImagen;
  }
     
  public String getNombre() {
    return nombre;
  }
     
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
     
  public String getTipo() {
    return tipo;
  }
     
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }
}