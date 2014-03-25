package assign6;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.maxent.*;
import opennlp.maxent.io.*;
import opennlp.model.EventStream;

public class Classifier {
	
	private FeatureExtractor feature_extractor;
	private GISModel model;
    private String modelFileName = "model";

	public Classifier(FeatureExtractor feature_extractor) {
		this.feature_extractor = feature_extractor;		
	}
	
	private void saveFeatures(BufferedWriter bw, String[][] lines, String[] names) {
		try {		
			for (int i = 0; i < lines.length; ++i) {
				for (int k = 0; k < names.length; ++k) {
					bw.write(names[k] + "=" + lines[i][k] + " ");					
				}
				bw.write(lines[i][names.length] + " " + lines[i][names.length + 1] + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}	
	
	public String genFeatures(String path) {
		String output = path + "_features";
        String line;
	    BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			File file = new File(output);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			List<String> lines = new ArrayList<String>();
	        while ((line = br.readLine()) != null) {	
	        	if (line.length() == 0) {
	        		String[][] features = feature_extractor.extractFeatures(lines);
	        		saveFeatures(bw, features, feature_extractor.feature_names);	
	        		lines.clear();
	        		continue;
	        	}
	        	lines.add(line);
	        }    	    
	        br.close();	
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done generating features for " + path);
		return output;
	}
	
	public void train(String training) {
	    try {
	        FileReader datafr = new FileReader(new File(training));
	        EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr));
	        model = GIS.trainModel(es, 100, 4);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void saveModel() {
		try {
	        File outputFile = new File(modelFileName);
	        GISModelWriter writer = new SuffixSensitiveGISModelWriter(model, outputFile);
	        writer.persist();
		} catch (IOException e) {
			e.printStackTrace();
		}	        
	}
	
	public void loadModel() {
		try {
			model = (GISModel) new SuffixSensitiveGISModelReader(new File(modelFileName)).getModel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void test() { 
	    String res = model.getBestOutcome(model.eval(features));
	    System.out.println(res);
	}
	
	public static void main(String[] args) {
		Classifier c = new Classifier(new SimpleFeatureExtractor());
		String training = c.genFeatures("data/training");
		c.train(training);
		c.saveModel();
		c.loadModel();
//		c.test(test);
	    
	    
	   

	}

}
