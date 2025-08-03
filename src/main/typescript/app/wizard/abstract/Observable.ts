export interface Observable{
    // Returns true if observed fields have been triggered
    checkForChanges(value: FormValue): boolean
}