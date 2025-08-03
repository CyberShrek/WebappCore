<script lang="ts">

    import {Module} from "../../../app/third-party/abstract/Module";
    import LeaderLine from "leader-line"
    import {onDestroy} from "svelte";

    export let
        from: HTMLElement,
        to: HTMLElement[],
        labels: string[] = []

    let leaderLineModule: LeaderLineModule

    $: if(from && to && (leaderLineModule == null || leaderLineModule.from !== from))
        leaderLineModule = new LeaderLineModule()

    $: if(to || labels)
        leaderLineModule?.redraw(to)

    class LeaderLineModule extends Module {

        private  leaderLinesMap: Map<HTMLElement, LeaderLine> = new Map()
        readonly from: HTMLElement
        private  to: HTMLElement[]

        constructor() {
            super()
            this.from = from
            this.to = to
            this.redraw()
        }

        redraw(to: HTMLElement[] = this.to) {
            let lineIndex = 0
            new Set([...this.to, ...to]).forEach(toElement => {
                try {
                    if (to.includes(toElement)) {
                        if(this.leaderLinesMap.has(toElement))
                            this.updateLine(toElement, labels[lineIndex])
                        else
                            this.createLine(toElement, labels[lineIndex])
                    } else {
                        this.removeLine(toElement)
                    }
                }
                catch (e) {
                    console.error(e)
                }
                lineIndex++
            })
            this.to = to
        }

        hide() {
            this.leaderLinesMap?.forEach(leaderLine => leaderLine.hide())
        }

        show() {
            this.leaderLinesMap?.forEach(leaderLine => leaderLine.show())
        }

        override destroy() {
            this.leaderLinesMap.forEach(leaderLine => leaderLine.remove())
            this.leaderLinesMap = new Map()
        }

        private createLine(to: HTMLElement, label?: string) {
            const leaderLine = new LeaderLine(this.from, to, {
                startSocket: 'bottom',
                endSocket: 'top',
                endPlug: 'arrow3',
                color: 'var(--rzd-color)',
                endLabel: label ?? undefined
            })
            this.leaderLinesMap.set(to, leaderLine)
            return leaderLine
        }

        private updateLine(to: HTMLElement, label?: string) {
            this.leaderLinesMap.get(to)?.setOptions({endLabel: label ?? ""})
            this.leaderLinesMap.get(to)?.position()
        }

        private removeLine(to: HTMLElement) {
            this.leaderLinesMap.get(to)?.remove()
            this.leaderLinesMap.delete(to)
        }
    }

    onDestroy(() => leaderLineModule?.destroy())


</script>