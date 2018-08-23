
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.awt.Point;


public class alpha_beta_with_time_trial2{
	
	public static Map<Integer,LinkedList<Integer>> loc_fruit_selected = new HashMap<Integer,LinkedList<Integer>>();
	public static Map<Integer,LinkedList<Integer>> temp_loc_fruit_selected = new HashMap<Integer,LinkedList<Integer>>();
	public static Map<Point,char[][]> act = new HashMap<Point,char[][]>();
	public static Map<Point,Integer> act_scores = new HashMap<Point,Integer>();
	public static Map<Point,Integer> act_max_scores = new HashMap<Point,Integer>();
	public static Map<Point,Integer> act_min_scores = new HashMap<Point,Integer>();
	public static Map<Point,Integer> temp_act_scores = new HashMap<Point,Integer>();
	public static Map<Point,Map<Integer,LinkedList<Integer>>> fruits_in_state = new HashMap<Point,Map<Integer,LinkedList<Integer>>>();
	public static Map<Integer,Point> max_score_point = new HashMap<Integer,Point>();
	public static Point selection_fruit;
	public static Point which_player = new Point();
	public static int squareBoard_size = 0; 	//squareBoard size
	public static char[][] squareBoard;
	public static int MIN_score = 0;
	public static int MAX_score = 0;
	public static int count_which_states = 0;
	public static int upper_depth = 0;
	public static Map<Integer,char[][]> scores_states = new HashMap<Integer,char[][]>();
	public static double start = (System.currentTimeMillis())/1000.0;
	public static double time_left_sec = 0;
	public static double breaking_point = 1;
	
	public static void main(String[] args) throws FileNotFoundException {
		
		String fileName = "test2.txt";
		String outputFileName = "output.txt";
		
		int fruit_types = 0;   // total baby fruits in the squareBoard
		int score = 0;
		int OUR_SCORE = 0;
		
		
		File inputFile = new File(fileName);
		Scanner in = new Scanner(inputFile);
		PrintWriter output_file = new PrintWriter(outputFileName);
		
		squareBoard_size = getsquareBoardSize(in);
		fruit_types = getFruitTypes(in);
		time_left_sec = getTimeinSec(in);
		System.out.println("time left:"+time_left_sec);
		squareBoard = new char[squareBoard_size][squareBoard_size]; 
		char[][] final_board = new char[squareBoard_size][squareBoard_size];
		squareBoard = getsquareBoard(in,squareBoard); //##########first state########
		
		System.out.println("-------------INPUT file--------------");
		for(int i =0;i<squareBoard_size;i++) {
			for(int j =0;j<squareBoard_size;j++) {
				System.out.print(squareBoard[i][j]);
			}
			System.out.println();
		}
		
		System.out.println("-------------------------------------");
		
		int depth = 4;
		upper_depth = depth - 1;
		int final_selection = AlphaBetaSearch(squareBoard,depth);
		
		System.out.println("which_player"+which_player);
		////////////////////Selecting Max Min entry////////////////////////
		Point final_fruit_selected = new Point();
		if(which_player.getX() == 1) {
			Map.Entry<Integer,Point> max_utility_Entry = null;
			for (Map.Entry<Integer,Point> entry : max_score_point.entrySet()) {
				if (max_utility_Entry == null || entry.getKey() > max_utility_Entry.getKey()) {
					max_utility_Entry = entry;
				}
			}
			System.out.println("Here1");
			final_fruit_selected = max_utility_Entry.getValue();
		}
		else {
			Map.Entry<Integer,Point> min_utility_Entry = null;
			for (Map.Entry<Integer,Point> entry : max_score_point.entrySet()) {
				if (min_utility_Entry == null || entry.getKey() < min_utility_Entry.getKey()) {
					min_utility_Entry = entry;
				}
			}
			System.out.println("Here2");
			final_fruit_selected = min_utility_Entry.getValue();
		}
		//////////////////////////////////////////////////////////////

		loc_fruit_selected = new HashMap<Integer,LinkedList<Integer>>();
		temp_loc_fruit_selected = new HashMap<Integer,LinkedList<Integer>>();
		final_board = CollectFruits((int)final_fruit_selected.getX(),(int)final_fruit_selected.getY(),squareBoard,squareBoard);
		
		System.out.println("-------------OUTPUT file--------------");
		for(int i =0;i<squareBoard_size;i++) {
			for(int j =0;j<squareBoard_size;j++) {
				System.out.print(final_board[i][j]);
			}
			System.out.println();
		}
		System.out.println("-------------------------------------");
		
		CreateOutputFile(output_file,final_board,final_fruit_selected);
		System.out.println(temp_loc_fruit_selected.size());
		OUR_SCORE = OUR_SCORE+CalculateScore(temp_loc_fruit_selected);
		System.out.println("our score: "+OUR_SCORE);
		in.close();
		output_file.close();
		
		System.out.println((System.currentTimeMillis()/1000.0) - start);
		
	}
	
