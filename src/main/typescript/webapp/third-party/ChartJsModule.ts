import {Module} from "./abstract/Module"
import {Chart as ChartJS, ChartConfiguration, ChartDataset, registerables} from 'chart.js'
import {deepCopyOf} from "../util/data";

ChartJS.register(...registerables)

export class ChartJsModule extends Module{

    private chartJs: ChartJS

    constructor(private simpleSet: SimpleSet,
                public  config: ChartConfig,
                private readonly canvas: HTMLCanvasElement) {
        super()
        this.chartJs = new ChartJS(
            this.canvas.getContext('2d'),
            this.createConfig()
        )
        this.update()
    }

    update(simpleSet = this.simpleSet,
           config = this.config) {
        this.simpleSet = simpleSet
        this.config = config
        this.chartJs.data.labels = simpleSet.columnNames
        this.chartJs.data.datasets = simpleSet.data.map(
            (row, index) => this.createDataset(
                row.map(cell => Number.isNaN(cell) ? 0 : Number(cell)),
                this.config.views[index]
            )
        )
        this.chartJs.options = {
            plugins: {
                title: {
                    display: true,
                    text: this.config.title
                }
            }
        }

        this.chartJs.update()
    }

    destroy() {
        this.chartJs?.destroy()
    }

    private createConfig(): ChartConfiguration {
        return {
            type: "line",
            data: {
                labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
                datasets: [{
                    label: 'My First Dataset',
                    data: [65, 59, 80, 81, 56, 55, 40],
                    fill: false,
                    borderColor: 'rgb(75, 192, 192)',
                    tension: 0.1
                }]
            },
            options: {}
        }
    }
    private createDataset(data: number[], viewConfig: ChartViewConfig): ChartDataset {
        return {
            data,
            type: viewConfig.type,
            label: viewConfig.label,
            backgroundColor: viewConfig.color,
            borderColor: viewConfig.color,
            borderWidth: 1,
            ...(viewConfig.type === "line" ? {

            } : viewConfig.type === "bar" ? {

            } : viewConfig.type === "pie" ? {

            } : {})
        }
    }
}