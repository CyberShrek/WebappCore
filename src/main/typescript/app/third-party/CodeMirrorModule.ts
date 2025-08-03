import {basicSetup} from "codemirror"
import {EditorView, keymap} from "@codemirror/view"
import {javascript} from "@codemirror/lang-javascript"
import {json} from "@codemirror/lang-json"
import {html} from "@codemirror/lang-html"
import {PostgreSQL, sql} from "@codemirror/lang-sql"
import {indentWithTab} from "@codemirror/commands"
import {InputModule} from "./abstract/InputModule"

export class CodeMirrorModule extends InputModule<string>{

    private view: EditorView

    constructor(private rootElement: HTMLElement,
                private lang: CodeareaConfig["lang"]) {
        super(value => this.view.dispatch({changes: {from: 0, to: this.view.state.doc.length, insert: value}}))
        this.view = new EditorView({
            extensions: [
                basicSetup,
                keymap.of([indentWithTab]),
                lang === "sql" ? sql({dialect: PostgreSQL}) : lang === "js" ? javascript() : lang === "html" ? html() : json(),
                // EditorView.updateListener.of((update) => {
                //     update.docChanged && this.setValue(update.state.doc.toString().trim(), false)
                // })
            ],
            parent: this.rootElement
        })
        this.view.dom.addEventListener("click", (ev) => ev.stopPropagation())
        this.view.dom.addEventListener("focusout", () => {
            this.setValue(this.view.state.doc.toString(), false)
        });
    }

    override destroy() {
        this.view.destroy()
    }
}