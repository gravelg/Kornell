package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class WizardSideItemLabel extends Label {
	
	private static WizardSideItemLabel dragging = null;
	final boolean droppable;

	public WizardSideItemLabel(String text, boolean draggable, boolean droppable) {
		super(text);
		if (draggable) {
			initDrag();
		}
		if (droppable) {
			initDrop();
		}
		this.droppable = droppable;

		if (droppable) {
			addStyleName("droppable");
		} else if (draggable) {
			addStyleName("draggable");
		}
	}

	private void initDrag() {
		getElement().setDraggable(Element.DRAGGABLE_TRUE);

		addDragStartHandler(new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {
				// Remember what's being dragged
				dragging = WizardSideItemLabel.this;
				event.setData("ID", "UniqueIdentifier");
				event.getDataTransfer().setDragImage(getElement(), 10, 10);
			}
		});
	}

	private void initDrop() {
		addDomHandler(new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {
				addStyleName("dropping");
			}
		}, DragOverEvent.getType());

		addDomHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				removeStyleName("dropping");
			}
		}, DragLeaveEvent.getType());

		addDomHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				if (dragging != null) {
					TreeItem target = null;
					TreeItem source = null;
					Tree tree = (Tree) WizardSideItemLabel.this.getParent();
					List<TreeItem> treeItems = new ArrayList<TreeItem>();
					treeItems.add(tree.getItem(0));
					while (!treeItems.isEmpty()) {
						TreeItem item = treeItems.remove(0);
						for (int i = 0; i < item.getChildCount(); i++) {
							treeItems.add(item.getChild(i));
						}

						Widget widget = item.getWidget();
						if (widget != null) {
							if (widget == dragging) {
								source = item;
								if (target != null) {
									break;
								}
							}
							if (widget == WizardSideItemLabel.this) {
								target = item;
								widget.removeStyleName("dropping");
								if (source != null) {
									break;
								}
							}
						}
					}

					if (source != null && target != null) {
						TreeItem testTarget = target;
						while (testTarget != null) {
							if (testTarget == source) {
								return;
							}
							testTarget = testTarget.getParentItem();
						}
						target.addItem(source);
						target.setState(true);
					}
					dragging = null;
				}
			}
		}, DropEvent.getType());
	}
}
