with main AS (
    SELECT
        CASE WHEN ('DAYS' = ANY(${periodDetailing}))   THEN departure_date::text
             WHEN ('MONTHS' = ANY(${periodDetailing})) THEN to_char(departure_date, 'YYYY-MM')
             WHEN ('YEARS' = ANY(${periodDetailing}))  THEN to_char(departure_date, 'YYYY')     END period,

        CASE WHEN cardinality(${depRoads})  = 0 THEN dep_gosk.g_name  END AS dep_country,
        CASE WHEN cardinality(${depRoads})  = 0 THEN dep_gosk.g_kod   END AS dep_country_code,
        CASE WHEN cardinality(${destRoads}) = 0 THEN dest_gosk.g_name END AS dest_country,
        CASE WHEN cardinality(${destRoads}) = 0 THEN dest_gosk.g_kod  END AS dest_country_code,

        CASE WHEN cardinality(${depRoads})  > 0 AND cardinality(${depStations})  = 0 THEN dep_dork.d_name END AS dep_road,
        CASE WHEN cardinality(${depRoads})  > 0 AND cardinality(${depStations})  = 0 THEN dep_dork.d_kod  END AS dep_road_code,
        CASE WHEN cardinality(${destRoads}) > 0 AND cardinality(${destStations}) = 0 THEN dest_dork.d_name END AS dest_road,
        CASE WHEN cardinality(${destRoads}) > 0 AND cardinality(${destStations}) = 0 THEN dest_dork.d_kod END AS dest_road_code,

        CASE WHEN cardinality(${depStations})  > 0 THEN dep_stanv.snazv  END AS dep_station,
        CASE WHEN cardinality(${depStations})  > 0 THEN dep_stanv.stan   END AS dep_station_code,
        CASE WHEN cardinality(${destStations}) > 0 THEN dest_stanv.snazv END AS dest_station,
        CASE WHEN cardinality(${destStations}) > 0 THEN dest_stanv.stan  END AS dest_station_code,

        bpd.weight                                                                                          AS weight,
        coalesce(CASE WHEN NOT array['3', '4']::char[] && (${type})::char[] THEN bpd.passqty ELSE 0 END, 0) AS pass_quantity,
        coalesce(CASE WHEN array['3', '4']::char[] && (${type})::char[]THEN bpd.carriage_qty ELSE 0 END, 0) AS carr_quantity,
        bpd.place_qty                                                                                       AS place_quantity,
        bcd.sum_nde                                                                                         AS payments,
        bmd.train_num

    FROM rawdl2_day.bag_main_day AS bmd
    JOIN rawdl2_day.bag_cost_day AS bcd USING(id_bag_main_day)
    JOIN rawdl2_day.bag_priz_day AS bpd USING(id_bag_main_day, id_bag_priz_day)

    JOIN nsi.dork AS dep_dork  ON (bmd.departure_dor   = dep_dork.d_kod
     AND ${period}[1]::date BETWEEN dep_dork.d_datan and dep_dork.d_datak
     AND ${period}[1]::date BETWEEN dep_dork.d_datani and dep_dork.d_dataki)

    JOIN nsi.dork AS dest_dork ON (bmd.destination_dor = dest_dork.d_kod
     AND ${period}[1]::date BETWEEN dest_dork.d_datan and dest_dork.d_datak
     AND ${period}[1]::date BETWEEN dest_dork.d_datani and dest_dork.d_dataki)

    JOIN nsi.gosk AS dep_gosk  ON (dep_dork.d_vidgos   = dep_gosk.g_vid
      AND ${period}[1]::date BETWEEN dep_gosk.g_datan and dep_gosk.g_datak
      AND ${period}[1]::date BETWEEN dep_gosk.g_datani and dep_gosk.g_dataki)

    JOIN nsi.gosk AS dest_gosk ON (dest_dork.d_vidgos  = dest_gosk.g_vid
     AND ${period}[1]::date BETWEEN dest_gosk.g_datan and dest_gosk.g_datak
     AND ${period}[1]::date BETWEEN dest_gosk.g_datani and dest_gosk.g_dataki)

    JOIN nsi.stanv AS dep_stanv  ON (bmd.departure_station = dep_stanv.stan
      AND ${period}[1]::date BETWEEN dep_stanv.datand and dep_stanv.datakd
      AND ${period}[1]::date BETWEEN dep_stanv.datani and dep_stanv.dataki)

    JOIN nsi.stanv AS dest_stanv ON (bmd.arrival_station   = dest_stanv.stan
     AND ${period}[1]::date BETWEEN dest_stanv.datand and dest_stanv.datakd
     AND ${period}[1]::date BETWEEN dest_stanv.datani and dest_stanv.dataki)

    WHERE departure_date BETWEEN ${period}[1]::date AND ${period}[2]::date
      AND bmd.carrier_code = ANY (${carriers}::numeric[])
      AND bpd.carriage_type <> '2'
      AND bpd.carriage_type <> '4'
      AND bcd.sum_code != 105
      AND bcd.sum_code != 104
      AND bcd.sum_code != 117
      AND bcd.sum_code != 120
      AND bcd.sum_code != 122
      AND bcd.sum_code > 99
      AND (
        (bcd.paymenttype != 'Ж' AND bcd.paymenttype != 'С')
       OR
        (bcd.paymenttype =  'Ж' AND bpd.flg_category_gd !='1')
        )
      AND (cardinality(${type}) = 0 OR
           '1' = ANY (${type}) AND bmd.shipment_type = 'Б'
                               AND bmd.flg_vag = '0' OR

           '2' = ANY (${type}) AND (bmd.shipment_type = 'ГН' OR
                                    bmd.shipment_type = 'ГП' OR
                                    bmd.shipment_type = 'Т')
                                AND bmd.flg_vag = '0' OR

           '3' = ANY (${type}) AND (bmd.shipment_type = 'ГН' OR
                                    bmd.shipment_type = 'ГП' OR
                                    bmd.shipment_type = 'Т')
                                AND bmd.flg_vag = '1' OR

           '4' = ANY (${type}) AND bmd.shipment_type = 'П'
                               AND bpd.carr_kind     = 'M'
                               AND bmd.flg_vag       = '1')

        AND (cardinality(${kind}) = 0 OR
             '1' = ANY (${kind}) AND bpd.flg_handluggage = '1'
                                 AND bpd.flg_speckupe    = '0' OR
             '2' = ANY (${kind}) AND bpd.flg_speckupe = '1' OR
             '3' = ANY (${kind}) AND (bpd.flg_car = '1' OR bpd.flg_car = '2') OR
             '4' = ANY (${kind}) AND (bpd.carr_kind = 'A' OR bpd.carr_kind = 'S') OR
             '5' = ANY (${kind}) AND bpd.carr_kind = 'B' OR
             '6' = ANY (${kind}) AND bpd.carr_kind = 'G' OR
             '7' = ANY (${kind}) AND bpd.carr_kind = 'N' OR
             '8' = ANY (${kind}) AND bpd.carr_kind = 'V' OR
             '9' = ANY (${kind}) AND bpd.carr_kind = 'Z' OR
            '10' = ANY (${kind}) AND bpd.carr_kind = 'E' OR
            '11' = ANY (${kind}) AND bpd.carr_kind = 'L' AND bpd.flg_carriagepass='0' OR
            '12' = ANY (${kind}) AND bpd.carr_kind = 'F' OR
            '13' = ANY (${kind}) AND bpd.carr_kind = 'L' AND bpd.flg_carriagepass='1')

        AND (cardinality(${extra}) = 0 OR
             '*' = ANY (${extra}) AND (bpd.flg_animals = '1' OR bpd.flg_animals <> '1' AND bpd.flg_mini_cargo <> '1' OR bpd.flg_mini_cargo = '1') OR
             '1' = ANY (${extra}) AND bpd.flg_animals = '1' OR
             '2' = ANY (${extra}) AND bpd.flg_animals <> '1' AND bpd.flg_mini_cargo <> '1' OR
             '3' = ANY (${extra}) AND bpd.flg_mini_cargo = '1' OR
             '4' = ANY (${extra}) AND bpd.flg_carriagerepairs = '1')

      AND dep_gosk.g_kod                                             = ANY (${depCountries}::char(2)[])
      AND dest_gosk.g_kod                                            = ANY (${destCountries}::char(2)[])
      AND (cardinality(${depRoads})     = 0 OR bmd.departure_dor     = ANY (${depRoads}::char[]))
      AND (cardinality(${destRoads})    = 0 OR bmd.destination_dor   = ANY (${destRoads}::char[]))
      AND (cardinality(${depStations})  = 0 OR bmd.departure_station = ANY (${depStations}::char(12)[]))
      AND (cardinality(${destStations}) = 0 OR bmd.arrival_station   = ANY (${destStations}::char(12)[]))
),
last_year AS (
    SELECT
        CASE WHEN cardinality(${depRoads})  = 0 THEN dep_gosk.g_kod   END AS dep_country_code,
        CASE WHEN cardinality(${destRoads}) = 0 THEN dest_gosk.g_kod  END AS dest_country_code,

        CASE WHEN cardinality(${depRoads})  > 0 AND cardinality(${depStations})  = 0 THEN dep_dork.d_kod  END AS dep_road_code,
        CASE WHEN cardinality(${destRoads}) > 0 AND cardinality(${destStations}) = 0 THEN dest_dork.d_kod END AS dest_road_code,

        CASE WHEN cardinality(${depStations})  > 0 THEN dep_stanv.stan   END AS dep_station_code,
        CASE WHEN cardinality(${destStations}) > 0 THEN dest_stanv.stan  END AS dest_station_code,

        bpd.weight                                                                                          AS weight,
        coalesce(CASE WHEN NOT array['3', '4']::char[] && (${type})::char[] THEN bpd.passqty ELSE 0 END, 0) AS pass_quantity,
        coalesce(CASE WHEN array['3', '4']::char[] && (${type})::char[]THEN bpd.carriage_qty ELSE 0 END, 0) AS carr_quantity,
        bpd.place_qty                                                                                       AS place_quantity,
        bcd.sum_nde                                                                                         AS payments,
        bmd.train_num

    FROM rawdl2_day.bag_main_day AS bmd
    JOIN rawdl2_day.bag_cost_day AS bcd USING(id_bag_main_day)
    JOIN rawdl2_day.bag_priz_day AS bpd USING(id_bag_main_day, id_bag_priz_day)

    JOIN nsi.dork AS dep_dork  ON (bmd.departure_dor   = dep_dork.d_kod
     AND ${period}[1]::date - '1 year'::interval BETWEEN dep_dork.d_datan and dep_dork.d_datak
     AND ${period}[1]::date - '1 year'::interval BETWEEN dep_dork.d_datani and dep_dork.d_dataki)

    JOIN nsi.dork AS dest_dork ON (bmd.destination_dor = dest_dork.d_kod
     AND ${period}[1]::date - '1 year'::interval BETWEEN dest_dork.d_datan and dest_dork.d_datak
     AND ${period}[1]::date - '1 year'::interval BETWEEN dest_dork.d_datani and dest_dork.d_dataki)

    JOIN nsi.gosk AS dep_gosk  ON (dep_dork.d_vidgos   = dep_gosk.g_vid
     AND ${period}[1]::date - '1 year'::interval BETWEEN dep_gosk.g_datan and dep_gosk.g_datak
     AND ${period}[1]::date - '1 year'::interval BETWEEN dep_gosk.g_datani and dep_gosk.g_dataki)

    JOIN nsi.gosk AS dest_gosk ON (dest_dork.d_vidgos  = dest_gosk.g_vid
     AND ${period}[1]::date - '1 year'::interval BETWEEN dest_gosk.g_datan and dest_gosk.g_datak
     AND ${period}[1]::date - '1 year'::interval BETWEEN dest_gosk.g_datani and dest_gosk.g_dataki)

    JOIN nsi.stanv AS dep_stanv  ON (bmd.departure_station = dep_stanv.stan
     AND ${period}[1]::date - '1 year'::interval BETWEEN dep_stanv.datand and dep_stanv.datakd
     AND ${period}[1]::date - '1 year'::interval BETWEEN dep_stanv.datani and dep_stanv.dataki)

    JOIN nsi.stanv AS dest_stanv ON (bmd.arrival_station   = dest_stanv.stan
     AND ${period}[1]::date - '1 year'::interval BETWEEN dest_stanv.datand and dest_stanv.datakd
     AND ${period}[1]::date - '1 year'::interval BETWEEN dest_stanv.datani and dest_stanv.dataki)

    WHERE ${compareWithLastYear}
      AND departure_date BETWEEN ${period}[1]::date - '1 year'::interval AND ${period}[2]::date - '1 year'::interval
      AND bmd.carrier_code      = ANY (${carriers}::numeric[])

      AND bpd.carriage_type <> '2'
      AND bpd.carriage_type <> '4'
      AND bcd.sum_code != 105
      AND bcd.sum_code != 104
      AND bcd.sum_code != 117
      AND bcd.sum_code != 120
      AND bcd.sum_code != 122
      AND bcd.sum_code > 99
      AND (
        (bcd.paymenttype != 'Ж' AND bcd.paymenttype != 'С')
            OR
        (bcd.paymenttype =  'Ж' AND bpd.flg_category_gd !='1')
        )
      AND (cardinality(${type}) = 0 OR
           '1' = ANY (${type}) AND bmd.shipment_type = 'Б'
               AND bmd.flg_vag = '0' OR

           '2' = ANY (${type}) AND (bmd.shipment_type = 'ГН' OR
                                    bmd.shipment_type = 'ГП' OR
                                    bmd.shipment_type = 'Т')
               AND bmd.flg_vag = '0' OR

           '3' = ANY (${type}) AND (bmd.shipment_type = 'ГН' OR
                                    bmd.shipment_type = 'ГП' OR
                                    bmd.shipment_type = 'Т')
               AND bmd.flg_vag = '1' OR

           '4' = ANY (${type}) AND bmd.shipment_type = 'П'
               AND bpd.carr_kind     = 'M'
               AND bmd.flg_vag       = '1')

      AND (cardinality(${kind}) = 0 OR
           '1' = ANY (${kind}) AND bpd.flg_handluggage = '1'
               AND bpd.flg_speckupe    = '0' OR
           '2' = ANY (${kind}) AND bpd.flg_speckupe = '1' OR
           '3' = ANY (${kind}) AND (bpd.flg_car = '1' OR bpd.flg_car = '2') OR
           '4' = ANY (${kind}) AND (bpd.carr_kind = 'A' OR bpd.carr_kind = 'S') OR
           '5' = ANY (${kind}) AND bpd.carr_kind = 'B' OR
           '6' = ANY (${kind}) AND bpd.carr_kind = 'G' OR
           '7' = ANY (${kind}) AND bpd.carr_kind = 'N' OR
           '8' = ANY (${kind}) AND bpd.carr_kind = 'V' OR
           '9' = ANY (${kind}) AND bpd.carr_kind = 'Z' OR
           '10' = ANY (${kind}) AND bpd.carr_kind = 'E' OR
           '11' = ANY (${kind}) AND bpd.carr_kind = 'L' AND bpd.flg_carriagepass='0' OR
           '12' = ANY (${kind}) AND bpd.carr_kind = 'F' OR
           '13' = ANY (${kind}) AND bpd.carr_kind = 'L' AND bpd.flg_carriagepass='1')

      AND (cardinality(${extra}) = 0 OR
           '*' = ANY (${extra}) AND (bpd.flg_animals = '1' OR bpd.flg_animals <> '1' AND bpd.flg_mini_cargo <> '1' OR bpd.flg_mini_cargo = '1') OR
           '1' = ANY (${extra}) AND bpd.flg_animals = '1' OR
           '2' = ANY (${extra}) AND bpd.flg_animals <> '1' AND bpd.flg_mini_cargo <> '1' OR
           '3' = ANY (${extra}) AND bpd.flg_mini_cargo = '1' OR
           '4' = ANY (${extra}) AND bpd.flg_carriagerepairs = '1')

      AND dep_gosk.g_kod                                             = ANY (${depCountries}::char(2)[])
      AND dest_gosk.g_kod                                            = ANY (${destCountries}::char(2)[])
      AND (cardinality(${depRoads})     = 0 OR bmd.departure_dor     = ANY (${depRoads}::char[]))
      AND (cardinality(${destRoads})    = 0 OR bmd.destination_dor   = ANY (${destRoads}::char[]))
      AND (cardinality(${depStations})  = 0 OR bmd.departure_station = ANY (${depStations}::char(12)[]))
      AND (cardinality(${destStations}) = 0 OR bmd.arrival_station   = ANY (${destStations}::char(12)[]))
)
SELECT
    m.period,
    m.dep_country,
    m.dest_country,
    m.dep_road,
    m.dest_road,
    m.dep_station,
    m.dest_station,

    round(sum(m.weight)::numeric/1000, 3)           AS weight,
    round(sum(ly.weight)::numeric/1000, 3)          AS ly_weight,
    CASE WHEN ${compareWithLastYear} THEN 0 END     AS weight_compared,
    sum(m.pass_quantity)  + sum(m.carr_quantity)    AS quantity,
    sum(ly.pass_quantity) + sum(ly.carr_quantity)   AS ly_quantity,
    CASE WHEN ${compareWithLastYear} THEN 0 END     AS quantity_compared,
    sum(m.place_quantity)                           AS place_quantity,
    sum(ly.place_quantity)                          AS ly_place_quantity,
    CASE WHEN ${compareWithLastYear} THEN 0 END     AS place_quantity_compared,
    round(sum(m.payments)/1000, 4)                  AS payments,
    round(sum(ly.payments)/1000, 4)                 AS ly_payments,
    CASE WHEN ${compareWithLastYear} THEN 0 END     AS payments_compared,
    count(DISTINCT m.train_num)                     AS trains_count,
    count(DISTINCT ly.train_num)                    AS ly_trains_count,
    CASE WHEN ${compareWithLastYear} THEN 0 END     AS trains_count_compared,

    m.dep_country_code,
    m.dest_country_code,
    m.dep_road_code,
    m.dest_road_code,
    m.dep_station_code,
    m.dest_station_code

FROM main m
    LEFT OUTER JOIN last_year as ly
    USING (dep_country_code, dest_country_code, dep_road_code, dest_road_code, dep_station_code, dest_station_code)

GROUP BY     m.period,
             m.dep_country,  m.dep_country_code,
             m.dest_country, m.dest_country_code,
             m.dep_road,     m.dep_road_code,
             m.dest_road,    m.dest_road_code,
             m.dep_station,  m.dep_station_code,
             m.dest_station, m.dest_station_code