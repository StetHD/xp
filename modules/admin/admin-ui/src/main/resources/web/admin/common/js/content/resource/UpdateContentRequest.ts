module api.content.resource {

    export class UpdateContentRequest extends ContentResourceRequest<api.content.json.ContentJson, Content> {

        private id: string;

        private name: ContentName;

        private data: api.data.PropertyTree;

        private meta: ExtraData[];

        private displayName: string;

        private requireValid: boolean;

        private language: string;

        private owner: api.security.PrincipalKey;

        private publishFrom: Date;

        private publishTo: Date;

        private permissions: api.security.acl.AccessControlList;

        private inheritPermissions: boolean;

        private overwritePermissions: boolean;

        constructor(id: string) {
            super();
            this.id = id;
            this.requireValid = false;
            this.setMethod('POST');
        }

        setId(id: string): UpdateContentRequest {
            this.id = id;
            return this;
        }

        setContentName(value: ContentName): UpdateContentRequest {
            this.name = value;
            return this;
        }

        setData(contentData: api.data.PropertyTree): UpdateContentRequest {
            this.data = contentData;
            return this;
        }

        setExtraData(extraData: ExtraData[]): UpdateContentRequest {
            this.meta = extraData;
            return this;
        }

        setDisplayName(displayName: string): UpdateContentRequest {
            this.displayName = displayName;
            return this;
        }

        setRequireValid(requireValid: boolean): UpdateContentRequest {
            this.requireValid = requireValid;
            return this;
        }

        setLanguage(language: string): UpdateContentRequest {
            this.language = language;
            return this;
        }

        setOwner(owner: api.security.PrincipalKey): UpdateContentRequest {
            this.owner = owner;
            return this;
        }

        setPublishFrom(date: Date): UpdateContentRequest {
            this.publishFrom = date;
            return this;
        }

        setPublishTo(date: Date): UpdateContentRequest {
            this.publishTo = date;
            return this;
        }

        setPermissions(permissions: api.security.acl.AccessControlList): UpdateContentRequest {
            this.permissions = permissions;
            return this;
        }

        setInheritPermissions(inheritPermissions: boolean): UpdateContentRequest {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        setOverwritePermissions(overwritePermissions: boolean): UpdateContentRequest {
            this.overwritePermissions = overwritePermissions;
            return this;
        }

        getParams(): Object {
            return {
                contentId: this.id,
                requireValid: this.requireValid,
                contentName: this.name.isUnnamed() ? ContentName.UNNAMED_PREFIX : this.name.toString(),
                data: this.data.toJson(),
                meta: (this.meta || []).map((extraData: ExtraData) => extraData.toJson()),
                displayName: this.displayName,
                language: this.language,
                owner: this.owner ? this.owner.toString() : undefined,
                publishFrom: this.publishFrom,
                publishTo: this.publishTo,
                permissions: this.permissions ? this.permissions.toJson() : undefined,
                inheritPermissions: this.inheritPermissions,
                overwriteChildPermissions: this.overwritePermissions
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'update');
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return this.fromJsonToContent(response.getResult());
            });
        }

    }

}
