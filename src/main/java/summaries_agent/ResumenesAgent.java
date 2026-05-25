package summaries_agent;
import base_agent.AgentBase;
import base_agent.AgentModel;

public class ResumenesAgent extends AgentBase{
	
	@Override
	protected void setup() {
		System.out.println("Agente inicializado. AID: " + this.getAID());

		//Valores por defecto 
		String guiasDirectory = "src/main/resources/guias";
		
		System.out.println("Usando rutas:\nGuias = " + guiasDirectory);
		
        //Configurar agente
        this.type = AgentModel.RESUMEN;
        registerAgentDF();

		//Agregamos el comportamiento de Busqueda
		BusquedaResumenBehaviour searchBehaviour = new BusquedaResumenBehaviour(guiasDirectory);
		addBehaviour(searchBehaviour);
    }
}
