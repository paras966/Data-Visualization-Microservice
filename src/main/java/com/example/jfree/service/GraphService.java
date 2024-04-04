package com.example.jfree.service;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class GraphService{
    public byte[] scatterCpuMemoryGraph(){

        try {
            URL url = new URL("http://localhost:8080/routers/resources");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            StringBuilder informationString = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                informationString.append(scanner.nextLine());
            }
            //Close the scanner
            scanner.close();
            JSONParser parse = new JSONParser();
            JSONArray jsonArray = (JSONArray) parse.parse(String.valueOf(informationString));
            XYSeries series = new XYSeries("Data");
            for(Object list : jsonArray) {
                ArrayList arrayList = (ArrayList) list;
                Number x = (Number) arrayList.get(0);
                Number y = (Number) arrayList.get(1);
                series.add(x,y);
            }

            // Add the series to the dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(series);

            // Create chart
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Scatter Plot  Correlation Between CPU / MEMORY",
                    "CPU Utilization",
                    "Memory Utilization",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            BufferedImage image = chart.createBufferedImage(1200, 700);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);

            return outputStream.toByteArray();

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
}
 public byte[] lineChartTimestampWithMemoryAndCPU(String devideId) throws IOException, ParseException, java.text.ParseException {
     URL url = new URL("http://localhost:8080/routers/deviceid?deviceid="+devideId);
     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
     conn.setRequestMethod("GET");
     conn.connect();

     StringBuilder informationString = new StringBuilder();
     Scanner scanner = new Scanner(url.openStream());

     while (scanner.hasNext()) {
         informationString.append(scanner.nextLine());
     }
     //Close the scanner
     scanner.close();
     JSONParser parse = new JSONParser();
     JSONArray jsonArray = (JSONArray) parse.parse(String.valueOf(informationString));

     TimeSeriesCollection dataset = new TimeSeriesCollection();
     TimeSeries cpuSeries = new TimeSeries("CPU Utilization");
     TimeSeries memorySeries = new TimeSeries("Memory Utilization");
     for(Object list : jsonArray) {
         ArrayList arrayList = (ArrayList) list;
         String timestamp = (String) arrayList.get(0);
         Number cpuUtilization = (Number) arrayList.get(1);
         Number memoryUtilization = (Number) arrayList.get(2);
         Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
         cpuSeries.add(new Second(date), cpuUtilization);
         memorySeries.add(new Second(date), memoryUtilization);
     }

     dataset.addSeries(cpuSeries);
     dataset.addSeries(memorySeries);

     JFreeChart chart = ChartFactory.createTimeSeriesChart(
              "Utilization Over Time",
              "Time",
              "Utilization (%)",
              dataset,
              true,
              true,
              false
      );
     BufferedImage image = chart.createBufferedImage(1200, 700);
     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
     ImageIO.write(image, "png", outputStream);

     return outputStream.toByteArray();
 }

 public  byte[] barChartDeviceWithMemoryAndCPU() throws IOException, ParseException {
     URL url = new URL("http://localhost:8080/routers/all");
     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
     conn.setRequestMethod("GET");
     conn.connect();

     StringBuilder informationString = new StringBuilder();
     Scanner scanner = new Scanner(url.openStream());

     while (scanner.hasNext()) {
         informationString.append(scanner.nextLine());
     }
     //Close the scanner
     scanner.close();
     JSONParser parse = new JSONParser();
     JSONArray jsonArray = (JSONArray) parse.parse(String.valueOf(informationString));
     DefaultCategoryDataset dataset = new DefaultCategoryDataset();
     double totalCpu1=0;
     double totalCpu2=0;
     double totalCpu3=0;
     double totalMemory1=0;
     double totalMemory2=0;
     double totalMemory3=0;
     for(Object list:  jsonArray) {
         JSONObject jsonObject = (JSONObject) list;
         String deviceId = (String) jsonObject.get("deviceId");
         Number cpuUtilization1 = (Number) jsonObject.get("cpuUtilization");
         double cpuUtilization = cpuUtilization1.doubleValue();
         Number memoryUtilization1 = (Number) jsonObject.get("memoryUtilization");
         double memoryUtilization = memoryUtilization1.doubleValue();
         if(Objects.equals(deviceId, "Router001")){
             totalCpu1 = totalCpu1 + cpuUtilization;
             totalMemory1 = totalMemory1 +  memoryUtilization;
         }else if (Objects.equals(deviceId, "Router002")){
             totalCpu2 = totalCpu2 +  cpuUtilization;
             totalMemory2 = totalMemory2 +  memoryUtilization;
         }else{
             totalCpu3 = totalCpu3 +  cpuUtilization;
             totalMemory3 = totalMemory3 +  memoryUtilization;
         }
     }
     dataset.addValue(totalCpu1, "CPU Utilization", "Router001");
     dataset.addValue(totalMemory1, "Memory Utilization", "Router001");
     dataset.addValue(totalCpu2, "CPU Utilization", "Router002");
     dataset.addValue(totalMemory2, "Memory Utilization", "Router002");
     dataset.addValue(totalCpu3, "CPU Utilization", "Router003");
     dataset.addValue(totalMemory3, "Memory Utilization", "Router003");

     JFreeChart chart = ChartFactory.createBarChart(
              "Device-wise Analysis",
              "DeviceID",
              "Utilization",
              dataset
      );

     BufferedImage image = chart.createBufferedImage(1200, 700);
     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
     ImageIO.write(image, "png", outputStream);

     return outputStream.toByteArray();
 }

 public byte[] pieChartNetworkTrafficAnalysis() throws ParseException, IOException {
     URL url = new URL("http://localhost:8080/routers/networktrafficstatus");
     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
     conn.setRequestMethod("GET");
     conn.connect();

     StringBuilder informationString = new StringBuilder();
     Scanner scanner = new Scanner(url.openStream());

     while (scanner.hasNext()) {
         informationString.append(scanner.nextLine());
     }
     //Close the scanner
     scanner.close();
     JSONParser parse = new JSONParser();
     JSONArray jsonArray = (JSONArray) parse.parse(String.valueOf(informationString));
     double normal=0;
     double medium =0;
     double high = 0;
     for( Object json: jsonArray){
         String value = (String) json;
         if(Objects.equals(value, "Normal")){
             normal+=1;
         } else if (Objects.equals(value,"HighCPU")) {
             medium+=1;
         }else{
             high+=1;
         }
     }
     DefaultPieDataset dataset = new DefaultPieDataset();
     dataset.setValue("Normal", normal);
     dataset.setValue("Medium", medium);
     dataset.setValue("High", high);

     JFreeChart chart = ChartFactory.createPieChart(
              "Network Traffic Status",
              dataset,
              true,
              true,
              false
      );
     BufferedImage image = chart.createBufferedImage(1200, 700);
     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
     ImageIO.write(image, "png", outputStream);

     return outputStream.toByteArray();

 }


}
