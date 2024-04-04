package com.example.jfree.controller;
import com.example.jfree.service.GraphService;
import org.jfree.data.Value;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@Controller
@RequestMapping("/graph")
public class GraphController {

    @Autowired
   GraphService graphService;
   @GetMapping(value = "/scatter", produces = "image/png")
   @ResponseBody
   public byte[] getScatterChart() throws IOException {
      //public static void main(String[] args) throws IOException, org.json.simple.parser.ParseException {
      return graphService.scatterCpuMemoryGraph();

   }

   @GetMapping(value = "/line", produces = "image/png")
   @ResponseBody
   public byte[] getLineChart(@RequestParam(value = "deviceid") String deviceId) throws IOException, ParseException, java.text.ParseException {
      return graphService.lineChartTimestampWithMemoryAndCPU(deviceId);
   }

   @GetMapping(value = "/bar", produces = "image/png")
   @ResponseBody
   public byte[] getBarChart() throws IOException, ParseException {
      return graphService.barChartDeviceWithMemoryAndCPU();
   }

   @GetMapping(value = "/pie", produces = "image/png")
   @ResponseBody
   public byte[] getPieChart() throws IOException, ParseException {
      return graphService.pieChartNetworkTrafficAnalysis();
   }

}