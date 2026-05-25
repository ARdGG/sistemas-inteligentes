package routing_agent;

import base_agent.AgentBase;
import base_agent.AgentModel;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class AgenteRutas extends AgentBase {
    @Override
    protected void setup() {
        this.type = AgentModel.RUTAS;
        registerAgentDF();

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = null;
                ArrayList<String> res = new ArrayList<>();
                try {
                    msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                    if (msg == null) {
                        return; // lanzar error
                    }
                    // receive
                    String prompt =
                            "Cómo llegar desde " + msg.getContent() + " a la Escuela Tecnica Superior de Ingenieros Informáticos de la Universidad Politécnica de Madrid. Devuelve el resultado de forma breve y numerada";
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
                    os.write(json.getBytes(StandardCharsets.UTF_8));
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
                    res.add(result.toString());
                    response.setContentObject((Serializable) res);
                    send(response);
                } catch (Exception ex) {
                    ACLMessage response = msg.createReply();
                    response.setPerformative(ACLMessage.INFORM);
                    try {
                        res.add("Ha habido un error");
                        response.setContentObject((Serializable) res);
                    } catch (IOException e) {
                        System.err.println("Error al enviar error");
                    }
                    send(response);
                    System.err.println("error en agenteRutas");
                    ex.printStackTrace();
                }
            }
        });
    }
}
