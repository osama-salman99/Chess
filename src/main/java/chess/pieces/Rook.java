package chess.pieces;

import chess.helper.ChessPosition;
import functionailties.DraggableImageView;
import javafx.scene.image.Image;

public class Rook extends Piece {
	private static final Image BLACK_SYMBOL;
	private static final Image WHITE_SYMBOL;

	static {
		BLACK_SYMBOL = new Image("/images/pieces/black_rook.png");
		WHITE_SYMBOL = new Image("/images/pieces/white_rook.png");
	}

	private DraggableImageView symbol;

	public Rook(PieceColor color, ChessPosition position) {
		super(color, position);
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
	public boolean validMovement(ChessPosition destinationPosition) {
		return false;
	}
}
