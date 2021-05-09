package chess.pieces;

import chess.helper.ChessPosition;
import functionailties.DraggableImageView;
import javafx.scene.image.Image;

public class King extends Piece {
	private static final Image BLACK_SYMBOL;
	private static final Image WHITE_SYMBOL;

	static {
		BLACK_SYMBOL = new Image("/images/pieces/black_king.png");
		WHITE_SYMBOL = new Image("/images/pieces/white_king.png");
	}

	private DraggableImageView symbol;

	public King(PieceColor color, ChessPosition position) {
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
		int fileDifference = Math.abs(destinationPosition.getFile() - position.getFile());
		int rankDifference = Math.abs(destinationPosition.getRank() - position.getRank());
		if (fileDifference == 0 && rankDifference == 0) {
			return false;
		}
		return fileDifference <= 1 && rankDifference <= 1;
	}

	@Override
	public Piece copy() {
		return new King(color, position);
	}
}
