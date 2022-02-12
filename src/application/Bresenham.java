package application;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Line;

/**
 * Implementation of the Bresenham line algorithm.
 * 
 * @author fragkakis
 *
 */
public class Bresenham {

	/**
	 * Returns the list of array elements that comprise the line.
	 * 
	 * @param grid the 2d array
	 * @param x0   the starting point x
	 * @param y0   the starting point y
	 * @param x1   the finishing point x
	 * @param y1   the finishing point y
	 * @return the line as a list of array elements
	 */
	public static <T> List<T> findLine(T[][] grid, double x0, double y0, double x1, double y1) {

		List<T> line = new ArrayList<T>();

		double dx = Math.abs(x1 - x0);
		double dy = Math.abs(y1 - y0);

		double sx = x0 < x1 ? 1.0 : -1.0;
		double sy = y0 < y1 ? 1.0 : -1.0;

		double err = dx - dy;
		double e2;
		double currentX = x0;
		double currentY = y0;

		while (true) {
			line.add(grid[(int) currentX][(int) currentY]);

			if (currentX == x1 && currentY == y1) {
				break;
			}

			e2 = 2.0 * err;
			if (e2 > -1.0 * dy) {
				err = err - dy;
				currentX = currentX + sx;
			}

			if (e2 < dx) {
				err = err + dx;
				currentY = currentY + sy;
			}
		}

		return line;
	}
//	public static Line findLine(Line grid, double x0, double y0, double x1, double y1) {
//		
//		List<T> line = new ArrayList<T>();
////		/Line line1 = new Line();
//		
//		
//		double dx = Math.abs(x1 - x0);
//		double dy = Math.abs(y1 - y0);
//		
//		double sx = x0 < x1 ? 1.0 : -1.0; 
//		double sy = y0 < y1 ? 1.0 : -1.0; 
//		
//		double err = dx-dy;
//		double e2;
//		double currentX = x0;
//		double currentY = y0;
//		
//		while(true) {
//			line1.getEndY();
//			
//			if(currentX == x1 && currentY == y1) {
//				break;
//			}
//			
//			e2 = 2.0*err;
//			if(e2 > -1.0 * dy) {
//				err = err - dy;
//				currentX = currentX + sx;
//			}
//			
//			if(e2 < dx) {
//				err = err + dx;
//				currentY = currentY + sy;
//			}
//		}
//				
//		return line;
//	}

}