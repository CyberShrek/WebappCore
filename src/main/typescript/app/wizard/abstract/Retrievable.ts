export interface Retrievable<RESPONSE_OBJECT> {
    retrieve(): RESPONSE_OBJECT | Promise<RESPONSE_OBJECT>
}