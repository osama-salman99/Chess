package chess.pieces;

import chess.helper.ChessPosition;
import chess.helper.PieceDragListener;
import functionailties.DraggableImageView;
import javafx.scene.image.Image;

public class Pawn extends Piece {
	private static final Image BLACK_SYMBOL;
	private static final Image WHITE_SYMBOL;

	static {
		BLACK_SYMBOL = new Image("/images/pieces/black_pawn.png");
		WHITE_SYMBOL = new Image("/images/pieces/white_pawn.png");
	}

	private DraggableImageView symbol;

	public Pawn(PieceColor color, ChessPosition chessPosition) {
		super(color, chessPosition);
		if (color == PieceColor.BLACK) {
			symbol = new DraggableImageView(BLACK_SYMBOL);
		} else {
			symbol = new DraggableImageView(WHITE_SYMBOL);
		}
	}

	public DraggableImageView getImageView() {
		return symbol = symbol.copy();
	}

	@Override
	public boolean validMovement(ChessPosition nextChessPosition) {
		int fileDifference = Math.abs(nextChessPosition.getFile() - position.getFile());
		int rankDifference = nextChessPosition.getRank() - position.getRank();
		if (color == PieceColor.WHITE) {
			return fileDifference >= 0 && fileDifference <= 1
					&& (rankDifference == 1);
		} else {
			return fileDifference >= 0 && fileDifference <= 1
					&& (rankDifference == -1);
		}
	}

	public Piece promote(Promotion promotion, ChessPosition position) {
		Piece piece;
		switch (promotion) {
			case Queen:
				piece = new Queen(color, position);
				break;
			case Rook:
				piece = new Rook(color, position);
				break;
			case Knight:
				piece = new Knight(color, position);
				break;
			case Bishop:
				piece = new Bishop(color, position);
				break;
			default:
				throw new IllegalArgumentException("Unknown argument " + promotion);
		}
		PieceDragListener dragListener = (PieceDragListener) piece.getDragListener();
		dragListener.setPiece(piece);
		piece.setDragListener(dragListener);
		return piece;
	}

	public enum Promotion {
		Queen, Rook, Knight, Bishop
	}
}
