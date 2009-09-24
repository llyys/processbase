/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.dashboard;

import java.awt.*;
import org.jfree.data.general.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.JFreeChart;
import org.naxitrale.processbase.ui.template.ImageSource;

/**
 *
 * @author mgubaidullin
 */
public class ThermometerImageSource extends ImageSource {

//    private final ArrayList<MeterInterval> intervals;
    public ThermometerImageSource(double lower, double upper, Color background) {
        super();
        dataset = new DefaultValueDataset();
        plot = new ThermometerPlot((DefaultValueDataset) dataset);
        chart = new JFreeChart(plot);
        ((ThermometerPlot) plot).setUnits(ThermometerPlot.UNITS_NONE);
        imagebuffer.reset();
    }

    public void setValue(Number value) {
        ((DefaultValueDataset) dataset).setValue(value);
    }
}
