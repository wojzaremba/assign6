package assign6;

import java.util.List;

public abstract class FeatureExtractor {

	public String[] feature_names;
	
	public abstract String[][] extractFeatures(List<String> lines);
	
}
