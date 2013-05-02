
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.LinkedList;

import org.jfree.data.xy.XYDataset;

/**
* Created with IntelliJ IDEA.
* User: inti
* Date: 4/27/13
* Time: 2:06 PM
* To change this template use File | Settings | File Templates.
*/
class AppPanelInformation extends JPanel {

//    ITrace2D traceCPU;
//    ITrace2D traceMemory;

    long initial = System.currentTimeMillis() / 1000;

    long lastCPU = 0;

    MyXYdata datasetCPU;
    MyXYdata datasetMemory;

    JFreeChart chartCPU;
    JFreeChart chartMem;

    class MyXYdata implements  XYDataset {

        static final int max = 120;
        double[] x = new double[max];
        double[] y = new double[max];
        int start = 0;
        int end = 0;
        int count = 0;
        private DatasetGroup dataGroup = new DatasetGroup("0");

        private LinkedList<DatasetChangeListener> listeners = new LinkedList<DatasetChangeListener>();

        MyXYdata() {
        }

        @Override
        public DomainOrder getDomainOrder() {
            return DomainOrder.ASCENDING;
        }

        @Override
        public int getItemCount(int i) {
            return count;
        }

        @Override
        public Number getX(int serieIndex, int index) {
            int i = (start + index) % max;
            return x[i];
        }

        @Override
        public double getXValue(int serieIndex, int index) {
            int i = (start + index) % max;
            return x[i];
        }

        @Override
        public Number getY(int serieIndex, int index) {
            int i = (start + index) % max;
            return y[i];
        }

        @Override
        public double getYValue(int serieIndex, int index) {
            int i = (start + index) % max;
            return y[i];
        }

        @Override
        public int getSeriesCount() {
            return 1;
        }

        @Override
        public Comparable getSeriesKey(int i) {
            return "CPU";
        }

        @Override
        public int indexOf(Comparable comparable) {
            return 0;
        }

        public void addPoint( double x, double y) {
            if (count < max) {
                this.x[end] = y;
                this.y[end] = x;
                end = (end + 1) % max;
                count++;
            }
            else {
                start = (start + 1) % max;
                this.x[end] = y;
                this.y[end] = x;
                end = (end + 1) % max;
            }

            for (DatasetChangeListener l : listeners)
                l.datasetChanged(new DatasetChangeEvent(this, this));
        }

        @Override
        public void addChangeListener(DatasetChangeListener datasetChangeListener) {
            listeners.add(datasetChangeListener);
        }

        @Override
        public void removeChangeListener(DatasetChangeListener datasetChangeListener) {
            listeners.remove(datasetChangeListener);
        }

        @Override
        public DatasetGroup getGroup() {
            return dataGroup;
        }

        @Override
        public void setGroup(DatasetGroup datasetGroup) {
            this.dataGroup = datasetGroup;
        }
    }


    AppPanelInformation(String appName, long initialCPU){
        this.lastCPU = initialCPU;
        this.setLayout(new GridLayout(1, 2));
        this.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED), new TitledBorder(appName)));

        datasetCPU = new MyXYdata();
        chartCPU = ChartFactory.createXYLineChart("", "Executed Instructions" , "Time", datasetCPU, PlotOrientation.HORIZONTAL, false, false, false);
        NumberAxis na = new NumberAxis("Time");
        na.setAutoRange(true);
        na.setAutoRangeIncludesZero(false);
        chartCPU.getXYPlot().setRangeAxis(0,na);
        this.add(new ChartPanel(chartCPU));

        datasetMemory = new MyXYdata();
        chartMem = ChartFactory.createXYLineChart("", "Allocated Objects" , "Time", datasetMemory, PlotOrientation.HORIZONTAL, false, false, false);
        na = new NumberAxis("Time");
        na.setAutoRange(true);
        na.setAutoRangeIncludesZero(false);
        chartMem.getXYPlot().setRangeAxis(0,na);
        this.add(new ChartPanel(chartMem));
    }

    public void setCpu(long l) {
        long inStep = l - lastCPU;
        lastCPU = l;
        datasetCPU.addPoint(System.currentTimeMillis() / 1000 - initial, inStep);
        chartCPU.fireChartChanged();
    }

    public void setMemory(long l) {
        datasetMemory.addPoint(System.currentTimeMillis()/ 1000 - initial, l);
        chartMem.fireChartChanged();
    }

}
