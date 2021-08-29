package redcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.*;

import player.*;

public class Redcode {

	private WarriorProcess warriorProcess;
	private String modifier;
	private String operandA;
	private String operandB;
	private Integer area;
	private String opCode;
	
	public Redcode() {
		// When an error in reader() occurs, we come here *i guess*
		this.modifier = "";
		this.operandA = "";
		this.operandB = "";
		this.area = null;
		this.opCode = "";
		this.warriorProcess = new WarriorProcess();
	}
	
	public Redcode(WarriorProcess p, String opc){
		this.opCode = opc.toUpperCase();
		this.operandA = "$0";
		this.operandB = "$0";
		this.area = null;
		this.warriorProcess = p;
		this.modifier = this.getDefaultModifier();
	}
	
	public Redcode(WarriorProcess p, String opc, String opa) {
		this.opCode = opc.toUpperCase();
		this.operandA = opa;
		this.operandB = "$0";
		this.area = null;
		this.warriorProcess = p;
		this.modifier = this.getDefaultModifier();
	}
	
	public Redcode(WarriorProcess p, String opc, String oA, String oB) {
		this.opCode = opc.toUpperCase();
		this.operandA = oA;
		this.operandB = oB;
		this.area = null;
		this.warriorProcess = p;
		this.modifier = this.getDefaultModifier();
	}

	public Redcode(WarriorProcess p, String opc, String m, String opa, String opb, Integer a){
		this.opCode = opc.toUpperCase();
		this.modifier = m.toUpperCase();
		this.operandA = opa;
		this.operandB = opb;
		this.area = a;
		this.warriorProcess = p;
	}

	public String getDefaultModifier(){
		//This will return the default modifier(used when the redcode modifier is empty)
		//the method will just look at the op code and the addressing type to return the good modifier
		if(this.opCode.equals("DAT")){return ".F";}
		else if(this.opCode.equals("SLT")){return ((this.operandA.substring(0, 1).equals("#")) ? ".AB" : ".B");}
		else if(Arrays.asList(new String[]{"JMP", "JMZ", "JMN", "DJN", "SPL", "NOP"}).contains(this.opCode)){return ".B";}
		else if(this.operandA.substring(0,1).equals("#")){return ".AB";}
		else if(this.operandB.substring(0,1).equals("#")){return ".B";}
		else{
			if(Arrays.asList(new String[]{"ADD", "SUB", "MUL", "DIV", "MOD"}).contains(this.opCode)){
				return ".F";
			}
			else if(Arrays.asList(new String[]{"MOV", "SEQ", "SNE"}).contains(this.opCode)){
				return ".I";
			}
		}
		return ""; //Must be an error
	}
	
	public String getModifier() {
		return this.modifier;
	}
	
	public void setModifier(String m) {
		this.modifier = m;
	}
	
	public String getA() {
		return this.operandA;
	}
	
	public void setA(String a) {
		this.operandA = a;
	}
	
	public String getB() {
		return this.operandB;
	}
	
	public void setB(String b) {
		this.operandB = b;
	}

	public int getArea() {
		return this.area;
	}

	public void setArea(int a) {
		this.area = a;
	}

	public String getCode() {
		return this.opCode;
	}

	public void setCode(String c) {
		this.opCode = c;
	}

	public WarriorProcess getWarriorProcess() {
		return this.warriorProcess;
	}

	public void setWarriorProcess(WarriorProcess p) {
		this.warriorProcess = p;
	}

