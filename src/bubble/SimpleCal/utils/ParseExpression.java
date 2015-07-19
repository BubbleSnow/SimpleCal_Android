package bubble.SimpleCal.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


/**
 * <p>Title: ParseInfixExp</p>
 * <p>Description: ������׺���ʽ��ֵ</p>
 * <p>Company: </p> 
 * @version 2.0.0.150717   
 * @since JDK 1.8.0_45
 * @author bubble
 * @date 2015-7-2
 */
public class ParseExpression {
	final static String ERROR = "��ʽ����";
	final static String[] ERROR_ARRAY = ERROR.split("");
	static String regOperator = "\\+|-|��|��";
	
	private static final Map<String, Integer> OPERATORS = new HashMap<String, Integer>();
 
	static {	//��������ȼ�
		OPERATORS.put( "+", 0);
		OPERATORS.put( "-", 0);
		OPERATORS.put( "��", 1);
		OPERATORS.put( "��", 1);
	 }

	/**
	 * <p>Title: isOperator</p>
	 * <p>Description: �ж��ַ����Ƿ��������</p>
	 * @param str 
	 * @return boolean
	 * @date 2015-7-2 
	 */
	public static boolean isOperator(String str) {
		return OPERATORS.containsKey(str);
	}
	
	/**
	 * <p>Title: isOperator</p>
	 * <p>Description: �ж��ַ��Ƿ��������</p>
	 * @param ch
	 * @return boolean
	 * @author bubble
	 * @date 2015-7-13 ����6:02:58
	 */
	public static boolean isOperator(char ch) {
		String s = String.valueOf(ch);
		return OPERATORS.containsKey(s);
	}
	
 
	/**
	 * <p>Title: comparePrior</p>
	 * <p>Description: �Ƚ���������ȼ�</p>
	 * @param op1 �����
	 * @param op2 �����
	 * @return op1<op2,���ظ�����op1=op2����0��op1>op2��������
	 * @date 2015-7-2
	 */
	public static final int comparePrior(String op1, String op2) {
		if (! isOperator(op1) || !isOperator(op2)) {
			throw new IllegalArgumentException( "Invalid operators:" + op1 + " " + op2);
		}
       
        return OPERATORS.get(op1) - OPERATORS.get(op2);
 }
 
	/**
	 * <p>Title: splitInfixExp</p>
	 * <p>Description: ������׺���ʽ��������������������벢���������</p>
	 * @param infixExp ��׺���ʽ
	 * @return ���ش�Ų��������������String����
	 * @date 2015-7-2
	 */
	public static String[] splitInfixExp(String infixExp) {
		if ( infixExp.matches(".*?\\(|" + regOperator + "$")){
	    	   return ERROR_ARRAY;
	       }
		else if ( ! isParenthesisMatch(infixExp) ){	//���Ų�ƥ��
			return ERROR_ARRAY;
		}
		else if ( infixExp.matches(".*?(" + regOperator + ")(" +regOperator + ")+.*?" ) ){	//����2�����������
			return ERROR_ARRAY;
		}
		else if ( infixExp.matches(".*?(\\.|%)[0-9](\\.|%).*?") ){
			return ERROR_ARRAY;
		}
		
		ArrayList<String> strArr = new ArrayList<String>();
       
		StringBuilder lastOperand = new StringBuilder();
		//������׺�ַ�����
		for ( char ch: infixExp.toCharArray()) {
			String str = Character.toString(ch);
			String arrString = strArr.toString().replaceAll("(.*?)(\\])$", "$1");
			
			if (str.matches("\\(")){
				strArr.add(str);
				lastOperand.setLength(0);
			}
			else if ( str.matches("\\)") ){
				if(lastOperand.length() != 0){
					strArr.add(lastOperand.toString());
					lastOperand.setLength(0);
					strArr.add(str);
				}
				else if(arrString.endsWith(")")){
					strArr.add(str);
				}
			}
			else if ( str.matches("%") ){
				if( arrString.endsWith(")") ){
					strArr.add(str);
				}
				else if( lastOperand.length() > 0){
					strArr.add(lastOperand.toString());
					lastOperand.setLength(0);
					strArr.add(str);
				}
			}
			else if ( isOperator(str) ) {
				if(lastOperand.length() != 0){
					strArr.add(lastOperand.toString());
				}
				else if( (arrString.endsWith("[")) || (arrString.endsWith("(")) ){
					if (str.equals("-")){	//����
						lastOperand.append(ch);
						continue;
					}
				}
				
				strArr.add(str);
				lastOperand.setLength(0);
			}
			else if ( str.matches("[0-9]|\\.") ){
				lastOperand.append(str);
			}
			else {
				return ERROR_ARRAY;
			}
		}
   
		if(lastOperand.length() != 0){
			strArr.add(lastOperand.toString());
		}
       
		String[] outputs = new String[strArr.size()];
        return strArr.toArray(outputs);
	}   
	
