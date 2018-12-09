import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class Viterbi_Sed
{
	
	public static Map<String, Node> all_tags1 = new HashMap<>();
	public static Map<String, Node> all_tags2 = new HashMap<>();
	
	
	
	public static void clear_map(Map<String, Node> mp) {
		for (Entry<String, Node> e : mp.entrySet()) {
			Node n = new Node(null);
			n.prob = 0.0;
			mp.put(e.getKey(), n);
		}
	}


	public static void processMaps(){


		//create states array
		states = new String[all_tags1.size()];
		int counter = 0;
		for (Entry<String, Node> entry: all_tags1.entrySet())
		{
			states[counter] = entry.getKey();
			counter++;
		}


		//create start_prob array
		start_probability = new double[all_tags1.size()];
		Arrays.fill(start_probability, 1.0);


	}


	private static String[] states;
	private static double[] start_probability;



	public static class TNode {
		public int[] v_path;
		public double v_prob;

		public TNode( int[] v_path, double v_prob) {
			this.v_path = copyIntArray(v_path);
			this.v_prob = v_prob;
		}
	}


	private static int[] copyIntArray(int[] ia) {
		int[] newIa = new int[ia.length];
		for (int i = 0; i < ia.length; i++) {
			newIa[i] = ia[i];
		}
		return newIa;
	}

	private static int[] copyIntArray(int[] ia, int newInt) {
		int[] newIa = new int[ia.length + 1];
		for (int i = 0; i < ia.length; i++) {
			newIa[i] = ia[i];
		}
		newIa[ia.length] = newInt;
		return newIa;
	}


	public static ArrayList<String> forwardViterbi(String[] word, String[] xState, double[] startProp) {
		TNode[] T = new TNode[xState.length];
		for (int state = 0; state < xState.length; state++) {
			int[] intArray = new int[1];
			intArray[0] = state;
			T[state] = new TNode( intArray, startProp[state] * main.get_emission_entry(word[0], xState[state]));
		}

		for (int output = 1; output < word.length; output++) {
			TNode[] U = new TNode[xState.length];
			for (int next_state = 0; next_state < xState.length; next_state++) {
				int[] argmax = new int[0];
				double valmax = 0;
				for (int state = 0; state < xState.length; state++) {
					int[] v_path = copyIntArray(T[state].v_path);
					double v_prob = T[state].v_prob;
					double transition = (main.transitionMatrix.get(xState[state]) == null) ? 0.2:0.1;
					if (transition == 0.1) {
						transition = (main.transitionMatrix.get(xState[state]).get(xState[next_state])==null)?0.1:main.transitionMatrix.get(xState[state]).get(xState[next_state]);
					} else {
						transition = 0.1;
					}

					double result = Math.log(1+main.get_emission_entry(word[output], xState[next_state])) * Math.log(1+transition) * 100;
					v_prob *= result;

					if (v_prob > valmax) {
						if (v_path.length == word.length) {
							argmax = copyIntArray(v_path);
						} else {
							argmax = copyIntArray(v_path, next_state);
						}
						valmax = v_prob;
					}
				}
				U[next_state] = new TNode( argmax, valmax);
			}
			T = U;
		}
		// apply sum/max to the final states:
		int[] argmax = new int[0];
		double valmax = 0;
		for (int state = 0; state < xState.length; state++) {
			int[] v_path = copyIntArray(T[state].v_path);
			double v_prob = T[state].v_prob;
			if (v_prob > valmax) {
				argmax = copyIntArray(v_path);
				valmax = v_prob;
			}
		}
		ArrayList<String> re = new ArrayList<>();
		//System.out.print("Viterbi_Sed path: [");
		for (int i = 0; i < argmax.length; i++) {
			//System.out.print(states[argmax[i]] + ", ");
			re.add(states[argmax[i]]);
		}
		//System.out.println("].\n Probability of the whole system: " + valmax);
		return re;
	}


	public static ArrayList<String> main(String args) throws IOException {
		// TODO Auto-generated method stub
		//main.main(new String[0]);
		//System.out.println((double) 0.1234567891234567891234567891234567865456432536432563245465743516425346546356);
		//System.out.println(all_tags1.size());
		String s = "The Public Service Commission has ketaAA ruled that this is a public utility subject to many regulations";
		s = args;
		//s = "and ( C ) to finance , for not more than three years beyond the end of said period , such activities as are required to correlate , coordinate , and round out the results of studies and research undertaken pursuant to this Act : Provided , That funds available in any one year for research and development may , subject to the approval of the Secretary of State to assure that such activities are consistent with the foreign policy objectives of the United States , be expended in cooperation with public or private agencies in foreign countries in the development of processes useful to the program in the United States : And provided further , That every such contract or agreement made with any public or private agency in a foreign country shall contain provisions effective to insure that the results or information developed in connection therewith shall be available without cost to the United States for the use of the United States throughout the world and for the use of the general public within the United States .";
		//s = "and ( C ) to finance , for not more than three years beyond the end of said period , such activities as are required to correlate , coordinate , and round out the results of studies and research undertaken pursuant to this Act : Provided , That funds available in any one year for research and development may , subject to the approval of the Secretary of State to assure that such activities are consistent with the foreign policy objectives of the United States , be expended in cooperation with public or private agencies in foreign countries in the development of processes useful to the program in the United States : And provided further , That every such contract or agreement";
		//s = "Furthermore , as an encouragement to revisionist thinking , it manifestly is fair to admit that any fraternity has a constitutional right to refuse to accept persons it dislikes .";
		//System.out.println(Arrays.toString(s.split(" ")));
		//s = "They are conscious of this state's new feeling of optimism and assurance and are definitely impressed by the number of new plants and construction projects in Rhode Island .";
		//s = "There is , of course , nothing new about dystopias , for they belong to a literary tradition which , including also the closely related satiric utopias , stretches from at least as far back as the eighteenth century and Swift's Gulliver's Travels to the twentieth century and Zamiatin's We , Capek's War With The Newts , Huxley's Brave New World , E. M. Forster's `` The Machine Stops '' , C. S. Lewis's That Hideous Strength , and Orwell's Nineteen Eighty-Four , and which in science fiction is represented before the present deluge as early as Wells's trilogy , The Time Machine , `` A Story Of The Days To Come '' , and When The Sleeper Wakes , and as recently as Jack Williamson's `` With Folded Hands '' ( 1947 ) , the classic story of men replaced by their own robots .";
		//s = "A report of Sr. Edw Grevyles minaces to the Baileefe Aldermen & Burgesses of Stratforde '' tells how Quiney was injured by Greville's men : `` in the tyme Mr. Ryc' Quyney was bayleefe ther came some of them whoe beinge druncke fell to braweling in ther hosts howse wher thei druncke & drewe ther dagers uppon the hoste : att a faier tyme the Baileefe being late abroade to see the towne in order & comminge by in hurley burley came into the howse & commawnded the peace to be kept butt colde nott prevayle & in hys endevor to sticle the brawle had his heade grevouselye brooken by one of hys ( Greville's ) men whom nether hymselfe ( Greville ) punnished nor wolde suffer to be punnished but with a shewe to turne them awaye & enterteyned agayne '' .";
		processMaps();
		return forwardViterbi(s.split(" "), states, start_probability);



		/*

		HashMap<String, Integer> bla = new HashMap<>();
		bla.put("*", 1);


		for (Entry<String, Integer> e : bla.entrySet())
		{
			System.out.println(e.getKey());
		}*/

//		ArrayList<String> path = viterbi(s);
//		for (String g : path) {
//			System.out.println(g);
//		}
	}

}
