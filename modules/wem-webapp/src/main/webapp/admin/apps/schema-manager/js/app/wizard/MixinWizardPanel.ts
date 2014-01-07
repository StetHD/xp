module app.wizard {

    export class MixinWizardPanel extends api.app.wizard.WizardPanel<api.schema.mixin.Mixin> {

        public static NEW_WIZARD_HEADER = "new mixin";

        private formIcon: api.app.wizard.FormIcon;

        private mixinIcon: api.icon.Icon;

        private mixinWizardHeader: api.app.wizard.WizardHeaderWithName;

        private persistedConfig: string;

        private mixinForm: MixinForm;

        constructor(tabId: api.app.AppBarTabId, persistedMixin: api.schema.mixin.Mixin, callback: (wizard: MixinWizardPanel) => void) {

            this.mixinWizardHeader = new api.app.wizard.WizardHeaderWithName();
            this.formIcon = new api.app.wizard.FormIcon(new api.schema.mixin.MixinIconUrlResolver().resolveDefault(),
                "Click to upload icon", api.util.getRestUri("blob/upload"));

            this.formIcon.addListener({
                onUploadStarted: null,
                onUploadFinished: (uploadItem: api.ui.UploadItem) => {
                    this.mixinIcon = new api.icon.IconBuilder().
                        setBlobKey(uploadItem.getBlobKey()).setMimeType(uploadItem.getMimeType()).build();
                    this.formIcon.setSrc(api.util.getRestUri('blob/' + this.mixinIcon.getBlobKey()));
                }
            });
            var actions = new MixinWizardActions(this);

            var mainToolbar = new MixinWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction()
            });

            this.mixinWizardHeader.setName(MixinWizardPanel.NEW_WIZARD_HEADER);

            this.mixinForm = new MixinForm();

            var steps: api.app.wizard.WizardStep[] = [];
            steps.push(new api.app.wizard.WizardStep("Mixin", this.mixinForm));

            super({
                tabId: tabId,
                persistedItem: persistedMixin,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                actions: actions,
                header: this.mixinWizardHeader,
                steps: steps
            }, () => {
                callback(this);
            });
        }

        initWizardPanel() {
            super.initWizardPanel();
            this.mixinForm.reRender();
        }

        layoutPersistedItem(persistedMixin: api.schema.mixin.Mixin) : Q.Promise<void> {

            var deferred = Q.defer<void>();

            this.mixinWizardHeader.setName(persistedMixin.getName());
            this.formIcon.setSrc(persistedMixin.getIconUrl());

            new api.schema.mixin.GetMixinConfigByNameRequest(persistedMixin.getMixinName()).
                send().
                done((response: api.rest.JsonResponse<api.schema.mixin.GetMixinConfigResult>) => {

                    this.mixinForm.reRender();
                    this.mixinForm.setFormData({"xml": response.getResult().mixinXml});
                    this.persistedConfig = response.getResult().mixinXml || "";
                    deferred.resolve(null)
                });

            return deferred.promise;
        }

        persistNewItem(): Q.Promise<api.schema.mixin.Mixin> {

            var deferred = Q.defer<api.schema.mixin.Mixin>();

            var formData = this.mixinForm.getFormData();

            var createRequest = new api.schema.mixin.CreateMixinRequest().
                setName(this.mixinWizardHeader.getName()).
                setConfig(formData.xml).
                setIcon(this.mixinIcon);

            createRequest.
                sendAndParse().
                done((mixin: api.schema.mixin.Mixin) => {

                    this.getTabId().changeToEditMode(mixin.getKey());
                    api.notify.showFeedback('Mixin was created!');

                    new api.schema.SchemaCreatedEvent(mixin).fire();

                    deferred.resolve(mixin);
                });

            return deferred.promise;
        }

        updatePersistedItem(): Q.Promise<api.schema.mixin.Mixin> {

            var deferred = Q.defer<api.schema.mixin.Mixin>();

            var formData = this.mixinForm.getFormData();

            var updateRequest = new api.schema.mixin.UpdateMixinRequest().
                setMixinToUpdate(this.getPersistedItem().getName()).
                setName(this.mixinWizardHeader.getName()).
                setConfig(formData.xml).
                setIcon(this.mixinIcon);

            updateRequest.
                sendAndParse().
                done((mixin: api.schema.mixin.Mixin) => {

                    api.notify.showFeedback('Mixin was updated!');

                    new api.schema.SchemaUpdatedEvent(mixin).fire();

                    deferred.resolve(mixin);
                });

            return deferred.promise;
        }

        hasUnsavedChanges(): boolean {
            var persistedMixin: api.schema.mixin.Mixin = this.getPersistedItem();
            if (persistedMixin == undefined) {
                return true;
            } else {
                return !api.util.isStringsEqual(persistedMixin.getName(), this.mixinWizardHeader.getName())
                    || !api.util.isStringsEqual(api.util.removeCarriageChars(this.persistedConfig),
                    api.util.removeCarriageChars(this.mixinForm.getFormData().xml));
            }
        }
    }
}