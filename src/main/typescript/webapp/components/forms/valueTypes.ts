export type Values = {
    period?: [string, string],
    periodDetailing?: boolean,
    carriers?:  {[key: string]: string},
    roads?:     {[key: string]: string},
    additional?: boolean
}
export type SalesValues = Values & {
    regions?:               {[key: string]: string},
    paymentTypes?:          {[key: string]: string},
    travelTicketTypes?:     {[key: string]: string},
    shippingTicketTypes?:   {[key: string]: string},
    operationTypes?:        {[key: string]: string}
}
export type DepsValues = Values & {
    trainCategories?:       {[key: string]: string}
}