	////////////////////////ALPHA BETA SEARCH//////////////////////////////////////////////
	private static int AlphaBetaSearch(char[][] squareBoard,int depth) {
		
		selection_fruit = new Point(0,0);
		int final_v = MaxValue(depth,squareBoard,Integer.MIN_VALUE,Integer.MAX_VALUE,Integer.MIN_VALUE,selection_fruit,breaking_point); 
		return final_v;
	}
	
	private static int MaxValue(int depth,char[][] squareBoard,int alpha,int beta,int v_max,Point selection_fruit,double breaking_point) {
		
		Point parent_state = new Point();
		parent_state.setLocation(selection_fruit.getX(),selection_fruit.getY());
		if((CutOff(squareBoard) == true) || (depth == 0)) {
			
			
			int utility = MIN_score + EvaluationFunction(squareBoard,-1); 
			if(depth == upper_depth) {
				max_score_point.put(utility, parent_state);
			}
			which_player.setLocation(1, 1);
			return  utility;
			
		}
		for(Map.Entry<Point,char[][]> entry : Actions(squareBoard,breaking_point).entrySet()) {
			
			MAX_score = MAX_score + act_scores.get(entry.getKey());
			selection_fruit = new Point(entry.getKey());
			v_max = Math.max(v_max,MinValue(depth-1,entry.getValue(),alpha,beta,Integer.MAX_VALUE,selection_fruit,breaking_point));
			MAX_score = MAX_score - act_scores.get(entry.getKey());
			
			if(v_max >= beta) {
				
				if(depth == upper_depth) {
					max_score_point.put(v_max, parent_state);
				}
				return v_max;
			}
			
			alpha = Math.max(alpha, v_max);
			
		}	
		if(depth == upper_depth) {
			max_score_point.put(v_max, parent_state);
		}
		return v_max;
	}
	
