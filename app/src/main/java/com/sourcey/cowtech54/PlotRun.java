package com.sourcey.cowtech54;

import android.content.Context;
import android.util.Log;

import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.TableOrder;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import javax.security.auth.Subject;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static java.lang.Math.ceil;
import static java.lang.Math.round;

/**
 * Created by Wily on 28/03/2018.
 */

public class PlotRun implements Runnable{

    private int ID;
    private LinkedList<Number> yVals1;
    private LinkedList<Number> yVals2;
    private LinkedList<Number> yVals3;
    private LinkedList<Number> yVals4;
    private LinkedList<Number> yVals5;
    private LinkedList<Number> yVals6;
    private LinkedList<Number> yVals7;
    private LinkedList<Number> yVals8;
    private LinkedList<Number> time;
    private XYPlot  plot;
    private Context context;

    private boolean yEnable1 = true;
    private boolean yEnable2 = true;
    private boolean yEnable3 = true;
    private boolean yEnable4 = true;
    private boolean yEnable5 = true;
    private boolean yEnable6 = true;
    private boolean yEnable7 = true;
    private boolean yEnable8 = true;

    private int[] enablerIntArr = {1,1,1,1,1,1,1,1,1};

    private volatile boolean stop = false;
    private volatile boolean running = false;

    public PlotRun() {
    }

    public PlotRun(int ID, XYPlot plot, Context context) {
        this.ID = ID;
        this.plot = plot;
        this.context = context;
    }

    public PlotRun(Context context, LinkedList<Number> time, LinkedList<Number> yVals1,
                   LinkedList<Number> yVals2, XYPlot plot) {
        this.context = context;
        this.yVals1 = yVals1;
        this.yVals2 = yVals2;
        this.time = time;
        this.plot = plot;
    }

