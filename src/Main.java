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
			String inputStr = Normalizer.normalize(sc.next(), Normalizer.Form.NFKC);
			ShowTable(table);
		}
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
