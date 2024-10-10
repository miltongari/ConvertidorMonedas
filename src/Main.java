import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String monedaInicial;
        String monedaFinal;
        double monto;

        Gson gson = new Gson();
        Set<Currency> listaDeDivisas = Currency.getAvailableCurrencies();

        outerLoop:
        while (true) {

            Mensaje.Bienvenida();

            monedaInicial = obtenerDivisa(scanner, listaDeDivisas, "origen");
            if (monedaInicial == null) break;

            monto = obtenerMonto(scanner);

            monedaFinal = obtenerDivisa(scanner, listaDeDivisas, "destino");
            if (monedaFinal == null) break;


            realizarConversion(scanner, gson, monedaInicial, monedaFinal, monto);


            if (!deseaContinuar(scanner)) {
                System.out.println("¡Hasta luego! Gracias por usar el conversor.");
                break;
            }
        }

        scanner.close();
    }

    private static String obtenerDivisa(Scanner scanner, Set<Currency> listaDeDivisas, String tipoDivisa) {
        String moneda;
        while (true) {
            System.out.printf("Ingresa la divisa de %s o 'salir' para finalizar: ", tipoDivisa);
            moneda = scanner.nextLine().toUpperCase();

            String finalMoneda = moneda;
            if (listaDeDivisas.stream().anyMatch(c -> c.getCurrencyCode().equals(finalMoneda))) {
                return moneda;
            } else if (moneda.equalsIgnoreCase("salir")) {
                return null;
            } else {
                System.out.println("Ingresa una divisa válida.");
            }
        }
    }

    private static double obtenerMonto(Scanner scanner) {
        double monto = 0;
        while (true) {
            try {
                System.out.print("Ingresa el monto a convertir: ");
                monto = scanner.nextDouble();
                scanner.nextLine();
                if (monto > 0) break;
                System.out.println("Ingresa un monto positivo.");
            } catch (InputMismatchException e) {
                System.out.println("Ingresa un valor numérico válido.");
                scanner.next();
            }
        }
        return monto;
    }

    private static void realizarConversion(Scanner scanner, Gson gson, String monedaInicial, String monedaFinal, double monto) {
        Solicitud solicitud = new Solicitud();
        String resultado = solicitud.solicitud(monedaInicial);

        try {
            Moneda moneda = gson.fromJson(resultado, Moneda.class);
            double tasa = moneda.conversion_rates().get(monedaFinal);
            double resultadoFinal = monto * tasa;
            DecimalFormat formato = new DecimalFormat("#,##0.00");
            String resultadoFormateado = formato.format(resultadoFinal);

            System.out.printf("""
                    Divisa de origen: %s
                    Divisa de destino: %s
                    Monto: %f
                    Tasa: %f
                    Total: %s
                    """, monedaInicial, monedaFinal, monto, tasa, resultadoFormateado);

            guardarHistorial(monedaInicial, monedaFinal, monto, tasa, resultadoFormateado);
        } catch (Exception e) {
            System.out.println("Ocurrió un error en la conversión. Intenta nuevamente.");
        }
    }

    private static void guardarHistorial(String monedaInicial, String monedaFinal, double monto, double tasa, String resultadoFormateado) {
        try {
            File file = new File("Historial-de-conversiones.txt");
            Date fecha = new Date();
            SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
            String fechaFormateada = formateador.format(fecha);

            FileWriter escritor = new FileWriter(file, true);
            escritor.write(String.format("""
                    Fecha: %s
                    Divisa de origen: %s
                    Divisa de destino: %s
                    Monto: %f
                    Tasa: %f
                    Total: %s
                    """, fechaFormateada, monedaInicial, monedaFinal, monto, tasa, resultadoFormateado));
            escritor.close();
        } catch (IOException e) {
            System.out.println("Error al guardar en el historial.");
        }
    }

    private static boolean deseaContinuar(Scanner scanner) {
        System.out.print("¿Deseas hacer otra conversión? (si/no): ");
        while (true) {
            String respuesta = scanner.nextLine().toLowerCase();
            if (respuesta.equals("no") || respuesta.equals("n") || respuesta.equals("salir")) {
                return false;
            } else if (respuesta.equals("si") || respuesta.equals("s")) {
                return true;
            } else {
                System.out.println("Ingresa una opción válida.");
            }
        }
    }
}
