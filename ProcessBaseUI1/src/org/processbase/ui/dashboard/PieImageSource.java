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
