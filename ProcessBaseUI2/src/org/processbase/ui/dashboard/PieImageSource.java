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

import java.text.NumberFormat;
import java.util.ArrayList;
import org.jfree.data.general.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.JFreeChart;
import org.processbase.ui.template.ImageSource;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

/**
 *
 * @author mgubaidullin
 */
public class PieImageSource extends ImageSource {

    public PieImageSource() {
        super();
        dataset = new DefaultPieDataset();
        plot = new PiePlot((DefaultPieDataset) dataset);
        chart = new JFreeChart(plot);
        ((PiePlot) plot).setCircular(true);
        ((PiePlot) plot).setLegendLabelGenerator(new StandardPieSectionLabelGenerator());
        imagebuffer.reset();
    }

    public void setValue(ArrayList<PieValue> values) {
        for (PieValue pv : values) {
            ((DefaultPieDataset) dataset).setValue(pv.getKey(), pv.getValue());
        }
    }
}
