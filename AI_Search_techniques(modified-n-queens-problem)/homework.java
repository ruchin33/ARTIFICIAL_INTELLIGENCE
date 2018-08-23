import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

/////////one column means each lizard gets only one column to get its child states
public class homework{
	public static void main(String[] args) throws FileNotFoundException{
		
		String fileName = "input23.txt";
		String outputFileName = "output.txt";
		String algo = ""; //which algorithm
		int nursery_size = 0; 	//nursery size
		int baby_lizards = 0;   // total baby lizards in the nursery
		File inputFile = new File(fileName);
		Scanner in = new Scanner(inputFile);
		PrintWriter output_file = new PrintWriter(outputFileName);
		
		//get the data from input file
		algo = getAlgo(in);
		nursery_size = getNurserySize(in);
		baby_lizards = getBabyLizards(in);
		int[][] nursery = new int[nursery_size][nursery_size]; 
		int[][] solution = new int[nursery_size][nursery_size];
		
		nursery = getNursery(in,nursery,nursery_size); //##########first state########
		
		int[][] dummy_nursery = nursery; /// for our updates
		
		
		/*int row = 0; //where queen is placed
		int col = 0;
		dummy_nursery = queenPlaced(row,col,nursery_size,dummy_nursery);
		
		for(int i =0;i<nursery_size;i++) {
			for(int j=0;j<nursery_size;j++) {
				System.out.print(dummy_nursery[i][j]);
			}
			System.out.println();
		}*/
		
		
		///////########## ALGOS STARTS #########/////////////
		
		if(algo.equals("BFS")) {
		
			solution = implementBFS(dummy_nursery,nursery,nursery_size,baby_lizards);
			
		}
		if(algo.equals("DFS")) {
			
			solution = implementDFS(dummy_nursery,nursery,nursery_size,baby_lizards);
			
		}
		if(algo.equals("SA")) {
			solution = implementSA(nursery,nursery_size,baby_lizards);
		}
		
		/////////checking if solution is a PASS OR a FAIL//////////
		if(solution[0][0] >= 0) {
			output_file.printf("OK \n");
			for(int i =0;i<nursery_size;i++) {
				for(int j=0;j<nursery_size;j++) {
					output_file.print(solution[i][j]);
				}
				output_file.printf("\n");
			}
			//System.out.println("Hi you are printing final nursery");
		}
		else {
			output_file.printf("FAIL \n");
		}
		
		in.close();
		output_file.close();
		
	}
	//////////private methods start//////////////////////
	
	///////////////data acquisition/////////////////////
	private static String getAlgo(Scanner in) {
		String alg = ""; 
		if(in.hasNextLine()){
			alg = in.nextLine();
		}
		return alg;	
	}
	private static int getNurserySize(Scanner in) {
		int n_size = 0;
		if(in.hasNextLine()) {
			n_size = in.nextInt();
		}
		return n_size;
	}
	private static int getBabyLizards(Scanner in) {
		int b_lizards = 0;
		if(in.hasNextLine()) {
			b_lizards = in.nextInt();
		}
		return b_lizards;
	}
	private static int[][] getNursery(Scanner in,int[][] nursery,int nursery_size){
		String curr_line = "";
		in.nextLine();
		Scanner ln;
		for(int i=0;i<nursery_size;i++) {
			if(in.hasNextLine()) {
				curr_line = in.nextLine();
			}
			ln = new Scanner(curr_line);
			ln.useDelimiter("");
			for(int j=0;j<nursery_size;j++) {
				
				nursery[i][j]=ln.nextInt();
				
			}
			
		}
		
		
		return nursery;
	}
	/////////data acquiring ends////////////
	
