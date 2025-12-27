SELECT ${compareWithLastYear}::boolean as compareWithLastYear,
       ${period}::text as period,
       ${period}[1] as date1,
       ${period}[2] as date2,
       ${carriers}::text as carriers,
       ${depCountries}::text as depCountries,
       ${distCountries}::text as distCountries,
       ${depRoads}::text as depRoads,
       ${distRoads}::text as distRoads,
       ${depStations}::text as depStations,
       ${distStations}::text as distStations

    jdbc:postgresql://pg01exp:5432/abd
    asul
    ASULADMIN20202

