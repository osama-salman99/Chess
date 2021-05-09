package functionailties;

import javafx.scene.Node;

public interface DragListener {
	void accept(Node node, MouseEventHandler.DragEvent dragEvent);
}
