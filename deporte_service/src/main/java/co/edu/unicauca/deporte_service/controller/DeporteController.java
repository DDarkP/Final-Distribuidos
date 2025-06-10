package co.edu.unicauca.deporte_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.deporte_service.DTOs.PendienteDeporteDTO;
import co.edu.unicauca.deporte_service.DTOs.SolicitudDTO;
import co.edu.unicauca.deporte_service.Service.DeporteService;

@RestController
@RequestMapping("/api/deporte")
@CrossOrigin
public class DeporteController {

    private final DeporteService servicio;

    @Autowired
    public DeporteController(DeporteService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/pendientes")
    public ResponseEntity<?> obtenerPendientes(@RequestBody SolicitudDTO solicitud) {
        String codigo = solicitud.getCodigoEstudiante();

        if (codigo == null || codigo.isBlank()) {
            return ResponseEntity.badRequest().body("El código del estudiante es obligatorio.");
        }

        // Validar si el estudiante existe (es decir, si tiene algún préstamo registrado)
        boolean existe = servicio.existeEstudiante(codigo);
        if (!existe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Estudiante no registrado en DeporteService.");
        }

        // Consultar y devolver los pendientes
        List<PendienteDeporteDTO> pendientes = servicio.consultarPendientes(codigo);
        return ResponseEntity.ok(pendientes);
    }

    @DeleteMapping("/pendientes")
    public Map<String, String> eliminarPendientes(@RequestBody SolicitudDTO solicitud) {
        servicio.eliminarPendientes(solicitud.getCodigoEstudiante());
        return Map.of("mensaje", "Implementos eliminados con éxito");
    }
}
