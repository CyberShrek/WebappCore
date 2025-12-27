WITH main AS (
    SELECT
        passprofile.prmg AS gender_sign,
        passprofile.ktar AS tariff_code,

        sum(correspon.kolpm   - correspon.kolvozm)                                              AS passengers,
        sum(correspon.kolvozm)                                                                  AS passengers_refund,
        sum(correspon.sumbil  + correspon.sumpl  + correspon.sumserv  + correspon.sumkomsb +
            correspon.sumbilv + correspon.sumplv + correspon.sumservv + correspon.sumkomsbv)    AS income,
        sum(correspon.sumbilv + correspon.sumplv + correspon.sumservv + correspon.sumkomsbv)    AS income_refund,
        sum(correspon.passkm  + correspon.passkmv)                                              AS passenger_turnover

    FROM rmest.day_trainpass AS trainpass
    JOIN rmest.day_correspon AS correspon USING (id_pzd)
    JOIN rmest.day_correspvag AS correspvag  USING (id_pzd, id_corresp)
    JOIN rmest.day_passprofile AS passprofile USING (id_pzd, id_corresp, id_sale, id_corrvag)

    WHERE trainpass.skp = ANY (${carriers}::numeric[])
      AND trainpass.pr_viddoc = 2
      AND trainpass.datapzd BETWEEN (${period}[1]::date - '7 days'::interval)::date AND ${period}[2]::date
      AND correspon.dateotp BETWEEN  ${period}[1]::date                             AND ${period}[2]::date

      AND correspon.stanotp || '|' || correspon.stannazn = ANY (${stationPairs})

    GROUP BY gender_sign, tariff_code

    ORDER BY gender_sign
),
 last_year AS (
     SELECT
         passprofile.prmg AS gender_sign,
         passprofile.ktar AS tariff_code,

         sum(correspon.kolpm   - correspon.kolvozm)                                              AS passengers,
         sum(correspon.kolvozm)                                                                  AS passengers_refund,
         sum(correspon.sumbil  + correspon.sumpl  + correspon.sumserv  + correspon.sumkomsb +
             correspon.sumbilv + correspon.sumplv + correspon.sumservv + correspon.sumkomsbv)    AS income,
         sum(correspon.sumbilv + correspon.sumplv + correspon.sumservv + correspon.sumkomsbv)    AS income_refund,
         sum(correspon.passkm  + correspon.passkmv)                                              AS passenger_turnover

     FROM rmest.day_trainpass AS trainpass
              JOIN rmest.day_correspon AS correspon USING (id_pzd)
              JOIN rmest.day_correspvag AS correspvag  USING (id_pzd, id_corresp)
              JOIN rmest.day_passprofile AS passprofile USING (id_pzd, id_corresp, id_corrvag)
              JOIN nsi.lgot	          AS lgot	     ON (lgot.lgot  = correspvag.numlgot)
              LEFT JOIN nsi.tplan       AS tplan       ON (tplan.lgot = lgot.lgot)

     WHERE ${compareWithLastYear}
       AND trainpass.skp = ANY (${carriers}::numeric[])
       AND trainpass.pr_viddoc = 2
       AND trainpass.datapzd BETWEEN (${period}[1]::date - '7 days'::interval)::date AND ${period}[2]::date
       AND correspon.dateotp BETWEEN  ${period}[1]::date                             AND ${period}[2]::date

       AND correspon.stanotp || '|' || correspon.stannazn = ANY (${stationPairs})

     GROUP BY gender_sign, tariff_code
 )
SELECT
    CASE WHEN m.gender_sign = 'Ж' THEN 'Женское'
         WHEN m.gender_sign = 'М' THEN 'Мужское'
         WHEN m.gender_sign = 'С' THEN 'Смешанное'
         ELSE 'Без указания пола' END as gender_sign,
    ktar.snazv AS tariff_name,

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

FROM main as m
JOIN nsi.ktar AS ktar ON ktar.tipm = m.tariff_code
LEFT OUTER JOIN last_year as ly USING (gender_sign, tariff_code)
ORDER BY gender_sign, tariff_code