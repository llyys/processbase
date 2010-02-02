/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.ui.dashboard;

import java.awt.*;
import org.jfree.data.general.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.JFreeChart;
import org.processbase.ui.template.ImageSource;

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
