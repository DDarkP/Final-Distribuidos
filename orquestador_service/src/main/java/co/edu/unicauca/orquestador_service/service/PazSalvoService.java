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
import reactor.core.publisher.Mono;

@Service
public class PazSalvoService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final WebClient webClient;
    private final NotificacionService notificacionService;

    // Inyectar NotificacionService junto con WebClient
    public PazSalvoService(WebClient webClient, NotificacionService notificacionService) {
        this.webClient = webClient;
        this.notificacionService = notificacionService;
    }

    // Método para enviar notificaciones después de construir el mensaje detallado (sincrónico)
    public String obtenerPendientesYNotificar(String codigoEstudiante) {
        Map<String, List<String>> pendientes = obtenerDetallesPendientes(codigoEstudiante);

        if (pendientes.isEmpty()) {
            // Notificación general o a algún tópico general si quieres
            notificacionService.enviarNotificacionGeneral(
                "Estudiante " + codigoEstudiante + " está a paz y salvo."
            );
            return "El estudiante está a paz y salvo.";
        } else {
            // Por cada área con pendientes, enviar notificación a su canal respectivo
            pendientes.forEach((area, detalles) -> {
                StringBuilder msg = new StringBuilder("Pendientes en " + area + ":\n");
                detalles.forEach(d -> msg.append(" * ").append(d).append("\n"));
                notificacionService.enviarNotificacionPorArea(area.toLowerCase(), 
                    "Estudiante " + codigoEstudiante + " tiene pendientes: \n" + msg.toString());
            });

            // También retornar el mensaje detallado al caller
            StringBuilder mensaje = new StringBuilder("El estudiante NO está a paz y salvo. Tiene pendientes en:\n");
            pendientes.forEach((area, detalles) -> {
                mensaje.append("- ").append(area).append(":\n");
                detalles.forEach(detalle -> mensaje.append("   * ").append(detalle).append("\n"));
            });
            return mensaje.toString();
        }
    }

    // Aquí puedes mantener el método sin notificaciones para llamadas directas
    public Map<String, List<String>> obtenerDetallesPendientes(String codigoEstudiante) {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setCodigoEstudiante(codigoEstudiante);

        Map<String, List<String>> detallesPendientes = new LinkedHashMap<>();

        // ... tu código existente para obtener pendientes

        return detallesPendientes;
    }

    // Método genérico para obtener pendientes (sin cambios)
    private <T> T[] obtenerPendientes(String url, EstudianteDTO dto, Class<T[]> responseType) {
        try {
            ResponseEntity<T[]> response = restTemplate.postForEntity(url, dto, responseType);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // Método asíncrono con notificaciones
    public Mono<String> construirMensajePendientesAsyncYNotificar(String codigoEstudiante) {
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
                        notificacionService.enviarNotificacionGeneral(
                            "Estudiante " + codigoEstudiante + " está a paz y salvo (modo asíncrono)."
                        );
                        return "El estudiante está a paz y salvo (modo asíncrono).";
                    } else {
                        pendientes.forEach((area, detalles) -> {
                            StringBuilder msg = new StringBuilder("Pendientes en " + area + ":\n");
                            detalles.forEach(d -> msg.append(" * ").append(d).append("\n"));
                            notificacionService.enviarNotificacionPorArea(area.toLowerCase(), 
                                "Estudiante " + codigoEstudiante + " tiene pendientes: \n" + msg.toString());
                        });

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