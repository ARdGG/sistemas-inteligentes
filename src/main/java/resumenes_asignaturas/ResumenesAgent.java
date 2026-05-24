package resumenes_asignaturas;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ResumenesAgent extends Agent{
	
	@Override
	protected void setup() {
		System.out.println("Agente inicializado. AID: " + this.getAID());

		//Valores por defecto 
		String guiasDirectory = "src/main/resources/guias";
		
		Object[] args = getArguments();
		if(args != null && args.length >= 1) {
			guiasDirectory = (String) args[0];
		}
		System.out.println("Usando rutas:\nGuias = " + guiasDirectory);
		
        //Configurar agente
        ServiceDescription sd = new ServiceDescription();
        sd.setType("resumen-asignaturas");
        sd.setName(getLocalName());
        DFAgentDescription dfdescription = new DFAgentDescription();
        dfdescription.setName(getAID());
        dfdescription.addServices(sd);
        //Dar de alta el agente en el DF
        try {
            DFService.register(this, dfdescription);
        } catch (FIPAException e) {
            doDelete();
            e.printStackTrace();
        }

		//Agregamos el comportamiento de Busqueda
		BusquedaResumenBehaviour searchBehaviour = new BusquedaResumenBehaviour(guiasDirectory);
		addBehaviour(searchBehaviour);
    }

	@Override
	protected void takeDown() {
		System.out.println("Arrivederci :D");

        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
	}
}