	private static int MinValue(int depth,char[][] squareBoard,int alpha,int beta,int v_min,Point selection_fruit,double breaking_point){
		
		Point parent_state = new Point();
		parent_state.setLocation(selection_fruit.getX(),selection_fruit.getY());
		
		if((CutOff(squareBoard) == true) || (depth == 0)) {
			
			int utility = MAX_score + EvaluationFunction(squareBoard,1);
			if(depth == upper_depth) {
				max_score_point.put(utility, parent_state);
			}
			which_player.setLocation(-1, -1);
			return  utility;
			
		}
	
		for(Map.Entry<Point,char[][]> entry : Actions(squareBoard,breaking_point).entrySet()) {
			
			MIN_score = MIN_score - act_scores.get(entry.getKey());
			selection_fruit = new Point(entry.getKey());
			v_min = Math.min(v_min,MaxValue(depth-1,entry.getValue(),alpha,beta,Integer.MIN_VALUE,selection_fruit,breaking_point));
			MIN_score = MIN_score + act_scores.get(entry.getKey());
			
			
			if(v_min <= alpha) {
				if(depth == upper_depth) {
					max_score_point.put(v_min, parent_state);
				}
				return v_min;
			}
			beta = Math.min(beta, v_min);
			
		}
		
		if(depth == upper_depth) {
			max_score_point.put(v_min, parent_state);
		}
		return v_min;
	}
	
	
	private static int EvaluationFunction(char[][] squareBoard,int player){
		
		int search_depth = 8;
		LinkedList<Integer> tentative_scores = new LinkedList<Integer>();
		int evaluation_value = 0;
		char[][] temp_board = new char[squareBoard_size][squareBoard_size];
		char[][] child_state;
		Map<Point,char[][]> state = new HashMap<Point,char[][]>();
		char[][] checking_board = new char[squareBoard_size][squareBoard_size];
		
		for(int a = 0;a<squareBoard_size;a++) {
			for(int b=0;b<squareBoard_size;b++) {
				temp_board[a][b] = squareBoard[a][b];
			}
		}
		
		for(int i = 0;(i<squareBoard_size) && (search_depth !=0);i++) {
			for(int j=0;(j<squareBoard_size) && (search_depth !=0);j++) {
				if((checking_board[i][j] != '*') && (squareBoard[i][j] != '*')) {
					
					loc_fruit_selected = new HashMap<Integer,LinkedList<Integer>>();
					temp_loc_fruit_selected = new HashMap<Integer,LinkedList<Integer>>();
					temp_board = CollectFruits(i,j,squareBoard,checking_board);
					tentative_scores.add(CalculateScore(temp_loc_fruit_selected));
					search_depth--;
				}
			}
		}
		Collections.sort(tentative_scores);
		
		while(tentative_scores.isEmpty() == false) {
			player = player*(-1);
			evaluation_value = evaluation_value + (player*tentative_scores.removeLast());
		}
		
		
		
		return evaluation_value;
	}
	
