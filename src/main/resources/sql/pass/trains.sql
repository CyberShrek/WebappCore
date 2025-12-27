WITH main AS (
    SELECT

        CASE WHEN bool_or(trainpass.pr_firm = '1')
             THEN concat(trainpass.pzd, ' Ф')
             ELSE trainpass.pzd END                                                             AS train_num,
        coalesce(pzdc.names, 'Неизвестно')                                                      AS category_name,

        sum(correspon.kolpm   - correspon.kolvozm)                                              AS passengers,
        sum(correspon.kolvozm)                                                                  AS passengers_refund,
        sum(correspon.sumbil  + correspon.sumpl  + correspon.sumserv  + correspon.sumkomsb +
            correspon.sumbilv + correspon.sumplv + correspon.sumservv + correspon.sumkomsbv)    AS income,
        sum(correspon.sumbilv + correspon.sumplv + correspon.sumservv + correspon.sumkomsbv)    AS income_refund,
        sum(correspon.passkm  + correspon.passkmv)                                              AS passenger_turnover

    FROM rmest.day_trainpass AS trainpass
    JOIN rmest.day_correspon AS correspon USING (id_pzd)
    FULL JOIN nsi.katpzdco32 AS pzdc ON
        regexp_replace(trainpass.pzd, '[A-Za-zА-Яа-я]', '', 'g')::int BETWEEN pzdc.pzdosnn AND pzdc.pzdosnk
        AND trainpass.datapzd                                         BETWEEN pzdc.datan   AND pzdc.datak

    WHERE trainpass.skp = ANY (${carriers}::numeric[])
      AND trainpass.pr_viddoc = 2
      AND trainpass.datapzd BETWEEN (${period}[1]::date - '7 days'::interval)::date AND ${period}[2]::date
      AND correspon.dateotp BETWEEN  ${period}[1]::date                             AND ${period}[2]::date
      AND correspon.stanotp || '|' || correspon.stannazn = ANY (${stationPairs})

    GROUP BY trainpass.pzd, category_name
    ORDER BY train_num, category_name
),
 last_year AS (
     SELECT
         CASE WHEN bool_or(trainpass.pr_firm = '1')
                  THEN concat(trainpass.pzd, ' Ф')
              ELSE trainpass.pzd END                                                              AS train_num,

         sum(correspvag.kolpm   - correspvag.kolvozm)                                             AS passengers,
         sum(correspvag.sumbil  + correspvag.sumpl  + correspvag.sumserv  + correspvag.sumkomsb +
             correspvag.sumbilv + correspvag.sumplv + correspvag.sumservv + correspvag.sumkomsbv) AS income,
         sum(correspvag.passkm  + correspvag.passkmv)                                             AS passenger_turnover

     FROM rmest.day_trainpass  AS trainpass
     JOIN rmest.day_correspon  AS correspon  USING (id_pzd)
     JOIN rmest.day_correspvag AS correspvag USING (id_pzd, id_corresp)

     WHERE ${compareWithLastYear}
       AND trainpass.skp = ANY (${carriers}::numeric[])
       AND trainpass.pr_viddoc = 2
       AND trainpass.datapzd BETWEEN ${period}[1]::date - '1 year'::interval - '7 days'::interval AND ${period}[2]::date - '1 year'::interval
       AND correspon.dateotp BETWEEN ${period}[1]::date - '1 year'::interval                      AND ${period}[2]::date - '1 year'::interval
       AND correspon.stanotp || '|' || correspon.stannazn = ANY (${stationPairs})

     GROUP BY trainpass.pzd
 )
SELECT
    m.train_num,
    m.category_name,

    m.passengers,
    m.passengers_refund,
    ly.passengers        AS ly_passengers,
    ly.passengers        AS passengers_compared,
    ROUND(m.passengers * 100.0 / NULLIF(SUM(m.passengers) OVER(), 0) , 1)       AS passengers_share,

    m.income,
    m.income_refund,
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

FROM main as m LEFT OUTER JOIN last_year as ly USING (train_num)