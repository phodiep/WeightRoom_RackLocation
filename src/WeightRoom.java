import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


public class WeightRoom {
	
	private static class Point {
		public int x;
		public int y;
		
		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		public String toString()
		{
			return "(" + this.x + "," + this.y + ")";
		}
		
		@Override
		public int hashCode()
		{
			return (Integer.toString(x) + "-" + Integer.toString(y)).hashCode();
		}
		
		@Override
		public boolean equals(Object other)
		{
			if (!(other instanceof Point))
			{
				return false;
			}
			
			Point p = (Point) other;
			return this.x == p.x && this.y == p.y;
		}
	}
	
	private static class BenchFringe {
		public Point[] fringe;
		public Point bench;
		
		public BenchFringe(Point bench, Point[] fringe)
		{
			this.bench = bench;
			this.fringe = fringe;
		}
	}
	
	private String[][] grid;
	private Map<Point,Set<Point>> cellsVisited;
	private List<Point> benches;
	private static final String BENCH = "@";
	private static final String WALL = "x";
	
	public static void main(String[] args)
	{
		String[][] grid = new String[11][9];
		
		for (int i = 0; i < grid.length; i++)
		{
			for (int j = 0; j < grid[i].length; j++)
			{
				grid[i][j] = "";
			}
		}
		
		//benches
		grid[9][1] = BENCH;
		grid[2][1] = BENCH;
		grid[2][6] = BENCH;
		grid[7][7] = BENCH;
		grid[9][7] = BENCH;
		
		//Walls
		grid[4][1] = WALL;
		grid[4][2] = WALL;
		grid[4][3] = WALL;
		grid[4][4] = WALL;
		grid[4][5] = WALL;
		grid[1][2] = WALL;
		grid[1][3] = WALL;
		grid[1][3] = WALL;
		grid[2][3] = WALL;
		grid[3][3] = WALL;
		grid[4][3] = WALL;
		grid[7][4] = WALL;
		grid[8][4] = WALL;
		grid[9][4] = WALL;		
		
		
		WeightRoom room = new WeightRoom(grid);
		
		System.out.println("Number of benches: " + room.benches.size());
		
		Point bestLocation = room.findBestLocation();
		
		System.out.println("Best Spot for weight rack: " + bestLocation.x + " " + bestLocation.y);
	}
	
	public WeightRoom(String[][] grid)
	{
		this.grid = grid;
		this.benches = new ArrayList<Point>();
		this.cellsVisited = new HashMap<Point,Set<Point>>();

		findBenches();
	}
	
	public Point findBestLocation()
	{
		Queue<BenchFringe> queue = new LinkedList<>();
		
		for (Point p : this.benches)
		{	
			BenchFringe bf = new BenchFringe(p,expandFringe(new BenchFringe(
					p,
					new Point[]{p})));
			queue.add(bf);
		}
		
		while (!queue.isEmpty())
		{
			BenchFringe bf = queue.remove();
			
			//System.out.println("fringe size for bench " + bf.bench + " is : " + bf.fringe.length);
			
			for (Point p : bf.fringe)
			{
				if (visitCell(p, bf.bench))
					return p;
			}
			
			bf.fringe = expandFringe(bf);
			
			if (bf.fringe.length > 0)
				queue.add(bf);
						
		}
		
		return null;
	}
	
	private Point[] expandFringe(BenchFringe bf)
	{
		List<Point> newFringe = new ArrayList<>();
		
		for (Point p : bf.fringe)
		{
			
			for (int x = p.x-1; x <= p.x+1; x++)
			{
				if (x < 0 || x >= grid.length)
				{
					continue;
				}

				for (int y = p.y-1; y <= p.y+1; y++)
				{
					if (y < 0 || y >= grid[x].length)
					{
						continue;
					}
					Point cell = new Point(x,y);
					if ((!cellsVisited.containsKey(cell) ||
							!cellsVisited.get(cell).contains(bf.bench) ) &&
							!grid[x][y].equals(WALL) && 
							!grid[x][y].equals(BENCH))
					{
						newFringe.add(cell);
					}
				}	
			}
		}
		return newFringe.toArray(new Point[]{});
	}
	
	private void findBenches()
	{
		for (int i = 0; i < grid.length; i++)
		{
			for (int j = 0; j < grid[i].length; j++)
			{
				if (grid[i][j].equals(BENCH))
				{
					benches.add(new Point(i,j));
				}
			}
		}
	}

	private boolean visitCell(Point p, Point bench)
	{
		final Set<Point> set;
		if (!cellsVisited.containsKey(p)) {
			set = new HashSet<>();
			cellsVisited.put(p, set);
		} else {
			set = cellsVisited.get(p);
		}
		
		set.add(bench);
		
		return set.size() == this.benches.size();
	}
	
}

