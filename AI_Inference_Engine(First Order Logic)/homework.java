import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;


public class homework{
	
	public static int num_queries = 0;
	public static int KB_size = 0;
	public static ArrayList<String> queries = new ArrayList<String>();
	public static ArrayList<Sentence> KB = new ArrayList<Sentence>();
	public static ArrayList<String> KB_sentence = new ArrayList<String>();
	public static ArrayList<Integer> KB_not_allowed_lines = new ArrayList<Integer>();
	public static Map<String,ArrayList<Integer>> pred_which_lines = new HashMap<String,ArrayList<Integer>>();
	public static int KB_sentence_num = -1;
	public static boolean INFERENCE = false;
	public static ArrayList<Integer> same_occurance_predicate = new ArrayList<Integer>();
	public static int which_occurance_child = 0;
	public static int which_occurance_curr_s = 0;
	
	public static void main(String[] args) throws FileNotFoundException {
		
		String fileName = "input23.txt";
		String outputFileName = "output.txt";
		
		File inputFile = new File(fileName);
		Scanner in = new Scanner(inputFile);
		PrintWriter output_file = new PrintWriter(outputFileName);
		
		num_queries = getNumberOfQueries(in);
		getQueries(in);
		KB_size = getKbSize(in);
		KB = GenerateKnowledgeBase(in);
		
		
		
		for(int i =0;i<KB.size();i++) {
			Sentence s1 = new Sentence();
			s1 = KB.get(i);
			
		}
		//System.out.println(pred_which_lines);
		
		ArrayList<Sentence> original_KB = new ArrayList<Sentence>(KB);
		ArrayList<String> original_KB_sentence = new ArrayList<String>(KB_sentence);
		ArrayList<Integer> original_KB_notallowed_lines = new ArrayList<Integer>(KB_not_allowed_lines);
		Map<String,ArrayList<Integer>> orig_pred_which_lines = new HashMap<String,ArrayList<Integer>>();
		for(Map.Entry<String,ArrayList<Integer>> entry : pred_which_lines.entrySet()) {
			
			ArrayList<Integer> a1 = new ArrayList<Integer>(entry.getValue());
			orig_pred_which_lines.put(entry.getKey(),a1);
		}
		
		
		
		while(queries.isEmpty() == false) {
			
			KB = new ArrayList<Sentence>(original_KB);
			KB_sentence_num = KB.size()-1;
			pred_which_lines = new HashMap<String,ArrayList<Integer>>();
			KB_not_allowed_lines = new ArrayList<Integer>();
			INFERENCE = false;
			
			for(Map.Entry<String,ArrayList<Integer>> entry : orig_pred_which_lines.entrySet()) {
				
				ArrayList<Integer> b1 = new ArrayList<Integer>(entry.getValue());
				pred_which_lines.put(entry.getKey(),b1);
			}	
			
			String current_query = queries.remove(0);
			if(current_query.charAt(0) == '~') {
				current_query = current_query.substring(1,current_query.length());
			}
			else {
				current_query = "~"+current_query;
			}
			
		
			int init_not_allowed_lines = KB_sentence_num+1;
			KB_not_allowed_lines.add(init_not_allowed_lines);
			Sentence curr_q = new Sentence(current_query,KB_not_allowed_lines,true);
			KB.add(curr_q);
			Stack<Sentence> dfs_sentences = new Stack<Sentence>();
			Stack<Map<String,ArrayList<Integer>>> sentence_traverser = new Stack<Map<String,ArrayList<Integer>>>();
		
		
			dfs_sentences.add(curr_q);
			
			while(dfs_sentences.isEmpty() == false) {
				Sentence curr_sentence = new Sentence();
				curr_sentence = dfs_sentences.pop();
				
				GenerateAndAddChild(curr_sentence,dfs_sentences);
				if(INFERENCE == true) {
					break;
				}
			
			}
			//System.out.println(INFERENCE);
			if(INFERENCE == true) {
				output_file.printf("TRUE \n");
				
			}
			else if(INFERENCE == false){
				output_file.printf("FALSE \n");
			}
		
		}
		in.close();
		output_file.close();
	}
	
