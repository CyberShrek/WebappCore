with main AS (
    SELECT
        CASE WHEN ('DAYS' = ANY(${periodDetailing}))   THEN departure_date::text
             WHEN ('MONTHS' = ANY(${periodDetailing})) THEN to_char(departure_date, 'YYYY-MM')
             WHEN ('YEARS' = ANY(${periodDetailing}))  THEN to_char(departure_date, 'YYYY')     END period,

        bmd.train_num,

        CASE WHEN bmd.train_speed = '0' THEN 'ПАССАЖИРСКИЙ'
             WHEN bmd.train_speed = '1' THEN 'СКОРЫЙ'
             WHEN bmd.train_speed = '2' THEN 'ПОЧТОВО-БАГАЖНЫЙ'
             WHEN bmd.train_speed = '3' THEN 'ГРУЗОПАССАЖИРСКИЙ' ELSE bmd.train_speed END AS train_category,

        bpd.weight                                                                                          AS weight,
        coalesce(CASE WHEN NOT array['3', '4']::char[] && (${type})::char[] THEN bpd.passqty ELSE 0 END, 0) AS pass_quantity,
        coalesce(CASE WHEN array['3', '4']::char[] && (${type})::char[]THEN bpd.carriage_qty ELSE 0 END, 0) AS carr_quantity,
        bcd.sum_nde                                                                                         AS payments,
        bpd.cargo_turn

    FROM rawdl2_day.bag_main_day AS bmd
    JOIN rawdl2_day.bag_cost_day AS bcd USING(id_bag_main_day)
    JOIN rawdl2_day.bag_priz_day AS bpd USING(id_bag_main_day, id_bag_priz_day)

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
        (bcd.paymenttype = 'Ж' AND bpd.flg_category_gd != '1')
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
           '1' = ANY (${extra}) AND bpd.flg_animals  = '1' OR
           '2' = ANY (${extra}) AND bpd.flg_animals <> '1' AND bpd.flg_mini_cargo <> '1' OR
           '3' = ANY (${extra}) AND bpd.flg_mini_cargo = '1' OR
           '4' = ANY (${extra}) AND bpd.flg_carriagerepairs = '1')
      AND bmd.departure_station || '|' || bmd.arrival_station = ANY (${stationPairs}::text[])
)
SELECT
    period,
    train_num,
    train_category,
    ROUND(SUM(weight)::numeric/1000, 3) AS weight,
    COALESCE(100 * SUM(weight) / NULLIF(SUM(SUM(weight)) OVER (), 0), 0) AS weight_share,
    SUM(pass_quantity) + SUM(carr_quantity) AS quantity,
    COALESCE(100 * (SUM(pass_quantity) + SUM(carr_quantity)) /
                   NULLIF(SUM(SUM(pass_quantity) + SUM(carr_quantity)) OVER (), 0), 0) AS quantity_share,
    ROUND(SUM(payments)/1000, 4) AS payments,
    COALESCE(100 * SUM(payments) / NULLIF(SUM(SUM(payments)) OVER (), 0), 0) AS payments_share,
    0 AS payments_per_quantity,
    COALESCE(100 * SUM(payments) / NULLIF(SUM(SUM(payments)) OVER (), 0), 0) AS payments_per_quantity_share,
    ROUND(AVG(cargo_turn)/1000, 3) AS weight_per_milliage,
    COALESCE(100 * SUM(cargo_turn) / NULLIF(SUM(SUM(cargo_turn)) OVER (), 0), 0) AS weight_per_milliage_share
FROM main
GROUP BY period, train_num, train_category
ORDER BY period, train_num;