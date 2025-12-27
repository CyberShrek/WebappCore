WITH main AS (
    SELECT
        correspon.stanotp                                                                        AS dep_station_code,
        dep_nsi.pnazv                                                                            AS dep_station,
        correspon.stannazn                                                                       AS dest_station_code,
        dest_nsi.pnazv                                                                           AS dest_station,

        sum(correspvag.kolpm   - correspvag.kolvozm)                                             AS passengers,
        sum(correspvag.kolvozm)                                                                  AS passengers_refund,
        sum(correspvag.sumbil  + correspvag.sumpl  + correspvag.sumserv  + correspvag.sumkomsb +
            correspvag.sumbilv + correspvag.sumplv + correspvag.sumservv + correspvag.sumkomsbv) AS income,
        sum(correspvag.sumbilv + correspvag.sumplv + correspvag.sumservv + correspvag.sumkomsbv) AS income_refund,
        sum(correspvag.passkm  + correspvag.passkmv)                                             AS passenger_turnover,
        count(DISTINCT trainpass.pzd)                                                            AS trains_count

    FROM rmest.day_trainpass  AS trainpass
    JOIN rmest.day_correspon  AS correspon  USING (id_pzd)
    JOIN rmest.day_correspvag AS correspvag USING (id_pzd, id_corresp)
    JOIN nsi.stanv AS dep_nsi ON (dep_nsi.stan = correspon.stanotp
        AND ${period}[1]::date between dep_nsi.datand and dep_nsi.datakd
        AND current_date       between dep_nsi.datani and dep_nsi.dataki
        )
    JOIN nsi.stanv AS dest_nsi ON (dest_nsi.stan = correspon.stannazn
        AND ${period}[1]::date between dest_nsi.datand and dest_nsi.datakd
        AND current_date       between dest_nsi.datani and dest_nsi.dataki
        )

    WHERE trainpass.skp = ANY (${carriers}::numeric[])
      AND trainpass.pr_viddoc = 2
      AND trainpass.datapzd BETWEEN ${period}[1]::date - '7 days'::interval AND ${period}[2]::date
      AND correspon.dateotp BETWEEN ${period}[1]::date                      AND ${period}[2]::date

      AND correspon.goso     = ANY (${depCountries})
      AND correspon.gosn     = ANY (${destCountries})
      AND correspon.doro     = ANY (${depRoads})
      AND correspon.dorn     = ANY (${destRoads})
      AND correspon.stanotp  = ANY (${depStations})
      AND correspon.stannazn = ANY (${destStations})

    GROUP BY dep_station_code, dep_station, dest_station_code, dest_station
),
last_year AS (
    SELECT
        sum(correspvag.kolpm   - correspvag.kolvozm)                                             AS passengers,
        sum(correspvag.sumbil  + correspvag.sumpl  + correspvag.sumserv  + correspvag.sumkomsb +
            correspvag.sumbilv + correspvag.sumplv + correspvag.sumservv + correspvag.sumkomsbv) AS income,
        sum(correspvag.passkm  + correspvag.passkmv)                                             AS passenger_turnover,
        correspon.stanotp                                                                        AS dep_station_code,
        correspon.stannazn                                                                       AS dest_station_code

    FROM rmest.day_trainpass  AS trainpass
    JOIN rmest.day_correspon  AS correspon  USING (id_pzd)
    JOIN main ON (main.dep_station_code = correspon.stanotp AND main.dest_station_code = correspon.stannazn)
    JOIN rmest.day_correspvag AS correspvag USING (id_pzd, id_corresp)

    WHERE ${compareWithLastYear}
      AND trainpass.skp = ANY (${carriers}::numeric[])
      AND trainpass.pr_viddoc = 2
      AND trainpass.datapzd BETWEEN ${period}[1]::date - '1 year'::interval - '7 days'::interval AND ${period}[2]::date - '1 year'::interval
      AND correspon.dateotp BETWEEN ${period}[1]::date - '1 year'::interval                      AND ${period}[2]::date - '1 year'::interval

    GROUP BY correspon.stanotp, correspon.stannazn
)
SELECT
    dep_station,
    dep_station_code,
    dest_station,
    dest_station_code,

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
    ROUND(m.passenger_turnover * 100.0 / NULLIF(SUM(m.passenger_turnover)OVER(), 0) , 1)   AS passenger_turnover_share,

    m.trains_count

FROM main as m LEFT OUTER JOIN last_year as ly USING (dep_station_code, dest_station_code)

ORDER BY dep_station, dest_station