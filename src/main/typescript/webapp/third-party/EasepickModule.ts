import {easepick} from "@easepick/core"
import {RangePlugin} from "@easepick/range-plugin"
import {AmpPlugin} from "@easepick/amp-plugin"
import {LockPlugin} from "@easepick/lock-plugin"
import {stringifyDate} from "../util/data"
import {serverLocations} from "../properties"
import {InputModule} from "./abstract/InputModule"
import {DateTime} from "@easepick/datetime"

export class EasepickModule extends InputModule<string[]>{

    private easepick: easepick.Core

    constructor(private rootElement: HTMLElement,
                private config: {
                    minYear?: number
                    maxYear?: number
                    range?:  boolean
                    maxDays?: number
                }) {

        super(pickedDate => {
            if(rootElement == null) return
            Array.isArray(pickedDate)                                       // @ts-ignore Resolved by module import
                ?  pickedDate[0] && rootElement.setStartDate(pickedDate[0]) // @ts-ignore Resolved by module import
                || pickedDate[1] && rootElement.setEndDate(pickedDate[1])   // @ts-ignore Resolved by module import
                : rootElement.setDate(pickedDate)
        })

        const initialDetail = {
            date:  new DateTime(),
            start: new DateTime(),
            end:   new DateTime()
        }

        this.easepick = new easepick.create({
            element: rootElement,
            calendars: config.range ? 2 : 1,
            grid: 2,
            zIndex: 100,
            plugins: [config.range ? RangePlugin : null, AmpPlugin, LockPlugin],
            lang: 'ru',
            date: initialDetail.date,
            RangePlugin: config.range ? {
                startDate: initialDetail.start,
                endDate: initialDetail.end,
                locale: {
                    one: 'день',
                    few: 'дня',
                    many: 'дней'
                },
                delimiter: " - "
            } : null,
            AmpPlugin: {
                darkMode: false,
                dropdown: {
                    minYear: Number(config.minYear ?? 2010),
                    maxYear: Number(config.maxYear), months: true, years: true
                }
            },
            LockPlugin: {
                maxDays: Number(config.maxDays)
            },
            css: [
                serverLocations.styles + "third-party/easepick.css"
            ],
            setup: (picker) => {
                picker.on("select", (e) => {
                    this.setValue(this.easepickDetailToCalendarValue(e.detail), false)
                    setTimeout(() => picker.hide(), 10)
                })
            }
        })

        this.value = this.easepickDetailToCalendarValue(initialDetail)
    }

    override destroy() {
        this.easepick.destroy();
    }

    private easepickDetailToCalendarValue(detail: any): string[] {
        return this.config.range
            ? [stringifyDate(detail.start), stringifyDate(detail.end)]
            : [stringifyDate(detail.date)]
    }
}