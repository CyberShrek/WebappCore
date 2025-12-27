WITH day_series AS (
    SELECT date::date, to_char(date,'DY') as day
    FROM generate_series(${period}[1]::date, ${period}[2]::date, interval '1 day') AS date
),
 daily_base as (
     SELECT
         date as day_date,
         CASE WHEN (day = 'MON') THEN 'пн' ELSE
         CASE WHEN (day = 'TUE') THEN 'вт' ELSE
         CASE WHEN (day = 'WED') THEN 'ср' ELSE
         CASE WHEN (day = 'THU') THEN 'чт' ELSE
         CASE WHEN (day = 'FRI') THEN 'пт' ELSE
         CASE WHEN (day = 'SAT') THEN 'сб' ELSE 'вс' END END END END END END || to_char(date, ' DD.MM.YYYY') AS formatted_day_date,
         sum(correspon.sumbil + correspon.sumpl  + correspon.sumserv  + correspon.sumkomsb
             + correspon.sumbilv + correspon.sumplv + correspon.sumservv + correspon.sumkomsbv) / 1000         AS daily_income

     FROM rmest.day_trainpass AS trainpass
              JOIN rmest.day_correspon AS correspon USING (id_pzd)
              RIGHT JOIN day_series ON (date = correspon.dateotp)

     WHERE trainpass.skp = ANY (${carriers}::numeric[])
       AND trainpass.pr_viddoc = 2
       AND trainpass.datapzd BETWEEN (${period}[1]::date - '7 days'::interval)::date AND ${period}[2]::date
       AND correspon.dateotp BETWEEN ${period}[1]::date AND ${period}[2]::date
       AND correspon.stanotp || '|' || correspon.stannazn = ANY (${stationPairs})

     GROUP BY date, day_series.day

     ORDER BY day_series.date
 )
SELECT formatted_day_date,
       round(daily_income) AS "Доходные поступления",
       round(avg(daily_income) OVER (PARTITION BY formatted_day_date IS NOT NULL)) AS "Средний показатель"

FROM daily_base

ORDER BY day_date