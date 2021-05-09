package functionailties;

import chess.helper.PieceDragListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DraggableImageView extends ImageView {
	private final MouseEventHandler mouseEventHandler;
	private final Image image;

	public DraggableImageView(Image image) {
		super(image);
		this.image = image;
		this.mouseEventHandler = new MouseEventHandler(this);
	}

	public PieceDragListener getDragListener() {
		return (PieceDragListener) mouseEventHandler.getDragListener();
	}

	public void setDragListener(chess.helper.PieceDragListener dragListener) {
		mouseEventHandler.setDragListener(dragListener);
	}

	public DraggableImageView copy() {
		DraggableImageView draggableImageView = new DraggableImageView(image);
		draggableImageView.setDragListener(getDragListener());
		return draggableImageView;
	}
}
