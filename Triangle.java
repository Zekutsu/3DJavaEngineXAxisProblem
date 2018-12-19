package p1;

public class Triangle
{
	private Point[] points = new Point[3];
	
	public Triangle(Point point0, Point point1, Point point2)
	{
		points[0] = point0;
		points[1] = point1;
		points[2] = point2;
	}
	
	public Point getPoint(int i)
	{
		return points[i];
	}
	
	public Triangle clone()
	{
		Point[] points = new Point[3];
		
		points[0] = new Point(this.points[0].x, this.points[0].y, this.points[0].z);
		points[1] = new Point(this.points[1].x, this.points[1].y, this.points[1].z);
		points[2] = new Point(this.points[2].x, this.points[2].y, this.points[2].z);
		
		return new Triangle(points[0], points[1], points[2]);
	}
}
