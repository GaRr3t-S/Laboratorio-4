import java.io.*;
import java.util.*;

public class Main {
    private static final String USUARIOS_CSV = "usuarios.csv";
    private static final String RESERVAS_CSV = "reservas.csv";
    private static Scanner scanner = new Scanner(System.in);
    private static Usuario usuarioActual;

    public static void main(String[] args) {
        while (true) {
            mostrarMenuInicial();
            int opcion = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcion) {
                case 1:
                    registrarUsuario();
                    break;
                case 2:
                    iniciarSesion();
                    break;
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
            }
        }
    }

    private static void mostrarMenuInicial() {
        System.out.println("1. Registrar Usuario");
        System.out.println("2. Iniciar Sesión");
        System.out.print("Selecciona una opción: ");
    }

    private static void registrarUsuario() {
        System.out.print("Ingrese nombre de usuario: ");
        String nombreUsuario = scanner.nextLine();
        System.out.print("Ingrese contraseña: ");
        String contraseña = scanner.nextLine();
        System.out.print("¿Desea el plan Base (B) o VIP (V)? ");
        boolean esPremium = scanner.nextLine().equalsIgnoreCase("V");

        Usuario nuevoUsuario = new Usuario(nombreUsuario, nombreUsuario, contraseña, esPremium);

        guardarUsuarioEnCSV(nuevoUsuario);

        System.out.println("Usuario registrado exitosamente.");
    }

    private static void iniciarSesion() {
        System.out.print("Ingrese nombre de usuario: ");
        String nombreUsuario = scanner.nextLine();
        System.out.print("Ingrese contraseña: ");
        String contraseña = scanner.nextLine();

        Usuario usuario = verificarCredenciales(nombreUsuario, contraseña);
        if (usuario != null) {
            usuarioActual = usuario;
            if (usuario.esPremium()) {
                menuUsuarioPremium();
            } else {
                System.out.println("El acceso a esta beta está restringido para usuarios VIP.");
            }
        } else {
            System.out.println("Credenciales incorrectas. Inténtalo de nuevo.");
        }
    }

    private static Usuario verificarCredenciales(String nombreUsuario, String contraseña) {
        try (Scanner scannerUsuarios = new Scanner(new File(USUARIOS_CSV))) {
            while (scannerUsuarios.hasNextLine()) {
                String[] datosUsuario = scannerUsuarios.nextLine().split(",");
                if (datosUsuario.length == 3 &&
                        datosUsuario[0].equals(nombreUsuario) &&
                        datosUsuario[1].equals(contraseña)) {
                    boolean esPremium = datosUsuario[2].equalsIgnoreCase("V");
                    return new Usuario(nombreUsuario, nombreUsuario, contraseña, esPremium);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error al leer el archivo de usuarios.");
        }
        return null;
    }

    private static void menuUsuarioPremium() {
        while (true) {
            mostrarMenuPremium();
            int opcion = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcion) {
                case 1:
                    realizarReserva();
                    break;
                case 2:
                    confirmarReserva();
                    break;
                case 3:
                    cambiarContraseña();
                    break;
                case 4:
                    System.out.println("Sesión cerrada. ¡Hasta luego!");
                    return;
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
            }
        }
    }

    private static void mostrarMenuPremium() {
        System.out.println("1. Realizar Reserva");
        System.out.println("2. Confirmar Reserva");
        System.out.println("3. Cambiar Contraseña");
        System.out.println("4. Salir");
        System.out.print("Selecciona una opción: ");
    }

    private static void realizarReserva() {
        System.out.print("Ingrese fecha de viaje (yyyy-MM-dd): ");
        String fechaViajeStr = scanner.nextLine();
        String fechaViaje = fechaViajeStr;

        System.out.print("¿Es un vuelo de ida y vuelta? (Sí/No): ");
        boolean esIdaYVuelta = scanner.nextLine().equalsIgnoreCase("Sí");

        System.out.print("Ingrese cantidad de boletos: ");
        int cantidadBoletos = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("Ingrese aerolínea: ");
        String aerolinea = scanner.nextLine();

        Reserva nuevaReserva = new Reserva(fechaViaje, esIdaYVuelta, cantidadBoletos, aerolinea, usuarioActual);

        guardarReservaEnCSV(nuevaReserva);

        System.out.println("Reserva realizada exitosamente.");
    }

    private static void confirmarReserva() {
        List<Reserva> reservasPorConfirmar = obtenerReservasPorConfirmar();

        if (!reservasPorConfirmar.isEmpty()) {
            System.out.println("Reservas por confirmar:");
            for (int i = 0; i < reservasPorConfirmar.size(); i++) {
                System.out.println((i + 1) + ". " + reservasPorConfirmar.get(i).obtenerInformacionReserva());
            }

            System.out.print("Seleccione el número de reserva que desea confirmar: ");
            int seleccion = scanner.nextInt();
            scanner.nextLine(); 

            if (seleccion > 0 && seleccion <= reservasPorConfirmar.size()) {
                Reserva reservaPorConfirmar = reservasPorConfirmar.get(seleccion - 1);

                System.out.print("Ingrese número de tarjeta: ");
                String numeroTarjeta = scanner.nextLine();

                System.out.print("Ingrese cantidad de maletas: ");
                int cantidadMaletas = scanner.nextInt();
                scanner.nextLine(); 

                reservaPorConfirmar.confirmar(numeroTarjeta, cantidadMaletas);

                actualizarReservaEnCSV(reservaPorConfirmar);

                System.out.println("Reserva confirmada exitosamente.");
            } else {
                System.out.println("Número de reserva no válido. Inténtalo de nuevo.");
            }
        } else {
            System.out.println("No hay reservaciones por confirmar.");
        }
    }

    private static List<Reserva> obtenerReservasPorConfirmar() {
        List<Reserva> reservasPorConfirmar = new ArrayList<>();

        try (Scanner scannerReservas = new Scanner(new File(RESERVAS_CSV))) {
            while (scannerReservas.hasNextLine()) {
                String[] datosReserva = scannerReservas.nextLine().split(",");
                if (datosReserva.length == 7 &&
                        datosReserva[0].equals(usuarioActual.getUsuario()) &&
                        datosReserva[6].equals("0")) {
                    String fechaViaje = datosReserva[1];
                    boolean esIdaYVuelta = Boolean.parseBoolean(datosReserva[2]);
                    int cantidadBoletos = Integer.parseInt(datosReserva[3]);
                    String aerolinea = datosReserva[4];

                    Reserva reserva = new Reserva(fechaViaje, esIdaYVuelta, cantidadBoletos, aerolinea, usuarioActual);
                    reserva.confirmar(datosReserva[5], Integer.parseInt(datosReserva[6]));

                    reservasPorConfirmar.add(reserva);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error al leer el archivo de reservas.");
        }

        return reservasPorConfirmar;
    }

    private static void cambiarContraseña() {
        System.out.print("Ingrese nueva contraseña: ");
        String nuevaContraseña = scanner.nextLine();

        actualizarContraseñaEnCSV(nuevaContraseña);

        System.out.println("Contraseña cambiada exitosamente.");
    }

    private static void guardarUsuarioEnCSV(Usuario usuario) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USUARIOS_CSV, true))) {
            writer.println(usuario.getUsuario() + "," + usuario.getContraseña() + "," + (usuario.esPremium() ? "V" : "B"));
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo de usuarios.");
        }
    }

    private static void guardarReservaEnCSV(Reserva reserva) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESERVAS_CSV, true))) {
            writer.println(reserva.getUsuario().getUsuario() + "," +
                    reserva.obtenerInformacionReserva());
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo de reservas.");
        }
    }

    private static void actualizarReservaEnCSV(Reserva reserva) {
        try {
            List<String> lineas = new ArrayList<>();
            try (Scanner scannerReservas = new Scanner(new File(RESERVAS_CSV))) {
                while (scannerReservas.hasNextLine()) {
                    String linea = scannerReservas.nextLine();
                    String[] datos = linea.split(",");
                    if (datos.length == 7 &&
                            datos[0].equals(reserva.getUsuario().getUsuario()) &&
                            datos[1].equals(reserva.obtenerInformacionReserva())) {
                        linea = reserva.getUsuario().getUsuario() + "," +
                                reserva.obtenerInformacionReserva();
                    }
                    lineas.add(linea);
                }
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(RESERVAS_CSV))) {
                for (String linea : lineas) {
                    writer.println(linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al actualizar el archivo de reservas.");
        }
    }

    private static void actualizarContraseñaEnCSV(String nuevaContraseña) {
        try {
            List<String> lineas = new ArrayList<>();
            try (Scanner scannerUsuarios = new Scanner(new File(USUARIOS_CSV))) {
                while (scannerUsuarios.hasNextLine()) {
                    String linea = scannerUsuarios.nextLine();
                    String[] datos = linea.split(",");
                    if (datos.length == 3 && datos[0].equals(usuarioActual.getUsuario())) {
                        linea = usuarioActual.getUsuario() + "," + nuevaContraseña + "," + datos[2];
                    }
                    lineas.add(linea);
                }
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(USUARIOS_CSV))) {
                for (String linea : lineas) {
                    writer.println(linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al actualizar el archivo de usuarios.");
        }
    }
}