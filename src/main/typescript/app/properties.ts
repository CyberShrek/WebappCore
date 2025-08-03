const rootPath = '/' + document.location.pathname.split('/')[1],
    resourcesPath = rootPath + '/resources'

export const

    serverLocations = {
        images: `${resourcesPath}/img/`,
        styles: `${resourcesPath}/css/`,
        modules: `${resourcesPath}/js/`,
        webapp: `${rootPath}/webapp`,
        user: `${rootPath}/user`,
        export: `${rootPath}/export`,
        formulas: `${rootPath}/formulas`,
        query: `${rootPath}/query`,
        options: `${rootPath}/options`,
        report: `${rootPath}/report`,
        dataSources: `${rootPath}/datasources`,
        serviceBank: `/servicebank/getdata`
    },
    serverErrors: {[name in (keyof typeof serverLocations)]: string} = {
        images: "Не удалось загрузить картинку",
        styles: "Не удалось загрузить стиль",
        modules: "Не удалось загрузить модуль",
        webapp: "Не удалось загрузить конфигурацию приложения",
        user: "Не удалось загрузить информацию о пользователе",
        export: "Не удалось экспортировать",
        formulas: "Не удалось выполнить формулу",
        query: "Не удалось выполнить SQL запрос",
        options: "Не удалось загрузить опции",
        report: "Ошибка отчёта",
        dataSources: "Не удалось загрузить источники данных",
        serviceBank: "Не удалось загрузить данные из банка микросервисов"
    },

    virtualSelectProperties = {
        additionalClasses: "multiselect",
        disabled: true,
        autofocus: false,
        markSearchResults: true,
        zIndex: 1000,
        optionsCount: 6,
        maxWidth: "100%",
        position: "bottom",
        disableAllOptionsSelectedText: true,
        // selectAllOnlyVisible: true,
        placeholder: "",
        noOptionsText: "Варианты не найдены",
        noSearchResultsText: "Результатов не найдено",
        selectAllText: "Выбрать все",
        searchPlaceholderText: "Поиск...",
        optionsSelectedText: "(выбрано)",
        optionSelectedText: "вариант выбран",
        allOptionsSelectedText: "Все",
        clearButtonText: "Очистить",
        moreText: "ещё..."
    },
    sweetAlertProperties = {
        confirmButtonColor: "var(--primary-color)",
        showCloseButton: true,
        allowEnterKey: false,
        showConfirmButton: false
    },
    tableText = {
        total: "Итого"
    }