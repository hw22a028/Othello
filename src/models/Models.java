package models;

public class Models {
	
	public final static int TABLE_SIZE = 8;
	public final static int PASS_MAX = 3;
	
	//駒の種類-----------------------------------------
	public enum PieceState
	{
		None,
		Black,
		White,
	}
	
	public enum Player
	{
		None,
		Player1,
		Player2,
	}
	
	public record Vector2(int x, int y) {}
	
	public static Vector2[] GetVecData()
	{
		Vector2[] vecData = new Vector2[] {
				new Vector2(0, -1),
				new Vector2(1, -1),
				new Vector2(-1, -1),
				new Vector2(1, 1),
				new Vector2(0, 1),
				new Vector2(-1, 1),
				new Vector2(1, 0),
				new Vector2(-1, 0),
		};
		
		return vecData;
	}
	
	//配列内かどうか----------------------------------------------------
	public static Boolean InsideArray(int x, int y)
	{
		Boolean col = 0 <= x && x < Models.TABLE_SIZE;
		Boolean raw = 0 <= y && y < Models.TABLE_SIZE;
		return col && raw;
	}
}
