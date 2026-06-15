import java.text.Normalizer;
import java.util.Scanner;

import models.Models.PieceState;
import models.Models.Player;

public class Main {

	public static void main(String[] args) {
		Service service = new Service();
		
		PieceState[][] table = service.GanerateTable();
		
		Player firstPlayer = service.ChooseFirstPlayer();
		System.out.println(firstPlayer + " が 先手(黒) です");
		System.out.println();
		
		Scanner sc = new Scanner(System.in);
		
		for (int i = 0; i < 100; i++)
		{
			if (service.JudgeFinish()) break;
			
			Player nowPlayer = service.GetNowPlayer();
			System.out.println(nowPlayer + "の手番");
			
			ShowTable(table);
			System.out.println();
			
			System.out.print("駒配置場所(縦横)：");
			String inputStr = Normalizer.normalize(sc.next(), Normalizer.Form.NFKC);
			System.out.println();
			
			if (inputStr.equals("PASS"))
			{
				service.PASS(nowPlayer);
				System.out.println("PLAYER1" + service.GetPassData().getOrDefault(Player.Player1, 0) + " PLAYER2" + service.GetPassData().getOrDefault(Player.Player2, 0));
				service.SwitchPlayer();
				continue;
			}
			
			if (service.InputCheck(inputStr))
				{
					System.out.println(inputStr);
					service.SetPiece();
				}
			else System.out.println("error");
			
			service.SwitchPlayer();
		}
		System.out.println(service.GetWinner() + "の勝利!!");
		
		sc.close();
	}
	
	//盤面表示---------------------------------------------------------------------------
	private static void ShowTable(PieceState[][] table)
	{
		System.out.println(" \tA\tB\tC\tD\tE\tF\tG\tH");
		for (int i = 0; i < table.length; i++)
		{
			System.out.print(i + 1 + "\t");
			for (int j = 0; j < table.length; j++)
			{
				switch (table[i][j]) {
				case PieceState.White:
					System.out.print("〇");
					break;
					
				case PieceState.Black:
					System.out.print("●");
					break;
					
				case PieceState.None:
					System.out.print("--");
					break;
					
				default:
					System.out.print("✕");
					break;
				}
				
				System.out.print("\t");
			}
			System.out.println();
		}
	}
}
