package co.edu.unicauca.orquestador_service.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    // Método para enviar notificaciones después de construir el mensaje detallado
    // (sincrónico)
    public String obtenerPendientesYNotificar(String codigoEstudiante) {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setCodigoEstudiante(codigoEstudiante);

        // Notificar solicitud a todas las áreas
        notificacionService.enviarNotificacionPorArea("laboratorios",
                "El estudiante con código " + codigoEstudiante + " ha solicitado paz y salvo.");
        notificacionService.enviarNotificacionPorArea("deportes",
                "El estudiante con código " + codigoEstudiante + " ha solicitado paz y salvo.");
        notificacionService.enviarNotificacionPorArea("financiera",
                "El estudiante con código " + codigoEstudiante + " ha solicitado paz y salvo.");

        Map<String, List<String>> pendientes = new LinkedHashMap<>();

        // Laboratorio
        Prestamo[] prestamosLab = obtenerPendientesConManejoErrores(
                "http://localhost:8084/api/laboratorio/pendientes", dto, Prestamo[].class, "laboratorio");
        if (prestamosLab != null && prestamosLab.length > 0) {
            List<String> detallesLab = new ArrayList<>();
            for (Prestamo p : prestamosLab) {
                detallesLab.add("Equipo: " + p.getEquipoPrestado() + ", Estado: " + p.getEstado());
            }
            pendientes.put("Laboratorios", detallesLab);
        }

        // Deportes
        PrestamoDeporte[] prestamosDep = obtenerPendientesConManejoErrores(
                "http://localhost:8082/api/deporte/pendientes", dto, PrestamoDeporte[].class, "deporte");
        if (prestamosDep != null && prestamosDep.length > 0) {
            List<String> detallesDep = new ArrayList<>();
            for (PrestamoDeporte p : prestamosDep) {
                detallesDep.add("Elemento: " + p.getImplemento() + ", Fecha devolución estimada: "
                        + p.getFechaEstimada());

            }
            pendientes.put("Deportes", detallesDep);
        }

        // Financiera
        DeudaFinanciera[] deudas = obtenerPendientesConManejoErrores(
                "http://localhost:8083/api/financiera/pendientes", dto, DeudaFinanciera[].class, "financiera");
        if (deudas != null && deudas.length > 0) {
            List<String> detallesFin = new ArrayList<>();
            for (DeudaFinanciera d : deudas) {
                detallesFin.add("Motivo: " + d.getMotivo() + ", Estado: "
                        + d.getEstado());
            }
            pendientes.put("Financiera", detallesFin);
        }

        if (pendientes.isEmpty()) {
            notificacionService.enviarNotificacionGeneral(
                    "Estudiante " + codigoEstudiante + " está a paz y salvo.");
            return "El estudiante está a paz y salvo.";
        } else {
            pendientes.forEach((area, detalles) -> {
                StringBuilder msg = new StringBuilder("Pendientes en " + area + ":\n");
                detalles.forEach(d -> msg.append(" * ").append(d).append("\n"));
                notificacionService.enviarNotificacionPorArea(area.toLowerCase(),
                        "Estudiante " + codigoEstudiante + " tiene pendientes: \n" + msg);
            });

            StringBuilder mensaje = new StringBuilder("El estudiante NO está a paz y salvo. Tiene pendientes en:\n");
            pendientes.forEach((area, detalles) -> {
                mensaje.append("- ").append(area).append(":\n");
                detalles.forEach(detalle -> mensaje.append("   * ").append(detalle).append("\n"));
            });
            return mensaje.toString();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T[] obtenerPendientesConManejoErrores(String url, EstudianteDTO dto, Class<T[]> responseType,
            String area) {
        try {
            ResponseEntity<T[]> response = restTemplate.postForEntity(url, dto, responseType);
            return response.getBody();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                System.out.println("[" + area + "] sin registros. Se considera paz y salvo en esta área.");
                return (T[]) java.lang.reflect.Array.newInstance(responseType.getComponentType(), 0); // array vacío
            } else {
                System.err.println("[" + area + "] Error HTTP " + e.getStatusCode());
            }
        } catch (Exception ex) {
            System.err.println("[" + area + "] Error inesperado: " + ex.getMessage());
        }
        return null; // Se interpretará como error grave o falla de comunicación
    }

    // Método genérico para obtener pendientes (sin cambios)
    private <T> T[] obtenerPendientes(String url, EstudianteDTO dto, Class<T[]> responseType) {
        try {
            ResponseEntity<T[]> response = restTemplate.postForEntity(url, dto, responseType);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            // El estudiante no tiene registros (lo tratamos como paz y salvo en esta área)
            return null;
        } catch (Exception e) {
            // Otra excepción que sí debe preocuparnos
            return newArrayInstance(responseType); // método auxiliar para crear arreglo vacío
        }
    }

    // Método auxiliar para crear arrays vacíos del tipo recibido
    @SuppressWarnings("unchecked")
    private <T> T[] newArrayInstance(Class<T[]> type) {
        return (T[]) java.lang.reflect.Array.newInstance(type.getComponentType(), 0);
    }

    // Método asíncrono con notificaciones
    public Mono<String> construirMensajePendientesAsyncYNotificar(String codigoEstudiante) {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setCodigoEstudiante(codigoEstudiante);

        // Notificar solicitud a todas las áreas
        notificacionService.enviarNotificacionPorArea("laboratorios",
                "El estudiante con código " + codigoEstudiante + " ha solicitado paz y salvo.");
        notificacionService.enviarNotificacionPorArea("deportes",
                "El estudiante con código " + codigoEstudiante + " ha solicitado paz y salvo.");
        notificacionService.enviarNotificacionPorArea("financiera",
                "El estudiante con código " + codigoEstudiante + " ha solicitado paz y salvo.");

        Mono<Prestamo[]> lab = webClient.post()
                .uri("http://localhost:8084/api/laboratorio/pendientes")
                .bodyValue(dto)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.just(new RuntimeException("Sin registros en laboratorio")))
                .bodyToMono(Prestamo[].class)
                .onErrorResume(e -> {
                    if (e.getMessage().contains("laboratorio")) {
                        return Mono.just(new Prestamo[0]); // está limpio en laboratorio
                    }
                    return Mono.error(e); // error real
                });

        Mono<PrestamoDeporte[]> dep = webClient.post()
                .uri("http://localhost:8082/api/deporte/pendientes")
                .bodyValue(dto)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.just(new RuntimeException("Sin registros en deporte")))
                .bodyToMono(PrestamoDeporte[].class)
                .onErrorResume(e -> {
                    if (e.getMessage().contains("deporte")) {
                        return Mono.just(new PrestamoDeporte[0]); // está limpio en deporte
                    }
                    return Mono.error(e); // error real
                });

        Mono<DeudaFinanciera[]> fin = webClient.post()
                .uri("http://localhost:8083/api/financiera/pendientes")
                .bodyValue(dto)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.just(new RuntimeException("Sin registros en financiera")))
                .bodyToMono(DeudaFinanciera[].class)
                .onErrorResume(e -> {
                    if (e.getMessage().contains("financiera")) {
                        return Mono.just(new DeudaFinanciera[0]); // está limpio en financiera
                    }
                    return Mono.error(e); // error real
                });

        return Mono.zip(lab, dep, fin)
                .map(resultados -> {
                    Map<String, List<String>> pendientes = new LinkedHashMap<>();

                    if (resultados.getT1().length > 0) {
                        List<String> detallesLab = new ArrayList<>();
                        for (Prestamo p : resultados.getT1()) {
                            detallesLab.add("Equipo: " + p.getEquipoPrestado() + ", Estado: " + p.getEstado());
                        }
                        pendientes.put("Laboratorios", detallesLab);
                    }

                    if (resultados.getT2().length > 0) {
                        List<String> detallesDep = new ArrayList<>();
                        for (PrestamoDeporte p : resultados.getT2()) {
                            detallesDep.add("Elemento: " + p.getImplemento() + ", Fecha devolución estimada: "
                                    + p.getFechaEstimada());
                        }
                        pendientes.put("Deportes", detallesDep);
                    }

                    if (resultados.getT3().length > 0) {
                        List<String> detallesFin = new ArrayList<>();
                        for (DeudaFinanciera d : resultados.getT3()) {
                            detallesFin.add("Motivo: " + d.getMotivo()
                                    + ", Estado: " + d.getEstado());
                        }
                        pendientes.put("Financiera", detallesFin);
                    }

                    if (pendientes.isEmpty()) {
                        notificacionService.enviarNotificacionGeneral(
                                "Estudiante " + codigoEstudiante + " está a paz y salvo (modo asíncrono).");
                        return "El estudiante está a paz y salvo (modo asíncrono).";
                    } else {
                        pendientes.forEach((area, detalles) -> {
                            StringBuilder msg = new StringBuilder("Pendientes en " + area + ":\n");
                            detalles.forEach(d -> msg.append(" * ").append(d).append("\n"));
                            notificacionService.enviarNotificacionPorArea(area.toLowerCase(),
                                    "Estudiante " + codigoEstudiante + " tiene pendientes: \n" + msg.toString());
                        });

                        StringBuilder msg = new StringBuilder(
                                "El estudiante NO está a paz y salvo (modo asíncrono). Tiene pendientes en:\n");
                        pendientes.forEach((area, detalles) -> {
                            msg.append("- ").append(area).append(":\n");
                            detalles.forEach(detalle -> msg.append("   * ").append(detalle).append("\n"));
                        });
                        return msg.toString();
                    }
                });
    }

}