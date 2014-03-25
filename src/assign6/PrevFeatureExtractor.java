package assign6;

import java.util.List;

public class PrevFeatureExtractor extends FeatureExtractor {

	public PrevFeatureExtractor() {
		name = "PrevFeatureExtractor";
		feature_names = new String[2];
		feature_names[0] = "tag";
		feature_names[1] = "prev_tag";
	}
	
	@Override
	public String[][] extractFeatures(List<String> lines) {
		String[][] ret = new String[lines.size()][feature_names.length];
		String prev_tag = "none";
		for (int i = 0; i < lines.size(); ++i) {
			String[] parts = lines.get(i).split(" ");
			ret[i][0] = parts[1];
			ret[i][1] = prev_tag;
			prev_tag = parts[1];
		}
		return ret;
	}

}
