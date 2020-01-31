package org.shoe.http_scrapping;

import org.apache.http.HttpException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencyConverter {
    public static Map<String, String> currencySymbols() throws IOException, HttpException {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        Content content = Request.
                Get("https://en.wikipedia.org/wiki/List_of_circulating_currencies")
                .execute()
                .returnContent();
        String[] split = content.asString().split("\n");
        for(int i = 0; i < split.length; ++i) {
            String current = split[i];
            if(current.matches("^<td data-sort-value.*$")) {
                current = current.substring(21);
                current = current.substring(0, current.indexOf('"'));
                String substring = split[i + 4].substring(4);
                if(substring.matches("[A-Z]{3}"))
                    result.put(substring, current);
            }
        }

        return result;
    }

    public static BigDecimal convertFromTo(String fromCurrency, String toCurrency) {

        try {
            Map<String, String> symbolToName = currencySymbols();
            if (!symbolToName.containsKey(fromCurrency))
                throw new IllegalArgumentException(String.format(
                        "Invalid from currency: %s", fromCurrency));
            if (!symbolToName.containsKey(toCurrency))
                throw new IllegalArgumentException(String.format(
                        "Invalid to currency: %s", toCurrency));

            String url = String.format("http://www.gocurrency.com/v2/dorate.php?inV=1&from=%s&to=%s&Calculate=Convert", toCurrency, fromCurrency);

            Content content = Request.
                    Get(url)
                    .execute()
                    .returnContent();

            StringBuffer result = new StringBuffer(content.toString());

            String theWholeThing = result.toString();
            int start = theWholeThing.lastIndexOf("<div id=\"converter_results\"><ul><li>");
            String substring = result.substring(start);
            int startOfInterestingStuff = substring.indexOf("<b>") + 3;
            int endOfInterestingStuff = substring.indexOf("</b>",
                    startOfInterestingStuff);
            String interestingStuff = substring.substring(
                    startOfInterestingStuff, endOfInterestingStuff);
            String[] parts = interestingStuff.split("=");
            String value = parts[1].trim().split(" ")[0];
            BigDecimal bottom = new BigDecimal(value);
            return bottom;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
