import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.plaf.BorderUIResource.EmptyBorderUIResource;

import java.util.Set;

public class ViterbiDN {
	
	public static Map<String, Node> all_tags1 = new HashMap<>();
	public static Map<String, Node> all_tags2 = new HashMap<>();
	public static Map<String, Node> all_tags_tmp = null;
	
	public static int precision = 0;
	
	public static void clear_map(Map<String, Node> mp) {
		for (Entry<String, Node> e : mp.entrySet()) {
			Node n = new Node(null);
			//n.prob = 0.0;
			n.prob = 0.0;
			mp.put(e.getKey(), n);
		}
	}
	
	public static ArrayList<String> viterbi(String s1, String truth) {
		String s = s1;
		int start = 1;
		ArrayList<String> path = new ArrayList<>();
		int count = 0;
		String[] sa = s.split(" ");
		int len = sa.length;
		double new_prob = 0.0;
		double tmp_prob = 0.0;
		double new_factor = 0.0;
		boolean path_added = false;
		if (sa.length == 1) { // if string is only one word long
			for (String g : sa) {
				Node n = new Node(path);
				n = new Node(path);
				double best_emission = 0.0;
				String state = "";
				if (!main.words.contains(g)) { // aus selbst gemessenen werten
					best_emission = (double) 0.17;
					state = "pps";
					if (g.endsWith("ed")) {
						best_emission = (double) 0.2;
						state = "vbn";
					}
					n.prob = (1.0 + best_emission);
					n.path.add(state);
					return n.path;
				}
				for (Entry<String, Node> e : all_tags1.entrySet()) {
					if (main.get_emission_entry(g, e.getKey()) > best_emission) {
						best_emission = main.get_emission_entry(g, e.getKey()) + 0.0001;
						state = e.getKey();
					}
				}
				n.prob = (1.0 + best_emission);
				n.path.add(state);
				return n.path;
			}
		}
		for (String g : sa) {
			// if first
			if (count == 0) {
				Node n = new Node(path);
				n = new Node(path);
				double best_emission = 0.0;
				String state = "";
				if (!main.words.contains(g)) { // aus selbst gemessenen werten
					best_emission = (double) 0.17;
					state = "pps";
					if (g.endsWith("ed")) {
						best_emission = (double) 0.2;
						state = "vbn";
					}
				} else {
					for (Entry<String, Node> e : all_tags1.entrySet()) {
						if (main.get_emission_entry(g, e.getKey()) > best_emission) {
							best_emission = main.get_emission_entry(g, e.getKey()) * 1;
							state = e.getKey();
						}
					}
				}
				n.prob = (1.0+best_emission);
				n.path.add(state);
				all_tags1.put(state, n);
				count++;
				continue;
			}
			

				clear_map(all_tags2);
				for (Entry<String, Node> e : all_tags1.entrySet()) {
					if (e.getValue().prob == 0.0) continue;
					double max = 0.0;
					for (Entry<String, Node> f : all_tags2.entrySet()) {
						if (!all_tags1.containsKey(e.getKey()) || !all_tags2.containsKey(e.getKey())) continue;
						if (!main.words.contains(g)) {
							if (main.transitionMatrix.get(e.getKey()).get(f.getKey()) == null) continue;
							if (max < main.transitionMatrix.get(e.getKey()).get(f.getKey())) {
								max = main.transitionMatrix.get(e.getKey()).get(f.getKey());
							}
							tmp_prob = (max < tmp_prob) ? tmp_prob : max;
							new_prob = (max < new_prob) ? new_prob : max;
							if (g.endsWith("ed")) { // most of times words ending with ed are verbs -> State = vbn
								f.getValue().log_prob = Math.log10(1.0+tmp_prob) + Math.log10(1.0+e.getValue().prob);
								f.getValue().prob = f.getValue().log_prob;
								f.getValue().path.clear();
								f.getValue().path.addAll(e.getValue().path);
								f.getValue().path.add("vbn");
								break;
							}
						} else {
							tmp_prob = (main.transitionMatrix.get(e.getKey()).get(f.getKey()) == null) ? 0.00005 : main.transitionMatrix.get(e.getKey()).get(f.getKey());
							//new_prob = Math.log(1.0+tmp_prob) + Math.log(e.getValue().prob) + Math.log(1.0+main.get_emission_entry(g, f.getKey()));
							//new_prob = (Math.log10(1.0+main.get_emission_entry(g, f.getKey())) + Math.log10(1.0+tmp_prob) + Math.log10(1.0+e.getValue().prob));
							new_prob = tmp_prob * e.getValue().prob * main.get_emission_entry(g, f.getKey());
							if (f.getValue().path.size() >= 3) {
								String a = f.getValue().path.get(f.getValue().path.size()-1);
								String b = f.getValue().path.get(f.getValue().path.size()-2);
								String c = f.getValue().path.get(f.getValue().path.size()-3);
								if (main.transitionMatrix.get(c).get(b) != null && main.transitionMatrix.get(b).get(a) != null) {
									new_factor = main.transitionMatrix.get(c).get(b) * main.transitionMatrix.get(b).get(a);
									new_factor = (new_factor > (double) 0.000000001) ? new_factor+0.07 : 0;
								} else {
									new_factor = 0.0;
								}
							}
							new_prob += new_factor;
							new_factor = 0.0;
						}
						if (f.getValue().prob < new_prob) { // set maximum path only for O(n*k^2)
							f.getValue().prob = new_prob;
							f.getValue().log_prob = (Math.log10(1.0+main.get_emission_entry(g, f.getKey())) + Math.log10(1.0+tmp_prob) + Math.log10(1.0+e.getValue().prob));
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
						f.getValue().prob = f.getValue().log_prob;
					}
				}
				all_tags_tmp = all_tags1;
				all_tags1 = all_tags2;
				all_tags2 = all_tags_tmp;

			// if last
			if (count == len - 1) {
				double largest = 0.0;
				Node n = null;
					for (Entry<String, Node> e : all_tags1.entrySet()) {
						if (largest < e.getValue().prob) {
							largest = e.getValue().prob;
							n = e.getValue();
						}
					}
				return n.path;
			}
			
			
			
			count++;
		}
		
		//System.out.println("nulllll");
		return path;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String[] arrays = {""};
		main.main(arrays);
		
		
		//System.out.println();
		
		//System.out.println((double) 0.1234567891234567891234567891234567865456432536432563245465743516425346546356);
		//System.out.println(all_tags1.size());
		String s = "The Public Service Commission has ruled this is not a public utility , subject to their many regulations .";		
		//s = "For example , the officials of Poughkeepsie town ( township ) where the project is located think highly of it because it simplifies their snow clearing problem .";
		//s = "and ( C ) to finance , for not more than three years beyond the end of said period , such activities as are required to correlate , coordinate , and round out the results of studies and research undertaken pursuant to this Act : Provided , That funds available in any one year for research and development may , subject to the approval of the Secretary of State to assure that such activities are consistent with the foreign policy objectives of the United States , be expended in cooperation with public or private agencies in foreign countries in the development of processes useful to the program in the United States : And provided further , That every such contract or agreement made with any public or private agency in a foreign country shall contain provisions effective to insure that the results or information developed in connection therewith shall be available without cost to the United States for the use of the United States throughout the world and for the use of the general public within the United States .";
		//s = "and ( C ) to finance , for not more than three years beyond the end of said period , such";
		//s = "Rookie Ron Nischwitz continued his pinpoint pitching Monday night as the Bears made it two straight over Indianapolis , 5-3 .";
		ArrayList<String> path = viterbi(s, s);
		for (String g : path) {
			System.out.println(g);
		}
		
		for (Entry<String, Double> g : main.emissions_matrix.entrySet()) {
			//System.out.println(g.getKey());
		}
	}

}
