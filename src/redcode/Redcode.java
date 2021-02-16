package redcode;

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
	
	public Redcode(String m) {
		// Also an error
		this.modifier = m;
		this.operandA = "";
		this.operandB = "";
	}
	
	public Redcode(String m, String oA) {
		this.modifier = m;
		this.operandA = oA;
		this.operandB = "";
	}
	
	public Redcode(String m, String oA, String oB) {
		this.modifier = m;
		this.operandA = oA;
		this.operandB = oB;
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
