import java.text.Normalizer;
import java.util.Scanner;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Models.PieceState;
import models.Models.Player;

public class Main extends Application {
	private final int HIGHT = 500;
	private final int WIDTH = 500;
	final int TABLE_SIZE = 300;
	
	private PieceState[][] table = new PieceState[9][9];
	
	public static void main(String[] args) 
	{
		final Boolean GUI = true;
		if (GUI) launch(args);
		else CUI();
	}
	
	public void start(Stage primaryStage)
	{
		Service service = new Service();
		table = service.GanerateTable();
		
		primaryStage.setScene(MainScene(service, primaryStage));
		
		Rerun(service, primaryStage);
		
		primaryStage.setTitle("BowringScore");
		primaryStage.show();
		
	}
	
	//インゲームシーン---------------------------------------------------------
	private BorderPane mainPane = new BorderPane();
	private Label playerLbl = new Label();
	private GridPane grid = new GridPane();
	private Label passCount1 = new Label();
	private Label passCount2 = new Label();
	
	private Scene MainScene(Service service, Stage stage)
	{
		//手番表示
		playerLbl.setText(String.valueOf(service.ChooseFirstPlayer()));
		playerLbl.setMaxWidth(Double.MAX_VALUE);
		playerLbl.setAlignment(Pos.CENTER);
		playerLbl.setFont(new Font("Roboto Regular", 20));
		
		//オセロ盤表示
		grid.setAlignment(Pos.CENTER);
		grid.setPrefSize(TABLE_SIZE, TABLE_SIZE);
		grid.setMaxHeight(TABLE_SIZE);
		grid.setMaxWidth(TABLE_SIZE);
		
		//パス数表示(Player1)
		passCount1.setText("Player1：" + String.valueOf(service.GetPassData().getOrDefault(Player.Player1, 0)));
		passCount1.setFont(new Font("Roboto Regular", 14));
		
		//パスボタン
		Button passBtn = new Button("PASS");
		passBtn.setOnAction(c -> {
			service.Pass(service.GetNowPlayer());
			service.SwitchPlayer();
			Rerun(service, stage);
		});
		
		//パス数表示(Player2)
		passCount2.setText("Player2：" + String.valueOf(service.GetPassData().getOrDefault(Player.Player2, 0)));
		passCount2.setFont(new Font("Roboto Regular", 14));
		
		HBox passBox = new HBox(50);
		passBox.setAlignment(Pos.CENTER);
		passBox.getChildren().addAll(passCount1, passBtn, passCount2);
		
		VBox centerBox = new VBox(5);
		centerBox.setAlignment(Pos.CENTER);
		centerBox.getChildren().addAll(playerLbl, grid, passBox);
		
		mainPane.setCenter(centerBox);
		
		return new Scene(mainPane, WIDTH, HIGHT);
	}
	
	//画面再描画-------------------------------------------------------------
	private Boolean finish = false;
	private void Rerun( Service service, Stage stage)
	{
		if (service.JudgeFinish())stage.setScene(ResultScene(service, stage));
		
		DispTable(service, stage);
		
		String color = "";
		if (service.GetFirstPlayer() == service.GetNowPlayer()) color = "（黒）";
		else color = "（白）";
		playerLbl.setText(String.valueOf(service.GetNowPlayer()) + color + " の手番");
		
		passCount1.setText("Player1：" + String.valueOf(service.GetPassData().getOrDefault(Player.Player1, 0)));
		passCount2.setText("Player2：" + String.valueOf(service.GetPassData().getOrDefault(Player.Player2, 0)));
		
		mainPane.requestFocus();
	}
	
	//オセロ盤表示-----------------------------------------------------------
	private void DispTable(Service service, Stage stage)
	{
		grid.getChildren().clear();
		for(int i = 0; i < table.length; i++)
		{
			for(int j = 0; j < table.length; j++)
			{
				PieceState piece = table[i][j];
				String str = SetSquareTxt(piece);
				Button btn = new Button(str);
				
				btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
				GridPane.setHgrow(btn, Priority.ALWAYS);
				GridPane.setVgrow(btn, Priority.ALWAYS);
				
				String txtColor = "black";
				if(piece == PieceState.White) txtColor = "white";
				btn.setStyle("-fx-background-color: lightgreen;"
						+ "-fx-text-fill:" + txtColor + ";"
						+ "-fx-border-color: dimgray;"
				);
				
				if(!finish) btn.setOnAction(e -> HandleCellClick(e, service, stage));
				
				grid.add(btn, j, i);
			}
		}
	}
	
	//表示文字設定-------------------------------------------------------------
	private String SetSquareTxt(PieceState state)
	{
		String line = "";
		switch (state)
		{
		case PieceState.None:
			line = "　";
			break;
			
		case PieceState.Black:
		case PieceState.White:
			line = "●";
			break;
			
			
		default:
			line = "✕";
			break;
		}
		return line;
	}
	
	//マス目ボタン押下処理------------------------------------------------------
	private void HandleCellClick(ActionEvent e, Service service, Stage stage) {
		PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
		pause.setOnFinished(event -> {
			service.AIPlay();
			grid.getChildren().clear();
			Rerun(service, stage);
		});
		
		Button clickedBtn = (Button) e.getSource();
		int col = GridPane.getColumnIndex(clickedBtn);
		int raw = GridPane.getRowIndex(clickedBtn);
		
		if(!service.PutCheck(service.IntToVector2(raw, col))) return;
		
		if(service.SetPiece(service.IntToVector2(raw, col)))
		{
			service.SwitchPlayer();
			grid.getChildren().clear();
			Rerun(service, stage);
			
			pause.play();
		}
	}
	
	//リザルトシーン-------------------------------------------------------------
	private Scene ResultScene(Service service, Stage stage)
	{
		finish = true;
		
		Label winnerLbl = new Label(String.valueOf(service.GetWinner()) + "の勝ち!!");
		if(service.GetWinner() == Player.None) winnerLbl.setText("引き分け");
		winnerLbl.setAlignment(Pos.CENTER);
		winnerLbl.setFont(new Font("Roboto Regular", 20));
		
		DispTable(service, stage);
		
		VBox centerBox = new VBox(15);
		centerBox.setAlignment(Pos.CENTER);
		centerBox.getChildren().addAll(winnerLbl, grid);
		
		return new Scene(centerBox, WIDTH, HIGHT);
	}
	
	
	
	//=============================================================================================
	
	
	
	private static void CUI()
	{
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
				service.Pass(nowPlayer);
				System.out.println("PLAYER1" + service.GetPassData().getOrDefault(Player.Player1, 0) + " PLAYER2" + service.GetPassData().getOrDefault(Player.Player2, 0));
				service.SwitchPlayer();
				continue;
			}
			
			if (service.InputCheck(inputStr))
				{
					System.out.println(inputStr);
					if(service.SetPiece(service.ParseToVector2(inputStr)))
					{
						service.SwitchPlayer();
					}
					else System.out.println("無効な設置場所");
				}
			else System.out.println("error");
		}
		ShowTable(table);
		
		Player winner = service.GetWinner();
		if(winner == Player.None) System.out.println("引き分け");
		else System.out.println(service.GetWinner() + "の勝利!!");
		
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
					System.out.print("●");
					break;
					
				case PieceState.Black:
					System.out.print("○");
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