	/**
	 * <p>Title: infix2Suffix</p>
	 * <p>Description: ����׺���ʽת���ɺ�׺���ʽ</p>
	 * @param infixExp ��׺���ʽ
	 * @return ��׺���ʽ
	 * @date 2015-7-2
	 */
	public static String infix2Suffix(String infixExp) {
		String suffixExp = "";
	   
		String[] inputs = splitInfixExp(infixExp);
	   
		if (inputs == null)
			return null;
	   
		Stack<String> stack = new Stack<String>();
	   
		for (String input: inputs) {
			if ( input.matches("\\(") ){
				stack.push(input);
			}
			else if (input.matches("%")){
				stack.push(input);
			}
			else if( input.matches("\\)") ){
				while ( !stack.empty() ){
					while( !stack.peek().matches("\\(") )
						suffixExp =suffixExp + " " + stack.pop();
					if(stack.peek().matches("\\("))
						break;
				}
				if( stack.peek().matches("\\(") ){
					stack.pop();
				}
				else
					return ERROR;
			}
			else if ( isOperator(input) ) {
				while ( !stack.empty() ) {
					if( isOperator(stack.peek()) ){
						if ( comparePrior(input, stack.peek()) <= 0) {
							suffixExp =suffixExp + " " + stack.pop();
							continue;
						}
					}
					else if( stack.peek().matches("%") ){
						suffixExp =suffixExp + " " + stack.pop();
						continue;
					}
					break;
				}
				stack.push(input);
			}
			else {
				suffixExp = suffixExp + " " + input;
			}
		}
	   
		while (!stack.empty()) {
			suffixExp = suffixExp + " " + stack.pop();
		}
	       
		return suffixExp.trim();
	} 
	
	/**
	 * <p>Title: calSuffix</p>
	 * <p>Description: �����׺���ʽ��ֵ</p>
	 * @param suffixExp ��׺���ʽ
	 * @return ���ʽ��ֵ
	 * @date 2015-7-2
	 */
	public static String calSuffix(String suffixExp) {
		String[] expression = suffixExp.split(" ");
		String resultString = "";
		String v1 = "";
		String v2 = "";
		String val = "";
		//�ַ����Ƿ�Ϊ�Ϸ�������
		String regNum = "-?[0-9]+(\\.[0-9]+)?";
		
		Stack<String> stack = new Stack<String>();
		for (String op: expression) {
			if( isOperator(op)) {
				if (stack.size() < 2)
					return ERROR;
		 
				v2 = stack.pop();
				v1 = stack.pop();
	 
				if ( v1.matches(regNum) && v2.matches(regNum) ){
					switch(op.charAt(0)){
						case '+':
							val = Arith.add(v1, v2);
							break;
						case '-':
							val = Arith.sub(v1, v2);
							break;
						case '��':
							val = Arith.mul(v1, v2);
							break;
						case '��':
							if( v2.matches("0") )
								return ERROR;
							val = Arith.div(v1, v2);
							break;
					}
					stack.push(val);
				}
				else 
					return ERROR;
			}
			else if( op.matches("%") ){
				if( stack.size() < 1)
					return ERROR;
				
				v1 = stack.pop();
				v2 = "100";
				if ( ! v1.matches(regNum) )
					return ERROR;
				
				val = Arith.div(v1, v2);
				stack.push(val);
			}
			else if ( op.matches(regNum) ){
				stack.push(op);
			}
			else{
				return ERROR;
			}
		}
		
		resultString = stack.pop();

		if ( ! stack.empty() ){
	    	return ERROR;
	    }
		
		//��ʽ����ֵ
		if(resultString.indexOf(".") > 0){  
			resultString = resultString.replaceAll("(0+?)$", "");//ȥ�������0  
			resultString = resultString.replaceAll("(\\.)$", "");//�����һλ��.��ȥ��  
        }  
		else if(resultString.indexOf(".") == 0){
			resultString = "0" + resultString;
		}
		
    	return resultString;
	}
	 