	private static boolean CutOff(char[][] squareBoard) {
		
		int count = 0;
		for(int i =0;i<squareBoard_size;i++) {
			for(int j =0;j<squareBoard_size;j++) {
				if(squareBoard[i][j] =='*') {
				count++;
				}
			}
		}
		if(count == (squareBoard_size*squareBoard_size)) {
			return true;
		}
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	private static Map<Point,char[][]> Actions(char[][] squareBoard,double breaking_point){
		
		char[][] temp_board = new char[squareBoard_size][squareBoard_size];
		char[][] child_state;
		Map<Point,char[][]> state = new HashMap<Point,char[][]>();
		char[][] checking_board = new char[squareBoard_size][squareBoard_size];
		
		for(int a = 0;a<squareBoard_size;a++) {
			for(int b=0;b<squareBoard_size;b++) {
				temp_board[a][b] = squareBoard[a][b];
			}
		}
		
		outer_loop:
		for(int i = 0;i<squareBoard_size;i++) {
			for(int j=0;j<squareBoard_size;j++) {
				if((checking_board[i][j] != '*') && (squareBoard[i][j] != '*')) {
					
					double current_time_left = time_left_sec - ((System.currentTimeMillis()/1000.0) - start);
					//System.out.println(current_time_left);
					if(current_time_left <= breaking_point) {
						
						//System.out.println(current_time_left);
						break outer_loop;
						
					}
					//System.out.println("chodu");
					loc_fruit_selected = new HashMap<Integer,LinkedList<Integer>>();
					temp_loc_fruit_selected = new HashMap<Integer,LinkedList<Integer>>();
					temp_board = CollectFruits(i,j,squareBoard,checking_board);
					state.put(new Point(i,j),temp_board);
					act_scores.put(new Point(i,j),CalculateScore(temp_loc_fruit_selected));
					fruits_in_state.put(new Point(i,j),temp_loc_fruit_selected);
					
				}
			}
		}
		return state;
	}
	
	/////////////////////////////CALCULATE SCORE//////////////////////////////////////////
	
	
	private static int CalculateScore(Map<Integer,LinkedList<Integer>> temp_loc_fruit_selected) {
		
		int total_fruits=0;
		int score = 0;
		for(Map.Entry<Integer, LinkedList<Integer>> entry : temp_loc_fruit_selected.entrySet()) {
			
			total_fruits = total_fruits + entry.getValue().size();
		}
		
		score = (total_fruits)*(total_fruits);
		return score;
		
	}
	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////GRAVITY DROP/////////////////////////////////////////////
	public static char[][] GravityDrop(char[][] returning_squareBoard){
		
		LinkedList<Integer> columns_affected = new LinkedList<Integer>();   //////LinkedList of columns affected
		
		for(Map.Entry<Integer, LinkedList<Integer>> columns : loc_fruit_selected.entrySet()) {
			columns_affected.add(columns.getKey());
		}
		
		while(columns_affected.isEmpty() == false) {
			
			
			int current_column = columns_affected.removeFirst();
			if(loc_fruit_selected.get(current_column).isEmpty() == false) {
				int lowest_fruit = loc_fruit_selected.get(current_column).removeLast();
				for(int i = lowest_fruit;i>=0;i--) {
				
					if(returning_squareBoard[i][current_column] == '$') {
						returning_squareBoard[i][current_column] = '*';
					}
					else if(Character.isDigit(returning_squareBoard[i][current_column])) {
						returning_squareBoard[lowest_fruit][current_column] = returning_squareBoard[i][current_column];
						returning_squareBoard[i][current_column] = '*';
						lowest_fruit = lowest_fruit-1;
					}
				
				}
			}
		}
		
		
		return returning_squareBoard;
	}
	
	/////////////////////////////COLLECTING FRUITS////////////////////////////////////////
	public static char[][] CollectFruits(int row,int col,char[][] squareBoard,char[][] checking_board){
		
		int sel_fruit_col = col;
		int sel_fruit_row = row;
		
		char fruit_selected = squareBoard[sel_fruit_row][sel_fruit_col];
		if(fruit_selected == '*') {
			return squareBoard;
		}
		Stack<Point> sel_fruit_loc = new Stack<Point>();

		sel_fruit_loc.add(new Point(sel_fruit_col,sel_fruit_row));
		
		
		char[][] temp_board = new char[squareBoard_size][squareBoard_size];
		char[][] returning_squareBoard = new char[squareBoard_size][squareBoard_size];
		
		for(int i = 0;i<squareBoard_size;i++) {
			for(int j=0;j<squareBoard_size;j++) {
				temp_board[i][j] = squareBoard[i][j];
				returning_squareBoard[i][j] = squareBoard[i][j];
			}
		}
		
		while(sel_fruit_loc.isEmpty() == false) {
			
			
			Point current_loc = new Point();
			current_loc = sel_fruit_loc.pop();
			
			int curr_row = (int)current_loc.getY();
			int curr_col = (int)current_loc.getX();
			LinkedList<Integer> r;
			
			if(loc_fruit_selected.containsKey(curr_col)){
				
				if(loc_fruit_selected.get(curr_col).contains(curr_row) == false) {
					loc_fruit_selected.get(curr_col).add(curr_row);
					Collections.sort(loc_fruit_selected.get(curr_col));
				}
			}
			else {
				r = new LinkedList<Integer>();
				r.add(curr_row);
				loc_fruit_selected.put(curr_col,r);
			}
			
			if((returning_squareBoard[curr_row][curr_col] == fruit_selected) || (returning_squareBoard[curr_row][curr_col] == '$')) {
				
				returning_squareBoard[curr_row][curr_col] = '$';
				temp_board[curr_row][curr_col] = '@'; 
				checking_board[curr_row][curr_col] = '*';
				
				///////////expand childs//////////
				//////child above///////
				if(curr_row != 0) {
					if(((returning_squareBoard[curr_row-1][curr_col] == fruit_selected) || (returning_squareBoard[curr_row-1][curr_col] == '$')) && (temp_board[curr_row-1][curr_col] != '@')){
						
						sel_fruit_loc.add(new Point(curr_col,curr_row-1));
						
					}
				}
				///////////////////////
				//////child below///////
				if(curr_row != (squareBoard_size - 1)) {
					if(((returning_squareBoard[curr_row+1][curr_col] == fruit_selected) || (returning_squareBoard[curr_row+1][curr_col] == '$')) && (temp_board[curr_row+1][curr_col] != '@')){
						
						sel_fruit_loc.add(new Point(curr_col,curr_row+1));
						
					}
				}
				///////////////////////
				//////child right///////
				if(curr_col != (squareBoard_size - 1)) {
					if(((returning_squareBoard[curr_row][curr_col+1] == fruit_selected) || (returning_squareBoard[curr_row][curr_col+1] == '$'))  && (temp_board[curr_row][curr_col+1] != '@')){
						
						sel_fruit_loc.add(new Point(curr_col+1,curr_row));
						
					}
				}
				///////////////////////
				//////child left///////
				if(curr_col != 0) {
					if(((returning_squareBoard[curr_row][curr_col-1] == fruit_selected) || (returning_squareBoard[curr_row][curr_col-1] == '$')) && (temp_board[curr_row][curr_col-1] != '@')){
						
						sel_fruit_loc.add(new Point(curr_col-1,curr_row));
						
					}
				}
				///////////////////////
				/////////////////////////////////
			}
			
		}
		
		
		for(Map.Entry<Integer, LinkedList<Integer>> entry : loc_fruit_selected.entrySet()) {
			
			int key = entry.getKey();
			LinkedList<Integer> t = new LinkedList<Integer>(entry.getValue());
			temp_loc_fruit_selected.put(key,t);
			
		}
		returning_squareBoard = GravityDrop(returning_squareBoard);
		
		return returning_squareBoard;
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////DATA acquiring////////////////////////////////////////
	
	private static int getsquareBoardSize(Scanner in) {
		int n_size = 0;
		if(in.hasNextLine()) {
			n_size = in.nextInt();
		}
		return n_size;
	}
	
	private static int getFruitTypes(Scanner in) {
		int fruits = 0;
		if(in.hasNextLine()) {
			fruits = in.nextInt();
		}
		return fruits;
	}
	
	private static double getTimeinSec(Scanner in) {
		double time = 0;
		if(in.hasNextLine()) {
			time = in.nextDouble();
		}
		return time;
	}
	
	private static char[][] getsquareBoard(Scanner in,char[][] squareBoard){
		String curr_line = "";
		in.nextLine();
		Scanner ln;
		for(int i=0;i<squareBoard_size;i++) {
			if(in.hasNextLine()) {
				curr_line = in.nextLine();
			}
			ln = new Scanner(curr_line);
			ln.useDelimiter("");
			for(int j=0;j<squareBoard_size;j++) {
				
				squareBoard[i][j]=ln.next().charAt(0);
				
			}
			
		}
		return squareBoard;
	}
	
	///////////////////DATA ACQUISITION ENDS///////////////////////////////////////////////////////////////
	
	////////////////////////////////CREATING OUTPUT FILE//////////////////////////////////////////////////
	
	public static void CreateOutputFile(PrintWriter output_file,char[][] squareBoard,Point selectionFruit) {
		
		int a = 65 + (int)selectionFruit.getY();
		output_file.print((char)(a));
		output_file.print((int)selectionFruit.getX()+1);
		output_file.printf("\n");
		for(int i =0;i<squareBoard_size;i++) {
			for(int j=0;j<squareBoard_size;j++) {
				output_file.print(squareBoard[i][j]);
			}
			output_file.printf("\n");
		}
	}
}