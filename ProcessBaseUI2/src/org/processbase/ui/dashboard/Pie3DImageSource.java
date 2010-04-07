/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.dashboard;

import java.awt.Color;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.data.general.*;
import org.jfree.chart.plot.*;
import org.processbase.ui.template.ImageSource;

/**
 *
 * @author mgubaidullin
 */
public class Pie3DImageSource extends ImageSource {

    public Pie3DImageSource(int width, int height) {
        super(width, height);
        dataset = new DefaultPieDataset();
        chart = ChartFactory.createPieChart3D("", ((DefaultPieDataset) dataset), false, false, false);
        plot = (PiePlot3D) chart.getPlot();
        plot.setForegroundAlpha(0.6f);
//        ((PiePlot3D) plot).setDepthFactor(1.23);
        ((PiePlot3D) plot).setDarkerSides(true);
//        ((PiePlot3D) plot).setCircular(true);
        ((PiePlot3D) plot).setBackgroundPaint(new Color(230, 235, 239));
        imagebuffer.reset();
    }

    public void setValue(ArrayList<PieValue> values) {
        for (PieValue pv : values) {
            ((DefaultPieDataset) dataset).setValue(pv.getKey(), pv.getValue());
        }
    }
}
