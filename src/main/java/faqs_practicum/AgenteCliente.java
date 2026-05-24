package faqs_practicum;

import es.upm.AgentLauncher.Utils;
import jade.core.Agent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.List;

public class AgenteCliente extends Agent {

    private ComportamientoUsuario cu;
    private MainGUI gui;
    public String texto;
    private String temp;
    public int accion;
    @Override
    protected void setup() {
        System.out.println("AgenteCliente Setup");

        gui = new MainGUI(this);
        //gui.run();

        gui.start();

        cu = new ComportamientoUsuario();
        addBehaviour(cu);
    }

    class ComportamientoUsuario extends CyclicBehaviour {
        @Override
        public void action() {
            myAgent.doWait();
            ACLMessage msg;
            switch (accion) {
                case 0:
                    gui.getFrame().getRespuestaResumenTexto().setText("");
                    System.out.println("ComportamientoUsuario Resumen");
                    Utils.enviarMensaje(myAgent,"Resumen",texto);
                    msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                    temp = procesarMensaje(msg);
                    gui.getFrame().getRespuestaResumenTexto().setText(temp);
                    break;
                case 1:
                    gui.getFrame().getRespuestaPracticumTexto().setText("");
                    System.out.println("ComportamientoUsuario Practicum");
                    Utils.enviarMensaje(myAgent,"Practicum",texto);
                    msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                    temp = procesarMensaje(msg);
                    gui.getFrame().getRespuestaPracticumTexto().setText(temp);
                    break;
                case 2:
                    gui.getFrame().getRespuestaLlegarTexto().setText("");
                    System.out.println("ComportamientoUsuario Llegar");
                    Utils.enviarMensaje(myAgent,"Rutas",texto);
                    msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                    temp = procesarMensaje(msg);
                    gui.getFrame().getRespuestaLlegarTexto().setText(temp);
                    break;
                default:
                    break;
            }
            myAgent.doWait(500);
            gui.getFrame().cambiarEstadoBotones(true);

        }

        private String procesarMensaje(ACLMessage message) {
            String respuesta = "";
            try
            {
                List<String> mensajes=(List<String>)message.getContentObject();
                for(int i=0;i<mensajes.size();i++)
                {
                    respuesta = respuesta + "\n" + mensajes.get(i);
                }
            }
            catch (UnreadableException e)
            {
                e.printStackTrace();
            }
            return respuesta;
        }
    }
}
