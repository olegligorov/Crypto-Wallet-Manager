package bg.sofia.uni.fmi.mjt.cryptowalletmanager;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto.Crypto;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto.CryptoListings;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.ExceptionLogger;
import com.google.gson.Gson;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
	// write your code here
        ExceptionLogger.setUpLogger();
        ExceptionLogger.writeException(new IllegalStateException("test"));
        String js = "{\"asset_id\": \"BTC\",\n" +
                "                \"name\": \"Bitcoin\",\n" +
                "                \"type_is_crypto\": 1,\n" +
                "                \"data_start\": \"2010-07-17\",\n" +
                "                \"data_end\": \"2019-11-03\",\n" +
                "                \"data_quote_start\": \"2014-02-24T17:43:05.0000000Z\",\n" +
                "                \"data_quote_end\": \"2019-11-03T17:55:07.6724523Z\",\n" +
                "                \"data_orderbook_start\": \"2014-02-24T17:43:05.0000000Z\",\n" +
                "                \"data_orderbook_end\": \"2019-11-03T17:55:17.8592413Z\",\n" +
                "                \"data_trade_start\": \"2010-07-17T23:09:17.0000000Z\",\n" +
                "                \"data_trade_end\": \"2019-11-03T17:55:11.8220000Z\",\n" +
                "                \"data_symbols_count\": 22711,\n" +
                "                \"volume_1hrs_usd\": 102894431436.49,\n" +
                "                \"volume_1day_usd\": 2086392323256.16,\n" +
                "                \"volume_1mth_usd\": 57929168359984.54,\n" +
                "                \"price_usd\": 9166.207274778093436220194944}";
        Gson gson = new Gson();
        Crypto crypto = gson.fromJson(js, Crypto.class);
        System.out.println(crypto);
/*
        System.out.println("here is the api testing");
        HttpClient client = HttpClient.newBuilder().build();

        CryptoListings cr = new CryptoListings(client);
        try {
            var list = cr.getListings();
            for (var i : list.keySet()) {
                System.out.println(list.get(i));
            //    sb.append(list.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println(currentTime);


 */
        Map<Crypto, Double> mp = new HashMap<>();
        mp.put(crypto, 1.1);
        crypto.setPrice_usd(5);
        System.out.println(crypto);
        System.out.println(mp);
    }
}
