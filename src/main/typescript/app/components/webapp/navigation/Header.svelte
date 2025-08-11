<script lang="ts">
    import {fade} from "svelte/transition"
    import {popupAction, popupList} from "../../../common/util/alert"
    import Button from "../../../common/components/input/Button.svelte"

    export let info: AppInfo

    function showAppInfo(){
        popupList(
            "Информация",
            [
                {icon: "🛈", text: "Версия программы: " + (info?.version ?? "")},
                {icon: "🗓", text: "Дата обновления: "  + (info?.updateDate ?? "")},
                {icon: "👤", text: "Технолог: "        + (info?.technologistName ?? "")}
            ],
            info?.comment ?? ""
        )
    }

    function showHelpDownloader(){
        popupAction(
            "Руководство",
            info?.description ?? "",
            "Скачать инструкцию",
            () => downloadUserManual(info?.helpPath ?? "")
        )
    }

    function downloadUserManual(href: string){
        const link = document.createElement('a')
        link.href = href
        link.target = '_blank'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
    }

</script>

<header id="header">
    {#if info}
        <a href="{info.groupPath}" transition:fade>
            {info.groupName}
        </a>|<p transition:fade>
            {info.name}
        </p>
        <Button hint="Сброс"                    frameless image="reset.svg" on:click={() => location.reload()}/>
        <Button hint="Информация о приложении"  frameless image="info.svg"  on:click={showAppInfo}/>
        <Button hint="Руководство пользователя" frameless image="help.svg"  on:click={showHelpDownloader}/>
    {/if}
</header>