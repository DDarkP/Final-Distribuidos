package co.edu.unicauca.orquestador_service.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class RespuestaPazSalvoDTO {
    private String estado; // "PAZ Y SALVO" o "NO PAZ Y SALVO"
    private Map<String, List<Map<String, Object>>> pendientes = new HashMap<>();
}
