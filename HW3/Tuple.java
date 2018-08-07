package HW3;

public class Tuple {

	public double Value;
	public double Length;

	public Tuple(double runLength, double result) {
		this.Value = runLength;
		this.Length = result;
	}

	public double getValue() {
		return Value;
	}

	public void setValue(int value) {
		Value = value;
	}

	public double getLength() {
		return Length;
	}

	public void setLength(int length) {
		Length = length;
	}

}
