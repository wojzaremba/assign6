package assign6;

import java.util.List;

public abstract class FeatureExtractor {

	public String[] feature_names;
	
	public String name;
	
	public abstract String[][] extractFeatures(List<String> lines);
 	
	public String[] featureStringRep(String[] f) {
		String[] ret = new String[f.length];
		for (int i = 0; i < f.length; ++i) {
			ret[i] = feature_names[i] + "=" + f[i];
		}
		return ret;
	}
	
	public char[] extractLabels(List<String> lines) {
		char[] labels = new char[lines.size()];
		for (int i = 0; i < lines.size(); ++i) {
			String[] parts = lines.get(i).split(" ");
			labels[i] = parts[2].charAt(0);
			if ((i > 0) && (labels[i] == 'I') && (labels[i - 1] == 'O')) {
				labels[i] = 'B';
			}
		}
		return labels;
	}
	
}
