package co.edu.unicauca.laboratorio_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.laboratorio_service.dto.EstudianteDTO;
import co.edu.unicauca.laboratorio_service.model.Prestamo;
import co.edu.unicauca.laboratorio_service.service.LaboratorioService;

import java.util.List;

@RestController
@RequestMapping("/api/laboratorio")
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    @Autowired
    public LaboratorioController(LaboratorioService laboratorioService) {
        this.laboratorioService = laboratorioService;
    }

    // POST → Consultar préstamos pendientes
    @PostMapping("/pendientes")
    public List<Prestamo> obtenerPendientes(@RequestBody EstudianteDTO dto) {
        return laboratorioService.obtenerPrestamosPendientes(dto.getCodigoEstudiante());
    }

    // DELETE → Eliminar préstamos pendientes
    @DeleteMapping("/pendientes")
    public void eliminarPendientes(@RequestBody EstudianteDTO dto) {
        laboratorioService.eliminarPendientes(dto.getCodigoEstudiante());
    }
}