	////////squares attacked by the lizard upon placing it in a certain square//////
	private static int[][] queenPlaced(int row,int col,int nursery_size,int[][] current_nursery){
	
		//////////copying so that it does not affect the current node as references are same
		int[][] temp_current_nursery = new int[nursery_size][nursery_size];
		for(int i=0;i<nursery_size;i++) {
			for(int j =0;j<nursery_size;j++) {
				temp_current_nursery[i][j] = current_nursery[i][j];
			}
		}
		
		temp_current_nursery[row][col] = 1;
		temp_current_nursery = SquaresAttacked(temp_current_nursery,nursery_size,row,col);
		temp_current_nursery = RowSquaresAttacked(temp_current_nursery,nursery_size,row,col);
		
		temp_current_nursery = ColSquaresAttacked(temp_current_nursery,nursery_size,row,col);
		temp_current_nursery = DiagonalSquaresAttacked(temp_current_nursery,nursery_size,row,col);
		return temp_current_nursery;
		
	}
	
	//////////////######Member methods of Queenplaced method##########////////////
	
	private static int[][] SquaresAttacked(int[][] current_nursery,int nursery_size,int row,int col){
		
		int temp_r = row;
		int temp_c = col;
		return current_nursery;
	}
	
	private static int[][] RowSquaresAttacked(int[][] current_nursery,int nursery_size,int row,int col){
		
		// for squares attacked on rows first we go left then we go right
					//first we go left
				
				int temp_r = row;
				int temp_c = col;
				while((temp_c != 0) && (current_nursery[temp_r][temp_c] != 2)) {
					temp_c = temp_c - 1;
					if(temp_c >= 0) {
						if(current_nursery[temp_r][temp_c] == 0) {
							current_nursery[temp_r][temp_c] = 3;
						}
					}
				}
					//now we go right
				temp_r = row;
				temp_c = col;
				while((temp_c != nursery_size) && (current_nursery[temp_r][temp_c] != 2)) {
					temp_c = temp_c + 1;
					if(temp_c < nursery_size) {
						if(current_nursery[temp_r][temp_c] == 0) {
							current_nursery[temp_r][temp_c] = 3;
						}
					}
				}
				
				return current_nursery;
		
	}
	
	private static int[][] ColSquaresAttacked(int[][] current_nursery,int nursery_size,int row,int col){
		// for squares attacked on cols first we go up then we go down
			//first we go up
		int temp_r = row;
		int temp_c = col;
		while((temp_r != 0) && (current_nursery[temp_r][temp_c] != 2)) {
			temp_r = temp_r - 1;
			if(temp_r >= 0) {
				if((current_nursery[temp_r][temp_c] == 0)) {
					current_nursery[temp_r][temp_c] = 3;
				}
			}
		}
			//now we go down
		temp_r = row;
		temp_c = col;
		while((temp_r != nursery_size) && (current_nursery[temp_r][temp_c] != 2)) {
			temp_r = temp_r + 1;
			if(temp_r < nursery_size) {
				if((current_nursery[temp_r][temp_c] == 0) ) {
					current_nursery[temp_r][temp_c] = 3;
				}
			}
		}
		
		return current_nursery;

	}
	
