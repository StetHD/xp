// Generated by typings
// Source: https://raw.githubusercontent.com/DefinitelyTyped/DefinitelyTyped/56295f5058cac7ae458540423c50ac2dcf9fc711/jquery.simulate/jquery.simulate.d.ts
interface JQuery {

    /**
     * Simulates an event.
     *
     * @param type
     *            the type of event (eg: "mousemove", "keydown", etc...)
     * @param options
     *            the options for the event (these are event-specific)
     */
    simulate(type: string, options?: any): void;
}