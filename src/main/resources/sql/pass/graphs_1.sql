 WITH month_series AS (
     SELECT date::date, date_part('month', date) as month, date_part('year', date) as year
     FROM generate_series(
                  date_trunc('month', ${period}[1]::date - '1 year'::interval),
                  ${period}[2]::date, interval '1 month') AS date
 ),
 monthly_base AS (
     SELECT year, month,
            correspon.mesotp|| '.' ||correspon.godotp                                                      AS month_date,
            sum(correspon.kolpm   - correspon.kolvozm)                                                     AS monthly_passengers
     FROM rmest.m_trainpass AS trainpass
              JOIN rmest.m_correspon AS correspon USING (id_pzd)
              RIGHT JOIN month_series ON (year = correspon.godotp AND month = correspon.mesotp)

     WHERE trainpass.skp = ANY (${carriers}::numeric[])
       AND trainpass.pr_viddoc = 2
       AND make_date(trainpass.godpzd, trainpass.mespzd, '01') BETWEEN
         date_trunc('month', ${period}[1]::date - '1 year'::interval) - '7 days'::interval and ${period}[2]::date
       AND make_date(correspon.godotp, correspon.mesotp, '01') BETWEEN
         date_trunc('month', ${period}[1]::date - '1 year'::interval) and ${period}[2]::date
       AND correspon.stanotp || '|' || correspon.stannazn = ANY (${stationPairs})

     GROUP BY month_date, year, month

     ORDER BY year, month
 )
SELECT month_date,
       monthly_passengers AS "Количество пассажиров",
       round(avg(monthly_passengers) OVER (PARTITION BY month_date IS NOT NULL)) AS "Средний показатель"

FROM monthly_base

ORDER BY year, month