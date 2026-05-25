package routing_agent;

import base_agent.AgentModel;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AgenteRutas extends Agent {
    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName("Obtener ruta hasta la escuela");
        sd.setType(AgentModel.RUTAS.getValue());
        sd.addOntologies("ontologia");
        sd.addLanguages(new SLCodec().getName());
        dfd.addServices(sd);
        try{
            DFService.register(this,dfd);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                try {
                    ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                    if (msg == null) {
                        return; // lanzar error
                    }
                    // receive
                    String prompt =
                            "Generate a route to go from " + msg.getContent() + "to Escuela Tecnica Superior de Ingenieros Informáticos de la Universidad Politécnica de Madrid";
                    URL url = new URL("http://localhost:11434/api/generate");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    String json = "{ \"model\": \"llama3:8b\", " +
                            "\"prompt\": \"" +
                            prompt.replace("\"", "\\\\\"") +
                            "\", " +
                            "\"stream\": false }";

                    OutputStream os = conn.getOutputStream();
                    os.write(json.getBytes());
                    os.flush();
                    os.close();
                    BufferedReader br =
                            new BufferedReader(
                                    new InputStreamReader(
                                            conn.getInputStream()
                                    )
                            );
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }
                    System.out.println("Ruta para llegar a la escuela desde " + msg.getContent() + ": " + result);
                    // Send result.toString()
                    ACLMessage response = msg.createReply();
                    response.setPerformative(ACLMessage.INFORM);
                    response.setContent(result.toString());
                    send(response);
                } catch (Exception ex) {
                    System.err.println("error en agenteRutas");
                }
            }
        });
    }
}
