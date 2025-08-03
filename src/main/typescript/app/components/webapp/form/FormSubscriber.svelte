<script lang="ts">

    import {FormObservatory, FormulaCallback} from "./FormObservatory";
    import {onDestroy, onMount} from "svelte";

    export let
        observatory: FormObservatory,
        formulaCreate: string = null,
        formulaCorrect: string = null,
        formulaMessage: string = null,
        create: boolean = true,
        correct: boolean = true,
        message: string  = null,
        onCreateChange: (val: boolean) => void = null,
        onCorrectChange: (val: boolean) => void = null,
        onMessageChange: (val: string)  => void = null,
        onAnyChange: (newForm: FormValue) => void = null

    let createFormulaCallback: FormulaCallback = formulaCreate ? (result) => {
            create = result
            onCreateChange?.(result)
        } : null,
        correctFormulaCallback: FormulaCallback = formulaCorrect ? (result) => {
            correct = result
            onCorrectChange?.(result)
        } : null,
        messageFormulaCallback: FormulaCallback = formulaMessage ? (result) => {
            message = result
            onMessageChange?.(result)
        } : null,
        anyFormulaCallback: FormulaCallback = () => {
            onAnyChange?.(observatory.formValue)
        }

    function subscribe() {
        if (!observatory) return
        formulaCreate && observatory.subscribe(formulaCreate, createFormulaCallback)
        formulaCorrect && observatory.subscribe(formulaCorrect, correctFormulaCallback)
        formulaMessage && observatory.subscribe(formulaMessage, messageFormulaCallback)
        observatory.subscribe("null", anyFormulaCallback)
    }

    function unsubscribe() {
        if (!observatory) return
        observatory.unsubscribe(createFormulaCallback)
        observatory.unsubscribe(correctFormulaCallback)
        observatory.unsubscribe(messageFormulaCallback)
        observatory.unsubscribe(anyFormulaCallback)
    }

    onMount(subscribe)

    onDestroy(unsubscribe)

</script>

{#if create}
    <slot/>
{/if}
