package simulator.misc;

import org.json.JSONArray;

public class Vector2D {

	double _x;
	double _y;

	private double width = 800;
	private double height = 600;

	// create the zero vector
	public Vector2D() {
		_x = _y = 0.0;
	}

	// copy constructor
	public Vector2D(Vector2D v) {
		_x = v._x;
		_y = v._y;
	}

	// create a vector from an array
	public Vector2D(double x, double y) {
		_x = x;
		_y = y;
	}

	// return the inner product of this Vector a and b
	public double dot(Vector2D that) {
		return _x * that._x + _y * that._y;
	}

	// return the length of the vector
	public double magnitude() {
		return Math.sqrt(dot(this));
	}

	// return the distance between this and that
	public double distanceTo(Vector2D that) {
		return minus(that).magnitude();
	}

	// create and return a new object whose value is (this + that)
	public Vector2D plus(Vector2D that) {
		double x = _x + that._x;
		double y = _y + that._y;
		return new Vector2D(x, y);
	}

	// create and return a new object whose value is (this - that)
	public Vector2D minus(Vector2D that) {
		return new Vector2D(_x - that._x, _y - that._y);
	}

	// return the corresponding coordinate
	public double getX() {
		return _x;
	}

	public double getY() {
		return _y;
	}

	// create and return a new object whose value is (this * factor)
	public Vector2D scale(double factor) {
		return new Vector2D(_x * factor, _y * factor);
	}

	// return the corresponding unit vector
	public Vector2D direction() {
		if (magnitude() > 0.0)
			return scale(1.0 / magnitude());
		else
			return new Vector2D(this);
	}

	public Vector2D rotate(int deg) {

		double degree = deg;

		assert (degree >= -180.0 && degree <= 180.0);

		double angle = degree * Math.PI / 180.0;
		double sine = Math.sin(angle);
		double cosine = Math.cos(angle);

		Vector2D r = new Vector2D();

		r._x = cosine * _x + (-sine) * _y;
		r._y = sine * _x + cosine * _y;

		return r;
	}

	public double angle(Vector2D v) {
		double a2 = Math.atan2(v.getX(), v.getY());
		double a1 = Math.atan2(_x, _y);
		double angle = a1 - a2;
		double K = a1 > a2 ? -2.0 * Math.PI : 2.0 * Math.PI;
		angle = (Math.abs(K + angle) < Math.abs(angle)) ? K + angle : angle;
		return angle * 180.0 / Math.PI;
	}

	public static Vector2D get_random_vector(double min, double max) {
		assert (max >= min);
		double x = min + Utils._rand.nextDouble(absolute(max - min));
		double y = min + Utils._rand.nextDouble(absolute(max - min));
		assert (x >= min && x <= max);
		assert (y >= min && y <= max);
		return new Vector2D(x, y);
	}

	private static double absolute(double num) {
		return num < 0 ? -num : num;
	}

	public JSONArray asJSONArray() {
		JSONArray a = new JSONArray();
		a.put(_x);
		a.put(_y);
		return a;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(_x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(_y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2D other = (Vector2D) obj;
		if (Double.doubleToLongBits(_x) != Double.doubleToLongBits(other._x))
			return false;
		if (Double.doubleToLongBits(_y) != Double.doubleToLongBits(other._y))
			return false;
		return true;
	}

	// return a string representation of the vector
	public String toString() {
		return "[" + _x + "," + _y + "]";
	}

	public void inMap() {
		double x = this._x;
		double y = this._y;
		while (x >= width)
			x = (x - width); 
		while (x < 0)
			x = (x + width);
		while (y >= height)
			y = (y - height);
		while (y < 0)
			y = (y + height);
		_x = x;
		_y = y;
	}

	public boolean noSale() {
		return (0 <= this._x && this._x <= width && 0 <= this._y && this._y <= height);
	}
}
