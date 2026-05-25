package base_agent;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.io.Serializable;

public class Utils {

	protected static DFAgentDescription[] buscarAgentes(Agent agent, String tipo) {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription templateSd = new ServiceDescription();
		templateSd.setType(tipo);
		template.addServices(templateSd);

		SearchConstraints sc = new SearchConstraints();
		sc.setMaxResults(Long.MAX_VALUE);

		try {
			return DFService.search(agent, template, sc);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void enviarMensaje(Agent agent, String tipo, String mensaje) {
		DFAgentDescription[] dfd = buscarAgentes(agent, tipo);

		if(dfd != null && dfd.length > 0) {
			ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);

			for(int i = 0; i < dfd.length; i++) {
				aclMessage.addReceiver(dfd[i].getName());
			}

			aclMessage.setOntology("ontologia");
			aclMessage.setLanguage(new SLCodec().getName());
			aclMessage.setEnvelope(new Envelope());
			aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
			aclMessage.setContent(mensaje);

			agent.send(aclMessage);
		}
		else {
			System.out.println("No se encontró ningún agente del tipo: " + tipo);
		}
	}
}

