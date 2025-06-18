package com.unipam;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class App {
    public static void main(String[] args) {
        String apiKey = "DEMO_KEY";
        String url = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            // Buscar dados da API
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            String title = jsonObject.get("title").getAsString();
            String explanation = jsonObject.get("explanation").getAsString();
            String imageUrl = jsonObject.get("url").getAsString();

            System.out.println("Título: " + title);
            System.out.println("Descrição: " + explanation);
            System.out.println("URL da Imagem: " + imageUrl);

            // Fazer download da imagem
            downloadImage(client, imageUrl);

        } catch (IOException | InterruptedException e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void downloadImage(HttpClient client, String imageUrl) {
        try {
            // Requisição para baixar a imagem
            HttpRequest imageRequest = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .build();

            HttpResponse<InputStream> imageResponse = client.send(imageRequest, 
                    HttpResponse.BodyHandlers.ofInputStream());

            // Salvar a imagem
            String fileName = "nasa_image.jpg";
            try (InputStream inputStream = imageResponse.body();
                 FileOutputStream outputStream = new FileOutputStream(fileName)) {
                
                inputStream.transferTo(outputStream);
                
                System.out.println("Imagem salva como: " + fileName);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Erro no download: " + e.getMessage());
            e.printStackTrace();
        }
    }
}