	////////////////////////Creating Childs of current sentence///////////////////////
	private static void GenerateAndAddChild(Sentence curr_s,Stack<Sentence> dfs_sentences) {
		ArrayList<String> parent_predicates = curr_s.getPredicates();
		which_occurance_curr_s = 0;
		for(int i=0;i<parent_predicates.size();i++) {
			String child_pred = parent_predicates.get(i);
			which_occurance_curr_s = i;
			ArrayList<String> child_pred_arg = new ArrayList<String>(curr_s.getPredArgs().get(i));
			ArrayList<Integer> Notpossible_lines = new ArrayList<Integer>(curr_s.getLinesNotAllowed());
			if(child_pred.charAt(0) == '~') {
				child_pred = child_pred.substring(1,child_pred.length());
			}
			else {
				child_pred = "~"+child_pred;
			}
			
			if(pred_which_lines.containsKey(child_pred)) {
				same_occurance_predicate = new ArrayList<Integer>();
				which_occurance_child = 0 ;
				ArrayList<Integer> child_line_num = new ArrayList<Integer>(pred_which_lines.get(child_pred));
				
				
				outer_loop:
				for(int j=0;j<child_line_num.size();j++) {
					for(int a=0;a<child_line_num.size();a++) {
						if(Notpossible_lines.contains(child_line_num.get(a))) {
							if(KB.get(child_line_num.get(a)).getPredicates().size() != 1) {
								child_line_num.remove(a);
							}
						}
					}
					
					if(child_line_num.size() == 0) {
						break outer_loop;
					}
					
					Resolve(j,curr_s,dfs_sentences,child_pred,child_pred_arg,child_line_num,Notpossible_lines);
					if(INFERENCE == true) {
						return;
					}
					
				}
			}
		}
		
	}
	
	
	
	private static void Resolve(int line_num,Sentence curr_s,Stack<Sentence> dfs_sentences,String parent_pred,ArrayList<String> parent_pred_arg,ArrayList<Integer> child_line_num,ArrayList<Integer> not_possible_traverse) {
		
		///Important to note that parent predicate here is negation of the actual parent predicate
		
		Map<String,String> Unified_subst = new HashMap<String,String>();
		same_occurance_predicate.add(child_line_num.get(line_num));
		which_occurance_child = Collections.frequency(same_occurance_predicate,child_line_num.get(line_num));
		int temp_occurance = which_occurance_child;
		int index_of_unifying_pred = 0;
		ArrayList<String> predicates_curr_line = new ArrayList<String>(KB.get(child_line_num.get(line_num)).getPredicates());
		
		outer_loop:
		for(int i=0;i<predicates_curr_line.size();i++) {
			if(predicates_curr_line.get(i).equals(parent_pred)) {
				temp_occurance--;
			}
			if(temp_occurance == 0) {
				index_of_unifying_pred = i;
				break outer_loop;
			}
		}
		
		
		
		ArrayList<String> E1 = new ArrayList<String>(parent_pred_arg);
		ArrayList<String> E2 = new ArrayList<String>(KB.get(child_line_num.get(line_num)).getPredArgs().get(index_of_unifying_pred));
		Unified_subst = UNIFY(E1,E2,new HashMap<String,String>());
		
		
		ArrayList<String> child_preds = new ArrayList<String>(KB.get(child_line_num.get(line_num)).getPredicates());
		ArrayList<ArrayList<String>> child_pred_arg = new ArrayList<ArrayList<String>>(KB.get(child_line_num.get(line_num)).getPredArgs());
		int pred_line_num = child_line_num.get(line_num);
		ArrayList<Integer> not_possible_lines = new ArrayList<Integer>(not_possible_traverse);
		ApplySubstitutionAndResolve(dfs_sentences,Unified_subst,parent_pred,parent_pred_arg,child_preds,child_pred_arg,pred_line_num,curr_s,not_possible_lines,line_num);
		if(INFERENCE == true) {
			return;
		}
		
		
	}
	
