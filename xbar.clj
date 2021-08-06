#!/usr/bin/env -P /usr/local/bin bb
(comment "
<xbar.title>Ambient Weather</xbar.title>
<xbar.version>v0.1</xbar.version>
<xbar.author>Geoff Gallaway</xbar.author>
<xbar.desc>Display readings from an ambient weather station</xbar.desc>
<xbar.abouturl>https://github.com/geoffeg/ambient-weather-xbar</xbar.abouturl>
<xbar.var>string(MAC_ADDR=""): MAC Address of weather station to dislpay</xbar.var>
<xbar.var>string(API_KEY=""): API Key from from https://ambientweather.net/account</xbar.var>
<xbar.var>string(APP_KEY=""): APP Key from from https://ambientweather.net/account</xbar.var>
")
(ns ambient-weather-xbar
  (:require [babashka.curl :as curl]
            [cheshire.core :as json]
            selmer.util
            [selmer.parser])
  (:import [java.time.format DateTimeFormatter]
           [java.time Instant ZoneId LocalDateTime]))

(defn get-latest-observation [mac-address application-key api-key]
  (->> (str "https://api.ambientweather.net/v1/devices/" mac-address 
            "?apiKey=" api-key
            "&applicationKey=" application-key
            "&limit=1")
       curl/get
       :body
       json/parse-string
       first))

(defn epoch-to-datetime [epoch] 
  (.format 
    (DateTimeFormatter/ofPattern "MM/dd/yyyy hh:mm a") 
    (LocalDateTime/ofInstant 
      (Instant/ofEpochMilli epoch) 
      (ZoneId/systemDefault))))

(defn write-template [observation]
  (str (observation "tempf") " °f" \newline
       "---" \newline
       "Temperature: " (observation "tempf") "°f" \newline
       "Humidity: " (observation "humidity") "%" \newline
       "Dewpoint: " (observation "dewPoint") "°f" \newline
       "Wind from: " (observation "winddir") "° at " (observation "windspeedmph") " mph gusting " (observation "windgustmph") " mph" \newline
       "Pressure: " (observation "baromrelin") " inHg" \newline
       "Feels like: " (observation "feelsLike") "°f" \newline
       "Solar radiation: " (* (observation "solarradiation") 638) " Lux" \newline
       "UV index: " (observation "uv") \newline
       "Rain: " (observation "eventrainin") " in" \newline
       "-- Hourly: " (observation "hourlyrainin") " in" \newline
       "-- Daily: " (observation "dailyrainin") " in" \newline
       "-- 24 hour: " (observation "24hourrainin") " in" \newline
       "-- Weekly: " (observation "weeklyrainin") " in" \newline
       "-- Monthly: " (observation "monthlyrainin") " in" \newline
       "-- Yearly: " (observation "yearlyrainin") " in" \newline
       "-- Total: " (observation "totalrainin") " in" \newline
       "Observed at: " (epoch-to-datetime (observation "dateutc"))))

(defn main [& args]
  (->> (get-latest-observation 
         (System/getenv "MAC_ADDR") 
         (System/getenv "APP_KEY") 
         (System/getenv "API_KEY"))
       write-template
       print))

(main)

