package raven.demo;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import net.miginfocom.swing.MigLayout;
import raven.chart.ChartLegendRenderer;
import raven.chart.ChartUtils;
import raven.chart.bar.HorizontalBarChart;
import raven.chart.data.category.DefaultCategoryDataset;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.line.LineChart;
import raven.chart.pie.PieChart;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Demo extends JFrame {

    public Demo() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1366, 768));
        setLocationRelativeTo(null);
        setLayout(new MigLayout("wrap"));
        //createLineChart();
        // createBarChart();
        // applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        createPieChart();
    }

    private void createLineChart() {
        lineChart = new LineChart();
        //lineChart.setChartType(LineChart.ChartType.LINE);
        JPanel panel = new JPanel(new BorderLayout());
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5,$Component.borderColor,,20;"
                + "background:$Chart.background");
        JScrollPane scroll = new JScrollPane(lineChart);
        scroll.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:0,0,0,0");
        panel.add(scroll);
        add(panel);
        createLineChartData();
    }

    private void createLineChartData() {
        DefaultCategoryDataset<String, String> categoryDataset = new DefaultCategoryDataset<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        Random ran = new Random();
        int randomDate = 7;
        for (int i = 1; i <= randomDate; i++) {
            String date = df.format(cal.getTime());
            if (i == 1) {
                categoryDataset.addValue(ran.nextInt(1) + 5, "Income", date);
                categoryDataset.addValue(ran.nextInt(1) + 5, "Expense", date);
                categoryDataset.addValue(ran.nextInt(1) + 5, "Profit", date);
            } else {
                categoryDataset.addValue(ran.nextInt(700) + 5, "Income", date);
                categoryDataset.addValue(ran.nextInt(700) + 5, "Expense", date);
                categoryDataset.addValue(ran.nextInt(700) + 5, "Profit", date);
            }

            cal.add(Calendar.DATE, 1);
        }

        /**
         * Control the legend we do not show all legend
         */
        try {
            Date date = df.parse(categoryDataset.getColumnKey(0));
            Date dateEnd = df.parse(categoryDataset.getColumnKey(categoryDataset.getColumnCount() - 1));

            DateCalculator dcal = new DateCalculator(date, dateEnd);
            long diff = dcal.getDifferenceDays();

            double d = Math.ceil((diff / 10f));
            lineChart.setLegendRenderer(new ChartLegendRenderer() {
                @Override
                public Component getLegendComponent(Object legend, int index) {
                    if (index % d == 0) {
                        return super.getLegendComponent(legend, index);
                    } else {
                        return null;
                    }
                }
            });
        } catch (ParseException e) {
            System.err.println(e);
        }

        lineChart.setCategoryDataset(categoryDataset);
        lineChart.getChartColor().addColor(Color.decode("#38bdf8"), Color.decode("#fb7185"), Color.decode("#34d399"));
        JLabel header = new JLabel("Income Data");
        header.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:+2");
        lineChart.setHeader(header);
    }

    private void createBarChart() {
        // BarChart 1
        HorizontalBarChart barChart1 = new HorizontalBarChart();
        JLabel header1 = new JLabel("Monthly Income");
        header1.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:+2");
        barChart1.setHeader(header1);
        barChart1.setBarColor(Color.decode("#f97316"));
        barChart1.setDataset(createData());
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5,$Component.borderColor,,20;"
                + "background:$Chart.background");
        panel1.add(barChart1);
        add(panel1, "split 2,gap 0 20, width 500");
    }

    private void createPieChart() {
        PieChart pieChart = new PieChart();
        JLabel header1 = new JLabel("Monthly Income");
        header1.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:+2");
        pieChart.setHeader(header1);
        pieChart.getChartColor().addColor(Color.decode("#f87171"), Color.decode("#fb923c"), Color.decode("#fbbf24"),Color.decode("#a3e635"),Color.decode("#34d399"),Color.decode("#22d3ee"),Color.decode("#818cf8"),Color.decode("#c084fc"));

        pieChart.setDataset(createData());
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5,$Component.borderColor,,20;"
                + "background:$Chart.background");
        panel1.add(pieChart);
        add(panel1);
    }

    private DefaultPieDataset createData() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Random random = new Random();
        dataset.addValue("July (ongoing)", random.nextInt(100));
        dataset.addValue("June", random.nextInt(100));
        dataset.addValue("May", random.nextInt(100));
        dataset.addValue("April", random.nextInt(100));
        dataset.addValue("March", random.nextInt(100));
        dataset.addValue("February", random.nextInt(100));
        return dataset;
    }

    private LineChart lineChart;

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacLightLaf.setup();
        EventQueue.invokeLater(() -> new Demo().setVisible(true));
    }
}
