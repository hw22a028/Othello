import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import models.Models;
import models.Models.PieceState;
import models.Models.Player;
import models.Models.Vector2;

public class Service {
	private OthelloAI AI = new OthelloAI();

	//盤面作成--------------------------------------------------------
	private PieceState[][] othelloTable = new PieceState[Models.TABLE_SIZE][Models.TABLE_SIZE];
	
	public PieceState[][] GanerateTable()
	{
		for (int i = 0; i < Models.TABLE_SIZE; i++) 
		{
			for (int j = 0; j < Models.TABLE_SIZE; j++) 
			{
				Boolean onWhite = (i == Models.TABLE_SIZE / 2 - 1 && j == Models.TABLE_SIZE / 2 - 1) || (i == Models.TABLE_SIZE / 2 && j == Models.TABLE_SIZE / 2);
				Boolean onBlack = (i == Models.TABLE_SIZE / 2 && j == Models.TABLE_SIZE / 2 - 1) || (i == Models.TABLE_SIZE / 2 - 1 && j == Models.TABLE_SIZE / 2);
				
				if (onWhite)  othelloTable[i][j] = PieceState.White;
				else if (onBlack) othelloTable[i][j] = PieceState.Black;
				else othelloTable[i][j] = PieceState.None;
			}
		}
		
		SetValidSelects(new Vector2(Models.TABLE_SIZE / 2 - 1, Models.TABLE_SIZE / 2 - 1));
		SetValidSelects(new Vector2(Models.TABLE_SIZE / 2, Models.TABLE_SIZE / 2));
		SetValidSelects(new Vector2(Models.TABLE_SIZE / 2, Models.TABLE_SIZE / 2 - 1));
		SetValidSelects(new Vector2(Models.TABLE_SIZE / 2 - 1, Models.TABLE_SIZE / 2));
		
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
	
	//先手プレイヤー取得-------------------
	public Player GetFirstPlayer() { return firstPlayer; }
	
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
	
	public void Pass(Player player)
	{
		passData.merge(player, 1, Integer::sum);
	}
	
	//パスデータ取得----------------------
	public Map<Player, Integer> GetPassData() { return passData; }
	
	//入力チェック----------------------------------------------------
	public Boolean InputCheck(String input)
	{
		//文字数チェック
		if (input.length() != 2) return false;
		
		//列の範囲チェック
		if ('H' < input.charAt(0) && input.charAt(0) > 'A') return false;
		
		//行の範囲チェック
		if ('8' < input.charAt(1) && input.charAt(1) > '1') return false;
		
		//駒が存在しないかチェック
		if(PutCheck(ParseToVector2(input))) return false;
		
		return true;
	}
	
	public Boolean PutCheck(Vector2 vec)
	{
		if (othelloTable[vec.x()][vec.y()] != PieceState.None) return false;
		else return true;
	}
	
	//入力変換-----------------------------------------------------
	public Vector2 ParseToVector2(String input)
	{
		return new Vector2((int)(input.charAt(1) - '1'), (int)(input.charAt(0) - 'A'));
	}
	
	public Vector2 IntToVector2(int x, int y)
	{
		return new Vector2(x, y);
	}
	

	//駒配置----------------------------------------------------------
	private Vector2[] vecData = Models.GetVecData();
	private int totalPieceCou = 4;
	private PieceState nowPiece = PieceState.None;
	
	public Boolean SetPiece(Vector2 setVec)
	{
		nowPiece = PieceState.None;
		if (nowPlayer == firstPlayer) nowPiece = PieceState.Black;
		else nowPiece = PieceState.White;
		
		Boolean reverse = false;
		for (int i = 0; i < vecData.length; i++)
		{
			int searchX = setVec.x();
			int searchY = setVec.y();
			Vector2 searchVec = vecData[i];
			for (int j = 0; j < Models.TABLE_SIZE; j++)
			{
				if (Models.InsideArray(searchX += searchVec.x(), searchY += searchVec.y()))
				{
					if (othelloTable[searchX][searchY] == PieceState.None) break;
					if (othelloTable[searchX][searchY] == nowPiece)
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
	
	//ひっくり返す-------------------------------------------------------
	private void Reverse(Vector2 searchPos, Vector2 searchVec, int reverseCou)
	{
		int reverseX = searchPos.x();
		int reverseY = searchPos.y();
		Vector2 reverseVec = new Vector2(searchVec.x() * -1, searchVec.y() * -1);
		for (int i = 0; i <= reverseCou; i++)
		{
			reverseX += reverseVec.x();
			reverseY += reverseVec.y();
			othelloTable[reverseX][reverseY] = nowPiece;
			
			Vector2 reversePos = new Vector2(reverseX, reverseY);
			if (validSelects.contains(reversePos)) validSelects.remove(validSelects.indexOf(reversePos));
			SetValidSelects(reversePos);
		}
	}
	
	//駒を置けるマスリスト------------------------------------------------
	private List<Vector2> validSelects = new ArrayList<Vector2>();
	
	private void SetValidSelects(Vector2 putSquare)
	{
		for (var vec : vecData)
		{
			int x = putSquare.x() + vec.x();
			int y = putSquare.y() + vec.y();
			
			Boolean isValid = false;
			if (Models.InsideArray(x, y)) isValid = othelloTable[x][y] == PieceState.None && !validSelects.contains(new Vector2(x, y));
			if (isValid) validSelects.add(new Vector2(x, y));
		}
	}
	
	//AI実行-------------------------------------------------------------
	public void AIPlay()
	{
		if (AI.Play(othelloTable, validSelects, nowPiece))
		{
			SwitchPlayer();
			totalPieceCou++;
		}
		else Pass(nowPlayer);
	}
	
	//終了判定----------------------------------------------------------
	private int whitePieceCou = 0;
	private int blackPieceCou = 0;
	private Player winner = Player.None;
	
	public Boolean JudgeFinish()
	{
		if (passData.getOrDefault(Player.Player1, 0) >= Models.PASS_MAX)
			{
				winner = Player.Player2;
				return true;
			}
		if (passData.getOrDefault(Player.Player2, 0) >= Models.PASS_MAX)
			{
				winner = Player.Player1;
				return true;
			}
		
		if (totalPieceCou >= Models.TABLE_SIZE * Models.TABLE_SIZE)
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
				
				if (blackPieceCou == whitePieceCou) winner = Player.None;
				else if (blackPieceCou > whitePieceCou) winner = Player.Player1;
				else if (blackPieceCou < whitePieceCou) winner = Player.Player2;
				
				return true;
			}
		
		return false;
	}
	
	//勝者取得-------------------------
	public Player GetWinner() { return winner; }
}
