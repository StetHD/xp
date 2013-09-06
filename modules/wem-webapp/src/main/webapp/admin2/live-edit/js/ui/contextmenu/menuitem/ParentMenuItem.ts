module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class ParentMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;

            this.init();
        }

        init():void {
            var $button = this.createButton({
                id: 'live-edit-button-parent',
                text: 'Select Parent',
                handler: (event) => {
                    event.stopPropagation();

                    // Fixme: extract to method
                    var parentElement:JQuery = this.menu.selectedComponent.getElement().parents('[data-live-edit-type]');

                    if (parentElement && parentElement.length > 0) {
                        var parentComponent = new LiveEdit.component.Component($(parentElement[0]));

                        $(window).trigger('deselectComponent.liveEdit');

                        LiveEdit.Selection.setSelectionOnElement(parentComponent.getElement());
                        $(window).trigger('selectComponent.liveEdit', [parentComponent]);

                        this.scrollComponentIntoView(parentComponent);
                    }
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.buttons.push(this);
        }

        scrollComponentIntoView(component:LiveEdit.component.Component):void {
            var dimensions:ElementDimensions = component.getElementDimensions();
            if (dimensions.top <= window.pageYOffset) {
                $('html, body').animate({scrollTop: dimensions.top - 10}, 200);
            }
        }
    }
}