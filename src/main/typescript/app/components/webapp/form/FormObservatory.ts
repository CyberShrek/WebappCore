import {deepCopyOf, equal} from "../../../common/util/data";
import {SqlPerformer} from "../../api/SqlPerformer";


export type FormulaCallback = (result: any) => void

/**
 * Выполняет вычисления формул при изменении состояния формы.
 * Управляет подписками на результаты вычислений:
 *  - При обновлении данных формы запускает расчет подписанных формул через SqlPerformer
 *  - Распределяет результаты вычислений по зарегистрированным колбэкам
 *
 * Механизм работы:
 *  1. При обновлении состояния (update) инициирует пересчет формул
 *  2. Вычисляет ВСЕ подписанные формулы через SqlPerformer.calculateFormulas()
 *  3. Рассылает результаты подписчикам конкретных формул
 *  4. Автоматически очищает пустые подписки при отмене callback'ов
 */
export class FormObservatory {

    private subscriptions = new Map<string, Set<FormulaCallback>>()
    private notifyTimeout = setTimeout(() => {})

    constructor(private value: FormValue,
                private valueCallback: (value: FormValue) => void) {}

    get formValue() { return this.value }


    update = async (newValue: FormValue) => {
        if (!equal(newValue, this.value)) {
            this.value = deepCopyOf(newValue)
            await this.notifySubscribers()
        }
    }

    forceUpdate = async () => {
        await this.notifySubscribers()
    }

    setField = (field: string, value: any) => {
        if (!equal(value, this.value[field])) {
            this.value[field] = value
            return this.notifySubscribers()
        }
    }

    subscribe = (formula: string, callback: FormulaCallback) => {
        if (!this.subscriptions.has(formula)) {
            this.subscriptions.set(formula, new Set())
        }
        this.subscriptions.get(formula)?.add(callback)
    }

    unsubscribe = (callback: FormulaCallback) => {
        this.subscriptions.forEach((callbacks, formula) => {
            callbacks.delete(callback)
            this.cleanEmptySubscriptions(formula, callbacks)
        })
    }

    private notifySubscribers = async () => {

        this.valueCallback?.(this.value)

        if (this.subscriptions.size === 0) return

        const formulas = Array.from(this.subscriptions.keys())
        const results = await SqlPerformer.calculateFormulas(formulas, this.value)

        formulas.forEach((formula, i) => {
            this.subscriptions.get(formula)?.forEach(cb => cb(results[i]))
        })
    }

    private cleanEmptySubscriptions = (formula: string, callbacks: Set<FormulaCallback>) => {
        if (callbacks.size === 0) {
            this.subscriptions.delete(formula)
        }
    }
}