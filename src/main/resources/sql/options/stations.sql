SELECT *
FROM nsi.stanv
WHERE stan = correspon.stanotp
AND ${period}[1]::date between datand and datakd
AND current_date       between datani and dataki
