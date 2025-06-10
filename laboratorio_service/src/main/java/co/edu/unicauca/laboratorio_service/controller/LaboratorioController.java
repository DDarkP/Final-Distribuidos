package co.edu.unicauca.laboratorio_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.laboratorio_service.dto.EstudianteDTO;
import co.edu.unicauca.laboratorio_service.model.Prestamo;
import co.edu.unicauca.laboratorio_service.service.LaboratorioService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/laboratorio")
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    @Autowired
    public LaboratorioController(LaboratorioService laboratorioService) {
        this.laboratorioService = laboratorioService;
    }

    @PostMapping("/pendientes")
    public ResponseEntity<?> obtenerPendientes(@RequestBody EstudianteDTO dto) {
        List<Prestamo> prestamos = laboratorioService.obtenerPrestamosPendientes(dto.getCodigoEstudiante());

        if (prestamos == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "mensaje", "El estudiante no tiene registros en el sistema de laboratorio."));
        }

        return ResponseEntity.ok(prestamos);
    }

    // POST → Consultar préstamos pendientes
    // @PostMapping("/pendientes")
    // public List<Prestamo> obtenerPendientes(@RequestBody EstudianteDTO dto) {
    // return
    // laboratorioService.obtenerPrestamosPendientes(dto.getCodigoEstudiante());
    // }

    // DELETE → Eliminar préstamos pendientes
    @DeleteMapping("/pendientes")
    public void eliminarPendientes(@RequestBody EstudianteDTO dto) {
        laboratorioService.eliminarPendientes(dto.getCodigoEstudiante());
    }
}
