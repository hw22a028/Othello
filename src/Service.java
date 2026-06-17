import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import models.Models.PieceState;
import models.Models.Player;

public class Service {
	final int TABLE_SIZE = 8;

	//盤面作成--------------------------------------------------------
	private PieceState[][] othelloTable = new PieceState[TABLE_SIZE][TABLE_SIZE];
	
	public PieceState[][] GanerateTable()
	{
		for (int i = 0; i < TABLE_SIZE; i++) 
		{
			for (int j = 0; j < TABLE_SIZE; j++) 
			{
				Boolean onWhite = (i == TABLE_SIZE / 2 - 1 && j == TABLE_SIZE / 2 - 1) ||
						(i == TABLE_SIZE / 2 && j == TABLE_SIZE / 2);
				Boolean onBlack = (i == TABLE_SIZE / 2 && j == TABLE_SIZE / 2 - 1) ||
						(i == TABLE_SIZE / 2 - 1 && j == TABLE_SIZE / 2);
				
				if (onWhite) othelloTable[i][j] = PieceState.White;
				else if (onBlack) othelloTable[i][j] = PieceState.Black;
				else othelloTable[i][j] = PieceState.None;
			}
		}
		return othelloTable;
	}
	
	//先手プレイヤー決定-----------------------------------------------
	private Player firstPlayer = Player.None;
	private Player nowPlayer = Player.None;
	
	public Player ChooseFirstPlayer()
	{
		Random rnd = new Random();
		if (rnd.nextBoolean()) firstPlayer = Player.Player1;
		else firstPlayer = Player.Player2;
		
		nowPlayer = firstPlayer;
		return firstPlayer;
	}
	
	//現在手番のプレイヤー取得--------------
	public Player GetNowPlayer() { return nowPlayer; }
	
	//手番切り替え-----------------------------------------------------
	public void SwitchPlayer()
	{
		if (nowPlayer == Player.Player1) nowPlayer = Player.Player2;
		else nowPlayer = Player.Player1;
	}
	
	//パス------------------------------------------------------------
	private Map<Player, Integer> passData = new HashMap();
	
	public void PASS(Player player)
	{
		passData.merge(player, 1, Integer::sum);
	}
	
	//パスデータ取得----------------------
	public Map<Player, Integer> GetPassData() { return passData; }
	
	//入力チェック----------------------------------------------------
	public Boolean InputCheck(String input)
	{
		//文字数チェック
		if (input.length() > 2) return false;
		
		//列の範囲チェック
		if ('H' < input.charAt(0) && input.charAt(0) > 'A') return false;
		
		//行の範囲チェック
		if ('8' < input.charAt(1) && input.charAt(1) > '1') return false;
		
		//駒が存在しないかチェック
		if(othelloTable[(int)(input.charAt(1) - '1')][(int)(input.charAt(0) - 'A')] != PieceState.None) return false;
		
		return true;
	}
	
	//入力変換-----------------------------------------------------
	public Vector2 ParseToVector2(String input)
	{
		return new Vector2((int)(input.charAt(1) - '1'), (int)(input.charAt(0) - 'A'));
	}
	
	//方向データ-----------------------------------------------------
	public record Vector2(int x, int y) {}
	private Vector2[] vecData = new Vector2[] {
			new Vector2(0, -1),
			new Vector2(1, -1),
			new Vector2(-1, -1),
			new Vector2(1, 1),
			new Vector2(0, 1),
			new Vector2(-1, 1),
			new Vector2(1, 0),
			new Vector2(-1, 0),
	};

	//駒配置----------------------------------------------------------
	private int totalPieceCou = 4;
	private PieceState nowPiece = PieceState.None;
	
	public Boolean SetPiece(Vector2 setVec)
	{
		nowPiece = PieceState.None;
		if(nowPlayer == firstPlayer) nowPiece = PieceState.Black;
		else nowPiece = PieceState.White;
		
		Boolean reverse = false;
		for(int i = 0; i < vecData.length; i++)
		{
			int searchX = setVec.x;
			int searchY = setVec.y;
			Vector2 searchVec = vecData[i];
			for(int j = 0; j < TABLE_SIZE; j++)
			{
				if(InsideArray(searchX += searchVec.x, searchY += searchVec.y))
				{
					if(othelloTable[searchX][searchY] == PieceState.None) break;
					if(othelloTable[searchX][searchY] == nowPiece)
					{
						if(j <= 0) break;
						
						Reverse(new Vector2(searchX, searchY), searchVec, j);
						reverse = true;
						break;
					}	
				}
			}
		}
		
		if (reverse) totalPieceCou++;
		return reverse;
	}
	
	//配列内かどうか----------------------------------------------------
	private Boolean InsideArray(int x, int y)
	{
		Boolean first = 0 < x && x < TABLE_SIZE;
		Boolean secound = 0 < y && y < TABLE_SIZE;
		return first && secound;
	}
	
	private void Reverse(Vector2 searchPos, Vector2 searchVec, int reverseCou)
	{
		int reverseX = searchPos.x;
		int reverseY = searchPos.y;
		Vector2 reverseVec = new Vector2(searchVec.x * -1, searchVec.y * -1);
		for (int i = 0; i <= reverseCou; i++)
		{
			othelloTable[reverseX += reverseVec.x][reverseY += reverseVec.y] = nowPiece;
		}
	}
	
	//終了判定----------------------------------------------------------
	private int whitePieceCou = 0;
	private int blackPieceCou = 0;
	
	public Boolean JudgeFinish()
	{
		if (passData.getOrDefault(nowPlayer, 0) >= 3) 
		{
			SetWinner(nowPlayer);
			return true;
		}
		
		if (totalPieceCou >= TABLE_SIZE * TABLE_SIZE)
			{
				System.out.println(totalPieceCou);
				
				for (var raw : othelloTable)
				{
					for (var a : raw)
					{
						if(a == PieceState.Black) blackPieceCou++;
						else whitePieceCou++;
					}
				}
				
				if(blackPieceCou == whitePieceCou) winner = Player.None;
				else if(blackPieceCou > whitePieceCou) winner = Player.Player1;
				else if(blackPieceCou < whitePieceCou) winner = Player.Player2;
				
				return true;
			}
		
		return false;
	}
	
	//勝者決定---------------------------------------------------------
	private Player winner = Player.None;
	
	public void SetWinner(Player nowPlayer)
	{
		if(nowPlayer == Player.Player1) winner = Player.Player2;
		else if (nowPlayer == Player.Player2) winner = Player.Player1;
	}
	
	//勝者取得-------------------------
	public Player GetWinner() { return winner; }
}
