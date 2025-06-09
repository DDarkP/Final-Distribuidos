package co.edu.unicauca.laboratorio_service.dto;


import java.util.List;

import co.edu.unicauca.laboratorio_service.model.Prestamo;

public class EstadoDTO {
    private boolean pazSalvo;
    private List<Prestamo> pendientes;

    public EstadoDTO() {}

    public EstadoDTO(boolean pazSalvo, List<Prestamo> pendientes) {
        this.pazSalvo = pazSalvo;
        this.pendientes = pendientes;
    }

    public boolean isPazSalvo() {
        return pazSalvo;
    }

    public void setPazSalvo(boolean pazSalvo) {
        this.pazSalvo = pazSalvo;
    }

    public List<Prestamo> getPendientes() {
        return pendientes;
    }

    public void setPendientes(List<Prestamo> pendientes) {
        this.pendientes = pendientes;
    }
}
