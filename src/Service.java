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
	
	//現在手番のプレイヤー取得----------------------------
	public Player GetNowPlayer() { return nowPlayer; }
	
	//手番切り替え-----------------------------------------------------
	public void SwitchPlayer()
	{
		if (nowPlayer == Player.Player1) nowPlayer = Player.Player2;
		else nowPlayer = Player.Player1;
	}
	
	//入力チェック----------------------------------------------------
	public Boolean InputCheck(String input)
	{
		return true;
	}
}
