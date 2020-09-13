package core.feature.shape;

import java.awt.Point;
import java.awt.Polygon;

public class ShapeTransform
{
	public static Polygon movePolygonTo(Polygon p, int x, int y)
	{
		int xDiff = x - p.getBounds().x;
		int yDiff = y - p.getBounds().y;
		
		int[] xpoints = new int[p.npoints];
		int[] ypoints = new int[p.npoints];
		
		for(int iX = 0; iX < p.npoints; iX++)
		{
			xpoints[iX] = p.xpoints[iX] + xDiff;
		}
		
		for(int iY = 0; iY < p.npoints; iY++)
		{
			ypoints[iY] = p.ypoints[iY] + yDiff;
		}
		
		Polygon returnable = new Polygon(xpoints, ypoints, p.npoints);
		
		return returnable;
	}
	
	public static Polygon movePolygonTo(Polygon p, Point point)
	{
		return movePolygonTo(p, point.x, point.y);
	}
	
	public static Polygon scalePolygon(Polygon p, float k)
	{
		int[] xpoints = new int[p.npoints];
		int[] ypoints = new int[p.npoints];
		
		for(int iX = 0; iX < p.npoints; iX++)
		{
			xpoints[iX] = (int) (p.xpoints[iX] * k);
		}
		
		for(int iY = 0; iY < p.npoints; iY++)
		{
			ypoints[iY] = (int) (p.ypoints[iY] * k);
		}
		
		Polygon returnable = new Polygon(xpoints, ypoints, p.npoints);
		
		return returnable;
	}
	
	public static Polygon inverseOrderFrom(Polygon p)
	{
		// System.out.println("original> start(" + p.xpoints[0] + "|" + p.ypoints[0] + "), end(" + p.xpoints[p.npoints-1] + "|" + p.ypoints[p.npoints-1] + ")");
		
		Polygon inversed = new Polygon();
		
		for(int i = p.npoints-1; i >= 0; i--)
		{
			inversed.addPoint(p.xpoints[i], p.ypoints[i]);
		}
		
		// System.out.println("inversed> start(" + inversed.xpoints[0] + "|" + inversed.ypoints[0] + "), end(" + inversed.xpoints[inversed.npoints-1] + "|" + inversed.ypoints[inversed.npoints-1] + ")");
		
		return inversed;
	}
	
	public static Polygon invertXFromPolygon(Polygon p)
	{
		int[] xpoints = new int[p.npoints];
		int[] ypoints = new int[p.npoints];
		
		for(int iX = 0; iX < p.npoints; iX++)
		{
			xpoints[iX] = -p.xpoints[iX];
			ypoints[iX] = p.ypoints[iX];
		}
		
		Polygon returnable = new Polygon(xpoints, p.ypoints, p.npoints);
		
		return returnable;
	}
	
	public static Polygon invertYFromPolygon(Polygon p)
	{
		int[] xpoints = new int[p.npoints];
		int[] ypoints = new int[p.npoints];
		
		for(int i = 0; i < p.npoints; i++)
		{
			xpoints[i] = p.xpoints[i];
			ypoints[i] = -p.ypoints[i];
		}
		
		Polygon returnable = new Polygon(p.xpoints, ypoints, p.npoints);
		
		return returnable;
	}
	
	public static Polygon invertPolygon(Polygon p)
	{
		return invertYFromPolygon(invertXFromPolygon(p));
	}
	
	public static Polygon flipVertically(Polygon p)
	{
		return movePolygonTo(invertXFromPolygon(p), p.getBounds().x, p.getBounds().y);
	}
	
	public static Polygon flipHorizontically(Polygon p)
	{
		return movePolygonTo(invertYFromPolygon(p), p.getBounds().x, p.getBounds().y);
	}
	
	public static Polygon flip(Polygon p)
	{
		return movePolygonTo(invertPolygon(p), p.getBounds().x, p.getBounds().y);
	}
}
