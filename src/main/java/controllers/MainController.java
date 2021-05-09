package controllers;

import chess.board.Board;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends Controller {
	public GridPane boardGridPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		// Board background
		Image backgroundImage = new Image("/images/boards/board_background.png");
		BackgroundSize backgroundSize = new BackgroundSize(
				boardGridPane.getPrefWidth(),
				boardGridPane.getPrefHeight(),
				true,
				true,
				true,
				true);
		boardGridPane.setBackground(new Background(
				new BackgroundImage(
						backgroundImage, null, null, null, backgroundSize)));

		Board board = Board.createChessBoard();
		board.setBoardGridPane(boardGridPane);
	}
}
