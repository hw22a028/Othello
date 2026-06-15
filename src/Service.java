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
				Boolean onWhite = (i == TABLE_SIZE / 2 - 1 && j == TABLE_SIZE / 2 - 1) || (i == TABLE_SIZE / 2 && j == TABLE_SIZE / 2);
				Boolean onBlack = (i == TABLE_SIZE / 2 && j == TABLE_SIZE / 2 - 1) || (i == TABLE_SIZE / 2 - 1 && j == TABLE_SIZE / 2);
				
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
		if(othelloTable[(int)(input.charAt(0) - 'A')][(int)(input.charAt(1) - '0') - 1] != PieceState.None) return false;
		
		return true;
	}
	
	//駒配置----------------------------------------------------------
	private int totalPieceCou = 4;
	private int whitePieceCou = 2;
	private int blackPieceCou = 2;
	
	public void SetPiece()
	{
		if(nowPlayer == firstPlayer) blackPieceCou++;
		else whitePieceCou++;
		totalPieceCou++;
	}
	
	//終了判定----------------------------------------------------------
	public Boolean JudgeFinish()
	{
		if (passData.getOrDefault(nowPlayer, 0) >= 3) 
		{
			SetWinner(nowPlayer);
			return true;
		}
		
//		if (pieceCou >= othelloTable.length * othelloTable[0].length) return true;
		if (totalPieceCou >= 5) 
			{
				SetWinner(nowPlayer);
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
