export const regexp = {
    functionArg: (functions: string[], arg: string) => new RegExp(`(?<=(?:\\W|^)(?:${functions.join('|')})\\s*\\()\s*${arg}\s*(?=\\))`, 'gm'),
    firstLetter: new RegExp('(?<=^\\W*?)\\w', 'm'),
    injectedField: new RegExp('(?<=\\$)\\w*', 'gm'),
    columnTitleSlitter: new RegExp('\\|', 'gm'),
    curlyBraceContent: new RegExp('(?<=\\{).*?(?=})', 'gm'),
}

export const capitalize=(word: string) => word.replace(
    regexp.firstLetter,
    word.match(regexp.firstLetter)[0].toUpperCase()
)

export const makeUriFriendly = (str: string, excludeSlash: boolean = false) => encodeURI(str
    // Оставляем только латинские буквы, цифры, пробелы и (опционально) слэши
    .replace(excludeSlash ? /[^a-zA-Z0-9 ]/g : /[^a-zA-Z0-9 /]/g, 'x')
    // Заменяем пробелы на подчеркивания
    .replace(/ /g, '_')
    // Если excludeSlash = true, заменяем слэши на подчеркивания
    .replace(/\/+/g, excludeSlash ? '_' : '/')
)

export const generateUniqueName =
    (name: string, names: string[]) => {
        let newName = name
        let i = 0
        while (names.includes(newName)) newName = `${name}_${i++}`
        return newName
    }