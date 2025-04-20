package ga.guimx.gbunkers.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class PluginUpdates {
    public static String getLatestVersion() throws IOException {
        HttpURLConnection conn = ((HttpURLConnection) URI.create("https://api.github.com/repos/UnaPepsi/gBunkers/releases/latest").toURL().openConnection());
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept","application/vnd.github+json");
        conn.setDoOutput(true);
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        if (conn.getResponseCode() != 200){
            throw new IOException();
        }
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        JsonObject jsonResponse = new JsonParser().parse(content.toString()).getAsJsonObject();
        return jsonResponse.get("tag_name").getAsString();
    }
}
