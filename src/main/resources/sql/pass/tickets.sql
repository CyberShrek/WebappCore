WITH main AS (
SELECT correspvag.viddok                                                                        AS doc_type,
    sum(correspvag.kolpm   - correspvag.kolvozm)                                                AS passengers,
    sum(correspvag.sumbil  + correspvag.sumpl  + correspvag.sumserv  + correspvag.sumkomsb +
        correspvag.sumbilv + correspvag.sumplv + correspvag.sumservv + correspvag.sumkomsbv)    AS income,
    sum(correspvag.passkm  + correspvag.passkmv)                                                AS passenger_turnover

    FROM rmest.day_trainpass AS trainpass
    JOIN rmest.day_correspon AS correspon USING (id_pzd)
    JOIN rmest.day_correspvag AS correspvag  USING (id_pzd, id_corresp)

    WHERE trainpass.skp = ANY (${carriers}::numeric[])
      AND trainpass.pr_viddoc = 2
      AND trainpass.datapzd BETWEEN (${period}[1]::date - '7 days'::interval)::date AND ${period}[2]::date
      AND correspon.dateotp BETWEEN ${period}[1]::date AND ${period}[2]::date
      AND correspon.stanotp || '|' || correspon.stannazn = ANY (${stationPairs})

    GROUP BY correspvag.viddok

    ORDER BY correspvag.viddok
),
last_year AS (
    SELECT correspvag.viddok                                                                           AS doc_type,
           sum(correspvag.kolpm   - correspvag.kolvozm)                                                AS passengers,
           sum(correspvag.sumbil  + correspvag.sumpl  + correspvag.sumserv  + correspvag.sumkomsb +
               correspvag.sumbilv + correspvag.sumplv + correspvag.sumservv + correspvag.sumkomsbv)    AS income,
           sum(correspvag.passkm  + correspvag.passkmv)                                                AS passenger_turnover

    FROM rmest.day_trainpass AS trainpass
    JOIN rmest.day_correspon AS correspon USING (id_pzd)
    JOIN rmest.day_correspvag AS correspvag  USING (id_pzd, id_corresp)
    JOIN main ON main.doc_type = correspvag.viddok

    WHERE trainpass.skp = ANY (${carriers}::numeric[])
      AND trainpass.pr_viddoc = 2
      AND trainpass.datapzd BETWEEN ${period}[1]::date - '1 year'::interval - '7 days'::interval AND ${period}[2]::date - '1 year'::interval
      AND correspon.dateotp BETWEEN ${period}[1]::date - '1 year'::interval                      AND ${period}[2]::date - '1 year'::interval
      AND correspon.stanotp || '|' || correspon.stannazn = ANY (${stationPairs})

    GROUP BY correspvag.viddok
)
SELECT CASE WHEN m.doc_type = 'P' THEN 'Полный или детский'
            WHEN m.doc_type = 'S' THEN '??'
            WHEN m.doc_type = 'L' THEN 'Льготный'
            WHEN m.doc_type = 'V' THEN 'Кредитовый или трансп. требование'
            WHEN m.doc_type = 'Z' THEN 'Посадочный или бесплатная плацкарта'
            WHEN m.doc_type = 'B' THEN 'Бесплатный'
            WHEN m.doc_type = '6' THEN 'Места по P06'
            WHEN m.doc_type = 'D' THEN 'Доплата'
            ELSE '[Неизвестно]' END  AS doc_type,

       m.passengers,
       ly.passengers        AS ly_passengers,
       ly.passengers        AS passengers_compared,
       ROUND(m.passengers * 100.0 / NULLIF(SUM(m.passengers) OVER(), 0) , 1)       AS passengers_share,

       m.income,
       ly.income            AS ly_income,
       ly.income            AS income_compared,
       ROUND(m.income * 100.0 / NULLIF(SUM(m.income) OVER(), 0) , 1)                AS income_share,

       ROUND(m.income / NULLIF(m.passengers, 0), 1)                                AS income_per_passenger,
       ROUND(ly.income / NULLIF(ly.passengers, 0), 1)                              AS ly_income_per_passenger,
       ROUND(ly.income / NULLIF(ly.passengers, 0), 1)                              AS income_per_passenger_compared,

       m.passenger_turnover     AS passenger_turnover,
       ly.passenger_turnover    AS ly_passenger_turnover,
       ly.passenger_turnover    AS passenger_turnover_compared,
       ROUND(m.passenger_turnover * 100.0 / NULLIF(SUM(m.passenger_turnover)OVER(), 0) , 1)   AS passenger_turnover_share

FROM main as m LEFT OUTER JOIN last_year as ly USING (doc_type)