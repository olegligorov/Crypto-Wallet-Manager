package bg.sofia.uni.fmi.mjt.cryptowalletmanager.crypto;

import bg.sofia.uni.fmi.mjt.cryptowalletmanager.commands.Utility;
import bg.sofia.uni.fmi.mjt.cryptowalletmanager.exceptions.CryptoListingsException;
import com.google.gson.Gson;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CryptoListings implements Serializable {
    private static final Gson GSON = new Gson();
   // private static final String str = "https://rest.coinapi.io/v1/assets?apikey=8703CF22-41DE-4AC5-9CBD-5CAFDB5E4F99";
    private static final String str = "https://rest.coinapi.io/v1/assets?apikey="+ Utility.API_KEY;

    private final HttpClient client;

    private LocalDateTime listingsTime;

    public CryptoListings(HttpClient client) {
        this.client = client;
    }

    public Map<String, Crypto> getListings() throws CryptoListingsException {
        List<Crypto> cryptoList = this.getCryptoListings();
        Map<String, Crypto> listings = new LinkedHashMap<>();

        for (var crypto : cryptoList) {
            listings.put(crypto.getAsset_id(), crypto);
        }
        return listings;
    }

    public LocalDateTime getListingsTime() {
        return listingsTime;
    }

    public void setListingsTime(LocalDateTime listingsTime) {
        this.listingsTime = listingsTime;
    }

    private List<Crypto> getCryptoListings() throws CryptoListingsException {
        HttpResponse<String> response;
        try {
            URI uri = new URI(str);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new CryptoListingsException("Could not retrieve crypto listings");
        }
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            Crypto[] cryptos = GSON.fromJson(response.body(), Crypto[].class);
            listingsTime = LocalDateTime.now();
            //#TODO change to 50
            return Arrays.stream(cryptos)
                    .filter(crypto -> crypto.getType_is_crypto() == 1)
                    .limit(10).toList();
        }
        switch (response.statusCode()) {
            case 400 -> throw new CryptoListingsException("Bad Request -- There is something wrong with your request");
            case 401 -> throw new CryptoListingsException("Unauthorized -- Your API key is wrong");
            case 403 -> throw new CryptoListingsException("Forbidden -- Your API key doesn't have enough privileges to access this resource");
            case 429 -> throw new CryptoListingsException("Too many requests -- You have exceeded your API key rate limits");
            default -> throw new CryptoListingsException("Could not retrieve crypto listings");
        }
    }
}
