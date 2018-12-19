package p1;


public class Mesh
{
	private Triangle[] triangles;
	
	public Mesh(Triangle[] triangles)
	{
		this.triangles = triangles;
	}
	
	public Triangle getTriangle(int i)
	{
		return triangles[i];
	}
	
	public int getNTriangle()
	{
		return triangles.length;
	}
}
