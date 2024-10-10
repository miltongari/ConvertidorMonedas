
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Solicitud {
    public String solicitud(String monedaOrigen) {
        // Inicializar variables necesarias para la solicitud de la API
        final String APIKEY = "d1adaf9de8f6fe0c1abfb3c9";
        final String URL = "https://v6.exchangerate-api.com/v6/" + APIKEY + "/latest/" + monedaOrigen;

        try {
            // Instanciar un nuevo cliente y una nueva solicitud
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .build();
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
            // Retorna el body de la respuesta
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error al realizar la solicitud.");
            return null;
        }
    }
}