 	// Evaluate an infix expression.
	//������׺���ʽ
	/**
	 * <p>Title: calInfix</p>
	 * <p>Description: ������׺���ʽ��ֵ</p>
	 * @param exp ��׺���ʽ
	 * @return ��׺���ʽ��ֵ
	 * @date 2015-7-2
	 */
	public static String calInfix(String exp) {
		String suffixExp = infix2Suffix(exp);
		if (suffixExp == null)
			return "";
		
		return calSuffix(suffixExp);
	}

    /**
     * <p>Title: inputParenthesis</p>
     * <p>Description: �������ţ�"("��")"</p>
     * @return "("��")"
     * @author bubble
     * @date 2015-7-6 ����11:15:17
     */
    public static String inputParenthesis(String expression){
    	String inputParenthesis = "";
    	if(expression.length() > 0){
    		String lastCharString = String.valueOf(expression.charAt(expression.length()-1));
    		if(isOperator(lastCharString) || lastCharString.matches("\\(") )
    			inputParenthesis = "(";
    		else if(lastCharString.matches( "[0-9]|\\.|\\)|%") ){
    			boolean b = isParenthesisMatch(expression);
    			if(b)
    				return "";
    			else
    				inputParenthesis = ")";
    		}
    		else 
    			return "";
    	}
    	else{ 
    		inputParenthesis = "(";
    	}
    	return inputParenthesis;
    }
    
    
    /**
     * <p>Title: isParenthesisMatch</p>
     * <p>Description: �жϱ��ʽ��С����"()"�Ƿ�ƥ��</p>
     * @param expression ���ʽ
     * @return ƥ�䷵��true����ƥ�䷵��false
     * @author bubble
     * @date 2015-7-10 ����3:38:04
     */
    public static boolean isParenthesisMatch(String expression){
    	Stack<Character> parenthesisStack = new Stack<Character>();
		char[] expArray = expression.toCharArray();
		for (char c:expArray){
			if(c == '(')
				parenthesisStack.push(c);
			else if( (c == ')') && (! parenthesisStack.isEmpty()) )
				parenthesisStack.pop();
		}
		if( parenthesisStack.isEmpty() )
			return true;
		else 
			return false;
    }
    
    /**
	 * <p>Title: insertDot2EndValid</p>
	 * <p>Description: �ж��ڱ��ʽβ��׷�ӵĵ�'.'�Ƿ���Ч</p>
	 * @param expression ������ʽ
	 * @return ����ĵ�'.'��Ч�򷵻�true
	 * @date 2015-7-3
	 */
    public static boolean appendDotValid(String expression) {
		//������ʽΪ�գ����������㣬��Ч
		if (expression.equals("")) 
			return true;
		
		int expLen = expression.length();
		for (int i = expLen - 1; i >= 0; --i) {
			char ch = expression.charAt(i);
			if ( isOperator(ch) || (ch == '(') )
				return true;
			else if ( ch == '.' || ch == '%' || ch == ')' )
				return false;
		}
		
		return true;
	}
}
