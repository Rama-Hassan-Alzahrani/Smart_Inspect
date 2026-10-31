package smart_inspect;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Helper class to fetch weather data from Visual Crossing API
 * Demonstrates URL/URLConnection concepts from Lecture 3 & 4
 */
public class WeatherHelper {
    
    private static final String API_KEY = "GHJQUFUHQ3TXR93PWBSUUJTK5";
    private static final String BASE_URL = 
        "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
    
    /**
     * Check current weather for a location
     * @param location City name (e.g., "Jeddah", "Riyadh", "Mecca")
     * @return Weather report as formatted string
     */
    public static String checkWeather(String location) {
        try {
            // Encode location for URL (handles spaces and special characters)
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            
            // Build URL - get today's weather only
            String urlString = BASE_URL + encodedLocation + "/today" +
                "?unitGroup=metric" +           // Use Celsius
                "&key=" + API_KEY +
                "&contentType=csv" +            // Get CSV format (easy to parse)
                "&include=days" +               // Only daily data
                "&elements=datetime,tempmax,tempmin,temp,conditions,description";
            
            System.out.println("Fetching weather from: " + urlString);
            
            // Create URL object
            URL url = new URL(urlString);
            
            // Open connection
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);  // 10 second timeout
            connection.setReadTimeout(10000);
            
            // Read response
            Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8);
            
            // Skip header line (column names)
            if (scanner.hasNextLine()) {
                String header = scanner.nextLine();
                System.out.println("CSV Header: " + header);
            }
            
            // Read data line (today's weather)
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println("CSV Data: " + line);
                
                // Parse CSV
                String[] parts = line.split(",");
                
                if (parts.length >= 6) {
                    String date = parts[0];
                    String tempMax = parts[1];
                    String tempMin = parts[2];
                    String temp = parts[3];
                    String conditions = parts[4];
                    String description = parts[5];
                    
                    scanner.close();
                    
                    return formatWeatherReport(location, temp, tempMax, tempMin, 
                                              conditions, description);
                } else {
                    scanner.close();
                    return "Error: Unexpected data format from weather service.";
                }
            }
            
            scanner.close();
            return "No weather data available for " + location;
            
        } catch (MalformedURLException e) {
            return "Error: Invalid location - " + location;
            
        } catch (SocketTimeoutException e) {
            return "Error: Weather service timeout.\nPlease check your internet connection and try again.";
            
        } catch (IOException e) {
            String message = e.getMessage();
            if (message.contains("400")) {
                return "Error: Location not found - " + location + "\nPlease try a different city name.";
            } else if (message.contains("401")) {
                return "Error: API key invalid. Please contact system administrator.";
            } else {
                return "Error: Could not connect to weather service.\n" + message;
            }
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Format weather data into readable report
     */
    private static String formatWeatherReport(String location, String temp, 
                                             String tempMax, String tempMin,
                                             String conditions, String description) {
        try {
            double temperature = Double.parseDouble(temp);
            double maxTemp = Double.parseDouble(tempMax);
            double minTemp = Double.parseDouble(tempMin);
            
            // Determine suitability for outdoor inspection
            String suitability;
            String icon;
            
            if (temperature > 45) {
                suitability = "NOT RECOMMENDED - Extreme heat";
                icon = "🔥";
            } else if (temperature > 40) {
                suitability = "CAUTION - Very hot, take precautions";
                icon = "⚠️";
            } else if (temperature < 15) {
                suitability = "COOL - Dress appropriately";
                icon = "❄️";
            } else if (conditions.toLowerCase().contains("rain")) {
                suitability = "RAIN EXPECTED - Bring protective cover";
                icon = "🌧️";
            } else if (conditions.toLowerCase().contains("storm")) {
                suitability = "NOT RECOMMENDED - Stormy conditions";
                icon = "⛈️";
            } else {
                suitability = "GOOD - Suitable for outdoor inspection";
                icon = "✅";
            }
            
            // Build report
            StringBuilder report = new StringBuilder();
            report.append("═══════════════════════════════════════\n");
            report.append("       WEATHER REPORT - ").append(location.toUpperCase()).append("\n");
            report.append("═══════════════════════════════════════\n\n");
            
            report.append("Current Temperature: ").append(String.format("%.1f", temperature)).append("°C\n");
            report.append("Today's High: ").append(String.format("%.1f", maxTemp)).append("°C\n");
            report.append("Today's Low: ").append(String.format("%.1f", minTemp)).append("°C\n\n");
            
            report.append("Conditions: ").append(conditions).append("\n");
            report.append("Description: ").append(description).append("\n\n");
            
            report.append("───────────────────────────────────────\n");
            report.append("INSPECTION ASSESSMENT:\n");
            report.append(icon).append(" ").append(suitability).append("\n");
            report.append("───────────────────────────────────────\n\n");
            
            report.append("Data source: Visual Crossing Weather API\n");
            report.append("(Demonstrates URL/HTTP concepts from Lecture 3 & 4)");
            
            return report.toString();
            
        } catch (NumberFormatException e) {
            return "Error parsing weather data: " + e.getMessage();
        }
    }
    
    /**
     * Test method - run this to verify API works
     */
    public static void main(String[] args) {
        System.out.println("Testing Weather API...\n");
        
        String[] testCities = {"Jeddah", "Riyadh", "Mecca"};
        
        for (String city : testCities) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Testing: " + city);
            System.out.println("=".repeat(50));
            
            String result = checkWeather(city);
            System.out.println(result);
            System.out.println();
        }
    }
}