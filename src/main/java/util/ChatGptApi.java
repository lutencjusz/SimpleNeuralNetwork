package util;

import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.BoardElement;
import org.fusesource.jansi.Ansi;

import java.awt.*;
import java.util.Arrays;

public class ChatGptApi {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    private static final String CHAT_GPT_KEY = dotenv.get("CHAT_GPT_KEY");
    private static final String CHAT_GPT_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String JAVELIN_ENDPOINT = "http://localhost:7000";

    /**
     * Demo Javelin REST service, do uruchomienia wymagane jest uruchomienie projektu JavelinRestServiceTest
     * (C:\Data\Java\TestJava\src\main\java\JavelinRestServiceTest.java)
     */
    public static void demoJavelin() {
        RestAssured.baseURI = JAVELIN_ENDPOINT;
        RequestSpecification request = RestAssured.given();

        Response response = request
                .header("Content-Type", "application/json")
                .get("/hello");

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
        System.out.println("message: " + response.jsonPath().get("message"));
    }

    public static String getChatGPTMessage(String message, String model, boolean isStrongerLogging) {
        RestAssured.baseURI = CHAT_GPT_ENDPOINT;
        RequestSpecification request = RestAssured.given();

        String requestBody = "{"
                + "\"model\": \"" + model + "\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]"
                + "}";

        Response response = request
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + CHAT_GPT_KEY)
                .body(requestBody)
                .post();

        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a(model).reset() + " Response Status Code: "
                + Ansi.ansi().fg(response.getStatusCode() == 200 ? Ansi.Color.GREEN : Ansi.Color.RED).a(response.getStatusCode()).reset());
        if (isStrongerLogging) {
            System.out.println("Response Body: " + response.getBody().asString());
        }
        return response.jsonPath().getString("choices[0].message.content");
    }

    private static int convertBoardToInt(String board) {
        String[] resultTable;
        try {
            resultTable = board.trim().replace("]", "").replace("[", "").replace(" ", "").split(",");
        } catch (Exception e) {
            return -1;
        }
        return Arrays.stream(resultTable).filter(s -> s.equals("1.0")).findFirst().map(Arrays.asList(resultTable)::indexOf).orElse(-1);
    }

    public static int getBestMove(String model, double[] board) {
        HeuristicStrategy heuristicStrategy = new HeuristicStrategy();
        String message = "Na postawie Fine-tuning modelu " + model + " przekaż odpowiedź dla następującej konfiguracji: " + Arrays.toString(board);
        String response = getChatGPTMessage(message, model, false);
        System.out.println("Response: " + Ansi.ansi().fg(Ansi.Color.YELLOW).a(response).reset());
        int chatGptMove = convertBoardToInt(response);
        System.out.println("Ruch zaproponowany przez ChatGPT: " + Ansi.ansi().fg(chatGptMove > -1 ? Ansi.Color.GREEN : Ansi.Color.RED).a(chatGptMove).reset());
        int heuristicsMove = heuristicStrategy.getBestMove(board, BoardElement.CIRCLE, false, true);
        if (chatGptMove != heuristicsMove) {
            System.out.println("Ruch zaproponowany przez Heurystykę: " + Ansi.ansi().fg(heuristicsMove > 0 ? Ansi.Color.GREEN : Ansi.Color.RED).a(heuristicsMove).reset());
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Chat GPT dla modelu " + model + " nie zwrócił poprawnego ruchu!").reset() + " Sprawdzam jeszcze raz...");
            message = "Sprawdź jeszcze raz, czy odpowiedź " + Arrays.toString(CheckStatusGame.convertNumberToArray(heuristicsMove, 1))
                    + " nie jest lepsza, przekaż odpowiedź dla następującej konfiguracji: " + Arrays.toString(board)
                    + ". Jeżeli potwierdzasz, że odpowiedź " + Arrays.toString(CheckStatusGame.convertNumberToArray(heuristicsMove, 1)) + " jest najlepsza, to zwróć tylko samą konfigurację, bez dodatkowych informacji.";
            response = getChatGPTMessage(message, model, false);
            System.out.println("Response: " + Ansi.ansi().fg(Ansi.Color.YELLOW).a(response).reset());
            chatGptMove = convertBoardToInt(response);
            System.out.println("Ponownie zaproponowany ruch przez ChatGPT: " + Ansi.ansi().fg(chatGptMove > 0 ? Ansi.Color.GREEN : Ansi.Color.RED).a(chatGptMove).reset());
        }
        return chatGptMove;
    }

    public static void main(String[] args) {
        String model = "ft:gpt-3.5-turbo-0125:sopim::9GNLB0jl";
        String message = "Na postawie Fine-tuning modelu ft:gpt-3.5-turbo-0125:sopim::9GNLB0jl przekaż odpowiedź dla następującej konfiguracji: [1.0,1.0,0.0,0.0,-1.0,0.0,0.0,0.0,0.0]";
//        String markdownText = getChatGPTMessage(message, model, false);
        int bestMove = getBestMove(model, new double[]{1.0, 1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0});
//        System.out.println(markdownText);
        System.out.println("Najlepszy ruch: " + bestMove);
    }
}