package resumenes_asignaturas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.Normalizer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.*;

public class BusquedaResumenBehaviour extends CyclicBehaviour{

    String guiasDirectory;
    private static final MessageTemplate MT = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    private static final String CAB_DESCRIPCION = "descripción de la asignatura";
    private static final String CAB_TEMARIO = "temario de la asignatura";


    public BusquedaResumenBehaviour(String guiasDirectory) {
        this.guiasDirectory = guiasDirectory;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive(MT);
		if(message == null) {
			this.block();
			return;
		}
		processIncomingMesage(message);
    }

    /*
	 * Crea un mensaje de respuesta de tipo INFORM y lo envia
	 */
	private void enviarRespuesta(ACLMessage message, String answer) {
        ACLMessage reply = message.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		reply.setContent(answer);
		myAgent.send(reply);
    }

    /*
	 * Extrae el contenido del mensaje recibido, lo procesa y envía la respuesta
	 */
	private void processIncomingMesage(ACLMessage message) {
        String userSubjectRaw = message.getContent();
		System.out.println("Mensaje recibido de " + message.getSender().getLocalName() + ": " + userSubjectRaw);

        try {
            String userSubject = normalizarAsignatura(userSubjectRaw);
            File guia = localizarGuia(userSubject);
            if(guia == null){
                enviarRespuesta(message, "No encontré la guía para esa asignatura");
                System.out.println("No encontré la guía para esa asignatura");
                return;
            }
            String texto = extraerTexto(guia);
            if(texto == null){
                enviarRespuesta(message, "El texto de la guía de aprendizaje está corrupto");
                System.out.println("El texto de la guía de aprendizaje está corrupto");
                return;
            }
            String descripcion = recortarDescripcion(texto);
            if(descripcion == null){
                enviarRespuesta(message, "Esta asignatura no contiene descripción en su guía de aprendizaje");
                System.out.println("Esta asignatura no contiene descripción en su guía de aprendizaje");
                return;
            }
            String answer = limpiarRuido(descripcion);

            System.out.println("Respuesta a mensaje de" + message.getSender().getLocalName() + ": " + answer);
            enviarRespuesta(message, answer);
        } catch (Exception e) {
            e.printStackTrace();
            enviarRespuesta(message, "Ha ocurrido un error procesando tu petición");
        }
    }

    /*
	 * Limpia y formatea la consulta del usuario para transformarla en una asignatura válida
	 */
	private String normalizarAsignatura(String userSubjectRaw){
        String userSubject = userSubjectRaw.trim().toLowerCase().replace(" ", "_");
        userSubject = Normalizer.normalize(userSubject, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return userSubject;
    }

    private File localizarGuia(String userSubject){
        String userSubjectPdf = userSubject;
        if(!userSubject.endsWith(".pdf")){
            userSubjectPdf = userSubject.concat(".pdf");
        }

        File fichero = new File(guiasDirectory, userSubjectPdf);
        if(fichero.exists()){
            // Suponemos que no hay problemas de privilegios
            return fichero;
        }
        else{
            //OJO, podría no haber guía
            return null;
        }

    }

    private String extraerTexto(File guia){
        //OJO, el texto podría estar corrupto
        
        try (PDDocument documento = PDDocument.load(guia)) {
            PDFTextStripper texto = new PDFTextStripper();
            return texto.getText(documento);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
    }

    private String recortarDescripcion(String texto){
        String textoLower = texto.toLowerCase();

        int indiceInicio = textoLower.lastIndexOf(CAB_DESCRIPCION);
        int indiceFinal = textoLower.indexOf(CAB_TEMARIO, indiceInicio);
        if(indiceInicio == -1 || indiceFinal == -1){
            System.out.println("No existe alguna de las cabeceras de la guia de estudio");
            return null;
        }
        //OJO, podría no haber cabecera descripción
        return texto.substring(indiceInicio + CAB_DESCRIPCION.length(), indiceFinal); 
    }

    private String limpiarRuido(String texto){
        String[] lineas = texto.split("\n");
        List<String> limpio = new ArrayList<>();

        int i = 0;
        while(i < lineas.length){
            if(lineas[i].startsWith("Página")){
                // Esta línea es "Página": quito las 4 que ya copié antes...
                int quitar = Math.min(4, limpio.size());
                for(int k = 0; k < quitar; k++){
                    limpio.remove(limpio.size() - 1);
                }
                // ...y salto esta "Página" + las 7 siguientes (sin copiarlas)
                i += 8;
            } else {
                limpio.add(lineas[i]);   // línea buena: la copio
                i++;
            }
        }
        limpio.remove(limpio.size() - 1); // La ultima línea es la numeración del subtitulo, que siempre está y hay que limpiar.
        return String.join("\n", limpio);
    }
}
