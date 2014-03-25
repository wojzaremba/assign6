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
    private List<List<String>> alllines;
    private List<String[][]> features;
    private List<char[]> labels;

	public Classifier(FeatureExtractor feature_extractor) {
		System.out.println("Creating Classifier");
		this.feature_extractor = feature_extractor;		
	}
	
	public void parseFile(String path) {
		System.out.println("Parsing file: " + path);		
        String line;
	    BufferedReader br;
	    alllines = new ArrayList<List<String>>();
		try {
			br = new BufferedReader(new FileReader(path));
			List<String> lines = new ArrayList<String>();
	        while ((line = br.readLine()) != null) {	
	        	if (line.length() == 0) {
	        		alllines.add(lines);
	        		lines = new ArrayList<String>();
	        		continue;
	        	}
	        	lines.add(line);
	        }    	    
	        br.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void genFeatures() {
		System.out.println("Generating features");
		features = new ArrayList<String[][]> ();
		for (int i = 0; i < alllines.size(); ++i) {
			String[][] f = feature_extractor.extractFeatures(alllines.get(i));
			features.add(f);
		}
	}

	public void genLabels() {
		System.out.println("Generating labels");
		labels = new ArrayList<char[]> ();
		for (int i = 0; i < alllines.size(); ++i) {
			char[] l = feature_extractor.extractLabels(alllines.get(i));			
			labels.add(l);
		}
	}	
	
	public void saveProcessedData(String path) {
		System.out.println("Saving features");
		try {		
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());				
			BufferedWriter bw = new BufferedWriter(fw);		
			String[] names = feature_extractor.feature_names;
			for (int i = 0; i < features.size(); ++i) {
				for (int j = 0; j < features.get(i).length; ++j) {
					for (int k = 0; k < names.length; ++k) {
						bw.write(names[k] + "=" + features.get(i)[j][k] + " ");					
					}
					bw.write(labels.get(i)[j] + "\n");
				}
			}
			bw.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}					
	}
	
	public void train(String training) {
		System.out.println("Training model on data: " + training);
	    try {
	        FileReader datafr = new FileReader(new File(training));
	        EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr));
	        model = GIS.trainModel(es, 100, 4, false, false);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void saveModel() {
		System.out.println("Saving model");
		try {
	        File outputFile = new File(modelFileName);
	        GISModelWriter writer = new SuffixSensitiveGISModelWriter(model, outputFile);
	        writer.persist();
		} catch (IOException e) {
			e.printStackTrace();
		}	        
	}
	
	public void loadModel() {
		System.out.println("Loading model");
		try {
			model = (GISModel) new SuffixSensitiveGISModelReader(new File(modelFileName)).getModel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void test() { 
		System.out.println("Testing");
		double correct = 0;
		double all = 0;
		double correct_phrases = 0;
		double real_phrases = 0;
		double mine_phrases = 0;
		for (int i = 0; i < features.size(); ++i) {
			String[][] fs = features.get(i);
			char[] l = labels.get(i);
			List<Integer> begins_real = new ArrayList<Integer>();
			List<Integer> ends_real = new ArrayList<Integer>();
			List<Integer> begins_mine = new ArrayList<Integer>();
			List<Integer> ends_mine = new ArrayList<Integer>();
			char[] r = new char[l.length];
			for (int j = 0; j < fs.length; ++j) {
				String[] f = fs[j];		
				String[] fstring = feature_extractor.featureStringRep(f);
			    String res = model.getBestOutcome(model.eval(fstring));
			    r[j] = res.charAt(0);
			}			
			for (int j = 0; j < r.length; ++j) {
			    all++;
			    if (r[j] == l[j]) {
			    	correct++;
			    }
			    if (r[j] == 'B') {
			    	begins_mine.add(j);
			    }
			    if (l[j] == 'B') {
			    	begins_real.add(j);
			    }			    
			    if (((r[j] == 'B') || (r[j] == 'I')) && ((j == r.length - 1) || (r[j + 1] == 'B') || (r[j + 1] == 'O'))) {
			    	ends_mine.add(j);
			    }			 			    
			    if (((l[j] == 'B') || (l[j] == 'I')) && ((j == l.length - 1) || (l[j + 1] == 'B') || (l[j + 1] == 'O'))) {
			    	ends_real.add(j);
			    }			    
			}
		    begins_real.add(10000000);
		    ends_real.add(10000000);	
			int idx_real = 0;
			for (int j = 0;j < begins_mine.size(); ++j) {				
				while (begins_mine.get(j) > begins_real.get(idx_real)) {
					idx_real++;
				}
				if ((begins_mine.get(j) == begins_real.get(idx_real)) && (ends_mine.get(j) == ends_real.get(idx_real))) {
					correct_phrases++;
				}
			}
			mine_phrases += begins_mine.size();
			real_phrases += ends_mine.size();
			begins_real.remove(begins_real.size() - 1);
			ends_real.remove(ends_real.size() - 1);
		}
		System.out.println("Extractor: " + feature_extractor.name);
		System.out.println("Accuracy = " + correct / all);
		double precision = correct_phrases / mine_phrases;
		double recall = correct_phrases / real_phrases;
		double f = (2 * precision * recall) / (precision + recall);
		System.out.println("Precision = " + precision);
		System.out.println("Recall = " + recall);
		System.out.println("F = " + f);
	}
	
	public String processFile(String path) {
		parseFile(path);
		genFeatures();
		genLabels();
		String output = path + "_features";
		saveProcessedData(output);		
		return output;
	}
	
	public static void main(String[] args) {
		FeatureExtractor[] extractors = {new SimpleFeatureExtractor(), new PrevFeatureExtractor(), new NextPrevFeatureExtractor(), new WordNextPrevFeatureExtractor()}; 
		for (int e = 0; e < extractors.length; ++e) {
			Classifier c = new Classifier(extractors[e]);
			String training = "data/training";
			String test = "data/test";
			String training_features = c.processFile(training);
			c.train(training_features);
			c.processFile(test);	
		    c.test();
		}
	}

}
