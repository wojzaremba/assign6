package assign6;

import java.util.List;

public class SimpleFeatureExtractor extends FeatureExtractor {

	public SimpleFeatureExtractor() {
		name = "SimpleFeatureExtractor";
		feature_names = new String[1];
		feature_names[0] = "tag";
	}
	
	@Override
	public String[][] extractFeatures(List<String> lines) {
		String[][] ret = new String[lines.size()][feature_names.length];
		for (int i = 0; i < lines.size(); ++i) {
			String[] parts = lines.get(i).split(" ");
			ret[i][0] = parts[1];
		}
		return ret;
	}

}