	private static void ApplySubstitutionAndResolve(Stack<Sentence> dfs_sentences,Map<String,String> Unified_subst,String parent_pred,ArrayList<String> parent_pred_arg,ArrayList<String> child_preds,ArrayList<ArrayList<String>> child_pred_arg,int pred_line_num,Sentence curr_s,ArrayList<Integer> not_possible_traverse,int line_num) {
		
		if(Unified_subst.containsKey("FAIL")) {  /////// IF fails to unify no need to add in the childs node
			return;
		}
		
		
		String curr_s_word = "";
		if(parent_pred.charAt(0) == '~') {     ///////////////////////because we are passing curr_sentence where we have not made any changes
			curr_s_word = parent_pred.substring(1,parent_pred.length());
		}
		else {
			curr_s_word = "~"+parent_pred;
		}
		
		ArrayList<String> curr_sentence_predicates = new ArrayList<String>(curr_s.getPredicates());
		ArrayList<ArrayList<String>> curr_sentence_pred_args = new ArrayList<ArrayList<String>>(curr_s.getPredArgs());
		int temp_occurance = which_occurance_child;
		//int resolving_pred_num = child_preds.indexOf(parent_pred);
		int resolving_pred_num = 0;
		outer_loop:
			for(int i=0;i<child_preds.size();i++) {
				if(child_preds.get(i).equals(parent_pred)) {
					temp_occurance--;
				}
				if(temp_occurance == 0) {
					resolving_pred_num = i;
					break outer_loop;
				}
			}
		//int resolving_pred_num_curr_s = curr_sentence_predicates.indexOf(curr_s_word);
		int resolving_pred_num_curr_s = which_occurance_curr_s;
		
		//System.out.print(curr_sentence_predicates+" "+curr_sentence_pred_args+"{"+curr_s.getSentenceNum()+"}"+"                              ");
		//System.out.println(child_preds+" "+child_pred_arg+"{"+line_num+"}"+"                              "+Unified_subst);
		
		
		curr_sentence_predicates.remove(resolving_pred_num_curr_s);
		curr_sentence_pred_args.remove(resolving_pred_num_curr_s);
		child_preds.remove(resolving_pred_num);
		child_pred_arg.remove(resolving_pred_num);
		
		if((curr_sentence_predicates.size()==0) && (child_preds.size()==0)) {    ///////////GOAL TEST if true then ther is a contadiction so infer true////////
			
			INFERENCE = true;
			return;
			
		}
		
		ArrayList<ArrayList<String>> child_to_be_added_arg = new ArrayList<ArrayList<String>>();
		
		
		for(int i=0;i<child_pred_arg.size();i++) {   ///////////////////Substituting on the childs side
			
			ArrayList<String> current_pred_arg_list = new ArrayList<String>(child_pred_arg.get(i));
			for(int j =0;j<current_pred_arg_list.size();j++) {
				if(Unified_subst.containsKey(current_pred_arg_list.get(j))) {
					String substitution = Unified_subst.get(current_pred_arg_list.get(j));
					current_pred_arg_list.remove(j);
					current_pred_arg_list.add(j,substitution);
					
				}
			}
			child_to_be_added_arg.add(current_pred_arg_list);
			
		}
		
		
		ArrayList<ArrayList<String>> curr_parent_to_be_added_arg = new ArrayList<ArrayList<String>>();
		
		for(int i=0;i<curr_sentence_pred_args.size();i++) {                /////////////// Substituting on the parents side
			
			ArrayList<String> current_pred_arg_list = new ArrayList<String>(curr_sentence_pred_args.get(i));
			int size = current_pred_arg_list.size();
			for(int j =0;j<current_pred_arg_list.size();j++) {
				if(Unified_subst.containsKey(current_pred_arg_list.get(j))) {
					String substitution = Unified_subst.get(current_pred_arg_list.get(j));
					current_pred_arg_list.remove(j);
					current_pred_arg_list.add(j,substitution);
				}
			}
			curr_parent_to_be_added_arg.add(current_pred_arg_list);
		}
		
		ArrayList<String> new_child_pred = new ArrayList<String>();
		ArrayList<ArrayList<String>> new_child_pred_args = new ArrayList<ArrayList<String>>();
		new_child_pred.addAll(curr_sentence_predicates);
		new_child_pred.addAll(child_preds);
		new_child_pred_args.addAll(curr_parent_to_be_added_arg);
		new_child_pred_args.addAll(child_to_be_added_arg);
		
		
		ArrayList<Integer> lines_notallowed_new_child = new ArrayList<Integer>(not_possible_traverse);
		lines_notallowed_new_child.add(pred_line_num);
		///////////////////////////////////////Factoring sentence////////////////////////////////////
		for(int m=0;m<new_child_pred.size();m++) {
			//System.out.println(new_child_pred_args.get(m));
			//System.out.println(new_child_pred_args.get(n));
			for(int n=(m+1);n<new_child_pred.size();n++) {
				//System.out.println(new_child_pred_args.get(m));
				//System.out.println(new_child_pred_args.get(n));
				if(new_child_pred.get(m).equals(new_child_pred.get(n))) {
					ArrayList<String> m_pred_arg = new ArrayList<String>(new_child_pred_args.get(m));
					ArrayList<String> n_pred_arg = new ArrayList<String>(new_child_pred_args.get(n));
					int m_init_size = m_pred_arg.size();
					m_pred_arg.retainAll(n_pred_arg);
					int m_final_size = m_pred_arg.size();
					if(m_init_size == m_final_size) {
						new_child_pred.remove(n);
						new_child_pred_args.remove(n);
					}
				}
				else if(new_child_pred.get(n).charAt(0) == '~') {
					//System.out.println(new_child_pred_args.get(m));
					//System.out.println(new_child_pred_args.get(n));
					if(new_child_pred.get(n).substring(1,new_child_pred.get(n).length()).equals(new_child_pred.get(m))){
						ArrayList<String> m_pred_arg = new ArrayList<String>(new_child_pred_args.get(m));
						ArrayList<String> n_pred_arg = new ArrayList<String>(new_child_pred_args.get(n));
						int m_init_size = m_pred_arg.size();
						m_pred_arg.retainAll(n_pred_arg);
						int m_final_size = m_pred_arg.size();
						if(m_init_size == m_final_size) {
							new_child_pred.remove(n);
							new_child_pred_args.remove(n);
							new_child_pred.remove(m);
							new_child_pred_args.remove(m);
						}
					}
				}
				else if(new_child_pred.get(m).charAt(0) == '~') {
					//System.out.println(new_child_pred_args.get(m));
					//System.out.println(new_child_pred_args.get(n));
					if(new_child_pred.get(m).substring(1,new_child_pred.get(m).length()).equals(new_child_pred.get(n))) {
						ArrayList<String> m_pred_arg = new ArrayList<String>(new_child_pred_args.get(m));
						ArrayList<String> n_pred_arg = new ArrayList<String>(new_child_pred_args.get(n));
						int m_init_size = m_pred_arg.size();
						m_pred_arg.retainAll(n_pred_arg);
						int m_final_size = m_pred_arg.size();
						if(m_init_size == m_final_size) {
							new_child_pred.remove(n);
							new_child_pred_args.remove(n);
							new_child_pred.remove(m);
							new_child_pred_args.remove(m);
						}
					}
				}
			}
		}
		if((new_child_pred.size() == 0)) {    ///////////GOAL TEST if true then ther is a contadiction so infer true////////
			
			INFERENCE = true;
			return;
			
		}
		////////////////////////////////////////////////////////////////////////////////////////////
		Sentence new_child = new Sentence(new_child_pred,new_child_pred_args,lines_notallowed_new_child,true);
		KB.add(new_child);  //adding newly generated sentence to the KB
		//System.out.println(new_child.getPredicates()+" "+new_child.getPredArgs()+"{"+KB_sentence_num+"}"+"Here1                              ");
		//System.out.println();
		//System.out.println();
		dfs_sentences.push(new_child);
	}
	