	public int decode(String operand, HashMap<Integer, Redcode> grid) {
		//We get the first character of the operand, which is the Addressing Mode
		Pattern p = Pattern.compile(".");
		Matcher m = p.matcher(operand);
		//We initialise some variables here
		String res = ""; //In res we will operand values to add them, very useful in much cases
		int targetAddress = 0; //In targetAddress we will stock the address of where were going
		String target = ""; //target will contain a value of the Redcode object found in the grid by targetAddress
		int decremented = 0; //decremented will stock a decremeted value of an operand

		if(m.find()) {
			switch(m.group(0)) {

				case "#": 
					//# addressing type means we stock a value, not an address where we fill find the value, so we return the current area
					return this.area; 
					
				case "$":
					//$ is the default addressing type, it means we get to search the value in the intended address
					return (this.area + getOperandValue(operand) + grid.size()) % grid.size();
					

				case "*":
					//* stands for A-Indirect, it means we get to use the pointer to another Redcode objet, which will itself point to another Redcode object
                    //Get the A operand of the target
					targetAddress = (this.area + getOperandValue(operand) + grid.size()) % grid.size();
					target = grid.get(targetAddress).getA();
                    //We again put our value into res
					res = String.valueOf(getOperandValue(target));
					return (targetAddress + Integer.parseInt(res) + grid.size()) % grid.size();

				case "@":
					//@ addressing type is the same as *, but using the operand B instead of the A one
                    //Get the B operand of the target
					targetAddress = (this.area + getOperandValue(operand) + grid.size()) % grid.size();
					target = grid.get(targetAddress).getB();
                    //We again put our value into res
					res = String.valueOf(getOperandValue(target));
					return (targetAddress + Integer.parseInt(res) + grid.size()) % grid.size() ;

				case "{":
					//Same as *, but we decrement the value before returning anything
					targetAddress = (this.area + getOperandValue(operand) + grid.size()) % grid.size();
					//Get the actual target
					target = grid.get(targetAddress).getA();
					//Get the addressing mode
					p = Pattern.compile(".");
					m = p.matcher(target);
					//We put it in res
					if(m.find()) {
						res += m.group(0);
					}
                    //We decrement the operand value
					decremented = (getOperandValue(target) - 1 + grid.size()) % grid.size(); 
					//We put it in res
					res += decremented;
					//We set the A operand of the target to the initial operand less one
					grid.get(targetAddress).setA(res);
					//We return what the target was
					return (targetAddress + getOperandValue(grid.get(targetAddress).getA()) + grid.size()) % grid.size();
					

				case "<":
					//Same as @, but we decrement the value before returning anything
					targetAddress = (this.area + getOperandValue(operand) + grid.size()) % grid.size();
					//Get the actual target
					target = grid.get(targetAddress).getB();
					//GEt the addressing mode
					p = Pattern.compile(".");
					m = p.matcher(target);
					//We put it in res
					if(m.find()) {
						res += m.group(0);
					}
					//We decrement it
					decremented = (getOperandValue(target) - 1 + grid.size()) % grid.size(); 
					//We put it in res
					res += decremented;
					//We set the B operand of the target to the initial operand less one
					grid.get(targetAddress).setB(res);
					//We return what the target was
					return (targetAddress + getOperandValue(grid.get(targetAddress).getB()) + grid.size()) % grid.size(); 

				case "}":
					//Same as *, but we increment the value after returning something (will be done by another method)
					//So here, we only get the asked area 
                    //Get the A operand of the target
					targetAddress = (this.area + getOperandValue(operand) + grid.size()) % grid.size();
					target = grid.get(targetAddress).getA();
                    //We again put our value into res
					res = String.valueOf(getOperandValue(target));
					return (targetAddress + Integer.parseInt(res) + grid.size()) % grid.size();
				
				case ">":
					//Same as @, but we increment the value after returning something (will be done by another method)
					//So here, we only get the asked area 
                    //Get the B operand of the target
					targetAddress = (this.area + getOperandValue(operand) + grid.size()) % grid.size();
					target = grid.get(targetAddress).getB();
                    //We again put our value into res
					res = String.valueOf(getOperandValue(target));
					return (targetAddress + Integer.parseInt(res) + grid.size()) % grid.size();

				default:
					return (this.area + Integer.parseInt(operand) + grid.size()) % grid.size();
			}
		}
		else {
			System.out.println("No match found"); //Only occurs if the analysed String is empty, should never happen
			return 0;
		}
	}

	public Integer getOperandValue(String operand) {
		//This method will register the sign and the value to return them, ignoring the addressing mode
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(operand);
		if(m.find()) {
			//We stock our value
			String value = m.group(0);
			//We check the sign to register it
			int sign = 1;
			p = Pattern.compile("-");
			m = p.matcher(operand);
			if(m.find()) {
				sign = -1;
			}
			return Integer.parseInt(value) * sign;         
		}
		System.out.println("Error when getting the operand value");
		return null;
		
	}

	public void changeOperand(String operand, String whatOperand) {
		//We get the first character of the operand to preserve it, and then add an operand value
		Pattern p = Pattern.compile(".");
		if(whatOperand == "A") {
			Matcher m = p.matcher(this.operandA);
			if(m.find()) {
				this.operandA = m.group(0) + getOperandValue(operand);      
			}
			else {
				System.out.println("Error when changing operand");
			}
			
		}
		else {
			Matcher m = p.matcher(this.operandB);
			if(m.find()) {
				this.operandB = m.group(0) + getOperandValue(operand);
			}
			else {
				System.out.println("Error when changing operand");
			}
		}
	}

	public Redcode copy() {
		//This method will copy every attribute our Redcode object has to create a new one
		WarriorProcess p = this.warriorProcess;
		String op = this.opCode;
		String m = this.modifier;
		String a = this.operandA;
		String b = this.operandB;
		int area = this.area;
		Redcode obj = new Redcode(p, op, m, a, b, area);
		return obj;
	}

