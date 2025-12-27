WITH main AS (
    SELECT correspvag.katvag                                                                        AS car_category,
           correspvag.klov                                                                          AS service_class,
           sum(correspvag.kolpm - correspvag.kolvozm)                                               AS passengers,
           sum(correspvag.kolvozm)                                                                  AS passengers_refund,
           sum(correspvag.sumbil + correspvag.sumpl + correspvag.sumserv + correspvag.sumkomsb +
               correspvag.sumbilv + correspvag.sumplv + correspvag.sumservv + correspvag.sumkomsbv) AS income,
           sum(correspvag.sumbilv + correspvag.sumplv + correspvag.sumservv +
               correspvag.sumkomsbv)                                                                AS income_refund,
           sum(correspvag.passkm + correspvag.passkmv)                                              AS passenger_turnover

    FROM rmest.day_trainpass AS trainpass
    JOIN rmest.day_correspon AS correspon USING (id_pzd)
    JOIN rmest.day_correspvag AS correspvag USING (id_pzd, id_corresp)

    WHERE trainpass.skp = ANY (${carriers}::numeric[])
    AND trainpass.pr_viddoc = 2
    AND trainpass.datapzd BETWEEN (${period}[1]::date - '7 days'::interval)::date AND ${period}[2]::date
    AND correspon.dateotp BETWEEN ${period}[1]::date AND ${period}[2]::date
    AND correspon.stanotp || '|' || correspon.stannazn = ANY (${stationPairs})

    GROUP BY correspvag.katvag, correspvag.klov

    ORDER BY car_category, service_class
),
last_year AS (
    SELECT correspvag.katvag                                                                        AS car_category,
           correspvag.klov                                                                          AS service_class,
           sum(correspvag.kolpm - correspvag.kolvozm)                                               AS passengers,
           sum(correspvag.kolvozm)                                                                  AS passengers_refund,
           sum(correspvag.sumbil + correspvag.sumpl + correspvag.sumserv + correspvag.sumkomsb +
               correspvag.sumbilv + correspvag.sumplv + correspvag.sumservv + correspvag.sumkomsbv) AS income,
           sum(correspvag.sumbilv + correspvag.sumplv + correspvag.sumservv +
               correspvag.sumkomsbv)                                                                AS income_refund,
           sum(correspvag.passkm + correspvag.passkmv)                                              AS passenger_turnover

    FROM rmest.day_trainpass  AS trainpass
    JOIN rmest.day_correspon  AS correspon USING (id_pzd)
    JOIN rmest.day_correspvag AS correspvag USING (id_pzd, id_corresp)
    JOIN main ON (main.car_category = correspvag.katvag AND main.service_class = correspvag.klov)

    WHERE ${compareWithLastYear}
      AND trainpass.skp = ANY (${carriers}::numeric[])
      AND trainpass.pr_viddoc = 2
      AND trainpass.datapzd BETWEEN ${period}[1]::date - '1 year'::interval - '7 days'::interval AND ${period}[2]::date - '1 year'::interval
      AND correspon.dateotp BETWEEN ${period}[1]::date - '1 year'::interval                      AND ${period}[2]::date - '1 year'::interval
      AND correspon.stanotp || '|' || correspon.stannazn = ANY (${stationPairs})

    GROUP BY correspvag.katvag, correspvag.klov
)
SELECT
    CASE
        WHEN car_category = 'Л' THEN 'Люкс'
        WHEN car_category = 'М' THEN 'Мягкий'
        WHEN car_category = 'К' THEN 'Купе'
        WHEN car_category = 'П' THEN 'Плацкарт'
        WHEN car_category = 'О' THEN 'Общий'
        WHEN car_category = 'С' THEN 'Сидячий'
        ELSE 'Неизвестно: ' || car_category END  AS car_category,
    service_class,

    m.passengers,
    m.passengers_refund,
    ly.passengers        AS ly_passengers,
    ly.passengers        AS passengers_compared,
    m.passengers * 100.0 / NULLIF(SUM(m.passengers) OVER(), 0)                   AS passengers_share,

    m.income,
    m.income_refund,
    ly.income            AS ly_income,
    ly.income            AS income_compared,
    m.income * 100.0 / NULLIF(SUM(m.income) OVER(), 0)                          AS income_share,

    ROUND(m.income / NULLIF(m.passengers, 0), 1)                                AS income_per_passenger,
    ROUND(ly.income / NULLIF(ly.passengers, 0), 1)                              AS ly_income_per_passenger,
    ROUND(ly.income / NULLIF(ly.passengers, 0), 1)                              AS income_per_passenger_compared,

    m.passenger_turnover     AS passenger_turnover,
    ly.passenger_turnover    AS ly_passenger_turnover,
    ly.passenger_turnover    AS passenger_turnover_compared,
    m.passenger_turnover * 100.0 / NULLIF(SUM(m.passenger_turnover) OVER(), 0)   AS passenger_turnover_share

FROM main as m LEFT OUTER JOIN last_year as ly USING (car_category, service_class)
ORDER BY car_category, service_class