	private static int[][] DiagonalSquaresAttacked(int[][] current_nursery,int nursery_size,int row,int col){
		//for squares attacked on left to right diagonal first we go up then we go down
			///for upper left diagonal
		int temp_r = row;
		int temp_c = col;
		while((temp_r != 0) && (temp_c != 0) && (current_nursery[temp_r][temp_c] != 2)) {
			temp_r = temp_r - 1;
			temp_c = temp_c - 1;
			if((temp_r >= 0 ) && (temp_c < nursery_size)) {
				if(current_nursery[temp_r][temp_c] == 0) {
					current_nursery[temp_r][temp_c] = 3;
				}
			}
		}
		///for lower left diagonal
		temp_r = row;
		temp_c = col;
		while((temp_r != nursery_size) && (temp_c != nursery_size) && (current_nursery[temp_r][temp_c] != 2)) {
			temp_r = temp_r + 1;
			temp_c = temp_c + 1;
			if((temp_r < nursery_size) && (temp_c < nursery_size)) {
				if(current_nursery[temp_r][temp_c] == 0) {
					current_nursery[temp_r][temp_c] = 3;
				}
			}
		}
	
		//for squares attacked on right to left diagonal first we go down then we go up
			///for upper diagonal
		temp_r = row;
		temp_c = col;
		while((temp_r != 0) && (temp_c != nursery_size) && (current_nursery[temp_r][temp_c] != 2)) {
			temp_r = temp_r - 1;
			temp_c = temp_c + 1;
			if((temp_r >= 0 ) && (temp_c < nursery_size)) {
				if(current_nursery[temp_r][temp_c] == 0) {
					current_nursery[temp_r][temp_c] = 3;
				}
			}
		}
			///for lower left diagonal
		temp_r = row;
		temp_c = col;
		while((temp_r != nursery_size) && (temp_c >= 0) && (current_nursery[temp_r][temp_c] != 2)) {
			temp_r = temp_r + 1;
			temp_c = temp_c - 1;
			if((temp_r < nursery_size ) && (temp_c >= 0)) {
				if(current_nursery[temp_r][temp_c] == 0) {
					current_nursery[temp_r][temp_c] = 3;
				}
			}
		}
	
		return current_nursery;
	}
	
	//////////Simulated annealing Algo methods/////////////
	private static int[][] implementSA(int[][] nursery,int nursery_size,int baby_lizards){

		int[][] new_random_state;
		int[][] current_state = new int[nursery_size][nursery_size];
		Map<Integer,Integer> LizardRow = new HashMap<Integer,Integer>();
		Map<Integer,Integer> LizardCol = new HashMap<Integer,Integer>();
		int current_state_attacks = 0;
		int next_state_attacks = 0;
		int delta_energy = 0;
		int attacks = 0;
		int iter = 0;
		double t = 1.0000001;
		double T = 1/Math.log10(t);
		double count = 1;
		double step = 1.577E-5;
		boolean goal = false;
		
		current_state = MakeInitialState(nursery,nursery_size,baby_lizards,LizardRow,LizardCol);
		current_state_attacks = getTotalLizardsAttacked(current_state,nursery_size,baby_lizards);
		
		
		if(current_state_attacks == 0) {
			return current_state;
		}
		//System.out.println(T);
		
		long start = System.currentTimeMillis();
		
		while((System.currentTimeMillis() - start) < (265000)) {
			T = 1/Math.log10(t);
			t++;
			if(T == 0) {
				current_state_attacks = getTotalLizardsAttacked(current_state,nursery_size,baby_lizards);
				if(current_state_attacks == 0) {
				
					return current_state;
				}
				else {
					current_state[0][0] = -1;
				
					return current_state;
				}
			}
			
			Map<Integer,Integer> CurrentStateLizardRow = new HashMap(LizardRow);
			Map<Integer,Integer> CurrentStateLizardCol = new HashMap(LizardCol);
			int[][] next_state = MakeNextState(current_state,nursery_size,baby_lizards,LizardRow,LizardCol);
			
			next_state_attacks = getTotalLizardsAttacked(next_state,nursery_size,baby_lizards);
			
			
			if(next_state_attacks == 0) {
				
				return next_state;
			}
			delta_energy = next_state_attacks - current_state_attacks;
			if(delta_energy < 0) {          ///////we are choosing less than zero because lower the better
				
				current_state = next_state;
				current_state_attacks = next_state_attacks;
				
			}
			else {
				double probability = Math.exp(-(delta_energy)/T);
				if(yes_accept(probability) == true) {
					current_state = next_state;
					current_state_attacks = next_state_attacks;
				}
				else {
					current_state = current_state;
					LizardRow = new HashMap(CurrentStateLizardRow);
					LizardCol = new HashMap(CurrentStateLizardCol);
				}
			}
		}
		
		current_state[0][0] = -1;
		
		return current_state;
	}
	
