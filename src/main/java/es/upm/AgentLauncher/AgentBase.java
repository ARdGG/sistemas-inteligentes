package es.upm.AgentLauncher;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public abstract class AgentBase extends Agent{
	protected AgentModel type = AgentModel.DESCONOCIDO;
	
	public void registerAgentDF() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType(this.type.getValue());
		sd.setName(this.getLocalName());
		
		dfd.addServices(sd);
		
		try {
			DFAgentDescription[] results = DFService.search(this, dfd);
			if(results == null || results.length == 0) {
				DFService.register(this, dfd);
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	public void deregisterAgentDF() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void doDelete() {
		super.doDelete();
		deregisterAgentDF();
		System.out.println("Agente " + getLocalName() + " terminado.");
	}

}
