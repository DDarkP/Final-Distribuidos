package co.edu.unicauca.financiera_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.financiera_service.dto.DeudaDTO;
import co.edu.unicauca.financiera_service.dto.EstudianteDTO;
import co.edu.unicauca.financiera_service.service.FinancieraService;

@RestController
@RequestMapping("/api/financiera")
@CrossOrigin
public class FinancieraController {

    private final FinancieraService service;

    @Autowired
    public FinancieraController(FinancieraService service) {
        this.service = service;
    }

    // @PostMapping("/pendientes")
    // public List<DeudaDTO> obtenerPendientes(@RequestBody EstudianteDTO dto) {
    // return service.obtenerPendientes(dto.getCodigoEstudiante());
    // }
    @PostMapping("/pendientes")
    public ResponseEntity<?> obtenerPendientes(@RequestBody EstudianteDTO dto) {
        var resultado = service.obtenerPendientes(dto.getCodigoEstudiante());

        if (resultado == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "mensaje", "El estudiante no está registrado en el sistema financiero."));
        }

        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/pendientes")
    public Map<String, String> eliminarPendientes(@RequestBody EstudianteDTO dto) {
        service.eliminarPendientes(dto.getCodigoEstudiante());
        return Map.of("mensaje", "Deudas eliminadas con éxito");
    }
}
