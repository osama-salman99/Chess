package chess.board;

import chess.exceptions.InvalidFenException;
import chess.helper.ChessPosition;
import chess.helper.PieceDragListener;
import chess.pieces.*;
import functionailties.DraggableImageView;
import functionailties.MouseEventHandler.DragEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class Board {
	@SuppressWarnings("SpellCheckingInspection")
	private static final String NEW_GAME_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
	private final ArrayList<Piece> pieces;
	private GridPane boardGridPane;
	private Piece.PieceColor turn;

	private Board() {
		pieces = new ArrayList<>();
		turn = Piece.PieceColor.WHITE;
	}

	public static Board createChessBoard() {
		try {
			return createChessBoard(NEW_GAME_FEN);
		} catch (InvalidFenException e) {
			throw new RuntimeException("Internal new-game FEN is invalid or is not being parsed correctly");
		}
	}

	public static Board createChessBoard(String fen) throws InvalidFenException {
		final Board board = new Board();
		ArrayList<Piece> pieces = board.pieces;
		int rank = 8;
		int file = 1;
		for (char c : fen.toCharArray()) {
			if (c == '/') {
				rank--;
				file = 1;
				continue;
			}
			if (file > 8 || rank < 1) {
				throw new InvalidFenException();
			}
			if (Character.isDigit(c)) {
				file += Character.getNumericValue(c);
				continue;
			}
			if (!Character.isAlphabetic(c)) {
				throw new InvalidFenException();
			}
			Piece.PieceColor color = Character.isLowerCase(c) ? Piece.PieceColor.BLACK : Piece.PieceColor.WHITE;
			c = Character.toLowerCase(c);
			Piece piece;
			ChessPosition position = new ChessPosition(file, rank);
			switch (c) {
				case 'b':
					piece = new Bishop(color, position);
					break;
				case 'k':
					piece = new King(color, position);
					break;
				case 'n':
					piece = new Knight(color, position);
					break;
				case 'p':
					piece = new Pawn(color, position);
					break;
				case 'q':
					piece = new Queen(color, position);
					break;
				case 'r':
					piece = new Rook(color, position);
					break;
				default:
					throw new InvalidFenException("Unknown character provided: " + c);
			}
			piece.setDragListener(new PieceDragListener(piece) {
				@Override
				public void accept(Node node, DragEvent dragEvent) {
					Bounds bounds = node.localToScene(node.getBoundsInLocal());
					Point2D center = new Point2D((bounds.getMinX() + bounds.getMaxX()) / 2,
							(bounds.getMinY() + bounds.getMaxY()) / 2);
					double sideLength = node.getBoundsInLocal().getHeight();
					int x = (int) (center.getX() / sideLength);
					int y = (int) (center.getY() / sideLength);
					ChessPosition destinationPosition = new ChessPosition(x + 1, 8 - y);
					if (dragEvent.equals(DragEvent.DragEnd)) {
						board.makeMove(getPiece(), destinationPosition);
					}
				}
			});
			pieces.add(piece);
			file++;
		}
		return board;
	}

	public void setBoardGridPane(GridPane boardGridPane) {
		this.boardGridPane = boardGridPane;
		refreshBoard();
	}

	public void makeMove(Piece piece, ChessPosition destinationPosition) {
		if (isLegal(piece, destinationPosition)) {
			pieces.remove(getOccupyingPiece(destinationPosition, pieces));
			piece.setPosition(destinationPosition);
			turn = turn == Piece.PieceColor.WHITE ? Piece.PieceColor.BLACK : Piece.PieceColor.WHITE;
			if (piece instanceof Pawn) {
				int destinationRank = destinationPosition.getRank();
				if (destinationRank == 8 || destinationRank == 1) {
					pieces.remove(piece);
					// TODO: Ask user for promotion piece
					pieces.add(((Pawn) piece).promote(Pawn.Promotion.Queen, destinationPosition));
				}
			}
		}
		refreshBoard();
	}

	public boolean isLegal(Piece piece, ChessPosition destinationPosition) {
		if (piece.getColor() != turn || !piece.validMovement(destinationPosition)) {
			return false;
		}
		Piece occupyingPiece = getOccupyingPiece(destinationPosition, pieces);
		if (piece instanceof Pawn) {
			ChessPosition position = piece.getPosition();
			int rank = position.getRank();
			int destinationRank = destinationPosition.getRank();
			int rankDifference = destinationRank - rank;
			int fileDifference = Math.abs(destinationPosition.getFile() - position.getFile());
			if (piece.getColor() == Piece.PieceColor.BLACK) {
				rank = 9 - position.getRank();
				rankDifference *= -1;
			}
			if (rankDifference == 2) {
				if (rank != 2 || fileDifference != 0) {
					System.out.println("Invalid two-square pawn move");
					return false;
				}
			}
			if (fileDifference == 0) {
				if (occupyingPiece != null) {
					System.out.println("Square is occupied");
					return false;
				}
			} else {
				if (occupyingPiece == null) {
					System.out.println("No piece to take");
					return false;
				}
			}
		}
		// Check if player is taking their own pieces
		if (occupyingPiece != null && occupyingPiece.getColor() == turn) {
			System.out.println("Player is taking their own piece");
			return false;
		}
		if (!(piece instanceof Knight)) {
			if (!emptyPath(piece.getPosition(), destinationPosition, pieces)) {
				System.out.println("Path is not empty");
				return false;
			}
		}
		return !kingInCheck(piece, destinationPosition);
	}

	public void refreshBoard() {
		boardGridPane.getChildren().clear();
		double sideLength = boardGridPane.getPrefHeight() / 8;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Pane emptyCell = new Pane();
				emptyCell.minHeightProperty().set(sideLength);
				emptyCell.minWidthProperty().set(sideLength);
				boardGridPane.add(emptyCell, x, y);
			}
		}
		for (Piece piece : pieces) {
			DraggableImageView pieceImageView = piece.getImageView();
			pieceImageView.setFitWidth(sideLength);
			pieceImageView.setFitHeight(sideLength);
			ChessPosition position = piece.getPosition();
			boardGridPane.add(pieceImageView, (position.getFile() - 1), 7 - (position.getRank() - 1));
		}
	}

	public Piece getOccupyingPiece(ChessPosition position, List<Piece> pieces) {
		for (Piece piece : pieces) {
			if (piece.getPosition().equals(position)) {
				return piece;
			}
		}
		return null;
	}

	private boolean emptyPath(ChessPosition startingPosition, ChessPosition destinationPosition, List<Piece> pieces) {
		int rankDifference = destinationPosition.getRank() - startingPosition.getRank();
		int fileDifference = destinationPosition.getFile() - startingPosition.getFile();
		if (rankDifference == 0) {
			// Horizontal
			int rank = startingPosition.getRank();
			int minFile = Math.min(startingPosition.getFile(), destinationPosition.getFile());
			int maxFile = Math.max(startingPosition.getFile(), destinationPosition.getFile());
			for (int file = minFile + 1; file < maxFile; file++) {
				if (getOccupyingPiece(new ChessPosition(file, rank), pieces) != null) {
					return false;
				}
			}
			return true;
		} else if (fileDifference == 0) {
			// Vertical
			int file = startingPosition.getFile();
			int minRank = Math.min(startingPosition.getRank(), destinationPosition.getRank());
			int maxRank = Math.max(startingPosition.getRank(), destinationPosition.getRank());
			for (int rank = minRank + 1; rank < maxRank; rank++) {
				if (getOccupyingPiece(new ChessPosition(file, rank), pieces) != null) {
					return false;
				}
			}
			return true;
		} else {
			// Diagonal
			int difference = Math.abs(rankDifference);
			int rankIncrement = rankDifference > 0 ? 1 : -1;
			int fileIncrement = fileDifference > 0 ? 1 : -1;
			for (int i = 1, file = startingPosition.getFile() + fileIncrement, rank = startingPosition.getRank() + rankIncrement;
				 i < difference - 1;
				 i++, file += fileIncrement, rank += rankIncrement) {
				if (getOccupyingPiece(new ChessPosition(file, rank), pieces) != null) {
					return false;
				}
			}
			return true;
		}
	}

	public boolean kingInCheck(Piece piece, ChessPosition destinationPosition) {
		List<Piece> mockPieces = new ArrayList<>(pieces);
		Piece occupyingPiece = getOccupyingPiece(destinationPosition, mockPieces);
		mockPieces.remove(occupyingPiece);
		mockPieces.remove(piece);
		Piece copyPiece = piece.copy();
		copyPiece.setPosition(destinationPosition);
		mockPieces.add(copyPiece);
		King king = null;
		for (Piece mockPiece : mockPieces) {
			if (mockPiece.getColor() == turn && mockPiece instanceof King) {
				king = (King) mockPiece;
			}
		}
		if (king == null) {
			throw new RuntimeException("No king found");
		}
		for (Piece mockPiece : mockPieces) {
			if (mockPiece.getColor() == turn) {
				continue;
			}
			if (mockPiece instanceof King || mockPiece instanceof Knight) {
				if (mockPiece.validMovement(king.getPosition())) {
					if (mockPiece instanceof Knight) {
						System.out.println("King in check by a knight");
					} else {
						System.out.println("King in check by the other king");
					}
					return true;
				}
			} else if (mockPiece instanceof Pawn) {
				int takeFileRight = mockPiece.getPosition().getFile() + 1;
				int takeFileLeft = mockPiece.getPosition().getFile() - 1;
				int takeRank;
				int kingFile = king.getPosition().getFile();
				if (mockPiece.getColor() == Piece.PieceColor.WHITE) {
					takeRank = mockPiece.getPosition().getRank() + 1;
				} else {
					takeRank = mockPiece.getPosition().getRank() - 1;
				}
				if (king.getPosition().getRank() == takeRank) {
					if (kingFile == takeFileRight || kingFile == takeFileLeft) {
						System.out.println("King in check by pawn");
						return true;
					}
				}
			} else {
				if (mockPiece.validMovement(king.getPosition())
						&& emptyPath(mockPiece.getPosition(), king.getPosition(), mockPieces)) {
					System.out.println("King in check by " + mockPiece.getClass().getName());
					return true;
				}
			}
		}
		return false;
	}
}
