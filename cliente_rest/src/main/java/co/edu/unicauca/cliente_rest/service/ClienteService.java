package co.edu.unicauca.cliente_rest.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import co.edu.unicauca.cliente_rest.dto.EstudianteDTO;

@Service
public class ClienteService {


    private final RestTemplate restTemplate = new RestTemplate();

    private static final String URL_ORQUESTADOR = "http://localhost:8080/api/orquestador/verificar";
    private static final String URL_ORQUESTADOR_ASYNC = "http://localhost:8080/api/orquestador/verificarAsync";

public String verificarPazSalvo(String codigoEstudiante) {
    EstudianteDTO dto = new EstudianteDTO(codigoEstudiante);
    int intentos = 0;
    int maxIntentos = 3;
    long esperaEntreIntentos = 4000; // 4 segundos
    long timeoutMs = 5000; // 5 segundos

    while (intentos < maxIntentos) {
        try {
            long start = System.currentTimeMillis();

            ResponseEntity<String> response = restTemplate.postForEntity(URL_ORQUESTADOR, dto, String.class);

            long tiempoRespuesta = System.currentTimeMillis() - start;
            if (tiempoRespuesta > timeoutMs) {
                throw new RuntimeException("Tiempo de espera superado.");
            }

            return response.getBody();
        } catch (Exception e) {
            intentos++;
            if (intentos >= maxIntentos) {
                return "Error tras " + maxIntentos + " intentos: " + e.getMessage();
            }

            System.out.println("Intento " + intentos + " fallido. Reintentando en 4 segundos...");
            try {
                Thread.sleep(esperaEntreIntentos);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return "Error: hilo interrumpido durante el reintento.";
            }
        }
    }

    return "No se pudo obtener respuesta después de múltiples intentos.";
}




public String verificarPazSalvoAsync(String codigoEstudiante) {
    EstudianteDTO dto = new EstudianteDTO(codigoEstudiante);
    try {
        ResponseEntity<String> response = restTemplate.postForEntity(URL_ORQUESTADOR_ASYNC, dto, String.class);
        return response.getBody();
    } catch (Exception e) {
        return "Error al comunicarse con el servicio asíncrono: " + e.getMessage();
    }
}
}
