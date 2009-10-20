package org.processbase.ui.dashboard;

import java.awt.*;
import org.jfree.chart.plot.dial.DialBackground;
import org.processbase.ui.template.ImageSource;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

/**
 *
 * 
 */
public class DialImageSource extends ImageSource {

    DefaultValueDataset hoursDataset = new DefaultValueDataset(6.0);
    DefaultValueDataset dataset2 = new DefaultValueDataset(15.0);

    public DialImageSource(Number hour, Number minute) {
        super();
        hoursDataset = new DefaultValueDataset(hour);
        dataset2 = new DefaultValueDataset(minute);
        plot = new DialPlot();
        ((DialPlot) plot).setView(0.0, 0.0, 1.0, 1.0);
        ((DialPlot) plot).setDataset(0, hoursDataset);
        ((DialPlot) plot).setDataset(1, dataset2);
        SimpleDialFrame dialFrame = new SimpleDialFrame();
        dialFrame.setBackgroundPaint(Color.lightGray);
        dialFrame.setForegroundPaint(Color.darkGray);
        ((DialPlot) plot).setDialFrame(dialFrame);

        DialBackground db = new DialBackground(Color.white);
        db.setGradientPaintTransformer(new StandardGradientPaintTransformer(
                GradientPaintTransformType.VERTICAL));
        ((DialPlot) plot).setBackground(db);

        StandardDialScale hourScale = new StandardDialScale(0, 12, 90, -360, 1.0, 0);
        hourScale.setFirstTickLabelVisible(false);
        hourScale.setMajorTickIncrement(1.0);
        hourScale.setTickRadius(0.88);
        hourScale.setTickLabelOffset(0.15);
        hourScale.setTickLabelFont(new Font("Dialog", Font.PLAIN, 14));
        ((DialPlot) plot).addScale(0, hourScale);

        StandardDialScale scale2 = new StandardDialScale(0, 60, 90, -360, 5.0, 0);
        scale2.setVisible(false);
        scale2.setMajorTickIncrement(5.0);
        scale2.setTickRadius(0.68);
        scale2.setTickLabelOffset(0.15);
        scale2.setTickLabelFont(new Font("Dialog", Font.PLAIN, 14));

        ((DialPlot) plot).addScale(1, scale2);

        DialPointer needle2 = new DialPointer.Pointer(0);
        needle2.setRadius(0.55);
        ((DialPlot) plot).addLayer(needle2);

        ((DialPlot) plot).mapDatasetToScale(1, 1);

        DialPointer needle = new DialPointer.Pointer(1);
        ((DialPlot) plot).addLayer(needle);

        DialCap cap = new DialCap();
        cap.setRadius(0.10);
        ((DialPlot) plot).setCap(cap);

        chart = new JFreeChart(((DialPlot) plot));
        chart.setTitle("");
        ChartPanel cp1 = new ChartPanel(chart);
        cp1.setPreferredSize(new Dimension(400, 400));

        imagebuffer.reset();
    }
}
