package faqs_practicum;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.*;

public class SearchBehaviour extends CyclicBehaviour {

	
	/*
	 * CyclicBehaviour: 
	 * El agente se queda en bucle escuchando una peticion (REQUEST), mientras no haya se queda hibernando
	 */
	
	private String indexDirectory;
	private static final MessageTemplate MT = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
	
	public SearchBehaviour(String indexDirectory) {
		this.indexDirectory = indexDirectory;
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
	 * Extrae el contenido del mensaje recibido, realiza la busqueda y envia la respuesta al remitente 
	 */
	private void processIncomingMesage(ACLMessage message) {
		String userQuery = message.getContent();
		System.out.println("Mensaje recibido de " + message.getSender().getLocalName() + ": " + userQuery);
		
		String answer = searchFaq(userQuery);
		System.out.println("Respuesta a mensaje de" + message.getSender().getLocalName() + ": " + answer);
		sendReply(message, answer);
	}
	
	private String searchFaq(String userQuery) {
		try {
			Directory directory = FSDirectory.open(Paths.get(this.indexDirectory));
			//Comprueba si dentro del directorio existen los ficheros que usa Lucene
			if(!DirectoryReader.indexExists(directory)) {
				return "El sistema se esta inicializando. Intentalo de nuevo en un momento";
			}
			return queryLucene(directory, userQuery);
			
		} catch (IOException e) {
			System.out.println("Error I/O al conectar con Lucene");
			return "Ha ocurrido un error al buscar en la base de datos";
		} catch (ParseException e) {
			System.out.println("Error al parsear la query");
			return "Ha ocurrido un error al procesar tu consulta";
		}
	}
	
	/*
	 * Abre el lector de Lucene, parsea el texto del usuario y busca el documento con mayor puntuación
	 * de coincidencia (solo el top 1)
	 */
	private String queryLucene(Directory directory, String userQuery) throws IOException, ParseException {
		try(IndexReader reader = DirectoryReader.open(directory)){
			IndexSearcher searcher = new IndexSearcher(reader);//Motor de busqueda
			Query parsedQuery = buildCleanQuery(userQuery);
			TopDocs results = searcher.search(parsedQuery, 1);//Solo la respuesta top 1
			return extractBestAnswer(searcher, results);
		}
	}
	
	/*
	 * Limpia y formatea la consulta del usuario para transformarla en un query valido para Lucene
	 */
	private Query buildCleanQuery(String userQuery) throws ParseException {
		//Elimina caracteres especiales como ?,!,*, que el parser interpeta como comodines
		String escapedQuery = QueryParserBase.escape(userQuery);
		
		Analyzer analyzer = new StandardAnalyzer();
		//Indica que debe enfrentar la query contra el campo "pregunta" de los documentos
		QueryParser parser = new QueryParser("pregunta", analyzer);
		return parser.parse(escapedQuery);
	}
	
	/*
	 * Evalua los resultados devueltos por el motor de busqueda
	 * Si hay coincidencias extrae el campo "respuesta", si no devuelve un mensaje generico
	 */
	private String extractBestAnswer(IndexSearcher searcher, TopDocs results) throws IOException {
		if(results.totalHits.value() == 0) {
			return "No tengo informacion en mis FAQs sobre esto";
		}
		int bestDocumentId = results.scoreDocs[0].doc;
		Document bestMatch = searcher.storedFields().document(bestDocumentId);
		return bestMatch.get("respuesta");
	}
	
	/*
	 * Crea un mensaje de respuesta de tipo INFORM y lo envia
	 */
	private void sendReply(ACLMessage message, String answer) {
		ACLMessage reply = message.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		reply.setContent(answer);
		myAgent.send(reply);
	}
}
