package faqs_practicum;

import jade.core.Agent;

public class IndexerAgent extends Agent{

	@Override
	protected void setup() {
		System.out.println("Agente inicializado. AID: " + this.getAID());
		
		//Valores por defecto 
		String jsonDirectory = "src/main/resources/json_files";
		String indexDirectory = "src/main/resources/lucene_index";
		
		Object[] args = getArguments();
		if(args != null && args.length >= 2) {
			jsonDirectory = (String) args[0];
			indexDirectory = (String) args[1];
		}
		System.out.println("Usando rutas:\nJson = " + jsonDirectory + "\nIndex = " + indexDirectory);
		
		IndexingBehaviour behaviour = new IndexingBehaviour(jsonDirectory, indexDirectory);
		addBehaviour(behaviour);
	}
	
	@Override
	protected void takeDown() {
		System.out.println("Lo lamento...");
	}
	
}
