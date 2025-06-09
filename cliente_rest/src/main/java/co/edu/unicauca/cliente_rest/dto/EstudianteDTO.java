package co.edu.unicauca.cliente_rest.dto;

public class EstudianteDTO {
    private String codigoEstudiante;

    public EstudianteDTO() {}

    public EstudianteDTO(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public void setCodigoEstudiante(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
    }
}
