package assign6;

import java.util.List;

public class WordNextPrevFeatureExtractor extends FeatureExtractor {

	public WordNextPrevFeatureExtractor() {
		name = "WordNextPrevFeatureExtractor";
		feature_names = new String[4];
		feature_names[0] = "tag";
		feature_names[1] = "prev_tag";
		feature_names[2] = "next_tag";
		feature_names[3] = "word";
	}
	
	@Override
	public String[][] extractFeatures(List<String> lines) {
		String[][] ret = new String[lines.size()][feature_names.length];
		String prev_tag = "none";
		for (int i = 0; i < lines.size(); ++i) {
			String[] parts = lines.get(i).split(" ");
			ret[i][3] = parts[0];
			ret[i][0] = parts[1];
			ret[i][1] = prev_tag;
			prev_tag = parts[1];
			if (i >= 1) {
				ret[i - 1][2] = ret[i][0];
			}
		}
		if (lines.size() > 1) {
			ret[0][2] = ret[1][0];	
		} else {
			ret[0][2] = "none";
 		}
		return ret;
	}

}
