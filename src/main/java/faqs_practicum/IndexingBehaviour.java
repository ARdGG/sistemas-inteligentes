package faqs_practicum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONArray;
import org.json.JSONObject;

import jade.core.behaviours.OneShotBehaviour;

class IndexingBehaviour extends OneShotBehaviour {
	
	/*
	 * OneShotBehaviour: 
	 * El agente solo debe realizar la indexacion (cargarlos documentos en la bbdd) una unica vez.
	 * Al inicio de su creación
	 */
	
	private String jsonDirectory;	//Directorio fuente de FAQs en formato JSON
	private String indexDirectory;//Directorio donde almacenaremos los documentos indexados
	
	public IndexingBehaviour(String jsonDirectory, String indexDirectory) {
		this.jsonDirectory = jsonDirectory;
		this.indexDirectory = indexDirectory;
	}
	
	@Override
	public void action() {
		
		try(IndexWriter indexWriter = connectToLucene()){
			List<File> jsonFiles = getJsonFiles();
			if(jsonFiles.isEmpty()) {
				System.out.println("No se encontraron ficheros .json");
				return;
			}
			
			int totalIndexed = 0;
			for(File file: jsonFiles) {
				totalIndexed += indexFaqFile(file, indexWriter);
			}
			
			indexWriter.commit();
			System.out.println("Se indexaron " + totalIndexed + " preguntas y respuestas");
			
		} catch (IOException e) {
			System.out.println("Error I/O al conectar con Lucene");
		}	
	}
	
	/*
	 * Configura e inicializa el Index Writer de Lucene
	 * Usa el analizador de texto estandar y la ruta del indice (donde reside
	 * la BBDD)
	 */
	private IndexWriter connectToLucene() throws IOException{
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		Directory directory = FSDirectory.open(Paths.get(this.indexDirectory));
		return new IndexWriter(directory, config);

	}
	
	/* 
	 * Busca en el directorio fuente todos los ficheros que acaben en .json
	 */
	private List<File> getJsonFiles() {
		File directory = new File(this.jsonDirectory);
		List<File> jsonFiles = new ArrayList<>();
		
		File[] fileList = directory.listFiles();
		if(fileList == null) {return jsonFiles;}
		
		for(File file: fileList) {
			if(file.isFile() && file.getName().toLowerCase().endsWith(".json"))
				jsonFiles.add(file);
		}
		return jsonFiles;
	}
	
	/*
	 * Procesa un fichero Json, lo carga en memoria, lee su estructura (espera que sea una
	 * lista de objetos Json, un JsonArray) y recorre cada objeto de la lista para poder
	 * indexarlo.
	 */
	private int indexFaqFile(File file, IndexWriter indexWriter) {
		int faqCount = 0;

		try {
			String fileContent = new String(Files.readAllBytes(file.toPath())); 
			JSONArray faqList = new JSONArray(fileContent);
			for(int i = 0; i < faqList.length(); i++) {
				try {
					JSONObject json = faqList.getJSONObject(i);
					Document doc = createLuceneDocument(json);
					indexWriter.addDocument(doc);
					faqCount++;
				}
				catch (Exception e) {System.out.println("Error indexando posicion" + i + ": " + e.getMessage());}
			}
		} catch (Exception e) {
			System.out.println("Error leyendo el fichero" + file.getName() + ": " + e.getMessage());
		}
		
		return faqCount;
	}
	
	/*
	 * Mapea el objeto Json y lo transforma los campos que requiere Lucene.
	 * Decision de diseño:
	 * id y categoria se guardan como palabras, de esta forma se pueden buscar de forma exacta
	 * pregunta y respuesta se indexan en funcion de su texto (se tokeniza), esto permite busquedas
	 * mas amplias (por terminos relacionados y/o sinonimos) 
	 */
	private Document createLuceneDocument(JSONObject json) {
		Document documento = new Document();
		documento.add(new StringField("id", String.valueOf(json.getInt("id")), 	Field.Store.YES));
		documento.add(new StringField("categoria", json.getString("categoria"), Field.Store.YES));
		documento.add(new TextField("pregunta", json.getString("pregunta"), 	Field.Store.YES));
		documento.add(new TextField("respuesta", json.getString("respuesta"), 	Field.Store.YES));
		return documento;
	}
}