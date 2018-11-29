import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Viterbi {
	
	public static Map<String, Node> all_tags1 = new HashMap<>();
	public static Map<String, Node> all_tags2 = new HashMap<>();
	
	
	
	public static void clear_map(Map<String, Node> mp) {
		for (Entry<String, Node> e : mp.entrySet()) {
			Node n = new Node(null);
			n.prob = 0.0;
			mp.put(e.getKey(), n);
		}
	}
	
	public static ArrayList<String> viterbi(String s) {
		int start = 1;
		ArrayList<String> path = new ArrayList<>();
		int count = 0;
		String[] sa = s.split(" ");
		int len = sa.length;
		// tags1 has latest entries
		boolean tags1 = true;
		double new_prob = 0.0;
		double tmp_prob = 0.0;
		boolean path_added = false;
		for (String g : sa) {
			// if first
			if (count == 0) {
				Node n = new Node(path);
				n = new Node(path);
				double best_emission = 0.0;
				String state = "";
				for (Entry<String, Node> e : all_tags1.entrySet()) {
					if (main.get_emission_entry(g, e.getKey()) > best_emission) {
						best_emission = main.get_emission_entry(g, e.getKey()) * start;
						state = e.getKey();
					}
				}
				n.prob = best_emission;
				n.path.add(state);
				all_tags1.put(state, n);
				count++;
				continue;
			}

			// if last
			if (count == len - 1) {
				double largest = 0.0;
				Node n = null;
				if (tags1) {
					for (Entry<String, Node> e : all_tags1.entrySet()) {
						if (largest < e.getValue().prob) {
							largest = e.getValue().prob;
							n = e.getValue();
						}
					}
				} else {
					for (Entry<String, Node> e : all_tags2.entrySet()) {
						if (largest < e.getValue().prob) {
							largest = e.getValue().prob;
							n = e.getValue();
						}
					}
				}
				return n.path;
			}

			if (tags1) {
				clear_map(all_tags2);
				for (Entry<String, Node> e : all_tags1.entrySet()) {
					if (e.getValue().prob == 0.0) continue;
					for (Entry<String, Node> f : all_tags2.entrySet()) {
						tmp_prob = (main.transitionMatrix.get(e.getKey()).get(f.getKey()) == null) ? 0.1 : main.transitionMatrix.get(e.getKey()).get(f.getKey());
						new_prob = Math.log10(1.0+tmp_prob) + e.getValue().prob + Math.log10(1.0+main.get_emission_entry(g, f.getKey()));
						if (f.getValue().prob < new_prob) { // set maximum path only for O(n*k^2)
							f.getValue().prob = new_prob;
							System.out.println(new_prob);
							f.getValue().log_prob = Math.log10(1.0+main.get_emission_entry(g, f.getKey())) + Math.log10(1.0+tmp_prob) + e.getValue().prob;
							f.getValue().path.clear();
							f.getValue().path.addAll(e.getValue().path);
							if (path_added) {
								f.getValue().path.remove(f.getValue().path.size()-1);
								f.getValue().path.add(f.getKey());
							} else {
								f.getValue().path.add(f.getKey());
								path_added = true;
							}
						}
						path_added = false;
					}
					for (Entry<String, Node> f : all_tags2.entrySet()) {
						//f.getValue().prob = f.getValue().log_prob;
					}
				}
				tags1 = false;
			} else {
				clear_map(all_tags1);
				for (Entry<String, Node> e : all_tags2.entrySet()) {
					if (e.getValue().prob == 0.0) continue;
					for (Entry<String, Node> f : all_tags1.entrySet()) {
						tmp_prob = (main.transitionMatrix.get(e.getKey()).get(f.getKey()) == null) ? 0.1 : main.transitionMatrix.get(e.getKey()).get(f.getKey());
						new_prob = Math.log10(1.0+tmp_prob) + e.getValue().prob + Math.log10(1.0+main.get_emission_entry(g, f.getKey()));
						if (f.getValue().prob < new_prob) { // set maximum path only for O(n*k^2)
							f.getValue().prob = new_prob;
							f.getValue().log_prob = Math.log10(1.0+main.get_emission_entry(g, f.getKey())) + Math.log10(1.0+tmp_prob) + e.getValue().prob;
							f.getValue().path.clear();
							f.getValue().path.addAll(e.getValue().path);
							if (path_added) {
								f.getValue().path.remove(f.getValue().path.size()-1);
								f.getValue().path.add(f.getKey());
							} else {
								f.getValue().path.add(f.getKey());
								path_added = true;
							}
						}
						path_added = false;
					}
					for (Entry<String, Node> f : all_tags1.entrySet()) {
						//f.getValue().prob = f.getValue().log_prob;
					}
				}
				tags1 = true;
			}
			
			
			
			count++;
		}
		
		return path;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		main.main(new String[0]);
		//System.out.println((double) 0.1234567891234567891234567891234567865456432536432563245465743516425346546356);
		//System.out.println(all_tags1.size());
		String s = "The Public Service Commission has ruled that this is not a public utility , subject to their many regulations .";		
		ArrayList<String> path = viterbi(s);
		for (String g : path) {
			System.out.println(g);
		}
	}

}
