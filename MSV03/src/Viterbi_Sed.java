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



	private static class TNode {
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


	public static int[] forwardViterbi(String[] word, String[] xState, double[] startProp) {
		TNode[] T = new TNode[xState.length];
		for (int state = 0; state < xState.length; state++) {
			int[] intArray = new int[1];
			intArray[0] = state;
			T[state] = new TNode( intArray, startProp[state] * main.get_emission_entry(word[0], xState[state]));
		}

		for (int output = 1; output < word.length-1; output++) {
			TNode[] U = new TNode[xState.length];
			for (int next_state = 0; next_state < xState.length; next_state++) {
				int[] argmax = new int[0];
				double valmax = 0;
				for (int state = 0; state < xState.length; state++) {
					int[] v_path = copyIntArray(T[state].v_path);
					double v_prob = T[state].v_prob;
					double transition = (main.transitionMatrix.get(xState[state]).get(xState[next_state])==null)?0.1:main.transitionMatrix.get(xState[state]).get(xState[next_state]);

					double result = main.get_emission_entry(word[output], xState[next_state])  * transition;
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
		System.out.print("Viterbi_Sed path: [");
		for (int i = 0; i < argmax.length; i++) {
			System.out.print(states[argmax[i]] + ", ");
		}
		System.out.println("].\n Probability of the whole system: " + valmax);
		return argmax;
	}


	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		main.main(new String[0]);
		//System.out.println((double) 0.1234567891234567891234567891234567865456432536432563245465743516425346546356);
		//System.out.println(all_tags1.size());
		String s = "The Public Service Commission has ruled that this is a public utility subject to many regulations";
		System.out.println(Arrays.toString(s.split(" ")));
		processMaps();
		forwardViterbi(s.split(" "), states, start_probability);





		HashMap<String, Integer> bla = new HashMap<>();
		bla.put("*", 1);


		for (Entry<String, Integer> e : bla.entrySet())
		{
			System.out.println(e.getKey());
		}

//		ArrayList<String> path = viterbi(s);
//		for (String g : path) {
//			System.out.println(g);
//		}
	}

}
