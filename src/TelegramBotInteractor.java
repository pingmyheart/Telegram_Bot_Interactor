import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
//import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.util.EntityUtils;
import org.json.*;
//import sun.net.www.http.HttpClient;

public class TelegramBotInteractor{

    public static String botToken = "";//public string that will contains the token from the bot father
    public static String chatID = "";//ID taken from the json returned from mainAPI+getUpdate
    public static String mainAPI = "https://api.telegram.org/bot";

    public static void main(String[] args)throws MalformedURLException, IOException{
        Scanner in = new Scanner(System.in);
        boolean available = true;
        printCopyright();
        while(available) {
            //clearScreen();
            printMenu();
            System.out.println("Select an option");
            switch (in.nextLine()) {
            case "1":
                System.out.println("Type bot token");
                if (setBotToken(in.nextLine())) {
                    System.out.println("Token Successfully Setted");
                } else {
                    System.out.println("Token Already Setted");
                }
                break;
            case "2":
                if(!botToken.equals("")){
                    System.out.println("Available Chats:");
                    System.out.println(getAvailableChats(getResponde(mainAPI + botToken + "/getUpdates", "POST")));
                    System.out.println("Type Chat Name");
                    String tempChatID = getChatId(botToken, in.nextLine(), getResponde(mainAPI + botToken + "/getUpdates", "POST"));
                    if(tempChatID == null){
                        System.out.println("No ChatID selected");
                    }else{
                        chatID = tempChatID;
                        System.out.println("Chat ID selected " + chatID);
                    }
                }else{
                    System.out.println("Bot Token Not Setted");
                }
                break;
            case "3":
                if(!botToken.equals("") && !chatID.equals("")){
                    System.out.println("Type Message To Be Sent");
                    sendMessage(in.nextLine());
                }else{
                    System.out.println("Bot Token and Chat ID not Setted");
                }
                break;
            case "4":
                if(!botToken.equals("") && !chatID.equals("")){
                    String latitude, longitude, title, address;
                    System.out.println("Type the latitude");
                    latitude = in.nextLine().replaceAll(",", ".");
                    System.out.println("Type the longitude");
                    longitude = in.nextLine().replaceAll(",", ".");
                    System.out.println("Type the title");
                    title = in.nextLine();
                    System.out.println("Type the address");
                    address = in.nextLine();
                    sendVenue(latitude, longitude, title, address);
                }else{
                    System.out.println("Bot Token and Chat ID not Setted");
                }
                break;
            case "5":
                if(!botToken.equals("") && !chatID.equals("")){
                    sendDocument();
                }else{
                    System.out.println("Bot Token and Chat ID not Setted");
                }
                break;
            case "99":
                available = false;
                break;
                default:
                System.out.println("Not Supported Operation");
            }
        }
        System.out.println("\nSee You Soon");
        in.close();
    }

    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }  

    public static void printMenu(){
        System.out.println("\n1->Set Bot Token");
        System.out.println("2->Set Chat Name(Case Sensitive)");
        System.out.println("3->Send Message");
        System.out.println("4->Send Venue");
        System.out.println("5->Send Document");
        System.out.println("99->Close Application");
    }

    public static void printCopyright(){
        System.out.println("\t\t\tCopyright © 2020 Alusoft s.r.l. All right reserved\n");
    }

    public static boolean setBotToken(String token){
        if(botToken.equals("")){
            botToken = token;
            return true;
        }
        return false;
    }

    public static String getResponde(String url, String method)throws MalformedURLException, IOException{
        HttpURLConnection connection;

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();

        URL uri = new URL(url);
        connection = (HttpURLConnection)uri.openConnection();
            //Request SetUp
        connection.setRequestMethod(method);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int status = connection.getResponseCode();

        if(status > 299){
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            while((line = reader.readLine()) != null){
                responseContent.append(line);
                responseContent.append("\n");
            }
            reader.close();
        }else{
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null){
                responseContent.append(line);
                responseContent.append("\n");
            }
            reader.close();
        }
        connection.disconnect();;

        return responseContent.toString();
    }

    public static String getChatId(String token, String chatName, String responseContent){
        JSONArray arrays = new JSONArray(responseContent.substring(responseContent.indexOf("["), responseContent.length()-1).replaceAll("channel_post", "message"));
        for(int i=0; i<arrays.length(); i++){
            //RESULT->MESSAGE->CHAT
            JSONObject fullJson = arrays.getJSONObject(i);
            JSONObject firstParse = fullJson.getJSONObject("message");
            JSONObject secondParse = firstParse.getJSONObject("chat");
            String chatID = secondParse.get("id").toString();
            if(secondParse.get("type").toString().equals("group") || secondParse.get("type").toString().equals("channel")){
                String chatNAME = secondParse.get("title").toString();
                if(chatNAME.equals(chatName)){
                    return chatID;
                }
            }
        }
        return null;
    }

    //FUNCTION IMPLEMENTATION FOR DIFFERENT TYPE RESTAPI
    
    public static void sendMessage(String message)throws MalformedURLException, IOException{
        getResponde(mainAPI + botToken + "/sendMessage?chat_id=" + chatID + "&text=" + message, "POST");
    }

    public static void sendVenue(String latitude, String longitude, String title, String address)throws MalformedURLException, IOException{
        getResponde(mainAPI + botToken + "/sendVenue?chat_id=" + chatID + "&latitude=" + latitude + "&longitude=" + longitude + "&title=" + title + "&address=" + address, "POST");
    }

    public static void sendDocument()throws MalformedURLException, IOException{
        File tempFile = getFile();
        if(tempFile != null) {
            HttpEntity entity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addTextBody("chat_id", chatID, ContentType.DEFAULT_BINARY)
                    .addBinaryBody("document", tempFile, ContentType.DEFAULT_BINARY, tempFile.getName())
                    .build();

            String uri = mainAPI + botToken + "/sendDocument";
            HttpPost request = new HttpPost(uri);
            request.setEntity(entity);

            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(request);
        }else{
            System.out.println("There is no file selected");
        }
    }

    public static String getAvailableChats(String responseContent){
        if(!botToken.equals("")) {
            ArrayList<String> availableChats = new ArrayList<String>();
            JSONArray arrays = new JSONArray(responseContent.substring(responseContent.indexOf("["), responseContent.length()-1).replaceAll("channel_post", "message"));
            for(int i=0; i<arrays.length(); i++){
                //RESULT->MESSAGE->CHAT
                JSONObject fullJson = arrays.getJSONObject(i);
                JSONObject firstParse = fullJson.getJSONObject("message");
                JSONObject secondParse = firstParse.getJSONObject("chat");
                if(secondParse.get("type").toString().equals("group") || secondParse.get("type").toString().equals("channel")){
                    boolean chatAlreadyPresent = false;
                    for(String s : availableChats){
                        if(s.equals(secondParse.get("title").toString())){
                            chatAlreadyPresent = true;
                        }
                    }
                    if(!chatAlreadyPresent){
                        availableChats.add(secondParse.get("title").toString());
                    }
                }
            }
            String info = "";
            for(String s : availableChats){
                info += s + "\n";
            }
            return info;
        }else{
            return "Bot Token Not Setted";
        }
    }

    /*public static void sendDocument()throws MalformedURLException, IOException{
        getResponde(mainAPI + botToken + "/sendDocument?chat_id=" + chatID + "&document=" + getFileAbsolutePath(),"POST");
    }*/

    public static File getFile(){
        JFileChooser fc = new JFileChooser();

        /*FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF Document", ".pdf");
        FileNameExtensionFilter gifFilter = new FileNameExtensionFilter("GIF Image", ".gif");
        FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("Archive ZIP", ".zip");
        FileNameExtensionFilter noFilter = new FileNameExtensionFilter("All files", ".*");
        fc.setFileFilter(noFilter);
        fc.setFileFilter(zipFilter);
        fc.setFileFilter(gifFilter);
        fc.setFileFilter(pdfFilter);*/
        
        fc.setCurrentDirectory(new java.io.File("."));
        fc.setDialogTitle("Select a document...");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);

        if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            return fc.getSelectedFile();
        }
        return null;
    }
}