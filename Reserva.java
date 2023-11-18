//Universidad del valle Guatemala
// Gabriel Soto
//23900

public class Reserva implements Premium {
    private String fechaViaje;
    private boolean esIdaYVuelta;
    private int cantidadBoletos;
    private String aerolinea;
    private Usuario usuario;
    private String numeroTarjeta;
    private int cantidadMaletas; 

    public Usuario getUsuario() {
        return usuario;
    }

    public Reserva(String fechaViaje, boolean esIdaYVuelta, int cantidadBoletos, String aerolinea, Usuario usuario) {
        this.fechaViaje = fechaViaje;
        this.esIdaYVuelta = esIdaYVuelta;
        this.cantidadBoletos = cantidadBoletos;
        this.aerolinea = aerolinea;
        this.usuario = usuario;
    }

    public void confirmar(String numeroTarjeta, int cantidadMaletas) {
        this.numeroTarjeta = numeroTarjeta;
        this.cantidadMaletas = cantidadMaletas;
    }

    public String obtenerInformacionReserva() {
        return "Fecha de vuelo: " + fechaViaje +
                ", Tipo de vuelo: " + (esIdaYVuelta ? "Ida y Vuelta" : "Solo Ida") +
                ", Cantidad de boletos: " + cantidadBoletos +
                ", Aerolínea: " + aerolinea +
                ", Nombre del usuario: " + usuario.getUsuario();
    }

    public String obtenerInformacionConfirmacion() {
        if (numeroTarjeta != null) {
            return "Número de tarjeta: " + numeroTarjeta +
                    ", Cantidad de maletas: " + cantidadMaletas +
                    ", " + obtenerInformacionReserva();
        } else {
            return "La reserva no ha sido confirmada.";
        }

    }

    public int getMaletas() {
        return cantidadMaletas;
    }

    public int getAsientos() {
        return cantidadBoletos;
    }
}