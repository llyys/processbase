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
import java.util.ArrayList;
import org.jfree.data.general.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.JFreeChart;
import org.jfree.data.Range;
import org.processbase.ui.template.ImageSource;

/**
 *
 * @author mgubaidullin
 */
public class MeterImageSource extends ImageSource {

    private final ArrayList<MeterInterval> intervals;

    public MeterImageSource(Range range, ArrayList<MeterInterval> i, String units, Color background) {
        super();
        dataset = new DefaultValueDataset();
        plot = new MeterPlot((DefaultValueDataset) dataset);
        chart = new JFreeChart(plot);
        intervals = i;
        ((MeterPlot) plot).setRange(range);
        for (MeterInterval mi : intervals) {
            ((MeterPlot) plot).addInterval(mi);
        }
        ((MeterPlot) plot).setUnits(units);
        ((MeterPlot) plot).setTickSize(1);
//        ((MeterPlot) plot).setBackgroundPaint(background != null ? background : new Color(230, 235, 239));
        imagebuffer.reset();
    }

    public void setValue(Number value) {
        ((DefaultValueDataset) dataset).setValue(value);
    }
}
