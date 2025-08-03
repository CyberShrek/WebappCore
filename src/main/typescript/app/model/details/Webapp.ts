interface WebappDetails {
    code: string
    frontend: FrontendConfig
    dataSource: string
    info?: WebappInfo
}

interface WebappInfo {
    name?: string
    groupName?: string
    contextRoot?: string
    groupPath?: string
    version?: string
    releaseDate?: string
    updateDate?: string
    technologistName?: string
    technologistPhone?: string
    technologistMail?: string
    helpPath?: string
    comment?: string
    tables?: string[]
    instructionPath?: string
    description?: string
}