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

    public Pie3DImageSource() {
        super();
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
