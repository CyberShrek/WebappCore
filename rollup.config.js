import applyTypescript from "@rollup/plugin-typescript"
import resolveNodeJs from '@rollup/plugin-node-resolve'
import applyTerser from '@rollup/plugin-terser'
import commonjs from '@rollup/plugin-commonjs'
import legacy from "@rollup/plugin-legacy";
import sveltePreprocess from "svelte-preprocess"
import svelte from "rollup-plugin-svelte"
import clear from "rollup-plugin-clear";

const
    dev = !!process.env.ROLLUP_WATCH,
    inputDir = "./src/main/typescript/webapp",
    outputDir = "./src/main/webapp/WEB-RES/js/built"

export default  {
    dev: dev,
    input: [
        `${inputDir}/webapp.ts`
    ],
    output: [
        {
            dir: outputDir,
            format: "es",
            sourcemap: dev,
            manualChunks:{
                chartjs: ["chart.js"],
                domtoimage: ["dom-to-image"],
                sweetAlert2: ["sweetalert2"],
                easepick: ["@easepick/amp-plugin", "@easepick/core", "@easepick/lock-plugin", "@easepick/range-plugin"]
            }
        }
    ],
    plugins: [
        clear({
            // required, point out which directories should be clear.
            targets: [outputDir],
            // optional, whether clear the directores when rollup recompile on --watch mode.
            watch: true, // default: false
        }),
        commonjs({
            namedExports:{
                "dom-to-image": ["dom-to-image"],
            }
        }),
        resolveNodeJs({
            mainFields: [ "module", "browser", "main" ],
            dedupe: ['s']
        }),
        applyTypescript(),
        applyTerser(),
        legacy({ 'node_modules/leader-line/leader-line.min.js': 'LeaderLine' }),
        svelte({
            compilerOptions: {
                dev
            },
            preprocess: sveltePreprocess(),
        })
    ],
    onwarn: (warning, handle) => {
        // Ignore node_modules warnings
        if(warning.loc?.file?.includes("node_modules") || warning.ids?.toString()?.includes("node_modules"))
            return

        handle(warning.message)
    }
}