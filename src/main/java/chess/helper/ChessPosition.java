package chess.helper;

public class ChessPosition {
	private static final char[] FILES = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
	private static final char[] RANKS = new char[]{'1', '2', '3', '4', '5', '6', '7', '8'};
	private final int file;
	private final int rank;

	public ChessPosition(int file, int rank) {
		file--;
		rank--;
		if (file < 0 || file > 7
				|| rank < 0 || rank > 7) {
			throw new IllegalArgumentException("File and rank must be between 1 and 8");
		}
		this.file = file;
		this.rank = rank;
	}

	public int getFile() {
		return file + 1;
	}

	public int getRank() {
		return rank + 1;
	}

	public String getChessNotation() {
		return FILES[file] + "" + RANKS[rank];
	}

	@Override
	public String toString() {
		return getChessNotation();
	}
}
