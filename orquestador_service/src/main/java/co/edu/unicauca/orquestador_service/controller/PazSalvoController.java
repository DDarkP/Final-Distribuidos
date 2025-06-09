package co.edu.unicauca.orquestador_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.orquestador_service.dto.EstudianteDTO;
import co.edu.unicauca.orquestador_service.service.PazSalvoService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/api/orquestador")
@RequiredArgsConstructor
public class PazSalvoController {

    private final PazSalvoService pazSalvoService;

    @PostMapping("/verificar")
    public ResponseEntity<String> verificarPazSalvo(@RequestBody EstudianteDTO dto) {
        String mensaje = pazSalvoService.obtenerPendientesYNotificar(dto.getCodigoEstudiante());
        if (mensaje.contains("NO está a paz y salvo")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mensaje);
        } else {
            return ResponseEntity.ok(mensaje);
        }
    }

    @PostMapping("/verificarAsync")
    public Mono<ResponseEntity<String>> verificarPazSalvoAsync(@RequestBody EstudianteDTO dto) {
        return pazSalvoService.construirMensajePendientesAsyncYNotificar(dto.getCodigoEstudiante())
                .map(msg -> {
                    if (msg.contains("NO está a paz y salvo")) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
                    } else {
                        return ResponseEntity.ok(msg);
                    }
                });
    }

}
