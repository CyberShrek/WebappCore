<script lang="ts">
    import {fade} from "svelte/transition"
    import {popupAction, popupList} from "../../../util/alert";
    import {appInfo} from "../../../webapp";
    import Button from "../input/Button.svelte";


    function showAppInfo(){
        popupList(
            "Информация",
            [
                {icon: "🛈", text: "Версия программы: " + (appInfo.version ?? "")},
                {icon: "🗓", text: "Дата обновления: "  + (appInfo.updateDate ?? "")},
                {icon: "👤", text: "Технолог: "        + (appInfo.technologistName ?? "")}
            ],
            appInfo.comment ?? ""
        )
    }

    function showHelpDownloader(){
        popupAction(
            "Руководство",
            appInfo.description ?? "",
            "Скачать инструкцию",
            () => downloadUserManual(appInfo.helpPath ?? "")
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
    <a href="{appInfo.groupPath}" transition:fade>
        {appInfo.groupName}
    </a>|<p transition:fade>
        {appInfo.name}
    </p>
    <Button frameless hint="Сброс"                    image="reset.svg" on:click={() => location.reload()}/>
    <Button frameless hint="Информация о приложении"  image="info.svg"  on:click={showAppInfo}/>
    <Button frameless hint="Руководство пользователя" image="help.svg"  on:click={showHelpDownloader}/>
</header>