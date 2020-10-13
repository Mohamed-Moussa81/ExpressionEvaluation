package app;
import java.io.*;
import java.util.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is
	 * created and stored, even if it appears more than once in the expression. At
	 * this time, values for all variables and all array items are set to zero -
	 * they will be loaded from a file in the loadVariableValues method.
	 * 
	 * @param expr   The expression
	 * @param vars   The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		int length = expr.length();
		char c;
		String tempv = "";
		for (int i = 0; i < length; i++) {
			c = expr.charAt(i);
			if (c == ' ' || Character.isDigit(c)) {
				continue;
			}
			if (delims.indexOf(c) == -1) {
				tempv += c;
				if (i == length - 1 && !tempv.equals("")) {
					
					Variable temp = new Variable(tempv);
					if (vars.contains(temp)) {
						temp = null;
					} else {
						vars.add(temp);
					}
					break;
				}
				if (i == length - 1) {
					break;
				}
				char checker = expr.charAt(i + 1);
				if (checker == '[') {
					Array tempA = new Array(tempv);
					if (arrays.contains(tempA)) {
						tempA = null;
					} else {
						arrays.add(tempA);
						tempv = "";
					}
				} else {
					if (delims.indexOf(checker) != -1) {
						Variable temp = new Variable(tempv);
						if (vars.contains(temp)) {
							temp = null;
						} else {
							vars.add(temp);
						}
						tempv = "";
					} else {
						continue;
					}

				}

			} else {
				tempv = "";
			}

		}

		/** COMPLETE THIS METHOD **/
		/**
		 * DO NOT create new vars and arrays - they are already created before being
		 * sent in to this method - you just need to fill them in.
		 **/
	}

	/**
	 * Loads values for variables and arrays in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input
	 * @param vars   The variables array list, previously populated by
	 *               makeVariableLists
	 * @param arrays The arrays array list - previously populated by
	 *               makeVariableLists
	 */
	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	/**
	 * Evaluates the expression.
	 * 
	 * @param vars   The variables array list, with values for all variables in the
	 *               expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		int length = expr.length();
		char c;
		String tempv = "";
		String arrstr = "";
		int index = 0;
		int arrindex = 0;
		int brackets = 0;
		int parent = 0;
		float value = 0;
		String exprc = "";
		for (int i = 0; i < length; i++) {
			c = expr.charAt(i);
			if (c == ' ') {
				continue;
			}
			if (Character.isDigit(c)) {
				exprc += c;
			} else {
				if (Character.isLetter(c)) {
					if (i == length - 1) {
						tempv += c;
						break;
					}
					char checker = expr.charAt(i + 1);
					if (!tempv.contentEquals("") && checker == '[') {
						tempv += c;
						arrstr = tempv;
					} else {
						if (checker != '[') {
							tempv += c;
						} else if (checker == '[') {
							arrstr = c + "";
						}
					}
					if (delims.indexOf(checker) == -1 && checker != '[') {
						continue;
					}
				} else {
					if (c == '[') {
						brackets++;
						arrindex = (int) evaluate(expr.substring(i + 1, length), vars, arrays);
						//fix if they return negative sign
						int ptr2 = i + 1;
						while (brackets != 0) {
							char x = expr.charAt(ptr2);
							if (x == ']') {
								brackets--;
							}
							if (x == '[') {
								brackets++;
							}
							ptr2++;

						}

						i = ptr2 - 1;
						Array temp2 = new Array(arrstr);
						if (arrays.contains(temp2)) {
							index = arrays.indexOf(temp2);
							exprc += (arrays.get(index).values[arrindex]);
						}
					}
					if (c == ']') {
						if (!tempv.contentEquals("")) {
							Variable temp = new Variable(tempv);
							if (vars.contains(temp)) {
								index = vars.indexOf(temp);
								exprc += vars.get(index).value;
							}
						}
						return Pemdas(exprc);
					}
					if (c == '(') {
						parent++;
						value = evaluate(expr.substring(i + 1, length), vars, arrays);
						int ptr1 = i + 1;
						while (parent != 0) {
							char y = expr.charAt(ptr1);
							if (y == '(') {
								parent++;
							}
							if (y == ')') {
								parent--;
							}
							ptr1++;
						}
						i = ptr1 - 1;
						exprc += value;
						Variable temp = new Variable(tempv);
						if (vars.contains(temp)) {
							index = vars.indexOf(temp);
							exprc += vars.get(index).value;
						}

					} else {
						Variable temp = new Variable(tempv);
						if (vars.contains(temp)) {
							index = vars.indexOf(temp);
							exprc += vars.get(index).value;
						}
						if (c == '*' || c == '+' || c == '-' || c == '/') {
							exprc += c;
						}
						tempv = "";
					}
					 if (c == ')') {
						return Pemdas(exprc);

					} 
				}

			}

		}
		Variable temp = new Variable(tempv);
		if (vars.contains(temp)) {
			index = vars.indexOf(temp);
			exprc += vars.get(index).value;
		}
		tempv = "";
		//System.out.println(exprc);
		return Pemdas(exprc);
	}

	private static float Pemdas(String expr) {
		// does the math, assuming numbers are passed computes it right, without
		// parentheses and brackets
		Stack<Character> tempo = new Stack<>();
		Stack<Float> temps = new Stack<>();
		String tempv = "";
		char c;
		char checker;
		int length = expr.length();
		for (int i = 0; i < length; i++) {
			c = expr.charAt(i);
			if (c == ' ') {
				continue;
			}
			if (delims.indexOf(c) != -1) {
				if( i ==0 && delims.indexOf(c+1) == -1) {
					tempv+=c;
					tempv+=expr.charAt(i+1);
					i=i+1;
					
				}
				else {
					tempo.push(c);	
				}
				
					if(i !=length-1 && expr.charAt(i+1) == '-'){
						tempv+=expr.charAt(i+1);
						i=i+1;
					}
				
			} 
			//fix negative numbers
			else
			 {
				if (i == length - 1) {
					tempv += c;
					temps.push(Float.parseFloat(tempv));
					break;
				}
				checker = expr.charAt(i + 1);
				tempv += c;
				if (delims.indexOf(checker) != -1) {
					temps.push(Float.parseFloat(tempv));
					tempv = "";
				} else {
					continue;
				}
			}
		}

		Stack<Character> operations = new Stack<>();
		while (!tempo.isEmpty()) {
			operations.push(tempo.pop());
		}
		Stack<Float> numbers = new Stack<>();

		while (!temps.isEmpty()) {
			numbers.push(temps.pop());

		}
		char temp;
		float pop1;
		float pop2;
		float pop3;
		float math;
		while (!numbers.isEmpty() && !operations.isEmpty()) {
			temp = operations.pop();
			if (operations.isEmpty()) {
				if (temp == '+') {
					math = numbers.pop() + numbers.pop();
					numbers.push(math);
					break;
				}
				if (temp == '-') {
					pop1 = numbers.pop();
					pop2 = numbers.pop();
					math = pop1 - pop2;
					numbers.push(math);
					break;
				}
				if (temp == '*') {
					math = numbers.pop() * numbers.pop();
					numbers.push(math);
					break;
				}
				if (temp == '/') {
					math = numbers.pop() / numbers.pop();
					numbers.push(math);

					break;
				}
			} else {
				if (temp == operations.peek()) {
					if (temp == '+') {
						math = numbers.pop() + numbers.pop();
						numbers.push(math);
						continue;
					} else if (temp == '-') {
						math = numbers.pop() - numbers.pop();
						numbers.push(math);
						continue;
					} else if (temp == '/') {
						math = numbers.pop() / numbers.pop();
						numbers.push(math);
						continue;
					} else if (temp == '*') {
						math = numbers.pop() * numbers.pop();
						numbers.push(math);
						continue;
					}

				}
				if (temp == '*') {
					math = numbers.pop() * numbers.pop();
					numbers.push(math);
					continue;
				}
				if (temp == '/') {
					math = numbers.pop() / numbers.pop();
					numbers.push(math);
					continue;

				}
				if (temp == '+') {
					if (operations.peek() == '*') {
						pop1 = numbers.pop();
						pop2 = numbers.pop();
						pop3 = numbers.pop();
						math = pop3 * pop2;
						numbers.push(math);
						numbers.push(pop1);
						operations.pop();
						operations.push('+');
						continue;
					}
					if (operations.peek() == '/') {
						pop1 = numbers.pop();
						pop2 = numbers.pop();
						pop3 = numbers.pop();
						math = pop2 / pop3;
						//it should be pop2/pop3?
						numbers.push(math);
						numbers.push(pop1);
						operations.pop();
						operations.push('+');
						continue;
					} else {
						math = numbers.pop() + numbers.pop();
						numbers.push(math);
						continue;
					}
				}
				if (temp == '-') {
					if (operations.peek() == '*') {
						pop1 = numbers.pop();
						pop2 = numbers.pop();
						pop3 = numbers.pop();
						math = pop2 * pop3;
						numbers.push(math);
						numbers.push(pop1);
						operations.pop();
						operations.push('-');
						continue;
					}
					if (operations.peek() == '/') {
						pop1 = numbers.pop();
						pop2 = numbers.pop();
						pop3 = numbers.pop();
						math = pop2 / pop3;
						numbers.push(math);
						numbers.push(pop1);
						operations.pop();
						operations.push('-');
						continue;
					} else {
						math = numbers.pop() - numbers.pop();
						numbers.push(math);
						continue;
					}
				}

			}
		}
		return numbers.pop();
	}
}