	///////////Simulated annealing member functions/////////////
		//////getting new random state////////
	
	private static boolean yes_accept(double prob) {
		
		double r = Math.random();
		
		if(r < prob) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private static int[][] MakeInitialState(int[][] nursery,int nursery_size,int baby_lizards,Map<Integer,Integer> LizardRow,Map<Integer,Integer> LizardCol) {
		
		int[][] temp_nursery = new int[nursery_size][nursery_size];
		for(int i = 0;i<nursery_size;i++) {
			for(int j =0;j<nursery_size;j++) {
				temp_nursery[i][j] = nursery[i][j];
			}
		}
		Random rand = new Random();
		int n =0;
		int row = 0;
		int col = 0;
		int lizards_placed = 0;
		while(lizards_placed != baby_lizards) {
			row = rand.nextInt(nursery_size);
			col = rand.nextInt(nursery_size);
			if((temp_nursery[row][col] != 2) && (temp_nursery[row][col] != 1)) {
				temp_nursery[row][col] = 1;
				lizards_placed++;
				LizardRow.put(lizards_placed,row);
				LizardCol.put(lizards_placed,col);
			}
		}
		
		return temp_nursery;
		
	}
	
	private static int[][] MakeNextState(int[][] current_state,int nursery_size,int baby_lizards,Map<Integer,Integer> LizardRow,Map<Integer,Integer> LizardCol) {
		
		
		Random rand = new Random();
		int row = 0;
		int col = 0;
		int to_be_changed_row = 0;
		int to_be_changed_col = 0;
		int whichLizard = 0;
		int squares_covered = 0;
		int one_lizard_randomly_placed = 0;
		int[][] temp_nursery = new int[nursery_size][nursery_size];
		int[][] dummy_nursery = new int[nursery_size][nursery_size];
		for(int i = 0;i<nursery_size;i++) {
			for(int j =0;j<nursery_size;j++) {
				dummy_nursery[i][j] = current_state[i][j];
			}
		}
		
		whichLizard = rand.nextInt(baby_lizards) + 1;
		row = LizardRow.get(whichLizard);
		col = LizardCol.get(whichLizard);
		to_be_changed_row = row;
		to_be_changed_col = col;
		
		while((one_lizard_randomly_placed == 0) && (squares_covered <(nursery_size*nursery_size))) {
			row = rand.nextInt(nursery_size);
			col = rand.nextInt(nursery_size);
			if(temp_nursery[row][col] != -7) {
				temp_nursery[row][col] = -7;
				squares_covered++;
			}
			if((dummy_nursery[row][col] != 2) && (dummy_nursery[row][col] != 1)) {
					
				dummy_nursery[row][col] = 1;
				one_lizard_randomly_placed++;
				LizardRow.put(whichLizard,row);
				LizardCol.put(whichLizard,col);
				dummy_nursery[to_be_changed_row][to_be_changed_col] = 0;
					
			}
		}
		
		return dummy_nursery;
		
	}
	
	public static int getTotalLizardsAttacked(int[][] new_random_state,int nursery_size,int baby_lizards) {
		
		int[][] dummy_nursery = new int[nursery_size][nursery_size];
		int attacks = 0;
		
		for(int i =0;i<nursery_size;i++) {
			for(int j =0;j<nursery_size;j++) {
				dummy_nursery[i][j] = new_random_state[i][j];
			}
		}		
		
		///////////counting attacks//////////////////
		for(int i =0;((i<nursery_size) && (attacks<baby_lizards));i++) {
			for(int j = 0;((j <nursery_size) && (attacks<baby_lizards));j++) {
				
								
				if((dummy_nursery[i][j] == 1 ) || (dummy_nursery[i][j] == 4 )) {
					/////row attacks///////////
					for(int curr_col = j+1;(curr_col<nursery_size) && (dummy_nursery[i][curr_col] != 2);curr_col++) {
						if((dummy_nursery[i][curr_col] == 1) || (dummy_nursery[i][curr_col] == 4)) {
							if(dummy_nursery[i][j] != 4) {
								dummy_nursery[i][j] = 4;
								attacks++;
							}
							if(dummy_nursery[i][curr_col] != 4) {
								dummy_nursery[i][curr_col] = 4;
								attacks++;
							}
						}
					}
					
					
					/////col attacks//////////////////////
					for(int curr_row = i+1;(curr_row<nursery_size) && (dummy_nursery[curr_row][j] != 2);curr_row++) {
						if((dummy_nursery[curr_row][j] == 1) || (dummy_nursery[curr_row][j] == 4)) {
							if(dummy_nursery[i][j] != 4 ) {
								dummy_nursery[i][j] = 4;
								attacks++;
							}
							if(dummy_nursery[curr_row][j] != 4) {
								dummy_nursery[curr_row][j] = 4;
								attacks++;
							}
						}
					}
					
					
					//////////diagonal L-R attacks//////////
					int curr_row=i+1;
					int curr_col=j+1;
					while((curr_row<nursery_size) && (curr_col<nursery_size) && (dummy_nursery[curr_row][curr_col] != 2)) {
						
						if((dummy_nursery[curr_row][curr_col] == 1) || (dummy_nursery[curr_row][curr_col] == 4)) {
							if(dummy_nursery[i][j] != 4) {
								dummy_nursery[i][j] = 4;
								attacks++;
							}
							if(dummy_nursery[curr_row][curr_col] != 4){
								dummy_nursery[curr_row][curr_col] = 4;
								attacks++;
							}
						}
						curr_row++;
						curr_col++;
					}
					
					
					//////////diagonal R-L attacks//////////
					curr_row=i+1;
					curr_col=j-1;
					while((curr_row<nursery_size) && (curr_col>=0) && (dummy_nursery[curr_row][curr_col] != 2)) {
						if((dummy_nursery[curr_row][curr_col] == 1) || (dummy_nursery[curr_row][curr_col] == 4)) {
							if(dummy_nursery[i][j] != 4) {
								dummy_nursery[i][j] = 4;
								attacks++;
							}
							if(dummy_nursery[curr_row][curr_col] != 4) {
								dummy_nursery[curr_row][curr_col] = 4;
								attacks++;
							}
						}
						curr_row++;
						curr_col--;
					}
					
				}
				
			}
		}
		
		return attacks;
		
	}

	
	public static int[][] implementBFS(int[][] dummy_nursery,int[][] nursery,int nursery_size,int baby_lizards){
		
		
		int[][] state_nursery = dummy_nursery;
		Queue<int[][]> open = new LinkedList<int[][]>();
		int[][] current_node = new int[nursery_size][nursery_size];
		int[][] tem_current_node = new int[nursery_size][nursery_size];
		
		int lizard_number = 0;
		int child_number = 0;
		int columns_traversed = 0;
		int iterations = 0;
		boolean free_square_found = false; 
		
		boolean goal = false;
		open.add(state_nursery);
		int count = 0;
		
		if(baby_lizards == 0) {     /////// most simple case
			return dummy_nursery;
		}
		
		while(true) {
			if(open.isEmpty()) {
				dummy_nursery[0][0] = -1;
			
				return dummy_nursery;
			}
			
			iterations++;
			tem_current_node = open.remove();
			///////making the current coordinate array///////
			for(int i=0;i<nursery_size;i++) {
				for(int j =0;j<nursery_size;j++) {
					current_node[i][j] = tem_current_node[i][j];
				}
			}
			
			
			goal = GoalTest(current_node,baby_lizards,nursery_size);
			if(goal == true) {
				for(int i =0;i<nursery_size;i++) {
					for(int j =0;j<nursery_size;j++) {
						if(current_node[i][j] == 3) {
							current_node[i][j] = 0;
						}
					}
				}
			
				return current_node;
			}
			
			/////expanding the children and putting them in queue////////
			for(int j = 0; (j<nursery_size) && (columns_traversed<1); j++) {
				for(int i=0; i<nursery_size; i++) {
					int[][] child_node = new int[nursery_size][nursery_size];

					if(current_node[i][j] == 0) {
						
						child_node = queenPlaced(i,j,nursery_size,current_node);
						open.add(child_node);
						child_number++;
						
						
						
						free_square_found = true;
									
					}
				}
				
				if(free_square_found == true) {    //////// to check how many columns are traversed
					columns_traversed++;
				}
				free_square_found = false;
				
			}
			columns_traversed = 0; /////////reinitializing how many columns traversed
			child_number = 0;  ////////////reinitializing child number
			
			//System.out.println("***************************EXPANDIND CHILDREN ENDS******************************");
			lizard_number++;
		}
		//return dummy_nursery;
		
	}
	
	public static int[][] implementDFS(int[][] dummy_nursery,int[][] nursery,int nursery_size,int baby_lizards){
		
		
		int[][] state_nursery = dummy_nursery;
		Stack<int[][]> open = new Stack<int[][]>();
		int[][] current_node = new int[nursery_size][nursery_size];
		int[][] tem_current_node = new int[nursery_size][nursery_size];
		
		int lizard_number = 0;
		int child_number = 0;
		int columns_traversed = 0;
		int iterations = 0;
		boolean free_square_found = false; 
		
		boolean goal = false;
		open.add(state_nursery);
		int count = 0;
		
		if(baby_lizards == 0) {     /////// most simple case
			return dummy_nursery;
		}
		
		while(true) {
			if(open.isEmpty()) {
				dummy_nursery[0][0] = -1;
				
				return dummy_nursery;
			}
			
			tem_current_node = open.pop();
			///////making the current coordinate array///////
			for(int i=0;i<nursery_size;i++) {
				for(int j =0;j<nursery_size;j++) {
					current_node[i][j] = tem_current_node[i][j];
				}
			}
			
			
			
			goal = GoalTest(current_node,baby_lizards,nursery_size);
			if(goal == true) {
				for(int i =0;i<nursery_size;i++) {
					for(int j =0;j<nursery_size;j++) {
						if(current_node[i][j] == 3) {
							current_node[i][j] = 0;
						}
					}
				}
			
				
				return current_node;
			}
			
			/////expanding the children and putting them in queue////////
			for(int j = 0; (j<nursery_size) && (columns_traversed<1); j++) {
				for(int i=0; i<nursery_size; i++) {
					int[][] child_node = new int[nursery_size][nursery_size];

					if(current_node[i][j] == 0) {
						
						child_node = queenPlaced(i,j,nursery_size,current_node);
						open.add(child_node);
						child_number++;
						
						
						
						free_square_found = true;
									
					}
				}
				
				if(free_square_found == true) {    //////// to check how many columns are traversed
					columns_traversed++;
				}
				free_square_found = false;
				
			}
			columns_traversed = 0; /////////reinitializing how many columns traversed
			child_number = 0;  ////////////reinitializing child number
			
			//System.out.println("***************************EXPANDIND CHILDREN ENDS******************************");
			lizard_number++;
			iterations++;
		}
		
	}
	
	/////////////Member functions of BFS and DFS algo//////////////////////
	
	
	private static boolean GoalTest(int[][] current_node,int baby_lizards,int nursery_size) {
		int total_lizards_placed=0;
		for(int i=0;i<nursery_size;i++) {
			for(int j =0;j<nursery_size;j++) {
				if(current_node[i][j] == 1) {
					total_lizards_placed++;
				}
			}
		}
		//System.out.println("total_lizards_placed: "+total_lizards_placed);
		if(total_lizards_placed == baby_lizards) {
			return true;
		}
		else {
			return false;
		}
	}

}