    public PlotRun(LinkedList<Number> time, LinkedList<Number> yVals1, LinkedList<Number> yVals2, LinkedList<Number> yVals3, LinkedList<Number> yVals4, LinkedList<Number> yVals5, LinkedList<Number> yVals6, LinkedList<Number> yVals7, LinkedList<Number> yVals8, XYPlot plot, Context context) {
        this.yVals1 = yVals1;
        this.yVals2 = yVals2;
        this.yVals3 = yVals3;
        this.yVals4 = yVals4;
        this.yVals5 = yVals5;
        this.yVals6 = yVals6;
        this.yVals7 = yVals7;
        this.yVals8 = yVals8;
        this.time = time;
        this.plot = plot;
        this.context = context;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean getStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    //To enable/disable which Byte we want to plot
    public void enablePlotByte(int i){
        this.enablerIntArr[i]=1;
    }
    public void disablePlotByte(int i){
        this.enablerIntArr[i]=0;
    }
    public void updatePlot() {
        if (plot != null){
            plot.clear();
            plotManager2(time, yVals1, yVals2, yVals3, yVals4, yVals5, yVals6, yVals7, yVals8);
            plot.redraw();
        }
        else if(time != null){
            //draw for the first time the plot
            plotManager2(time, yVals1, yVals2, yVals3, yVals4, yVals5, yVals6, yVals7, yVals8);
        }
    }


    @Override
    public void run() {
        stop = false;
        while(!stop) {
            for (int i = 1; i <= 930; i++) {
                running = true;
                try {

                    // Sleep for 2 seconds
                    Thread.sleep(956);

                    // Use Thread.sleep(startTime * 1000); to
                    // make sleep time variable
                } catch (InterruptedException e) {
                }
                if (Thread.currentThread().isInterrupted()) {
                    Log.d("PLOT", "Thread interrupted");
                    break;
                }
                if(stop) break;

                /*plot.clear();
                plotManager2(time, yVals1, yVals2,yVals3, yVals4, yVals5, yVals6, yVals7, yVals8);
                plot.redraw();
*/

                plot.clear();
                plotManager2(time, yVals1, yVals2,yVals3, yVals4, yVals5, yVals6, yVals7, yVals8);

                //plotManager(time, yVals1, yVals2);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        plot.redraw();
                    }
                });

            }
            break;
        }

        stop = false;
        running = false;

    }

    private void plotManager(LinkedList<Number> domainList, LinkedList<Number> seriesList1, LinkedList<Number> seriesList2){
        // Convert from arraylist back to primitive Array Number
        //final Number[] domainArray;

        try {
            XYSeries series1 = new SimpleXYSeries(
                    domainList, seriesList1, "Series1");
            XYSeries series2 = new SimpleXYSeries(
                    domainList, seriesList2, "Series2");

            // add a new series' to the xyplot:

            // create formatters to use for drawing a series using LineAndPointRenderer
            // and configure them from xml:
            LineAndPointFormatter series1Format =
                    new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels);
            LineAndPointFormatter series2Format =
                    new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_2);

            // (optional) add some smoothing to the lines:
            // see: http://androidplot.com/smooth-curves-and-androidplot/
            series1Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
            series2Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));



            //Boundaries

            int lastDomain = (int)domainList.getLast();
            lastDomain = lastDomain/1000;
            lastDomain = lastDomain*1000 + 5000;
            int Domain = 90000;
            plot.setRangeBoundaries(0, 90, BoundaryMode.GROW);
            plot.setDomainBoundaries(lastDomain - Domain , lastDomain, BoundaryMode.FIXED);



            if(enablerIntArr[1]==1) plot.addSeries(series1, series1Format);
            if(enablerIntArr[2]==1) plot.addSeries(series2, series2Format);

        }catch (ConcurrentModificationException ie){

            long tsLong = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
            Date resultdate = new Date(tsLong);
            String rsltDate = sdf.format(resultdate);

            Log.d("PlotRun", rsltDate +": ConcurrentMod Error: " + ie);
        }


        //Give some format tu the axis labals
       /* plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainArray[i]);
            }
            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });*/


    }

    private void plotManager2(LinkedList<Number> domainList,
                              LinkedList<Number> seriesList1,
                              LinkedList<Number> seriesList2,
                              LinkedList<Number> seriesList3,
                              LinkedList<Number> seriesList4,
                              LinkedList<Number> seriesList5,
                              LinkedList<Number> seriesList6,
                              LinkedList<Number> seriesList7,
                              LinkedList<Number> seriesList8){
        // Convert from arraylist back to primitive Array Number
        //final Number[] domainArray;
        try {
            LinkedList<Number> domainListc = (LinkedList<Number>) domainList.clone();
            LinkedList<Number> seriesList1c = (LinkedList<Number>) seriesList1.clone();
            LinkedList<Number> seriesList2c = (LinkedList<Number>) seriesList2.clone();
            LinkedList<Number> seriesList3c = (LinkedList<Number>) seriesList3.clone();
            LinkedList<Number> seriesList4c = (LinkedList<Number>) seriesList4.clone();
            LinkedList<Number> seriesList5c = (LinkedList<Number>) seriesList5.clone();
            LinkedList<Number> seriesList6c = (LinkedList<Number>) seriesList6.clone();
            LinkedList<Number> seriesList7c = (LinkedList<Number>) seriesList7.clone();
            LinkedList<Number> seriesList8c = (LinkedList<Number>) seriesList8.clone();

            XYSeries series1 = new SimpleXYSeries(domainListc,
                    seriesList1c, "B1");
            XYSeries series2 = new SimpleXYSeries(domainListc,
                    seriesList2c, "B2");
            XYSeries series3 = new SimpleXYSeries(domainListc,
                    seriesList3c, "B3");
            XYSeries series4 = new SimpleXYSeries(domainListc,
                    seriesList4c, "B4");
            XYSeries series5 = new SimpleXYSeries(domainListc,
                    seriesList5c, "B5");
            XYSeries series6 = new SimpleXYSeries(domainListc,
                    seriesList6c, "B6");
            XYSeries series7 = new SimpleXYSeries(domainListc,
                    seriesList7c, "B7");
            XYSeries series8 = new SimpleXYSeries(domainListc,
                    seriesList8c, "B8");

            // add a new series' to the xyplot:

            // create formatters to use for drawing a series using LineAndPointRenderer
            // and configure them from xml:
            LineAndPointFormatter series1Format =
                    new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels);
            LineAndPointFormatter series2Format =
                    new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_2);
            LineAndPointFormatter series3Format =
                    new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_3);
            LineAndPointFormatter series4Format =
                    new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_4);
            LineAndPointFormatter series5Format =
                    new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_5);
            LineAndPointFormatter series6Format =
                    new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_6);
            LineAndPointFormatter series7Format =
                    new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_7);
            LineAndPointFormatter series8Format =
                    new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_8);

            // (optional) add some smoothing to the lines:
            // see: http://androidplot.com/smooth-curves-and-androidplot/
            series1Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
            series2Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
            series3Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
            series4Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
            series5Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
            series6Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
            series7Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
            series8Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

            //Boundaries
//            int lastDomain = (int)domainList.getLast();
//            lastDomain = lastDomain/1000;
//            lastDomain = lastDomain*1000 + 5000;
//            int Domain = 90000;

            double lastDomain = (double)domainList.getLast();
            lastDomain = ceil(lastDomain);//round up
            lastDomain = lastDomain + (10-lastDomain%10);
            double Domain = 100;
            plot.setRangeBoundaries(0, 90, BoundaryMode.AUTO);
            plot.setDomainBoundaries(lastDomain - Domain , lastDomain, BoundaryMode.FIXED);

            if(enablerIntArr[1]==1) plot.addSeries(series1, series1Format);
            if(enablerIntArr[2]==1) plot.addSeries(series2, series2Format);
            if(enablerIntArr[3]==1) plot.addSeries(series3, series3Format);
            if(enablerIntArr[4]==1) plot.addSeries(series4, series4Format);
            if(enablerIntArr[5]==1) plot.addSeries(series5, series5Format);
            if(enablerIntArr[6]==1) plot.addSeries(series6, series6Format);
            if(enablerIntArr[7]==1) plot.addSeries(series7, series7Format);
            if(enablerIntArr[8]==1) plot.addSeries(series8, series8Format);

        }catch(ConcurrentModificationException | IllegalArgumentException ie){

            long tsLong = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
            Date resultdate = new Date(tsLong);
            String rsltDate = sdf.format(resultdate);

            Log.d("PlotRun", rsltDate +": Exc: " + ie);
            ie.printStackTrace();

        }

        //Give some format tu the axis labals
       /* plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainArray[i]);
            }
            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });*/
    }
}

