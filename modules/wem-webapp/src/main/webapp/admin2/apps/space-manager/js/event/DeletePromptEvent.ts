module APP.event {

    export class DeletePromptEvent extends SpaceModelEvent {
        constructor(model:any) {
            super('deletePrompt', model);
        }

        static on(handler:(event:DeletePromptEvent) => void) {
            API.event.onEvent('deletePrompt', handler);
        }
    }
}

