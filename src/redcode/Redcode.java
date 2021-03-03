package redcode;

import java.util.Arrays;

public class Redcode {

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
	}
	
	public Redcode(String opc){
		this.opCode = opc;
		this.modifier = getDefaultModifier();
		this.operandA = "$0";
		this.operandB = "$0";
		this.area = null;
	}
	
	public Redcode(String opc, String opa) {
		this.opCode = opc;
		this.modifier = getDefaultModifier();
		this.operandA = opa;
		this.operandB = "$0";
		this.area = null;
	}
	
	public Redcode(String opc, String oA, String oB) {
		this.opCode = opc;
		this.modifier = getDefaultModifier();
		this.operandA = oA;
		this.operandB = oB;
		this.area = null;
	}

	public Redcode(String opc, String m, String opa, String opb, Integer a){
		this.opCode = opc;
		this.modifier = m;
		this.operandA = opa;
		this.operandB = opb;
		this.area = a;
	}

	public String getDefaultModifier(){
		String mod = "";
		if(this.opCode == "dat"){return "f";}
		else if(this.opCode == "slt"){return ((this.operandA.substring(0, 1) == "#") ? "ab" : "b");}
		else if(Arrays.asList(new String[]{"jmp", "jmz", "jmn", "djn", "spl", "nop"}).contains(this.opCode)){return "b";}
		else if(this.operandA.substring(0,1) == "#"){return "ab";}
		else if(this.operandB.substring(0,1) == "#"){return "b";}
		else{
			if(Arrays.asList(new String[]{"add", "sub", "mul", "div", "mod"}).contains(this.opCode)){
				return "f";
			}
			else if(Arrays.asList(new String[]{"mov", "seq", "sne"}).contains(this.opCode)){
				return "i";
			}
		}
		return mod;
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

}
