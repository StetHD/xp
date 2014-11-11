module api.app.browse {

    export class BrowseItemsSelectionPanel<M extends api.Equitable> extends api.ui.panel.Panel {

        private deselectedListeners: {(event: ItemDeselectedEvent<M>):void}[] = [];
        private items: BrowseItem<M>[] = [];
        private selectionItems: SelectionItem<M>[] = [];
        private messageForNoSelection = "You are wasting this space - select something!";

        constructor() {
            super("items-selection-panel");
            this.getEl().addClass('no-selection').setInnerHtml(this.messageForNoSelection);
        }

        setItems(items: BrowseItem<M>[]) {
            var itemsToRemove = this.items.filter((item: BrowseItem<M>) => {
                for (var i = 0; i < items.length; i++) {
                    if (item.getPath() == items[i].getPath()) {
                        return false;
                    }
                }
                return true;
            });
            itemsToRemove.forEach((item: BrowseItem<M>) => {
                this.removeItem(item);
            });

            items.forEach((item: BrowseItem<M>) => {
                this.addItem(item);
            });
        }

        addItem(item: BrowseItem<M>) {
            var index = this.indexOf(item);
            if (index >= 0) {
                // item already exist
                var currentItem = this.items[index];
                if (!currentItem.equals(item)) {
                    // update current item
                    this.items[index] = item;
                }
                return;
            }

            if (this.items.length == 0) {
                this.removeClass('no-selection');
                this.removeChildren();
            }

            var removeCallback = () => {
                this.removeItem(item);
            };
            var selectionItem = new SelectionItem(item, removeCallback);

            this.appendChild(selectionItem);
            this.selectionItems.push(selectionItem);
            this.items.push(item);
        }

        removeItem(item: BrowseItem<M>) {
            var index = this.indexOf(item);
            if (index < 0) {
                return;
            }

            this.selectionItems[index].remove();
            this.selectionItems.splice(index, 1);
            this.items.splice(index, 1);

            if (this.items.length == 0) {
                this.getEl().addClass('no-selection').setInnerHtml(this.messageForNoSelection);
            }

            this.notifyDeselected(item);
        }

        getItems(): BrowseItem<M>[] {
            return this.items;
        }

        private indexOf(item: BrowseItem<M>): number {
            for (var i = 0; i < this.items.length; i++) {
                if (item.getPath() == this.items[i].getPath()) {
                    return i;
                }
            }
            return -1;
        }

        onDeselected(listener: (event: ItemDeselectedEvent<M>)=>void) {
            this.deselectedListeners.push(listener);
        }

        unDeselected(listener: (event: ItemDeselectedEvent<M>)=>void) {
            this.deselectedListeners = this.deselectedListeners.filter((currentListener: (event: ItemDeselectedEvent<M>)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyDeselected(item: BrowseItem<M>) {
            this.deselectedListeners.forEach((listener: (event: ItemDeselectedEvent<M>)=>void) => {
                listener.call(this, new ItemDeselectedEvent(item));
            });
        }

    }

    export class SelectionItem<M extends api.Equitable> extends NamesAndIconView {

        private browseItem: api.app.browse.BrowseItem<M>;

        constructor(browseItem: BrowseItem<M>, removeCallback?: () => void) {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small);

            super(namesAndIconView);
            this.browseItem = browseItem;
            if (this.browseItem.getIconUrl()) {
                this.setIconUrl(this.browseItem.getIconUrl());
            } else {
                this.setIconClass(this.browseItem.getIconClass());
            }

            this.setMainName(this.browseItem.getDisplayName());
            this.setSubName(this.browseItem.getPath());
            this.addRemoveButton(removeCallback);
        }

        private addRemoveButton(callback?: () => void) {
            var removeEl = new api.dom.DivEl("icon remove");
            removeEl.onClicked((event: MouseEvent) => {
                if (callback) {
                    callback();
                }
            });
            this.appendChild(removeEl);
        }

        getBrowseItem(): BrowseItem<M> {
            return this.browseItem;
        }
    }

}
