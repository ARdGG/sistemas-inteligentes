package faqs_practicum;

import es.upm.AgentLauncher.AgentBase;
import es.upm.AgentLauncher.AgentModel;

import jade.core.Agent;

public class PracticumAgent extends AgentBase{

	/*
	 * 	Notas:
	 *	Usamos Apache Lucene como motor de Information Retrieval, esta basado en un indice invertido
	 */
	
	@Override
	protected void setup() {
		System.out.println("Agente inicializado. AID: " + this.getAID());
		
		this.type = AgentModel.PRACTICUM;
		registerAgentDF();
		
		//Valores por defecto 
		String jsonDirectory = "src/main/resources/json_files";
		String indexDirectory = "src/main/resources/lucene_index";
		
		Object[] args = getArguments();
		if(args != null && args.length >= 2) {
			jsonDirectory = (String) args[0];
			indexDirectory = (String) args[1];
		}
		System.out.println("Usando rutas:\nJson = " + jsonDirectory + "\nIndex = " + indexDirectory);
	
		//Agregamos el comportamiento de Indexing (pobla la base de datos)
		IndexingBehaviour indexingBehaviour = new IndexingBehaviour(jsonDirectory, indexDirectory);
		addBehaviour(indexingBehaviour);
		
		//Agregamos el comportamiento de Busqueda
		SearchBehaviour searchBehaviour = new SearchBehaviour(indexDirectory);
		addBehaviour(searchBehaviour);
	}
	
	@Override
	protected void takeDown() {
		System.out.println("Lo lamento... 8==D");
	}
	
}