	public void postIncrement(Redcode target) {
		//This method will check if there are values to increase, if there are it will increment them
		//We get the addressing mode
		Pattern p = Pattern.compile(".");
		//We match it with our operands
		Matcher m = p.matcher(this.operandA);
		Matcher m2 = p.matcher(this.operandB);
		if(m.find() && m2.find()) {
			//We save the addressing mode
			String res = m.group(0);
			String res2 = m2.group(0);
			//We now check if we need to increment values, and what values
			if(res.equals(">")) {			
				res += getOperandValue(target.getB()) + 1;
				target.setB(res);	
			}
			else if(res.equals("}")){
				res += getOperandValue(target.getA()) + 1;
				target.setA(res);
			}
			if(res2.equals(">")) {
				res2 += getOperandValue(target.getB()) + 1;
				target.setB(res2);
			}
			else if(res2.equals("}")){
				res2 += getOperandValue(target.getA()) + 1;
				target.setA(res2);
			}	
		}
	}

	public void add(HashMap<Integer, Redcode> grid) { 
		//First, we decode our operands to get the addresses we need
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
		//Only then, we get the Redcode objets we need to work
		Redcode r1 = grid.get(b);
		Redcode r2 = grid.get(a);
		switch(this.modifier) {

			case ".AB":
				//Add the target's B operand to r2's A operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())+getOperandValue(r2.getA()) % grid.size())), "B");
				break;
				
			case ".A":
				//Add the target's B operand to r2's A operand, and put it into the A operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())+getOperandValue(r2.getA()) % grid.size())), "A");
				break;

			case ".BA":
				//Add the target's A operand to r2's B operand, and put it into the A operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())+getOperandValue(r2.getB()) % grid.size())), "A");
				break;
				
			case ".B":
				//Add the target's A operand to r2's B operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())+getOperandValue(r2.getB()) % grid.size())), "B");
				break;

			case ".F":
				//Add the target's A operand to r2's A operand, and put it into the A operand of the target
				//Then Add the target's B operand to r2's B operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())+getOperandValue(r2.getA()) % grid.size())), "A");
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())+getOperandValue(r2.getB()) % grid.size())), "B");
				break;

			case ".X":
				//Add the target's A operand to r2's B operand, and put it into the A operand of the target
				//Then Add the target's B operand to r2's A operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())+getOperandValue(r2.getB()) % grid.size())), "A");
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())+getOperandValue(r2.getA()) % grid.size())), "B");
				break;
				
			case ".I":
				//Add the target's A operand to r2's A operand, and put it into the A operand of the target
				//Then Add the target's B operand to r2's B operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())+getOperandValue(r2.getA()) % grid.size())), "A");
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())+getOperandValue(r2.getB()) % grid.size())), "B");
				break;
		}
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getB(),grid));
		Redcode target2 = grid.get(decode(finder.getA(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
		//We increment the token of the warriorProcess 
		this.warriorProcess.setToken(this.warriorProcess.getToken()+1);
	}

	public void sub(HashMap<Integer, Redcode> grid) { 
		//First, we decode our operands to get the addresses we need
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
		//Only then we define Redcode objects, which will be both the target and the source
		Redcode r1 = grid.get(b);
		Redcode r2 = grid.get(a);
		switch(this.modifier) {

			case ".AB":
				//Substract the target's B operand by r2's A operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())-getOperandValue(r2.getA()+grid.size()) % grid.size())), "B");
				break;
			
			case ".A":
				//Substract the target's B operand by r2's A operand, and put it into the A operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())-getOperandValue(r2.getA()+grid.size()) % grid.size())), "A");
				break;

			case ".BA":
				//Subtract the target's A operand by r2's B operand, and put it into the A operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())-getOperandValue(r2.getB()+grid.size()) % grid.size())), "A");
				break;
			
			case ".B":
				//Substract the target's A operand by r2's B operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())-getOperandValue(r2.getB()+grid.size()) % grid.size())), "B");
				break;

			case ".F":
				//Substract the target's A operand by r2's A operand, and put it into the A operand of the target
				//Then Substract the target's B operand by r2's B operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())-getOperandValue(r2.getA()+grid.size()) % grid.size())), "A");
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())-getOperandValue(r2.getB()+grid.size()) % grid.size())), "B");
				break;

			case ".X":
				//Substract the target's A operand by r2's B operand, and put it into the A operand of the target
				//Then Substract the target's B operand by r2's A operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())-getOperandValue(r2.getB()+grid.size()) % grid.size())), "A");
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())-getOperandValue(r2.getA()+grid.size()) % grid.size())), "B");
				break;
			
			case ".I":
				//Substract the target's A operand by r2's A operand, and put it into the A operand of the target
				//Then Substract the target's B operand by r2's B operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())-getOperandValue(r2.getA()+grid.size()) % grid.size())), "A");
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())-getOperandValue(r2.getB()+grid.size()) % grid.size())), "B");
				break;
		}
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getB(),grid));
		Redcode target2 = grid.get(decode(finder.getA(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
		//We increment the token of the warriorProcess 
		this.warriorProcess.setToken(this.warriorProcess.getToken()+1);
	}
	
	public void mul(HashMap<Integer, Redcode> grid) { 
		//First, we decode our operands to get the addresses we need
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
		//Only then we define Redcode objects, which will be both the target and the source
		Redcode r1 = grid.get(b);
		Redcode r2=grid.get(a);
		switch(this.modifier) {

			case ".AB":
				//Multiply the target's B operand by r2's A operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())*getOperandValue(r2.getA()) % grid.size())), "B");
				break;
			
			case ".A":
				//Multiply the target's B operand by r2's A operand, and put it into the A operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())*getOperandValue(r2.getA()) % grid.size())), "A");
				break;

			case ".BA":
				//Multiply the target's A operand by r2's B operand, and put it into the A operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())*getOperandValue(r2.getB()) % grid.size())), "A");
				break;
			
			case ".B":
				//Multiply the target's A operand by r2's B operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())*getOperandValue(r2.getB()) % grid.size())), "B");
				break;

			case ".F":
				//Multiply the target's A operand by r2's A operand, and put it into the A operand of the target
				//Then multiply the target's B operand by r2's B operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())*getOperandValue(r2.getA()) % grid.size())), "A");
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())*getOperandValue(r2.getB()) % grid.size())), "B");
				break;

			case ".X":
				//Multiply the target's A operand by r2's B operand, and put it into the A operand of the target
				//Then multiply the target's B operand by r2's A operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())*getOperandValue(r2.getB()) % grid.size())), "A");
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())*getOperandValue(r2.getA()) % grid.size())), "B");
				break;
			
			case ".I":
				//Multiply the target's A operand by r2's A operand, and put it into the A operand of the target
				//Then multiply the target's B operand by r2's B operand, and put it into the B operand of the target
				r1.changeOperand((String.valueOf(getOperandValue(r1.getA())*getOperandValue(r2.getA()) % grid.size())), "A");
				r1.changeOperand((String.valueOf(getOperandValue(r1.getB())*getOperandValue(r2.getB()) % grid.size())), "B");
				break;
		}
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getB(),grid));
		Redcode target2 = grid.get(decode(finder.getA(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
		//We increment the token of the warriorProcess 
		this.warriorProcess.setToken(this.warriorProcess.getToken()+1);
	}		
	
	public void mov(HashMap<Integer, Redcode> grid) { 
		//First, we decode our operands to get the addresses we need
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
		//Only then we define Redcode objects, which will be both the target and the source
		Redcode r1 = grid.get(b);
		Redcode r2 = grid.get(a);
		switch(this.modifier) {

			case ".AB":
				//Here, we change mostly every attribute Redcode objets have by our source attributes, but we keep the area number as it is
				//In this specific case, B becomes A
				r1.setB(r2.getA());
				r1.setA("$0");
				r1.setWarriorProcess(this.warriorProcess);
				break;
				
			case ".A":
				//Here, we change mostly every attribute Redcode objets have by our source attributes, but we keep the area number as it is
				//In this specific case, A becomes A
				r1.setA(r2.getA());
				r1.setB("$0");
				r1.setWarriorProcess(this.warriorProcess);
				break;

			case ".BA":
				//Here, we change mostly every attribute Redcode objets have by our source attributes, but we keep the area number as it is
				//In this specific case, A becomes B
				r1.setA(r2.getB());
				r1.setB("$0");
				r1.setWarriorProcess(this.warriorProcess);
				break;
				
			case ".B":
				//Here, we change mostly every attribute Redcode objets have by our source attributes, but we keep the area number as it is
				//In this specific case, B becomes B
				r1.setB(r2.getB());
				r1.setA("$0");
				r1.setWarriorProcess(this.warriorProcess);
				break;

			case ".F":
				//Here, we change mostly every attribute Redcode objets have by our source attributes, but we keep the area number as it is
				//In this specific case, A becomes A and B becomes B
				r1.setA(r2.getA());
				r1.setB(r2.getB());
				r1.setWarriorProcess(this.warriorProcess);
				break;

			case ".X":
				//Here, we change mostly every attribute Redcode objets have by our source attributes, but we keep the area number as it is
				//In this specific case, B becomes A and A becomes B
				r1.setA(r2.getB());
				r1.setB(r2.getA());
				r1.setWarriorProcess(this.warriorProcess);
				break;
				
			case ".I":
				//Here, we change mostly every attribute Redcode objets have by our source attributes, but we keep the area number as it is
				//In this specific case, A becomes A and B becomes B
				r1.setCode(r2.getCode());
				r1.setModifier(r2.getModifier());
				r1.setA(r2.getA());
				r1.setB(r2.getB());
				r1.setWarriorProcess(this.warriorProcess);
				break;
		}
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
		//We increment the token of the warriorProcess 
		this.warriorProcess.setToken(this.warriorProcess.getToken()+1);
	}

	public void div (HashMap<Integer,Redcode> grid){
		//First, we decode our operands to get the addresses we need 
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
		//then we get the redcode which is target by the addresses
		Redcode	r1=grid.get(a);
		Redcode r2=grid.get(b);
		try {	
			switch(this.modifier) { 
				case ".AB":
					//we divide r2's B operand by r1's A and the result is placed in the r2's B operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getB())/getOperandValue(r1.getA()) % grid.size()), "B");
					break;
				
				case ".BA":
					//we divide r2's A operand by r1's B and the result is placed in the r2's A operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getA())/getOperandValue(r1.getB()) % grid.size()), "A");
					break;
				
				case ".B":
					//we divide r2's B operand by r1's B and the result is placed in the r2's B operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getB())/getOperandValue(r1.getB()) % grid.size()), "B"); 
					break;				
				
				case ".A":
					//we divide r2's A operand by r1's A and the result is placed in the r2's A operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getA())/getOperandValue(r1.getA()) % grid.size()), "A"); 
					break;	
					
				case ".F": 
					//we divide r2's A operand by r1's A and the result is placed in the r2's A operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getA())/getOperandValue(r1.getA()) % grid.size()), "A");
					//we divide r2's B operand by r1's B and the result is placed in the r2's B operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getB())/getOperandValue(r1.getB()) % grid.size()), "B"); 
					break;
		
				case ".I": 
					//we divide r2's B operand by r1's A and the result is placed in the r2's B operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getA())/getOperandValue(r1.getA()) % grid.size()), "A"); 
					//we divide r2's B operand by r1's B and the result is placed in the r2's B operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getB())/getOperandValue(r1.getB()) % grid.size()), "B");
					break;

				case ".X": 
					//we divide r2's A operand by r1's B and the result is placed in the r2's A operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getA())/getOperandValue(r1.getB()) % grid.size()), "A");
					//we divide r2's B operand by r1's A and the result is placed in the r2's B operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getB())/getOperandValue(r1.getA()) % grid.size()), "B"); 
					break;
			}
		}
		catch(Exception e){
			this.warriorProcess.dies();	
		}
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
		//We increment the token of the warriorProcess 
		this.warriorProcess.setToken(this.warriorProcess.getToken()+1);
	}

	public void mod (HashMap<Integer,Redcode> grid){
		//First, we decode our operands to get the addresses we need 
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
		//Only then we define Redcode objects, which will be both the target and the source
		Redcode	r1=grid.get(a);
		Redcode r2=grid.get(b);
		try {	
			switch(this.modifier) { 

				case ".AB":
					//we do modulo of the division r2's B operand by r1's A and the result is placed in the r2's B operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getB())%getOperandValue(r1.getA()) % grid.size()), "B");
					break;
				
				case ".BA":
					//we do modulo of the division r2's A operand by r1's B and the result is placed in the r2's A operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getA())%getOperandValue(r1.getB()) % grid.size()), "A");
					break;
				
				case ".B":
					//we do modulo of the division r2's B operand by r1's B and the result is placed in the r2's B operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getB())%getOperandValue(r1.getB()) % grid.size()), "B"); 
					break;				
				
				case ".A":
					//we do modulo of the division r2's A operand by r1's A and the result is placed in the r2's A operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getA())%getOperandValue(r1.getA()) % grid.size()), "A"); 
					break;	
					
				case ".F": 
					//we do modulo of the division r2's A operand by r1's A and the result is placed in the r2's A operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getA())%getOperandValue(r1.getA()) % grid.size()), "A");
					//we do modulo of the division r2's B operand by r1's B and the result is placed in the r2's B operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getB())%getOperandValue(r1.getB()) % grid.size()), "B"); 
					break;
		
				case ".I": 
					//we do modulo of the division r2's A operand by r1's A and the result is placed in the r2's A operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getA())%getOperandValue(r1.getA()) % grid.size()), "A");
					//we do modulo of the division r2's B operand by r1's B and the result is placed in the r2's B operand 
					r2.changeOperand(String.valueOf(getOperandValue(r2.getB())%getOperandValue(r1.getB()) % grid.size()), "B");
					break;

				case ".X": 
					//we do modulo of the division r2's A operand by r1's B and the result is placed in the r2's A operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getA())%getOperandValue(r1.getB()) % grid.size()), "A");
					//we do modulo of the division r2's B operand by r1's A and the result is placed in the r2's B operand
					r2.changeOperand(String.valueOf(getOperandValue(r2.getB())%getOperandValue(r1.getA()) % grid.size()), "B"); 
					break;
			}
		}
		catch(Exception e){
			this.warriorProcess.dies();	
		}
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
		//We increment the token of the warriorProcess 
		this.warriorProcess.setToken(this.warriorProcess.getToken()+1);
	}

	public void jmp(HashMap<Integer, Redcode> grid){
		//First, we decode our operands to get the addresses we need 
		int a = decode(this.operandA, grid);
		decode(this.operandB, grid);

		this.warriorProcess.setToken(a);

		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
	}

	public void jmz(HashMap<Integer, Redcode> grid){
		//First, we decode our operands to get the addresses we need 
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
		//Only then we define Redcode objects, which will be both the target and the source	
		Redcode r2=grid.get(b);

		switch(this.modifier) { 

			case ".AB":
				//look if the r2's B operand is equal to 0 and in that case jump to the addresse target by a
				if(getOperandValue(r2.getB())==0){
					this.warriorProcess.setToken(a);
				}
				break;
			
			case ".BA":
				//look if the r2's A operand is equal to 0 and in that case jump to the addresse target by a
				if(getOperandValue(r2.getA())==0){
					this.warriorProcess.setToken(a);
				}
				break;
			
			case ".B":
				//look if the r2's B operand is equal to 0 and in that case jump to the addresse target by a
				if(getOperandValue(r2.getB())==0){
					this.warriorProcess.setToken(a);
				}
				break;				
			
			case ".A":
				//look if the r2's A operand is equal to 0 and in that case jump to the addresse target by a
				if(getOperandValue(r2.getA())==0){
					this.warriorProcess.setToken(a);
				}
				break;	
				
			case ".F": 
				//look if the r2's B and the r2's A operand is equal to 0 and in that case jump to the addresse target by A
				if(getOperandValue(r2.getB())==0 && getOperandValue(r2.getA())==0 ){
					this.warriorProcess.setToken(a);
				}
				break;
	
			case ".I": 
				//look if the r2's B and the r2's A operand is equal to 0 and in that case jump to the addresse target by A
				if(getOperandValue(r2.getB())==0 && getOperandValue(r2.getA())==0 ){
					this.warriorProcess.setToken(a);
				}
				break;

			case ".X": 
				//look if the r2's B and the r2's A operand is equal to 0 and in that case jump to the addresse target by A
				if(getOperandValue(r2.getB())==0 && getOperandValue(r2.getA())==0 ){
					this.warriorProcess.setToken(a);
				}
				break;
		}

		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
	}

	public void jmn(HashMap<Integer, Redcode> grid){
		
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);

		Redcode r2=grid.get(b);

		switch(this.modifier) { 

			case ".AB":
				//look if the r2's B operand is not equal to 0 and in that case jump to the addresse target by a
				if(getOperandValue(r2.getB())!=0){
					this.warriorProcess.setToken(a);
				}
				break;
			
			case ".BA":
				//look if the r2's A operand is not equal to 0 and in that case jump to the addresse target by a
				if(getOperandValue(r2.getA())!=0){
					this.warriorProcess.setToken(a);
				}
				break;
			
			case ".B":
				//look if the r2's B operand is not equal to 0 and in that case jump to the addresse target by a
				if(getOperandValue(r2.getB())!=0){
					this.warriorProcess.setToken(a);
				}
				break;				
			
			case ".A":
				//look if the r2's A operand is not equal to 0 and in that case jump to the addresse target by a
				if(getOperandValue(r2.getA())!=0){
					this.warriorProcess.setToken(a);
				}
				break;	
				
			case ".F": 
				//look if the r2's B and the r2's A operand is equal to 0 and in that case jump to the addresse target by A
				if(getOperandValue(r2.getB())!=0 && getOperandValue(r2.getA())!=0 ){
					this.warriorProcess.setToken(a);
				}
				break;
	
			case ".I": 
				//look if the r2's B and the r2's A operand is equal to 0 and in that case jump to the addresse target by A
				if(getOperandValue(r2.getB())!=0 && getOperandValue(r2.getA())!=0 ){
					this.warriorProcess.setToken(a);
				}
				break;

			case ".X": 
				//look if the r2's B and the r2's A operand is not equal to 0 and in that case jump to the addresse target by A
				if(getOperandValue(r2.getB())!=0 && getOperandValue(r2.getA())!=0 ){
					this.warriorProcess.setToken(a);
				}
				break;
		}

		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
	}

	public void dat(HashMap<Integer, Redcode> grid) {
		//Executing a DAT instruction ends the current process, so we decode both addresses in case there are values to decrement
		decode(this.operandA, grid);
		decode(this.operandB, grid);
		
		//Then we kill the process
		this.warriorProcess.dies();

		//And then we run the postIncrement instructions
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
	}

	public void djn(HashMap<Integer, Redcode> grid) { // decrement and jump if not zero
		//We get both addresses
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
		//The only object needed for this method is the object at address b, so we only point at it
		Redcode r2=grid.get(b);
		
		switch(this.modifier) { 

			case ".A":
				//We only decrement the A operand
				r2.changeOperand(String.valueOf(getOperandValue(this.operandA) - 1), "A");
				if(getOperandValue(r2.getA()) != 0){
					//We now jump to the object at a address
					this.warriorProcess.setToken(a);
				}
				break;

			case ".B":
				//We only decrement the B operand
				r2.changeOperand(String.valueOf(getOperandValue(this.operandB) - 1), "B");
				if(getOperandValue(r2.getB()) != 0){
					//We now jump to the object at a address
					this.warriorProcess.setToken(a);
				}
				break;	

			case ".AB":
				//We only decrement the B operand
				r2.changeOperand(String.valueOf(getOperandValue(this.operandB) - 1), "B");
				if(getOperandValue(r2.getB()) != 0){
					//We now jump to the object at a address
					this.warriorProcess.setToken(a);
				}
				break;
			
			case ".BA":
				//We only decrement the A operand
				r2.changeOperand(String.valueOf(getOperandValue(this.operandA) - 1), "A");
				if(getOperandValue(r2.getA()) != 0){
					//We now jump to the object at a address
					this.warriorProcess.setToken(a);
				}
				break;
				
			case ".F": 
				//We decrement both operands
				r2.changeOperand(String.valueOf(getOperandValue(this.operandA) - 1), "A");
				r2.changeOperand(String.valueOf(getOperandValue(this.operandB) - 1), "B");
				if(getOperandValue(r2.getB()) != 0 || getOperandValue(r2.getA()) != 0 ){
					//We now jump to the object at a address
					this.warriorProcess.setToken(a);
				}
				break;

			case ".X": 
				//We decrement both operands
				r2.changeOperand(String.valueOf(getOperandValue(this.operandA) - 1), "A");
				r2.changeOperand(String.valueOf(getOperandValue(this.operandB) - 1), "B");
				if(getOperandValue(r2.getB()) != 0 || getOperandValue(r2.getA()) != 0 ){
					//We now jump to the object at a address
					this.warriorProcess.setToken(a);
				}
				break;

			case ".I": 
				//We decrement both operands
				r2.changeOperand(String.valueOf(getOperandValue(this.operandA) - 1), "A");
				r2.changeOperand(String.valueOf(getOperandValue(this.operandB) - 1), "B");
				if(getOperandValue(r2.getB()) !=0 || getOperandValue(r2.getA()) != 0 ){
					//We now jump to the object at a address
					this.warriorProcess.setToken(a);
				}
				break;
		}

		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
	}


	public void spl(HashMap<Integer, Redcode> grid) { // Split
		//Decode both addresses, even if the B-field is useless, decoding it is needed since we might decrement some stuff
		int a = decode(this.operandA, grid);
		decode(this.operandB, grid);
		//We point to the object in the play grid
		Redcode r1 = grid.get(a);

		//We create a new process, we get it started at a position, we add the process to the meant Player, and then we set the warriorProcess of the Redcode object
		//to the new one
		WarriorProcess wp = new WarriorProcess(this.warriorProcess.getAttachedPlayer(), a);
		this.warriorProcess.getAttachedPlayer().addProcess(wp);
		r1.setWarriorProcess(wp);

		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
		//We increment the token of the warriorProcess 
		this.warriorProcess.setToken(this.warriorProcess.getToken()+1);
	}

	public void slt(HashMap<Integer, Redcode> grid) { // skip if less than
		//We get both addresses
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
		//we need two object for this method, the object at address a and b, so we point them
		Redcode r1=grid.get(a);
		Redcode r2=grid.get(b);
		
		switch(this.modifier) { 
	
			case ".A":
				//we test if A source is inferior to A destination
				if(getOperandValue(r1.getA()) < getOperandValue(r2.getA()) ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
	
			case ".B":
				//we test if B source is inferior to B destination
				if(getOperandValue(r1.getB()) < getOperandValue(r2.getB())){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;	
	
			case ".AB":
				//we test if A source is inferior to B destination
				if(getOperandValue(r1.getA()) < getOperandValue(r2.getB())) {
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
			
			case ".BA":
				//we test if B source is inferior to A destination
				if(getOperandValue(r1.getB()) < getOperandValue(r2.getA())){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
				
				
			case ".F": 
				//we test if A source is inferior to A destination and if B source is inferior to B destination
				if(getOperandValue(r1.getA()) < getOperandValue(r2.getA()) && getOperandValue(r1.getB()) < getOperandValue(r2.getB()) ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
	
	
			case ".X":
				//we test if A source is inferior to B destination and if B source is inferior to A destination
				if(getOperandValue(r1.getA()) < getOperandValue(r2.getB()) && getOperandValue(r1.getB()) < getOperandValue(r2.getA()) ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
			
			case ".I":
				//we test if A source is inferior to A destination and if B source is inferior to B destination
				if(getOperandValue(r1.getA()) < getOperandValue(r2.getA()) && getOperandValue(r1.getB()) < getOperandValue(r2.getB())  ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
		}
	
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
	}
	
	public void seq(HashMap<Integer, Redcode> grid) { // skip if equal (cmp alias)
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
	
		Redcode r1=grid.get(a);
		Redcode r2=grid.get(b);
		
		switch(this.modifier) { 
	
			case ".A":
				//we test if A source is equal to A destination
				if(getOperandValue(r1.getA()) == getOperandValue(r2.getA()) ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
	
			case ".B":
				//we test if B source is equal to B destination
				if(getOperandValue(r1.getB()) == getOperandValue(r2.getB())){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;	
	
			case ".AB":
				//we test if A source is equal to B destination
				if(getOperandValue(r1.getA()) == getOperandValue(r2.getB())) {
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
			
			case ".BA":
				//we test if B source is equal to A destination
				if(getOperandValue(r1.getB()) == getOperandValue(r2.getA())){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
				
				
			case ".F": 
				//we test if A source is equal to A destination and if B source is equal to B destination
				if(getOperandValue(r1.getA()) == getOperandValue(r2.getA()) && getOperandValue(r1.getB()) == getOperandValue(r2.getB()) ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
	
	
			case ".X":
				//we test if A source is equal to B destination and if B source is equal to A destination
				if(getOperandValue(r1.getA()) == getOperandValue(r2.getB()) && getOperandValue(r1.getB()) == getOperandValue(r2.getA()) ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
			
			case ".I":
				//we test if A source is equal to A destination and if B source is equal to B destination
				if(getOperandValue(r1.getA()) == getOperandValue(r2.getA()) && getOperandValue(r1.getB()) == getOperandValue(r2.getB())  ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
		}
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
	}
	
	public void sne(HashMap<Integer, Redcode> grid) { // skip if not equal
		int a = decode(this.operandA, grid);
		int b = decode(this.operandB, grid);
	
		Redcode r1=grid.get(a);
		Redcode r2=grid.get(b);
		
		switch(this.modifier) { 
	
			case ".A":
				//we test if A source is not equal to A destination
				if(getOperandValue(r1.getA()) != getOperandValue(r2.getA()) ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
	
			case ".B":
				//we test if B source is not equal to B destination
				if(getOperandValue(r1.getB()) != getOperandValue(r2.getB())){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;	
	
			case ".AB":
				//we test if A source is not equal to B destination
				if(getOperandValue(r1.getA()) != getOperandValue(r2.getB())) {
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
			
			case ".BA":
				//we test if B source is not equal to A destination
				if(getOperandValue(r1.getB()) != getOperandValue(r2.getA())){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
				
				
			case ".F": 
				//we test if A source is not equal to A destination and if B source is not equal to B destination
				if(getOperandValue(r1.getA()) != getOperandValue(r2.getA()) && getOperandValue(r1.getB()) != getOperandValue(r2.getB()) ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
	
	
			case ".X":
				//we test if A source is not equal to B destination and if B source is not equal to A destination
				if(getOperandValue(r1.getA()) != getOperandValue(r2.getB()) && getOperandValue(r1.getB()) != getOperandValue(r2.getA()) ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
			
			case ".I":
				//we test if A source is not equal to A destination and if B source is not equal to B destination
				if(getOperandValue(r1.getA()) != getOperandValue(r2.getA()) && getOperandValue(r1.getB()) != getOperandValue(r2.getB())  ){
					//if the test is true we increment the token by 2 so we skip the next adress 
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 2);
				}
				else{
					//if the test is false we increment the token only by 1 so we just go to the next adress
					this.warriorProcess.setToken(this.warriorProcess.getToken() + 1);
				}
				break;
		}
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
	}
	
	public void nop(HashMap<Integer, Redcode> grid) {
		//Since there might be some stuff to decrement, we need to decode each operand
		decode(this.operandA, grid);
		decode(this.operandB, grid);
	
		//There can also be a need to pre-increment stuff here, so here it goes :
		//First, we create a copy of the current Redcode object
		Redcode finder = this.copy();
		//Then, we set A and B addressing modes to $, the goal is to find the direct address of the operand, since it's this one that we must decrement
		finder.setA("$" + getOperandValue(finder.getA()));
		finder.setB("$" + getOperandValue(finder.getB()));
		//We get both Redcode objects, decode won't decrement anything since our operands are direct mode
		Redcode target = grid.get(decode(finder.getA(),grid));
		Redcode target2 = grid.get(decode(finder.getB(),grid));
		//We run the postIncrement() method now
		this.postIncrement(target);
		this.postIncrement(target2);
	}
	

	public String toString() {
		return "Command : " + this.opCode + this.modifier + " " + this.operandA + "," + this.operandB + System.lineSeparator() +
		 "Area : " + this.area + System.lineSeparator() + "player : " + this.warriorProcess.getAttachedPlayer().getName(); 
	}

}
