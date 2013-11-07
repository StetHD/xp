module app_view {

    export class EditSchemaAction extends api_ui.Action {

        constructor(panel:api_app_view.ItemViewPanel<api_schema.Schema>) {
            super("Edit");

            this.addExecutionListener(() => {
                new app_browse.EditSchemaEvent([panel.getItem().getModel()]).fire();
            });
        }
    }

    export class DeleteSchemaAction extends api_ui.Action {

        constructor(panel:api_app_view.ItemViewPanel<api_schema.Schema>) {
            super("Delete", "mod+del");
            this.addExecutionListener(() => {
                api_ui_dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this schema?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        panel.close();

                        var schema = panel.getItem().getModel();

                        var responsePromise;
                        if (schema.getSchemaKind() == api_schema.SchemaKind.CONTENT_TYPE) {
                            responsePromise = new api_schema_content.DeleteContentTypeRequest()
                                .addQualifiedName(schema.getName()).send();
                        } else if (schema.getSchemaKind() == api_schema.SchemaKind.RELATIONSHIP_TYPE) {
                            responsePromise = new api_schema_relationshiptype.DeleteRelationshipTypeRequest()
                                .addQualifiedName(schema.getName()).send();
                        } else if (schema.getSchemaKind() == api_schema.SchemaKind.MIXIN) {
                            responsePromise = new api_schema_mixin.DeleteMixinRequest()
                                .addQualifiedName(schema.getName()).send();
                        }

                        if (responsePromise) {
                            responsePromise.done((jsonResponse:api_rest.JsonResponse<api_schema.SchemaDeleteJson>) => {
                                var json = jsonResponse.getResult();

                                if (json.successes && json.successes.length > 0) {
                                    api_notify.showFeedback('Content [' + json.successes[0].name + '] deleted!');
                                    new api_schema.SchemaDeletedEvent([schema]).fire();
                                }
                            });
                        }

                    }).open();
            });
        }
    }

    export class CloseSchemaAction extends api_ui.Action {

        constructor(panel:api_ui.Panel, checkCanRemovePanel:boolean = true) {
            super("Close");

            this.addExecutionListener(() => {
                new app_browse.CloseSchemaEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }

}
