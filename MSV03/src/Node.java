import java.util.ArrayList;

public  class Node {
		//Map<String, Double> all_tags = new HashMap<>();
		ArrayList<String> path;
		double prob;
		double log_prob;
		public Node(ArrayList<String> path) {
			this.path = new ArrayList<>();
			this.prob = (double) 0.0;
			this.log_prob = (double) 0.0;
		}
	}