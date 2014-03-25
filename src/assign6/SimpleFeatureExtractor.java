package assign6;

import java.util.List;

public class SimpleFeatureExtractor extends FeatureExtractor {

	public SimpleFeatureExtractor() {
		feature_names = new String[1];
		feature_names[0] = "word";
	}
	
	@Override
	public String[][] extractFeatures(List<String> lines) {
		String[][] ret = new String[lines.size()][feature_names.length + 2];
		for (int i = 0; i < lines.size(); ++i) {
			String[] parts = lines.get(i).split(" ");
			ret[i][0] = parts[0];
			ret[i][1] = parts[1];
			ret[i][2] = parts[2];
		}
		System.out.println();
		return ret;
	}

}
