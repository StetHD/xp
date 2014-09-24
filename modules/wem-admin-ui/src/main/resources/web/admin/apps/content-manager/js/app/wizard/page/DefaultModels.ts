module app.wizard.page {

    import PageTemplate = api.content.page.PageTemplate;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;

    export interface DefaultModelsConfig {

        pageTemplate: PageTemplate;

        partDescriptor: PartDescriptor;

        layoutDescriptor: LayoutDescriptor;
    }

    export class DefaultModels {

        private pageTemplate: PageTemplate;

        private partDescriptor: PartDescriptor;

        private layoutDescriptor: LayoutDescriptor;

        constructor(config: DefaultModelsConfig) {
            this.pageTemplate = config.pageTemplate;
            this.partDescriptor = config.partDescriptor;
            this.layoutDescriptor = config.layoutDescriptor;
        }

        hasPageTemplate(): boolean {
            return !this.pageTemplate ? false : true;
        }

        getPageTemplate(): PageTemplate {
            return this.pageTemplate;
        }

        getPartDescriptor(): PartDescriptor {
            return this.partDescriptor;
        }

        getLayoutDescriptor(): LayoutDescriptor {
            return this.layoutDescriptor;
        }
    }
}