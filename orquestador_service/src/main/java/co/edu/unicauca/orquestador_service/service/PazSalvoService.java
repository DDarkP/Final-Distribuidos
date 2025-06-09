package co.edu.unicauca.orquestador_service.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import co.edu.unicauca.orquestador_service.dto.DeudaFinanciera;
import co.edu.unicauca.orquestador_service.dto.EstudianteDTO;
import co.edu.unicauca.orquestador_service.dto.Prestamo;
import co.edu.unicauca.orquestador_service.dto.PrestamoDeporte;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import reactor.core.publisher.Mono;


@Service
public class PazSalvoService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final WebClient webClient;

    public PazSalvoService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Método para obtener detalles completos de pendientes por área (sincrónico)
    public Map<String, List<String>> obtenerDetallesPendientes(String codigoEstudiante) {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setCodigoEstudiante(codigoEstudiante);

        Map<String, List<String>> detallesPendientes = new LinkedHashMap<>();

        // Laboratorio
        Prestamo[] prestamosLab = obtenerPendientes("http://localhost:8084/api/laboratorio/pendientes", dto, Prestamo[].class);
        if (prestamosLab != null && prestamosLab.length > 0) {
            List<String> detallesLab = new ArrayList<>();
            for (Prestamo p : prestamosLab) {
                detallesLab.add("Equipo: " + p.getEquipoPrestado() + ", Estado: " + p.getEstado());
            }
            detallesPendientes.put("Laboratorio", detallesLab);
        }

        // Deporte
        PrestamoDeporte[] prestamosDep = obtenerPendientes("http://localhost:8082/api/deporte/pendientes", dto, PrestamoDeporte[].class);
        if (prestamosDep != null && prestamosDep.length > 0) {
            List<String> detallesDep = new ArrayList<>();
            for (PrestamoDeporte p : prestamosDep) {
                detallesDep.add("Elemento: " + p.getElemento() + ", Fecha devolución estimada: " + p.getFechaDevolucionEstimada());
            }
            detallesPendientes.put("Deporte", detallesDep);
        }

        // Financiera
        DeudaFinanciera[] deudasFin = obtenerPendientes("http://localhost:8083/api/financiera/pendientes", dto, DeudaFinanciera[].class);
        if (deudasFin != null && deudasFin.length > 0) {
            List<String> detallesFin = new ArrayList<>();
            for (DeudaFinanciera d : deudasFin) {
                detallesFin.add("Monto: $" + d.getMontoAdeudado() + ", Motivo: " + d.getMotivo() + ", Estado: " + d.getEstado());
            }
            detallesPendientes.put("Financiera", detallesFin);
        }

        return detallesPendientes;
    }

    // Método genérico para obtener pendientes (sincrónico)
    private <T> T[] obtenerPendientes(String url, EstudianteDTO dto, Class<T[]> responseType) {
        try {
            ResponseEntity<T[]> response = restTemplate.postForEntity(url, dto, responseType);
            return response.getBody();
        } catch (Exception e) {
            // Podrías manejar el error o reintentar
            return null;
        }
    }

    // Método para construir mensaje detallado para el controlador REST
    public String construirMensajePendientes(String codigoEstudiante) {
        Map<String, List<String>> pendientesConDetalles = obtenerDetallesPendientes(codigoEstudiante);
        if (pendientesConDetalles.isEmpty()) {
            return "El estudiante está a paz y salvo.";
        } else {
            StringBuilder mensaje = new StringBuilder("El estudiante NO está a paz y salvo. Tiene pendientes en:\n");
            pendientesConDetalles.forEach((area, detalles) -> {
                mensaje.append("- ").append(area).append(":\n");
                detalles.forEach(detalle -> mensaje.append("   * ").append(detalle).append("\n"));
            });
            return mensaje.toString();
        }
    }


    // La versión asíncrona también se puede mejorar para obtener detalles
    public Mono<String> construirMensajePendientesAsync(String codigoEstudiante) {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setCodigoEstudiante(codigoEstudiante);

        Mono<Prestamo[]> lab = webClient.post()
                .uri("http://localhost:8084/api/laboratorio/pendientes")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Prestamo[].class)
                .onErrorReturn(new Prestamo[0])
                .defaultIfEmpty(new Prestamo[0]);

        Mono<PrestamoDeporte[]> dep = webClient.post()
                .uri("http://localhost:8082/api/deporte/pendientes")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(PrestamoDeporte[].class)
                .onErrorReturn(new PrestamoDeporte[0])
                .defaultIfEmpty(new PrestamoDeporte[0]);

        Mono<DeudaFinanciera[]> fin = webClient.post()
                .uri("http://localhost:8083/api/financiera/pendientes")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(DeudaFinanciera[].class)
                .onErrorReturn(new DeudaFinanciera[0])
                .defaultIfEmpty(new DeudaFinanciera[0]);

        return Mono.zip(lab, dep, fin)
                .map(resultados -> {
                    Map<String, List<String>> pendientes = new LinkedHashMap<>();

                    if (resultados.getT1().length > 0) {
                        List<String> detallesLab = new ArrayList<>();
                        for (Prestamo p : resultados.getT1()) {
                            detallesLab.add("Equipo: " + p.getEquipoPrestado() + ", Estado: " + p.getEstado());
                        }
                        pendientes.put("Laboratorio", detallesLab);
                    }

                    if (resultados.getT2().length > 0) {
                        List<String> detallesDep = new ArrayList<>();
                        for (PrestamoDeporte p : resultados.getT2()) {
                            detallesDep.add("Elemento: " + p.getElemento() + ", Fecha devolución estimada: " + p.getFechaDevolucionEstimada());
                        }
                        pendientes.put("Deporte", detallesDep);
                    }

                    if (resultados.getT3().length > 0) {
                        List<String> detallesFin = new ArrayList<>();
                        for (DeudaFinanciera d : resultados.getT3()) {
                            detallesFin.add("Monto: $" + d.getMontoAdeudado() + ", Motivo: " + d.getMotivo() + ", Estado: " + d.getEstado());
                        }
                        pendientes.put("Financiera", detallesFin);
                    }

                    if (pendientes.isEmpty()) {
                        return "El estudiante está a paz y salvo (modo asíncrono).";
                    } else {
                        StringBuilder msg = new StringBuilder("El estudiante NO está a paz y salvo (modo asíncrono). Tiene pendientes en:\n");
                        pendientes.forEach((area, detalles) -> {
                            msg.append("- ").append(area).append(":\n");
                            detalles.forEach(detalle -> msg.append("   * ").append(detalle).append("\n"));
                        });
                        return msg.toString();
                    }
                });
    }
}
