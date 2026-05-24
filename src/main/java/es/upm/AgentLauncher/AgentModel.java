package es.upm.AgentLauncher;

public enum AgentModel {
	INTERFAZ("Interfaz"),
	RESUMEN("Resumen"),
	PRACTICUM("Practicum"),
	RUTAS("Rutas"),
	DESCONOCIDO("Desconocido");
	
	private final String value;
	
	AgentModel(String value){
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public static AgentModel getEnum(String value) {
		switch (value) {
			case "Interfaz": 
				return INTERFAZ;
			case "Resumen":  
				return RESUMEN;
			case "Practicum":
				return PRACTICUM;
			case "Rutas":    
				return RUTAS;
			default:         
				return DESCONOCIDO;
		}
	}
}