	/////////////////////////////Unification///////////////////////////////////////////
	private static Map<String,String> UNIFY(ArrayList<String> e1,ArrayList<String> e2,Map<String,String> subst) {
		
		if((e1.size() == 0)&&(e2.size()==0)) {
			return subst;
		}
		else {
			String arg_e1 = e1.remove(0);
			String arg_e2 = e2.remove(0);
			if(Character.isUpperCase(arg_e1.charAt(0)) && Character.isUpperCase(arg_e2.charAt(0))) { ///// if both arguments are constants
				
				if(arg_e1.equals(arg_e2) == false) {
					Map<String,String> failed_unify = new HashMap<String,String>();
					failed_unify.put("FAIL", "FAIL");
					return failed_unify;
				}
				/*else {
					subst.put(arg_e1,arg_e2);
				}*/
			}
			else if(Character.isLowerCase(arg_e1.charAt(0))) { //////if first arg of e1 is variable 
				
				if(subst.containsKey(arg_e1)) {
					if(arg_e2.equals(subst.get(arg_e1))==false) {
						//System.out.println("Here 3");
						Map<String,String> failed_unify = new HashMap<String,String>();
						failed_unify.put("FAIL", "FAIL");
						return failed_unify;
					}
				}
				else {
					subst.put(arg_e1,arg_e2);
				}
			}
			else if(Character.isLowerCase(arg_e2.charAt(0))) { //////if first arg of e1 is variable 
				
				if(subst.containsKey(arg_e2)) {
					if(arg_e1.equals(subst.get(arg_e2))==false) {
						//System.out.println("Here 4");
						Map<String,String> failed_unify = new HashMap<String,String>();
						failed_unify.put("FAIL", "FAIL");
						return failed_unify;
					}
				}
				else {
					
					subst.put(arg_e2,arg_e1);
				}
			}
			
			return UNIFY(e1,e2,subst);
			
			
		}
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////DATA acquiring////////////////////////////////////////
	
	private static int getNumberOfQueries(Scanner in) {
		int n_queries = 0;
		if(in.hasNextLine()) {
			n_queries = in.nextInt();
		}
		return n_queries;
	}
	
	private static void getQueries(Scanner in) {
		
		in.nextLine();
		Scanner ln;
		
		for(int i=0;i<num_queries;i++) {
			
			queries.add(in.nextLine());
			
		}
	}
	
	
	private static int getKbSize(Scanner in) {
		int n_size = 0;
		
		if(in.hasNextLine()) {
			n_size = in.nextInt();
		}
		return n_size;
	}
	
	private static ArrayList<Sentence> GenerateKnowledgeBase(Scanner in) {
		
		in.nextLine();
		ArrayList<Sentence> knowledge_base = new ArrayList<Sentence>();
		ArrayList<Integer> not_possible_traverse = new ArrayList<Integer>();
		boolean not_possible_flag = false;
		for(int i =0;i<KB_size;i++) {
			
			String current_line = in.nextLine();
			not_possible_traverse.add(-1);
			not_possible_flag = false;
			knowledge_base.add(new Sentence(current_line,not_possible_traverse,not_possible_flag));
			
			
			
		}
		
		return knowledge_base;
		
	}
	
	
	static class Sentence{
		
		ArrayList<String> predicate = new ArrayList<String>();
		ArrayList<ArrayList<String>> pred_argument = new ArrayList<ArrayList<String>>();
		Map<String,ArrayList<Integer>> temp_pred_which_lines = new HashMap<String,ArrayList<Integer>>();
		ArrayList<Integer> line_not_allowed = new ArrayList<Integer>();
		int sentence_num =0;
		
		public Sentence() {
			
		}
		
		public Sentence(ArrayList<String> res_pre,ArrayList<ArrayList<String>> res_pre_arg,ArrayList<Integer> not_possible_traverse,boolean not_possible_flag) {
			
			KB_sentence_num++;
			sentence_num = KB_sentence_num;
			if(not_possible_flag == true) {
				line_not_allowed = new ArrayList<Integer>(not_possible_traverse);
			}
			else {
				ArrayList<Integer> line_not_allowed = new ArrayList<Integer>();
			}
			predicate = new ArrayList<String>(res_pre);
			for(int i =0;i<res_pre_arg.size();i++) {
				ArrayList<String> standardized_res_pre_i = new ArrayList<String>(Standardize(res_pre_arg.get(i)));
				pred_argument.add(new ArrayList<String>(standardized_res_pre_i));
			}
			
			for(int i=0;i<res_pre.size();i++) {
				if(pred_which_lines.containsKey(res_pre.get(i))) {
					pred_which_lines.get(res_pre.get(i)).add(KB_sentence_num);
				}
				else {
					ArrayList<Integer> curr_pred_lines = new ArrayList<Integer>();
					curr_pred_lines.add(KB_sentence_num);
					pred_which_lines.put(res_pre.get(i),curr_pred_lines);
				}
			
			}
			//line_num_allowed.remove(line_num_allowed.indexOf(line_checked));
			for(int m=0;m<predicate.size();m++) {
				//System.out.println(new_child_pred_args.get(m));
				//System.out.println(new_child_pred_args.get(n));
				for(int n=(m+1);n<predicate.size();n++) {
					//System.out.println(new_child_pred_args.get(m));
					//System.out.println(new_child_pred_args.get(n));
					if(predicate.get(m).equals(predicate.get(n))) {
						ArrayList<String> m_pred_arg = new ArrayList<String>(pred_argument.get(m));
						ArrayList<String> n_pred_arg = new ArrayList<String>(pred_argument.get(n));
						int m_init_size = m_pred_arg.size();
						m_pred_arg.retainAll(n_pred_arg);
						int m_final_size = m_pred_arg.size();
						if(m_init_size == m_final_size) {
							predicate.remove(n);
							pred_argument.remove(n);
						}
					}
					else if(predicate.get(n).charAt(0) == '~') {
						//System.out.println(new_child_pred_args.get(m));
						//System.out.println(new_child_pred_args.get(n));
						if(predicate.get(n).substring(1,predicate.get(n).length()).equals(predicate.get(m))){
							ArrayList<String> m_pred_arg = new ArrayList<String>(pred_argument.get(m));
							ArrayList<String> n_pred_arg = new ArrayList<String>(pred_argument.get(n));
							int m_init_size = m_pred_arg.size();
							m_pred_arg.retainAll(n_pred_arg);
							int m_final_size = m_pred_arg.size();
							if(m_init_size == m_final_size) {
								predicate.remove(n);
								pred_argument.remove(n);
								predicate.remove(m);
								pred_argument.remove(m);
							}
						}
					}
					else if(predicate.get(m).charAt(0) == '~') {
						//System.out.println(new_child_pred_args.get(m));
						//System.out.println(new_child_pred_args.get(n));
						if(predicate.get(m).substring(1,predicate.get(m).length()).equals(predicate.get(n))) {
							ArrayList<String> m_pred_arg = new ArrayList<String>(pred_argument.get(m));
							ArrayList<String> n_pred_arg = new ArrayList<String>(pred_argument.get(n));
							int m_init_size = m_pred_arg.size();
							m_pred_arg.retainAll(n_pred_arg);
							int m_final_size = m_pred_arg.size();
							if(m_init_size == m_final_size) {
								predicate.remove(n);
								pred_argument.remove(n);
								predicate.remove(m);
								pred_argument.remove(m);
							}
						}
					}
				}
			}
			
		}
		
		public Sentence(String curr_line,ArrayList<Integer> not_possible_traverse,boolean not_possible_flag) {
			
			KB_sentence.add(curr_line);
			KB_sentence_num++;
			sentence_num = KB_sentence_num;
			if(not_possible_flag == true) {
				line_not_allowed = new ArrayList<Integer>(not_possible_traverse);
				KB_not_allowed_lines = new ArrayList<Integer>(not_possible_traverse);
			}
			for(int i=0;i<curr_line.length();i++) {
				
				if((curr_line.charAt(i) != ' ') && (curr_line.charAt(i) != '|') ) {
					
					int index = i;
					
					////////////////////////getting predicate//////////////////////
					String curr_pred = "";
					while(curr_line.charAt(index) != '(') {
						curr_pred += curr_line.charAt(index);
						index++;
						
					}
					predicate.add(curr_pred);
					if(pred_which_lines.containsKey(curr_pred)) {
						pred_which_lines.get(curr_pred).add(KB_sentence_num);
					}
					else {
						ArrayList<Integer> curr_pred_lines = new ArrayList<Integer>();
						curr_pred_lines.add(KB_sentence_num);
						pred_which_lines.put(curr_pred,curr_pred_lines);
					}
					index++;
					////////////////////////////////////////////////////////////
				
					////////////////////getting arguments of current predicate//////////////////////
				
					ArrayList<String> curr_pred_arg = new ArrayList<String>();
					outer_loop:
					while(curr_line.charAt(index) != ')') {
					
						String curr_argument = "";
						while(curr_line.charAt(index) != ',') {
							curr_argument += curr_line.charAt(index);
							index++;
							if(curr_line.charAt(index) == ')') {
								
								curr_pred_arg.add(curr_argument);
								index++;
								break outer_loop;
							}
						}
						curr_pred_arg.add(curr_argument);
						index++;
					}
					ArrayList<String> standardized_curr_pred_arg = new ArrayList<String>(Standardize(curr_pred_arg));
					pred_argument.add(standardized_curr_pred_arg);
					i=index;
					///////////////////////////////////////////////////////////////////////////////
				}
				
			}
			for(int m=0;m<predicate.size();m++) {
				//System.out.println(new_child_pred_args.get(m));
				//System.out.println(new_child_pred_args.get(n));
				for(int n=(m+1);n<predicate.size();n++) {
					//System.out.println(new_child_pred_args.get(m));
					//System.out.println(new_child_pred_args.get(n));
					if(predicate.get(m).equals(predicate.get(n))) {
						ArrayList<String> m_pred_arg = new ArrayList<String>(pred_argument.get(m));
						ArrayList<String> n_pred_arg = new ArrayList<String>(pred_argument.get(n));
						int m_init_size = m_pred_arg.size();
						m_pred_arg.retainAll(n_pred_arg);
						int m_final_size = m_pred_arg.size();
						if(m_init_size == m_final_size) {
							predicate.remove(n);
							pred_argument.remove(n);
						}
					}
					else if(predicate.get(n).charAt(0) == '~') {
						//System.out.println(new_child_pred_args.get(m));
						//System.out.println(new_child_pred_args.get(n));
						if(predicate.get(n).substring(1,predicate.get(n).length()).equals(predicate.get(m))){
							ArrayList<String> m_pred_arg = new ArrayList<String>(pred_argument.get(m));
							ArrayList<String> n_pred_arg = new ArrayList<String>(pred_argument.get(n));
							int m_init_size = m_pred_arg.size();
							m_pred_arg.retainAll(n_pred_arg);
							int m_final_size = m_pred_arg.size();
							if(m_init_size == m_final_size) {
								predicate.remove(n);
								pred_argument.remove(n);
								predicate.remove(m);
								pred_argument.remove(m);
							}
						}
					}
					else if(predicate.get(m).charAt(0) == '~') {
						//System.out.println(new_child_pred_args.get(m));
						//System.out.println(new_child_pred_args.get(n));
						if(predicate.get(m).substring(1,predicate.get(m).length()).equals(predicate.get(n))) {
							ArrayList<String> m_pred_arg = new ArrayList<String>(pred_argument.get(m));
							ArrayList<String> n_pred_arg = new ArrayList<String>(pred_argument.get(n));
							int m_init_size = m_pred_arg.size();
							m_pred_arg.retainAll(n_pred_arg);
							int m_final_size = m_pred_arg.size();
							if(m_init_size == m_final_size) {
								predicate.remove(n);
								pred_argument.remove(n);
								predicate.remove(m);
								pred_argument.remove(m);
							}
						}
					}
				}
			}
			
		}
		
		
		private  ArrayList<String> getPredicates(){
			
			return predicate;
			
		}
		
		private  ArrayList<ArrayList<String>> getPredArgs(){
			
			return pred_argument;
			
		}
		
		private ArrayList<Integer> getLinesNotAllowed(){
			
			return line_not_allowed;
			
		}
		
		private int getSentenceNum(){
			
			return sentence_num;
			
		}
		
		
	}
	
	private static ArrayList<String> Standardize(ArrayList<String> curr_arg_list){
		
		ArrayList<String> standardized_curr_arg = new ArrayList<String>(curr_arg_list);
		for(int i=0;i<standardized_curr_arg.size();i++) {
			String curr_arg = standardized_curr_arg.get(i);
			if(Character.isLowerCase(curr_arg.charAt(0))) {
				String new_standardized_var = standardized_curr_arg.remove(i).substring(0,1);
				new_standardized_var = new_standardized_var+KB_sentence_num;
				standardized_curr_arg.add(i,new_standardized_var);
			}
		}
		return standardized_curr_arg;
	}
	
	///////////////////DATA ACQUISITION ENDS///////////////////////////////////////////////////////////////
	
}

